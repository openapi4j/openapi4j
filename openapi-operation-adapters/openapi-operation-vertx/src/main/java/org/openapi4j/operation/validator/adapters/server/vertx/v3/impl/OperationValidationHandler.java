package org.openapi4j.operation.validator.adapters.server.vertx.v3.impl;

import com.fasterxml.jackson.databind.JsonNode;

import org.openapi4j.core.exception.ResolutionException;
import org.openapi4j.core.validation.ValidationException;
import org.openapi4j.core.validation.ValidationResults;
import org.openapi4j.operation.validator.model.Request;
import org.openapi4j.operation.validator.validation.OperationValidator;
import org.openapi4j.operation.validator.validation.RequestValidator;
import org.openapi4j.parser.model.v3.Operation;
import org.openapi4j.parser.model.v3.Path;

import java.util.Map;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

import static java.util.Objects.requireNonNull;

class OperationValidationHandler implements Handler<RoutingContext> {
  private static final String INVALID_REQUEST_ERR_MSG = "Invalid request";
  private static final String REQUEST_REQUIRED_ERR_MSG = "Request is required.";
  private static final String SETUP_ERR_MSG = "Unable to setup operation validators.";

  private final RequestValidator requestValidator;
  private final Path path;
  private final Operation operation;

  OperationValidationHandler(RequestValidator requestValidator, Path path, Operation operation) {
    this.requestValidator = requestValidator;
    this.path = path;
    this.operation = operation;
  }

  @Override
  public void handle(RoutingContext rc) {
    try {
      validate(rc, VertxRequest.of(rc), path, operation);
      rc.next();
    } catch (ValidationException e) {
      rc.fail(e);
    }
  }

  /**
   * Validate the request against the given API operation
   *
   * @param rc        The current routing context.
   * @param request   The request to validate. Must be {@code nonnull}.
   * @param operation OpenAPI operation. Must be {@code nonnull}.
   * @throws ValidationException A validation report containing validation errors
   */
  private void validate(RoutingContext rc,
                        Request request,
                        Path path,
                        Operation operation) throws ValidationException {

    requireNonNull(request, REQUEST_REQUIRED_ERR_MSG);

    OperationValidator opValidator;
    try {
      opValidator = requestValidator.compile(path, operation);
    } catch (ResolutionException e) {
      // Should never happen
      ValidationException ex = new ValidationException(SETUP_ERR_MSG);
      ex.initCause(e);
      throw ex;
    }

    ValidationResults results = new ValidationResults();

    Map<String, JsonNode> pathParameters = opValidator.validatePath(request, results);
    Map<String, JsonNode> queryParameters = opValidator.validateQuery(request, results);
    Map<String, JsonNode> headerParameters = opValidator.validateHeaders(request, results);
    Map<String, JsonNode> cookieParameters = opValidator.validateCookies(request, results);
    opValidator.validateBody(request, results);

    if (!results.isValid()) {
      throw new ValidationException(INVALID_REQUEST_ERR_MSG, results);
    }

    RequestParameters rqParameters = new RequestParameters(
      pathParameters,
      queryParameters,
      headerParameters,
      cookieParameters
    );

    rc.data().put("rqParameters", rqParameters);
  }
}
