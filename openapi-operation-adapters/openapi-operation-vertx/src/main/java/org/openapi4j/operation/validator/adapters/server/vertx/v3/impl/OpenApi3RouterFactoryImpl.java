package org.openapi4j.operation.validator.adapters.server.vertx.v3.impl;

import org.openapi4j.core.exception.ResolutionException;
import org.openapi4j.core.model.v3.OAI3;
import org.openapi4j.operation.validator.adapters.server.vertx.v3.OpenApi3RouterFactory;
import org.openapi4j.operation.validator.util.PathResolver;
import org.openapi4j.operation.validator.validation.OperationValidator;
import org.openapi4j.operation.validator.validation.RequestValidator;
import org.openapi4j.parser.model.v3.OpenApi3;
import org.openapi4j.parser.model.v3.Operation;
import org.openapi4j.parser.model.v3.Path;
import org.openapi4j.parser.model.v3.RequestBody;
import org.openapi4j.parser.model.v3.Response;
import org.openapi4j.schema.validator.ValidationContext;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

import static org.openapi4j.operation.validator.util.PathResolver.Options.END_STRING;

public class OpenApi3RouterFactoryImpl implements OpenApi3RouterFactory {
  private static final String OP_ID_NOT_FOUND_ERR_MSG = "Operation with id '%s' not found.";

  private final Vertx vertx;
  private final RequestValidator<Void> rqValidator;
  private final Map<String, OperationSpec> operationSpecs;
  private final SecurityRequirementHelper securityHelper;

  public OpenApi3RouterFactoryImpl(Vertx vertx, OpenApi3 openApi) {
    this(vertx, new ValidationContext<>(openApi.getContext()), openApi);
  }

  @SuppressWarnings("WeakerAccess")
  public OpenApi3RouterFactoryImpl(Vertx vertx, ValidationContext<OAI3, Void> context, OpenApi3 openApi) {
    this.vertx = vertx;
    this.operationSpecs = new LinkedHashMap<>();
    securityHelper = new SecurityRequirementHelper();
    rqValidator = new RequestValidator<>(context, openApi);

    setupOperations(openApi);
  }

  @Override
  public OpenApi3RouterFactory addSecurityHandler(String securityRequirementName, Handler<RoutingContext> handler) {
    securityHelper.addSecurityHandler(securityRequirementName, handler);
    return this;
  }

  @Override
  public OpenApi3RouterFactory addSecurityScopedHandler(String securityRequirementName, String scopeName, Handler<RoutingContext> handler) {
    securityHelper.addSecurityScopedHandler(securityRequirementName, scopeName, handler);
    return this;
  }

  @Override
  public OpenApi3RouterFactory addOperationHandler(String operationId, Handler<RoutingContext> handler) throws ResolutionException {
    addOperationHandler(operationId, null, handler);
    return this;
  }

  @Override
  public OpenApi3RouterFactory addOperationHandler(String operationId, BodyHandler bodyHandler, Handler<RoutingContext> handler) throws ResolutionException {
    OperationSpec op = operationSpecs.get(operationId);
    if (op == null) {
      throw new ResolutionException(String.format(OP_ID_NOT_FOUND_ERR_MSG, operationId));
    }
    op.setBodyHandler(bodyHandler);
    op.addHandler(handler);
    return this;
  }

  @Override
  public Router getRouter() throws ResolutionException {
    Router router = Router.router(vertx);

    for (OperationSpec operationSpec : operationSpecs.values()) {
      // Pre-compile validators from the operation spec.
      // This flatten the operation and combine path & operation parameters
      OperationValidator<Void> opValidator = rqValidator.getValidator(operationSpec.pathModel, operationSpec.operation);
      // Create route with regex path if needed
      Route route = createRoute(router, operationSpec);
      // Set produces/consumes
      setConsumesProduces(route, operationSpec);
      // Set body handler
      if (operationSpec.bodyHandler != null) {
        route.handler(operationSpec.bodyHandler);
      }
      // Security handlers
      Collection<Handler<RoutingContext>> opSecurityHandlers = securityHelper.getHandlers(opValidator.getOperation().getSecurityRequirements());
      for (Handler<RoutingContext> handler : opSecurityHandlers) {
        route.handler(handler);
      }
      // Content validation handler
      route.handler(new OperationValidationHandler(rqValidator, operationSpec.pathModel, opValidator.getOperation()));
      // User handlers
      for (Handler<RoutingContext> handler : operationSpec.handlers) {
        route.handler(handler);
      }
    }

    return router;
  }

  private Route createRoute(Router router, OperationSpec operationSpec) {
    Pattern pattern = PathResolver
      .instance()
      .solve(operationSpec.path, EnumSet.of(END_STRING));

    // If this optional is empty, this route doesn't need regex
    return pattern != null
      ? router.routeWithRegex(operationSpec.method, pattern.pattern())
      : router.route(operationSpec.method, operationSpec.path);
  }

  private void setConsumesProduces(Route route, OperationSpec operationSpec) {
    // Consumes
    RequestBody requestBody = operationSpec.operation.getRequestBody();
    if (requestBody != null && requestBody.getContentMediaTypes() != null) {
      for (String ct : requestBody.getContentMediaTypes().keySet()) {
        route.consumes(ct);
      }
    }

    // Produces
    Map<String, Response> responses = operationSpec.operation.getResponses();
    for (Response response : responses.values()) {
      if (response.getContentMediaTypes() != null) {
        for (String ct : response.getContentMediaTypes().keySet()) {
          route.produces(ct);
        }
      }
    }
  }

  private void setupOperations(OpenApi3 openApi) {
    for (Map.Entry<String, Path> pathEntry : openApi.getPaths().entrySet()) {
      for (Map.Entry<String, Operation> opEntry : pathEntry.getValue().getOperations().entrySet()) {
        OperationSpec operationSpec = new OperationSpec(
          HttpMethod.valueOf(opEntry.getKey().toUpperCase()),
          opEntry.getValue(),
          pathEntry.getKey(),
          pathEntry.getValue());

        operationSpecs.put(opEntry.getValue().getOperationId(), operationSpec);
      }
    }
  }

  private static class OperationSpec {
    private final HttpMethod method;
    private final String path;
    private final Path pathModel;
    private final Operation operation;
    private final List<Handler<RoutingContext>> handlers;
    private BodyHandler bodyHandler;

    OperationSpec(HttpMethod method, Operation operation, String path, Path pathModel) {
      this.method = method;
      this.operation = operation;
      this.path = path;
      this.pathModel = pathModel;
      handlers = new ArrayList<>();
    }

    void setBodyHandler(BodyHandler bodyHandler) {
      this.bodyHandler = bodyHandler;
    }

    void addHandler(Handler<RoutingContext> handler) {
      handlers.add(handler);
    }
  }
}
