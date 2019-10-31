package org.perf.check.operation;

import org.openapi4j.core.validation.ValidationException;
import org.openapi4j.operation.validator.model.Headers;
import org.openapi4j.operation.validator.model.Request;
import org.openapi4j.operation.validator.model.impl.Body;
import org.openapi4j.operation.validator.model.impl.DefaultRequest;
import org.openapi4j.operation.validator.validation.OperationValidator;
import org.openapi4j.operation.validator.validation.RequestValidator;
import org.openapi4j.parser.OpenApi3Parser;
import org.openapi4j.parser.model.v3.OpenApi3;
import org.openapi4j.parser.model.v3.Operation;
import org.openapi4j.parser.model.v3.Path;

import java.net.URL;

class OpenApi4j {
  private final Path path;
  private final Operation operation;
  private final RequestValidator requestValidator;

  OpenApi4j(String schemaFile) throws ValidationException {
    URL specPath = OperationPerfRunner.class.getResource(schemaFile);
    OpenApi3 api = new OpenApi3Parser().parse(specPath, true);
    operation = api.getOperationById("test");
    path = api.getPathItemByOperationId("test");
    requestValidator = new RequestValidator(api);
  }

  void validate() throws ValidationException {
    DefaultRequest.Builder rqBuilder = new DefaultRequest.Builder(Request.Method.POST, "/");
    Request rq = rqBuilder
      .header(Headers.CONTENT_TYPE, "application/x-www-form-urlencoded; charset=utf-8")
      .body(Body.from("fieldInt=1&field%20space=value%202&fieldBool= true &fieldFloat=1.2"))
      .build();

    requestValidator.validate(rq, path, operation);
  }

  public String getVersion() {
    return OperationValidator.class.getPackage().getImplementationVersion();
  }
}
