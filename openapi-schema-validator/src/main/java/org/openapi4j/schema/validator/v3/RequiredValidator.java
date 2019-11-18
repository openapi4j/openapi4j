package org.openapi4j.schema.validator.v3;

import com.fasterxml.jackson.databind.JsonNode;

import org.openapi4j.core.model.v3.OAI3;
import org.openapi4j.core.validation.ValidationResults;
import org.openapi4j.schema.validator.BaseJsonValidator;
import org.openapi4j.schema.validator.ValidationContext;

import java.util.ArrayList;
import java.util.List;

import static org.openapi4j.core.model.v3.OAI3SchemaKeywords.REQUIRED;

/**
 * required keyword validator.
 * <p/>
 * <a href="https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.2.md#schemaObject" />
 * <p/>
 * <a href="https://tools.ietf.org/html/draft-wright-json-schema-validation-00#page-9" />
 */
class RequiredValidator extends BaseJsonValidator<OAI3> {
  private static final String ERR_MSG = "Field '%s' is required.";

  private final List<String> fieldNames;

  static RequiredValidator create(ValidationContext<OAI3> context, JsonNode schemaNode, JsonNode schemaParentNode, SchemaValidator parentSchema) {
    return new RequiredValidator(context, schemaNode, schemaParentNode, parentSchema);
  }

  private RequiredValidator(final ValidationContext<OAI3> context, final JsonNode schemaNode, final JsonNode schemaParentNode, final SchemaValidator parentSchema) {
    super(context, schemaNode, schemaParentNode, parentSchema);

    if (schemaNode.isArray()) {
      fieldNames = new ArrayList<>();

      for (JsonNode fieldName : schemaNode) {
        fieldNames.add(fieldName.asText());
      }
    } else {
      fieldNames = null;
    }
  }

  @Override
  public void validate(final JsonNode valueNode, final ValidationResults results) {
    if (fieldNames == null) return;

    for (String fieldName : fieldNames) {
      if (null == valueNode.get(fieldName)) {
        results.addError(String.format(ERR_MSG, fieldName), REQUIRED);
      }
    }
  }
}
