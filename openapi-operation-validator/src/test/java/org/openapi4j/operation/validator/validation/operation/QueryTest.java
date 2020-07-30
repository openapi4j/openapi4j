package org.openapi4j.operation.validator.validation.operation;

import org.junit.Test;
import org.openapi4j.operation.validator.model.impl.DefaultRequest;
import org.openapi4j.operation.validator.validation.OperationValidator;

import static org.openapi4j.operation.validator.model.Request.Method.GET;

public class QueryTest extends OperationValidatorTestBase {
  @Test
  public void queryCheck() throws Exception {
    OperationValidator val = loadOperationValidator("/operation/operationValidator.yaml", "paramCheck");

    check(
      new DefaultRequest.Builder("/foo", GET).query("boolQueryParam=true").build(),
      val::validateQuery,
      true);

    check(
      new DefaultRequest.Builder("/foo", GET).query("boolQueryParam=true&stringQueryParam").build(),
      val::validateQuery,
      true);

    check(
      new DefaultRequest.Builder("/foo", GET).query("boolQueryParam=false&intQueryParam=12").build(),
      val::validateQuery,
      true);

    // nullable
    check(
      new DefaultRequest.Builder("/foo", GET).query("boolQueryParam=false&intQueryParam=").build(),
      val::validateQuery,
      false);

    check(
      new DefaultRequest.Builder("/foo", GET).query("boolQueryParam=yes").build(),
      val::validateQuery,
      false);

    check(
      new DefaultRequest.Builder("/foo", GET).query("boolQueryParam").build(),
      val::validateQuery,
      false);

    check(
      new DefaultRequest.Builder("/foo", GET).query("boolQueryParam=true&intQueryParam").build(),
      val::validateQuery,
      false);

    // required
    check(
      new DefaultRequest.Builder("/foo", GET).build(),
      val::validateQuery,
      false);
  }
}
