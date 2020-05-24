package org.openapi4j.operation.validator.validation.operation;

import org.junit.Test;
import org.openapi4j.operation.validator.model.impl.DefaultRequest;
import org.openapi4j.operation.validator.validation.OperationValidator;

import static org.openapi4j.operation.validator.model.Request.Method.GET;

public class CookieTest extends OperationValidatorTestBase {
  @Test
  public void cookieCheck() throws Exception {
    OperationValidator val = loadOperationValidator("/operation/operationValidator.yaml", "paramCheck");

    check(
      new DefaultRequest.Builder("/foo", GET).cookie("dtCookieParam", "1996-12-19T16:39:57-08:00").build(),
      val::validateCookies,
      true);

    // Not a date-time
    check(
      new DefaultRequest.Builder("/foo", GET).cookie("dtCookieParam", "1996-12-19").build(),
      val::validateCookies,
      false);

    // required
    check(
      new DefaultRequest.Builder("/foo", GET).build(),
      val::validateCookies,
      false);
  }
}
