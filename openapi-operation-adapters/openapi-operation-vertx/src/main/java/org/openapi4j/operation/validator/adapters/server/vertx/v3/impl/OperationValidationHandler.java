package org.openapi4j.operation.validator.adapters.server.vertx.v3.impl;

import org.openapi4j.core.validation.ValidationException;
import org.openapi4j.operation.validator.model.impl.RequestParameters;
import org.openapi4j.operation.validator.validation.RequestValidator;
import org.openapi4j.parser.model.v3.Operation;
import org.openapi4j.parser.model.v3.Path;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

class OperationValidationHandler implements Handler<RoutingContext> {
  private static final String RQ_PARAMETERS = "rqParameters";

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
      RequestParameters rqParameters = requestValidator.validate(VertxRequest.of(rc), path, operation);
      rc.data().put(RQ_PARAMETERS, rqParameters);
      rc.next();
    } catch (ValidationException e) {
      rc.fail(400, e);
    }
  }
}
