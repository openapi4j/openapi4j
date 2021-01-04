package org.openapi4j.operation.validator.validation;

import org.junit.Test;
import org.openapi4j.core.exception.ResolutionException;
import org.openapi4j.core.validation.ValidationException;
import org.openapi4j.operation.validator.model.Request;
import org.openapi4j.operation.validator.model.Response;
import org.openapi4j.operation.validator.model.impl.DefaultRequest;
import org.openapi4j.operation.validator.model.impl.DefaultResponse;
import org.openapi4j.parser.OpenApi3Parser;
import org.openapi4j.parser.model.v3.OpenApi3;
import org.openapi4j.parser.model.v3.Operation;
import org.openapi4j.parser.model.v3.Path;

import java.net.URL;

import static org.junit.Assert.fail;
import static org.junit.Assert.assertNotNull;
import static org.openapi4j.operation.validator.model.Request.Method.GET;
import static org.openapi4j.operation.validator.model.Request.Method.POST;

public class RequestValidatorTest {
  @Test(expected = ValidationException.class)
  public void operationMethodNotFound() throws ValidationException, ResolutionException {
    URL specPath = RequestValidatorTest.class.getResource("/request/requestValidator.yaml");
    OpenApi3 api = new OpenApi3Parser().parse(specPath, false);
    RequestValidator requestValidator = new RequestValidator(api);

    Request rq = new DefaultRequest.Builder("https://api.com/fixed/", GET).build();

    try {
      requestValidator.validate(rq);
    } catch (ValidationException ex) {
      if (!ex.getMessage().contains("Operation not found from URL")) {
        fail();
      } else {
        throw ex;
      }
    }
  }

  @Test
  public void withoutServerPathFindOperationCheck() throws Exception {
    URL specPath = RequestValidatorTest.class.getResource("/request/requestValidator.yaml");
    OpenApi3 api = new OpenApi3Parser().parse(specPath, false);
    RequestValidator requestValidator = new RequestValidator(api);

    checkRequest(
      api,
      "op2",
      requestValidator,
      new DefaultRequest.Builder("https://api.com/fixed/1/fixed/2/fixed/", GET).build(),
      true);

    checkRequest(
      api,
      "op2",
      requestValidator,
      new DefaultRequest.Builder("https://api.com/fixed/string/fixed/2/fixed/", GET).build(),
      false);

    // wrong path, parameters are not bound
    checkRequest(
      api,
      "op2",
      requestValidator,
      new DefaultRequest.Builder("https://api.com/fixed/fixed/2/fixed/", GET).build(),
      false);

    // Empty string is still valid
    checkRequest(
      api,
      "op2",
      requestValidator,
      new DefaultRequest.Builder("https://api.com/fixed/1/fixed//fixed/", GET).build(),
      true);
  }

  @Test
  public void withServerPathFindOperationCheck() throws Exception {
    URL specPath = RequestValidatorTest.class.getResource("/request/requestValidator-with-servers.yaml");
    OpenApi3 api = new OpenApi3Parser().parse(specPath, false);
    RequestValidator requestValidator = new RequestValidator(api);

    // absolute url
    checkRequest(
      api,
      "op2",
      requestValidator,
      new DefaultRequest.Builder("https://api.com/fixed/1/fixed/2/fixed/", GET).build(),
      true);
    checkRequest(
      api,
      "op2",
      requestValidator,
      new DefaultRequest.Builder("https://api.com/v1/fixed/1/fixed/2/fixed/", GET).build(),
      true);
    checkValidator(
      api,
      "op2",
      requestValidator,
      new DefaultRequest.Builder("https://api.com/fixed/1/fixed/2/fixed/", GET).build(),
      true);

    // relative (value = file:/2, so we serve from file:/.../openapi4j/openapi-operation-validator/build/resources/test/request/requestValidator-with-servers.yaml)
    checkRequest(
      api,
      "op2",
      requestValidator,
      new DefaultRequest.Builder("file:/v2/fixed/1/fixed/2/fixed/", GET).build(),
      true);
    checkRequest(
      api,
      "op2",
      requestValidator,
      new DefaultRequest.Builder("file:/v2/bar/fixed/1/fixed/2/fixed/", GET).build(),
      true);

    // with path parameter
    checkRequest(
      api,
      "op2",
      requestValidator,
      new DefaultRequest.Builder("https://foo.api.com/bar/fixed/1/fixed/2/fixed/", GET).build(),
      true);

    checkRequest(
      api,
      "op1",
      requestValidator,
      new DefaultRequest.Builder("https://foo.api.com/bar/fixed/", POST).header("Content-Type", "application/json").build(),
      true);
  }

  @Test
  public void responseTest() throws Exception {
    URL specPath = RequestValidatorTest.class.getResource("/request/requestValidator-with-servers.yaml");
    OpenApi3 api = new OpenApi3Parser().parse(specPath, false);
    RequestValidator requestValidator = new RequestValidator(api);

    // Wrong value type
    checkResponse(
      api,
      "op2",
      requestValidator,
      new DefaultResponse.Builder(200).header("Content-Type", "application/json").header("X-Rate-Limit", "1 abc").build(),
      false);

    checkResponse(
      new DefaultRequest.Builder("https://foo.api.com/bar/fixed/1/fixed/2/fixed/", GET).build(),
      requestValidator,
      new DefaultResponse.Builder(200).header("Content-Type", "application/json").header("X-Rate-Limit", "1").build(),
      true
    );

    checkResponse(
      api,
      "op2",
      requestValidator,
      new DefaultResponse.Builder(200).header("Content-Type", "application/json").header("X-Rate-Limit", "1").build(),
      true);
  }

  private void checkValidator(OpenApi3 api, String opId, RequestValidator requestValidator, Request rq, boolean shouldBeValid) {
    // Check with request definition detection
    try {
      requestValidator.getValidator(rq);
    } catch (ValidationException e) {
      if (shouldBeValid) {
        fail();
      }
    }

    // Check with request definition given
    Path path = api.getPathItemByOperationId(opId);
    Operation operation = api.getOperationById(opId);

    assertNotNull(requestValidator.getValidator(path, operation));
  }

  private void checkRequest(OpenApi3 api, String opId, RequestValidator requestValidator, Request rq, boolean shouldBeValid) {
    // Check with request definition detection
    try {
      requestValidator.validate(rq);
    } catch (ValidationException e) {
      if (shouldBeValid) {
        fail();
      }
    }

    // Check with request definition given
    Path path = api.getPathItemByOperationId(opId);
    Operation operation = api.getOperationById(opId);

    try {
      requestValidator.validate(rq, path, operation);
    } catch (ValidationException e) {
      if (shouldBeValid) {
        System.out.println(e.toString());
        fail();
      }
    }
  }

  private void checkResponse(Request rq, RequestValidator requestValidator, Response resp, boolean shouldBeValid) {
    try {
      requestValidator.validate(resp, rq);
    } catch (ValidationException e) {
      if (shouldBeValid) {
        System.out.println(e.toString());
        fail();
      }
    }
  }

  private void checkResponse(OpenApi3 api, String opId, RequestValidator requestValidator, Response resp, boolean shouldBeValid) {
    Path path = api.getPathItemByOperationId(opId);
    Operation operation = api.getOperationById(opId);

    try {
      requestValidator.validate(resp, path, operation);
    } catch (ValidationException e) {
      if (shouldBeValid) {
        System.out.println(e.toString());
        fail();
      }
    }
  }
}
