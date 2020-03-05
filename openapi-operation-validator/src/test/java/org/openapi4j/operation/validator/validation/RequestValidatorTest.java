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
    URL specPath = RequestValidatorTest.class.getResource("/request/requestValidator.yaml");
    OpenApi3 api = new OpenApi3Parser().parse(specPath, false);
    RequestValidator requestValidator = new RequestValidator(api);

    // absolute url
    check(
      requestValidator,
      new DefaultRequest.Builder("https://api.com/v1/foo/fixed/1/fixed/2/fixed/", GET).build(),
      true);

    // relative
    check(
      requestValidator,
      new DefaultRequest.Builder("/v1/foo/fixed/1/fixed/2/fixed/", GET).build(),
      true);
  }

  private void check(RequestValidator requestValidator, Request rq, boolean shouldBeValid) {
    try {
      requestValidator.validate(rq);
    } catch (ValidationException e) {
      System.out.println(e);
      if (shouldBeValid) {
        fail();
      }
    }
  }
}
