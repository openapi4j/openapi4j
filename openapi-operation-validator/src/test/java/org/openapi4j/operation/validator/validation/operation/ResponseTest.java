package org.openapi4j.operation.validator.validation.operation;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import org.junit.Test;
import org.openapi4j.operation.validator.model.impl.Body;
import org.openapi4j.operation.validator.model.impl.DefaultRequest;
import org.openapi4j.operation.validator.model.impl.DefaultResponse;
import org.openapi4j.operation.validator.validation.OperationValidator;

import static org.openapi4j.operation.validator.model.Request.Method.POST;

public class ResponseTest extends OperationValidatorTestBase {
  @Test
  public void wrongBodyDefinition() throws Exception {
    OperationValidator val = loadOperationValidator("/operation/operationValidator.yaml", "wrong_definition_for_body_response");

    check(
      new DefaultRequest.Builder("/wrong_definition_for_body_response", POST).build(),
      val::validateBody,
      true);

    check(
      new DefaultResponse.Builder(200).build(),
      val::validateBody,
      true);

    val = loadOperationValidator("/operation/operationValidator.yaml", "wrong_definition_for_body_response2");

    check(
      new DefaultResponse.Builder(200).build(),
      val::validateBody,
      true);
  }

  @Test
  public void responseCheck() throws Exception {
    OperationValidator val = loadOperationValidator("/operation/operationValidator.yaml", "rqBodyCheck");

    check(
      new DefaultResponse.Builder(500).header("Content-Type", "application/json").build(),
      val::validateBody,
      true);

    check(
      new DefaultResponse.Builder(200).header("Content-Type", "text/plain").body(Body.from("dummy")).build(),
      val::validateBody,
      true);

    // Wrong content type
    check(
      new DefaultResponse.Builder(500).header("Content-Type", "foo").build(),
      val::validateBody,
      false);

    // No header validators
    check(
      new DefaultResponse.Builder(500).header("X-Rate-Limit", "1").build(),
      val::validateHeaders,
      true);

    val = loadOperationValidator("/operation/operationValidator.yaml", "paramCheck");

    // No default response
    check(
      new DefaultResponse.Builder(500).build(),
      val::validateBody,
      true);

    check(
      new DefaultResponse.Builder(500).header("X-Rate-Limit", "1").build(),
      val::validateHeaders,
      true);

    val = loadOperationValidator("/operation/response_without_content.yaml", "post");
    check(
      new DefaultResponse.Builder(201).build(),
      val::validateBody,
      true);
  }

  @Test
  public void fullyReferencedOperation() throws Exception {
    OperationValidator val = loadOperationValidator("/operation/operationValidator_build_flat.yaml", "post");

    JsonNode bodyDecimal = JsonNodeFactory.instance.objectNode().set(
      "type",
      JsonNodeFactory.instance.numberNode(1.1));

    // Missing
    check(
      new DefaultResponse.Builder(200).build(),
      val::validateBody,
      false);
    check(
      new DefaultResponse.Builder(200).header("Content-Type", "application/json").build(),
      val::validateBody,
      false);
    // Wrong header
    check(
      new DefaultResponse.Builder(200).header("Content-Type", "text/html").body(Body.from(bodyDecimal)).build(),
      val::validateBody,
      false);
    // Wrong value
    check(
      new DefaultResponse.Builder(200).header("Content-Type", "application/json").body(Body.from("{\"foo\": 1}")).build(),
      val::validateBody,
      false);
    // ok
    check(
      new DefaultResponse.Builder(200).header("Content-Type", "application/json").body(Body.from(bodyDecimal)).build(),
      val::validateBody,
      true);

    // headers
    check(
      new DefaultResponse.Builder(200).header("headerWithSchema", "1").build(),
      val::validateHeaders,
      false);
    check(
      new DefaultResponse.Builder(200).header("headerWithContent", "1").build(),
      val::validateHeaders,
      false);
    check(
      new DefaultResponse.Builder(200).header("headerWithSchema", "1").header("headerWithContent", "1").build(),
      val::validateHeaders,
      true);
  }
}
