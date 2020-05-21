package org.openapi4j.operation.validator.validation.operation;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import org.junit.Test;
import org.openapi4j.operation.validator.model.impl.Body;
import org.openapi4j.operation.validator.model.impl.DefaultRequest;
import org.openapi4j.operation.validator.validation.OperationValidator;

import static org.openapi4j.operation.validator.model.Request.Method.GET;
import static org.openapi4j.operation.validator.model.Request.Method.POST;

public class RequestBodyTest extends OperationValidatorTestBase {
  @Test
  public void requestBodyCheck() throws Exception {
    OperationValidator val = loadOperationValidator("/operation/operationValidator.yaml", "rqBodyCheck");

    JsonNode bodyInteger = JsonNodeFactory.instance.objectNode().set(
      "paramInteger",
      JsonNodeFactory.instance.numberNode(1));
    JsonNode bodyIntegerWrong = JsonNodeFactory.instance.objectNode().set(
      "paramInteger",
      JsonNodeFactory.instance.textNode("1"));


    JsonNode bodyString = JsonNodeFactory.instance.objectNode().set(
      "paramString",
      JsonNodeFactory.instance.textNode("2"));
    JsonNode bodyStringWrong = JsonNodeFactory.instance.objectNode().set(
      "paramString",
      JsonNodeFactory.instance.numberNode(1));

    check(
      new DefaultRequest.Builder("/foo", GET).header("Content-Type", "application/json").body(Body.from(bodyInteger)).build(),
      val::validateBody,
      true);
    check(
      new DefaultRequest.Builder("/foo", GET).header("Content-Type", "application/json").body(Body.from(bodyIntegerWrong)).build(),
      val::validateBody,
      false);

    check(
      new DefaultRequest.Builder("/foo", GET).header("Content-Type", "application/json").body(Body.from(bodyString)).build(),
      val::validateBody,
      true);
    check(
      new DefaultRequest.Builder("/foo", GET).header("Content-Type", "application/json").body(Body.from(bodyStringWrong)).build(),
      val::validateBody,
      false);

    check(
      new DefaultRequest.Builder("/foo", GET).header("Content-Type", "application/json").build(),
      val::validateBody,
      false);

    check(
      new DefaultRequest.Builder("/foo", GET).body(Body.from(bodyInteger)).build(),
      val::validateBody,
      false);

    check(
      new DefaultRequest.Builder("/foo", GET).header("Content-Type", "text/plain").body(Body.from(bodyInteger)).build(),
      val::validateBody,
      false);

    check(
      new DefaultRequest.Builder("/foo", GET).header("Content-Type", "text/plain; charset=utf-8").body(Body.from("dummy")).build(),
      val::validateBody,
      true);

    check(
      new DefaultRequest.Builder("/foo", GET).header("Content-Type", "text/plain; charset=iso-8859-1").body(Body.from("dummy")).build(),
      val::validateBody,
      false);

    check(
      new DefaultRequest.Builder("/foo", GET).header("Content-Type", "image/png").body(Body.from("dummy")).build(),
      val::validateBody,
      true);

    check(
      new DefaultRequest.Builder("/foo", GET).build(),
      val::validateBody,
      false);
  }

  @Test
  public void fullyReferencedOperation() throws Exception {
    OperationValidator val = loadOperationValidator("/operation/operationValidator_build_flat.yaml", "post");

    JsonNode bodyString = JsonNodeFactory.instance.objectNode().set(
      "paramString",
      JsonNodeFactory.instance.textNode("2"));

    JsonNode bodyDecimal = JsonNodeFactory.instance.objectNode().set(
      "type",
      JsonNodeFactory.instance.numberNode(1.1));

    // Missing
    check(
      new DefaultRequest.Builder("/post", POST).build(),
      val::validateBody,
      false);
    check(
      new DefaultRequest.Builder("/post", POST).header("Content-Type", "application/json").build(),
      val::validateBody,
      false);
    // Wrong value
    check(
      new DefaultRequest.Builder("/post", POST).header("Content-Type", "application/json").body(Body.from(bodyString)).build(),
      val::validateBody,
      false);
    // ok
    check(
      new DefaultRequest.Builder("/post", POST).header("Content-Type", "application/json").body(Body.from(bodyDecimal)).build(),
      val::validateBody,
      true);
  }
}
