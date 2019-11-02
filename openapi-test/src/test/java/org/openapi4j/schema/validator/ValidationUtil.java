package org.openapi4j.schema.validator;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

import org.junit.Assert;
import org.openapi4j.core.model.v3.OAI3;
import org.openapi4j.core.model.v3.OAI3Context;
import org.openapi4j.core.util.TreeUtil;
import org.openapi4j.core.validation.ValidationException;
import org.openapi4j.core.validation.ValidationResults;
import org.openapi4j.schema.validator.util.ExtValidatorInstance;
import org.openapi4j.schema.validator.v3.SchemaValidator;

import java.net.URI;
import java.util.Map;

class ValidationUtil {
  static void validate(String testPath,
                       Map<Byte, Boolean> options,
                       Map<String, ExtValidatorInstance> validators) throws Exception {
    ArrayNode testCases = (ArrayNode) TreeUtil.json.readTree(ValidationUtil.class.getResource(testPath));

    for (int index = 0; index < testCases.size(); index++) {
      JsonNode testCase = testCases.get(index);
      JsonNode schemaNode = testCase.get("schema");

      OAI3Context apiContext = new OAI3Context(new URI("/"), schemaNode);
      ValidationContext<OAI3> validationContext = new ValidationContext<>(apiContext);

      if (options != null) {
        options.forEach(validationContext::setOption);
      }
      if (validators != null) {
        validators.forEach(validationContext::addValidator);
      }

      SchemaValidator schemaValidator = new SchemaValidator(validationContext, "schema", schemaNode);
      doTests(schemaValidator, testCase);
    }
  }

  static void validate(String testPath) throws Exception {
    ArrayNode testCases = (ArrayNode) TreeUtil.json.readTree(ValidationUtil.class.getResource(testPath));

    for (int index = 0; index < testCases.size(); index++) {
      JsonNode testCase = testCases.get(index);
      JsonNode schemaNode = testCase.get("schema");

      SchemaValidator schemaValidator = new SchemaValidator("schema", schemaNode);
      doTests(schemaValidator, testCase);
    }
  }

  private static void doTests(SchemaValidator schemaValidator, JsonNode testCase) {
    ArrayNode testNodes = (ArrayNode) testCase.get("tests");
    for (int i = 0; i < testNodes.size(); i++) {
      JsonNode test = testNodes.get(i);
      JsonNode contentNode = test.get("data");
      ValidationResults results = null;

      boolean isValidExpected = test.get("valid").asBoolean();

      try {
        schemaValidator.validate(contentNode);
      } catch (ValidationException ex) {
        results = ex.getResults();
      } finally {
        if (results == null) {
          results = new ValidationResults();
        }
      }

      if (isValidExpected != results.isValid()) {
        String message = String.format(
          "TEST FAILURE : %s\nData : %s\n%s",
          test.get("description"),
          contentNode,
          results.toString());

        System.out.println(message);
        Assert.fail();
      }
    }
  }
}
