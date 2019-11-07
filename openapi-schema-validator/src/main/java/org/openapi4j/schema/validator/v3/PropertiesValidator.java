package org.openapi4j.schema.validator.v3;

import com.fasterxml.jackson.databind.JsonNode;

import org.openapi4j.core.model.v3.OAI3;
import org.openapi4j.core.validation.ValidationResults;
import org.openapi4j.schema.validator.BaseJsonValidator;
import org.openapi4j.schema.validator.ValidationContext;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * properties keyword validator.
 * <p/>
 * <a href="https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.2.md#schemaObject" />
 * <p/>
 * <a href="https://tools.ietf.org/html/draft-wright-json-schema-validation-00#page-9" />
 */
class PropertiesValidator extends BaseJsonValidator<OAI3> {
  private final Map<String, SchemaValidator> schemas;

  static PropertiesValidator create(ValidationContext<OAI3> context, JsonNode schemaNode, JsonNode schemaParentNode, SchemaValidator parentSchema) {
    return new PropertiesValidator(context, schemaNode, schemaParentNode, parentSchema);
  }

  PropertiesValidator(final ValidationContext<OAI3> context, final JsonNode schemaNode, final JsonNode schemaParentNode, final SchemaValidator parentSchema) {
    super(context, schemaNode, schemaParentNode, parentSchema);

    schemas = new HashMap<>();
    for (Iterator<String> it = schemaNode.fieldNames(); it.hasNext(); ) {
      String pname = it.next();
      schemas.put(pname, new SchemaValidator(context, pname, schemaNode.get(pname), schemaParentNode, parentSchema));
    }
  }

  @Override
  public void validate(final JsonNode valueNode, final ValidationResults results) {
    for (Map.Entry<String, SchemaValidator> entry : schemas.entrySet()) {
      SchemaValidator propertySchema = entry.getValue();
      JsonNode propertyNode = valueNode.get(entry.getKey());

      if (propertyNode != null) {
        propertySchema.validate(propertyNode, results);
      }
    }
  }
}
