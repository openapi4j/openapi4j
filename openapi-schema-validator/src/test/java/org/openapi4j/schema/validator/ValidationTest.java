package org.openapi4j.schema.validator;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import org.junit.Test;
import org.openapi4j.core.model.v3.OAI3;
import org.openapi4j.core.model.v3.OAI3Context;
import org.openapi4j.core.model.v3.OAI3SchemaKeywords;
import org.openapi4j.core.util.TreeUtil;
import org.openapi4j.core.validation.ValidationSeverity;
import org.openapi4j.schema.validator.v3.*;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.openapi4j.schema.validator.v3.ValidationOptions.ADDITIONAL_PROPS_RESTRICT;

public class ValidationTest {
  @Test
  public void additionalPropertiesValidator() throws Exception {
    ValidationUtil.validate("/schema/additionalProperties.json");
  }

  @Test
  public void allOfValidator() throws Exception {
    ValidationUtil.validate("/schema/allOf.json");
  }

  @Test
  public void anyOfValidator() throws Exception {
    ValidationUtil.validate("/schema/anyOf.json");
  }

  @Test
  public void dependenciesValidator() throws Exception {
    ValidationUtil.validate("/schema/dependencies.json");
  }

  @Test
  public void discriminatorValidator() throws Exception {
    ValidationUtil.validate("/schema/discriminator.json");
  }

  @Test
  public void enumValidator() throws Exception {
    ValidationUtil.validate("/schema/enum.json");
  }

  @Test
  public void formatValidator() throws Exception {
    ValidationUtil.validate("/schema/format.json");
  }

  @Test
  public void itemsValidator() throws Exception {
    ValidationUtil.validate("/schema/items.json");
  }

  @Test
  public void maximumValidator() throws Exception {
    ValidationUtil.validate("/schema/maximum.json");
  }

  @Test
  public void maxItemsValidator() throws Exception {
    ValidationUtil.validate("/schema/maxItems.json");
  }

  @Test
  public void maxLengthValidator() throws Exception {
    ValidationUtil.validate("/schema/maxLength.json");
  }

  @Test
  public void maxPropertiesValidator() throws Exception {
    ValidationUtil.validate("/schema/maxProperties.json");
  }

  @Test
  public void minimumValidator() throws Exception {
    ValidationUtil.validate("/schema/minimum.json");
  }

  @Test
  public void minItemsValidator() throws Exception {
    ValidationUtil.validate("/schema/minItems.json");
  }

  @Test
  public void minLengthValidator() throws Exception {
    ValidationUtil.validate("/schema/minLength.json");
  }

  @Test
  public void minPropertiesValidator() throws Exception {
    ValidationUtil.validate("/schema/minProperties.json");
  }

  @Test
  public void multipleOfValidator() throws Exception {
    ValidationUtil.validate("/schema/multipleOf.json");
  }

  @Test
  public void notValidator() throws Exception {
    ValidationUtil.validate("/schema/not.json");
  }

  @Test
  public void nullableValidator() throws Exception {
    ValidationUtil.validate("/schema/nullable.json");
  }

  @Test
  public void oneOfValidator() throws Exception {
    ValidationUtil.validate("/schema/oneOf.json");
  }

  @Test
  public void patternValidator() throws Exception {
    ValidationUtil.validate("/schema/pattern.json");
  }

  @Test
  public void patternPropertiesValidator() throws Exception {
    ValidationUtil.validate("/schema/patternProperties.json");
  }

  @Test
  public void propertiesValidator() throws Exception {
    ValidationUtil.validate("/schema/properties.json");
  }

  @Test
  public void requiredValidator() throws Exception {
    ValidationUtil.validate("/schema/required.json");
  }

  @Test
  public void refValidator() throws Exception {
    ValidationUtil.validate("/schema/reference.json");
  }

  @Test
  public void typeValidator() throws Exception {
    ValidationUtil.validate("/schema/type.json");
  }

  @Test
  public void uniqueItemsValidator() throws Exception {
    ValidationUtil.validate("/schema/uniqueItems.json");
  }

  @Test
  public void optionalBignum() throws Exception {
    ValidationUtil.validate("/schema/optional/bignum.json");
  }

  @Test
  public void optionalFormat() throws Exception {
    ValidationUtil.validate("/schema/optional/format.json");
  }

  @Test
  public void optionalZeroTerminatedFloats() throws Exception {
    ValidationUtil.validate("/schema/optional/zeroTerminatedFloats.json");
  }

  @Test
  public void additionalPropsRestrictValidation() throws Exception {
    Map<Byte, Boolean> options = new HashMap<>();
    options.put(ADDITIONAL_PROPS_RESTRICT, true);
    ValidationUtil.validate("/schema/override/additionalPropsRestrictOption.json", options, null, true);
  }

  @Test
  public void overriddenValidation() throws Exception {
    Map<String, ValidatorInstance> validators = new HashMap<>();
    validators.put(OAI3SchemaKeywords.MAXIMUM, MaximumToleranceValidator::new);
    validators.put("x-myentity-val", MyEntityValidator::new);

    ValidationUtil.validate("/schema/override/maximumTolerance.json", null, validators, true);
  }

  @Test
  public void additionalValidation() throws Exception {
    Map<String, ValidatorInstance> validators = new HashMap<>();
    validators.put("x-myentity-val", MyEntityValidator::new);

    ValidationUtil.validate("/schema/override/myEntityValidation.json", null, validators, true);
    ValidationUtil.validate("/schema/override/myEntityValidation.json", null, validators, false);
  }

  @Test
  public void infoInAnyOfValidation() throws Exception {
    JsonNode schemaNode = TreeUtil.json.readTree("{ \"properties\": { \"foo\": { \"anyOf\": [ { \"type\": \"integer\" }, { \"minimum\": 2 } ] } }}");

    OAI3Context apiContext = new OAI3Context(new URL("file:/"), schemaNode);
    ValidationContext<OAI3> validationContext = new ValidationContext<>(apiContext);
    validationContext.addValidator("type", TypeInfoValidator::new);

    SchemaValidator validator = new SchemaValidator(validationContext, null, schemaNode);

    ValidationData<TypeInfoDelegate> validation = new ValidationData<>();
    validator.validate(JsonNodeFactory.instance.objectNode().set("foo", JsonNodeFactory.instance.numberNode(1)), validation);

    assertEquals("foo", validation.results().items().get(0).dataCrumbs());
    assertEquals("foo.<anyOf>.<type>", validation.results().items().get(0).schemaCrumbs());
    assertEquals(ValidationSeverity.INFO, validation.results().items().get(0).severity());
  }

  @Test
  public void infoInOneOfValidation() throws Exception {
    JsonNode schemaNode = TreeUtil.json.readTree("{ \"properties\": { \"foo\": { \"oneOf\": [ { \"type\": \"integer\" }, { \"minimum\": 2 } ] } }}");

    OAI3Context apiContext = new OAI3Context(new URL("file:/"), schemaNode);
    ValidationContext<OAI3> validationContext = new ValidationContext<>(apiContext);
    validationContext.addValidator("type", TypeInfoValidator::new);

    SchemaValidator validator = new SchemaValidator(validationContext, null, schemaNode);

    ValidationData<TypeInfoDelegate> validation = new ValidationData<>();
    validator.validate(JsonNodeFactory.instance.objectNode().set("foo", JsonNodeFactory.instance.numberNode(1)), validation);

    assertEquals("foo", validation.results().items().get(0).dataCrumbs());
    assertEquals("foo.<oneOf>.<type>", validation.results().items().get(0).schemaCrumbs());
    assertEquals(ValidationSeverity.INFO, validation.results().items().get(0).severity());
  }

  @Test
  public void delegatedValidation() throws Exception {
    JsonNode schemaNode = TreeUtil.json.readTree("{ \"properties\": { \"foo\": { \"oneOf\": [ { \"type\": \"integer\" }, { \"minimum\": 2 } ] } }}");

    OAI3Context apiContext = new OAI3Context(new URL("file:/"), schemaNode);
    ValidationContext<OAI3> validationContext = new ValidationContext<>(apiContext);
    validationContext.addValidator("type", TypeInfoValidator::new);

    SchemaValidator validator = new SchemaValidator(validationContext, null, schemaNode);

    ValidationData<TypeInfoDelegate> validation = new ValidationData<>(new TypeInfoDelegate(true));
    validator.validate(JsonNodeFactory.instance.objectNode().set("foo", JsonNodeFactory.instance.numberNode(1)), validation);

    assertEquals("foo", validation.results().items().get(0).dataCrumbs());
    assertEquals("foo.<oneOf>.<type>", validation.results().items().get(0).schemaCrumbs());
    assertEquals(ValidationSeverity.INFO, validation.results().items().get(0).severity());
    assertEquals(ValidationSeverity.INFO, validation.results().items().get(1).severity());
    assertEquals("true", validation.results().items().get(1).message());
  }

  @Test
  public void dataJsonPointer() throws Exception {
    JsonNode schemaNode = TreeUtil.json.readTree("{ \"properties\": { \"f~/oo\": { \"oneOf\": [ { \"type\": \"integer\" }, { \"minimum\": 2 } ] } }}");

    OAI3Context apiContext = new OAI3Context(new URL("file:/"), schemaNode);
    ValidationContext<OAI3> validationContext = new ValidationContext<>(apiContext);
    validationContext.addValidator("type", TypeInfoValidator::new);

    SchemaValidator validator = new SchemaValidator(validationContext, null, schemaNode);

    ValidationData<TypeInfoDelegate> validation = new ValidationData<>();
    validator.validate(JsonNodeFactory.instance.objectNode().set("f~/oo", JsonNodeFactory.instance.numberNode(1)), validation);

    assertEquals("/f~0~1oo", validation.results().items().get(0).dataJsonPointer());
  }

  @Test(expected = RuntimeException.class)
  public void schemaValidatorResolutionException() throws RuntimeException, IOException {
    new SchemaValidator(
      null,
      "my_schema",
      TreeUtil.json.readTree(ValidationTest.class.getResource("/schema/reference.json")));
  }

  @Test
  public void doNotChangeContextIfGiven() throws Exception {
    JsonNode schemaNode = TreeUtil.json.readTree("{\"not\": {\"type\": \"integer\"} }");

    OAI3Context apiContext = new OAI3Context(new URL("file:/"), schemaNode);
    ValidationContext<OAI3> validationContext = new ValidationContext<>(apiContext);
    SchemaValidator validator = new SchemaValidator(validationContext, "my_schema", schemaNode);

    assertEquals(validationContext, validator.getContext());
  }
}
