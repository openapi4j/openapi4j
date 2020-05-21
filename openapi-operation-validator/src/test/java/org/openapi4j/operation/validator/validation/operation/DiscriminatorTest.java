package org.openapi4j.operation.validator.validation.operation;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.Test;
import org.openapi4j.core.util.TreeUtil;
import org.openapi4j.operation.validator.model.impl.Body;
import org.openapi4j.operation.validator.model.impl.DefaultRequest;
import org.openapi4j.operation.validator.validation.OperationValidator;

import static org.openapi4j.operation.validator.model.Request.Method.POST;

public class DiscriminatorTest extends OperationValidatorTestBase {
  @Test
  public void discriminatorCheck() throws Exception {
    OperationValidator val = loadOperationValidator("/operation/operationValidator.yaml", "discriminator");

    JsonNode body = TreeUtil.json.readTree("{\"pet_type\": \"Cat\", \"age\": 3}");
    check(
      new DefaultRequest.Builder("/discriminator", POST).header("Content-Type", "application/json").body(Body.from(body)).build(),
      val::validateBody,
      true);

    body = TreeUtil.json.readTree("{\"pet_type\": \"Dog\", \"bark\": true}");
    check(
      new DefaultRequest.Builder("/discriminator", POST).header("Content-Type", "application/json").body(Body.from(body)).build(),
      val::validateBody,
      true);

    body = TreeUtil.json.readTree("{\"pet_type\": \"Dog\", \"bark\": false, \"breed\": \"Dingo\"}");
    check(
      new DefaultRequest.Builder("/discriminator", POST).header("Content-Type", "application/json").body(Body.from(body)).build(),
      val::validateBody,
      true);

    body = TreeUtil.json.readTree("{\"age\": 3}");
    check(
      new DefaultRequest.Builder("/discriminator", POST).header("Content-Type", "application/json").body(Body.from(body)).build(),
      val::validateBody,
      false);

    body = TreeUtil.json.readTree("{\"pet_type\": \"Dog\", \"bark\": false, \"breed\": \"foo\"}");
    check(
      new DefaultRequest.Builder("/discriminator", POST).header("Content-Type", "application/json").body(Body.from(body)).build(),
      val::validateBody,
      false);
  }
}
