package org.openapi4j.operation.validator.validation;

import org.junit.Test;
import org.openapi4j.core.exception.ResolutionException;
import org.openapi4j.core.validation.ValidationException;
import org.openapi4j.operation.validator.model.Request;
import org.openapi4j.operation.validator.model.impl.DefaultRequest;
import org.openapi4j.parser.OpenApi3Parser;
import org.openapi4j.parser.model.v3.OpenApi3;

import java.net.URL;

import static org.junit.Assert.fail;
import static org.openapi4j.operation.validator.model.Request.Method.GET;
import static org.openapi4j.operation.validator.model.Request.Method.POST;

public class RequestValidatorTest {
  @Test
  public void withoutServerPathFindOperationCheck() throws ResolutionException, ValidationException {
    URL specPath = RequestValidatorTest.class.getResource("/request/requestValidator.yaml");
    OpenApi3 api = new OpenApi3Parser().parse(specPath, false);
    RequestValidator requestValidator = new RequestValidator(api);

    check(
      requestValidator,
      new DefaultRequest.Builder("https://api.com/fixed/1/fixed/2/fixed/", GET).build(),
      true);

    check(
      requestValidator,
      new DefaultRequest.Builder("https://api.com/fixed/string/fixed/2/fixed/", GET).build(),
      false);

    // wrong path, parameters are not bound
    check(
      requestValidator,
      new DefaultRequest.Builder("https://api.com/fixed/fixed/2/fixed/", GET).build(),
      false);

    // Empty string is still valid
    check(
      requestValidator,
      new DefaultRequest.Builder("https://api.com/fixed/1/fixed//fixed/", GET).build(),
      true);
  }

  @Test
  public void withServerPathFindOperationCheck() throws ResolutionException, ValidationException {
    URL specPath = RequestValidatorTest.class.getResource("/request/requestValidator-with-servers.yaml");
    OpenApi3 api = new OpenApi3Parser().parse(specPath, false);
    RequestValidator requestValidator = new RequestValidator(api);

    // absolute url
    check(
      requestValidator,
      new DefaultRequest.Builder("https://api.com/fixed/1/fixed/2/fixed/", GET).build(),
      true);
    check(
      requestValidator,
      new DefaultRequest.Builder("https://api.com/v1/fixed/1/fixed/2/fixed/", GET).build(),
      true);

    // relative (value = file:/2, so we serve from file:/.../openapi4j/openapi-operation-validator/build/resources/test/request/requestValidator-with-servers.yaml)
    check(
      requestValidator,
      new DefaultRequest.Builder("file:/v2/fixed/1/fixed/2/fixed/", GET).build(),
      true);
    check(
      requestValidator,
      new DefaultRequest.Builder("file:/v2/bar/fixed/1/fixed/2/fixed/", GET).build(),
      true);

    // with path parameter
    check(
      requestValidator,
      new DefaultRequest.Builder("https://foo.api.com/bar/fixed/1/fixed/2/fixed/", GET).build(),
      true);

    check(
      requestValidator,
      new DefaultRequest.Builder("https://foo.api.com/bar/fixed/", POST).header("Content-Type", "application/json").build(),
      true);
  }

  private void check(RequestValidator requestValidator, Request rq, boolean shouldBeValid) {
    try {
      requestValidator.validate(rq);
    } catch (ValidationException e) {
      System.out.println(e.toString());
      if (shouldBeValid) {
        fail();
      }
    }
  }
}
