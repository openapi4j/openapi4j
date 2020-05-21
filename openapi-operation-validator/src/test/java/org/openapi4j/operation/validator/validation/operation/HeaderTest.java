package org.openapi4j.operation.validator.validation.operation;

import org.junit.Test;
import org.openapi4j.operation.validator.model.impl.DefaultRequest;
import org.openapi4j.operation.validator.validation.OperationValidator;

import static org.openapi4j.operation.validator.model.Request.Method.GET;
import static org.openapi4j.operation.validator.model.Request.Method.POST;

public class HeaderTest extends OperationValidatorTestBase {
  @Test
  public void mergePathToOperationParametersTest() throws Exception {
    OperationValidator val = loadOperationValidator("/operation/operationValidator.yaml", "merge_parameters");

    check(
      new DefaultRequest.Builder("/merge_parameters", GET).build(),
      val::validateHeaders,
      false);

    check(
      new DefaultRequest.Builder("/merge_parameters", GET).header("pathStringHeaderParam", "foo").header("refIntHeaderParameter", "-1").build(),
      val::validateHeaders,
      true);
  }

  @Test
  public void headerCheck() throws Exception {
    OperationValidator val = loadOperationValidator("/operation/operationValidator.yaml", "paramCheck");

    check(
      new DefaultRequest.Builder("/foo", GET).header("pathStringHeaderParam", "foo").header("floatHeaderParam", "0.1").build(),
      val::validateHeaders,
      true);

    check(
      new DefaultRequest.Builder("/foo", GET).header("pathStringHeaderParam", "foo").header("floatHeaderParam", ".1").build(),
      val::validateHeaders,
      true);

    check(
      new DefaultRequest.Builder("/foo", GET).header("pathStringHeaderParam", "foo").header("floatHeaderParam", "0,1").build(),
      val::validateHeaders,
      false);

    // operation param required
    check(
      new DefaultRequest.Builder("/foo", GET).header("pathStringHeaderParam", "foo").build(),
      val::validateHeaders,
      false);

    // path param required
    check(
      new DefaultRequest.Builder("/foo", GET).header("floatHeaderParam", "0.1").build(),
      val::validateHeaders,
      false);
  }

  @Test
  public void fullyReferencedOperation() throws Exception {
    OperationValidator val = loadOperationValidator("/operation/operationValidator_build_flat.yaml", "post");

    // Missing headers
    check(
      new DefaultRequest.Builder("/post", POST).build(),
      val::validateHeaders,
      false);
    check(
      new DefaultRequest.Builder("/post", POST).header("paramWithSchema", "1").build(),
      val::validateHeaders,
      false);
    check(
      new DefaultRequest.Builder("/post", POST).header("paramWithContent", "1").build(),
      val::validateHeaders,
      false);
    // Wrong values
    check(
      new DefaultRequest.Builder("/post", POST).header("paramWithSchema", "a").header("paramWithContent", "1").build(),
      val::validateHeaders,
      false);
    check(
      new DefaultRequest.Builder("/post", POST).header("paramWithSchema", "1").header("paramWithContent", "a").build(),
      val::validateHeaders,
      false);
    // ok
    check(
      new DefaultRequest.Builder("/post", POST).header("paramWithSchema", "0.9").header("paramWithContent", "1.2").build(),
      val::validateHeaders,
      true);
  }
}
