package org.openapi4j.operation.validator.adapters.server.vertx.v3.impl;

import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import org.openapi4j.core.exception.ResolutionException;
import org.openapi4j.operation.validator.adapters.server.vertx.v3.OpenApi3RouterFactory;
import org.openapi4j.operation.validator.validation.OperationValidator;
import org.openapi4j.operation.validator.validation.RequestValidator;
import org.openapi4j.parser.model.v3.*;

import java.util.*;
import java.util.regex.Pattern;

public class OpenApi3RouterFactoryImpl implements OpenApi3RouterFactory {
  private static final String OP_ID_NOT_FOUND_ERR_MSG = "Operation with id '%s' not found.";

  private final Vertx vertx;
  private final RequestValidator rqValidator;
  private final Map<String, OperationSpec> operationSpecs;
  private final SecurityRequirementHelper securityHelper;

  public OpenApi3RouterFactoryImpl(Vertx vertx, OpenApi3 openApi) {
    this.vertx = vertx;
    this.operationSpecs = new LinkedHashMap<>();
    securityHelper = new SecurityRequirementHelper();
    rqValidator = new RequestValidator(openApi);

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
      OperationValidator opValidator = rqValidator.compile(operationSpec.pathModel, operationSpec.operation);
      // Create route with regex path if needed
      Route route = createRoute(router, operationSpec);
      // Set produces/consumes
      setConsumesProduces(route, operationSpec);
      // Set body handler
      if (!HttpMethod.GET.equals(operationSpec.method) && operationSpec.bodyHandler != null) {
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

  private Route createRoute(Router router, OperationSpec operationSpec) throws ResolutionException {
    Optional<Pattern> optRegEx = OAI3PathConverter
      .instance()
      .solve(operationSpec.path, operationSpec.operation.getParametersIn("path"));

    // If this optional is empty, this route doesn't need regex
    return optRegEx.isPresent()
      ? router.routeWithRegex(operationSpec.method, optRegEx.get().toString())
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
    if (responses != null) {
      for (Response response : operationSpec.operation.getResponses().values()) {
        if (response.getContentMediaTypes() != null) {
          for (String ct : response.getContentMediaTypes().keySet()) {
            route.produces(ct);
          }
        }
      }
    }
  }

  private void setupOperations(OpenApi3 openApi) {
    for (Map.Entry<String, Path> pathEntry : openApi.getPaths().entrySet()) {
      for (Map.Entry<String, Operation> opEntry : pathEntry.getValue().getOperations().entrySet()) {
        OperationSpec operationSpec = new OperationSpec(
          HttpMethod.valueOf(opEntry.getKey()),
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
