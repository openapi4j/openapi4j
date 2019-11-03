package org.openapi4j.operation.validator.adapters.server.vertx.v3.impl;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import org.openapi4j.core.validation.ValidationException;
import org.openapi4j.operation.validator.validation.RequestValidator;
import org.openapi4j.parser.model.v3.Operation;
import org.openapi4j.parser.model.v3.Path;

class OperationValidationHandler implements Handler<RoutingContext> {
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
      requestValidator.validate(VertxRequest.of(rc), path, operation);
      rc.next();
    } catch (ValidationException e) {
      rc.fail(e);
    }
  }
}
