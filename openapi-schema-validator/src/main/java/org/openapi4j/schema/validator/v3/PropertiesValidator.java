package org.openapi4j.schema.validator.v3;

import com.fasterxml.jackson.databind.JsonNode;
import org.openapi4j.core.model.v3.OAI3;
import org.openapi4j.core.validation.ValidationResults;
import org.openapi4j.schema.validator.BaseJsonValidator;
import org.openapi4j.schema.validator.ValidationContext;
import org.openapi4j.schema.validator.ValidationData;

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
class PropertiesValidator<V> extends BaseJsonValidator<OAI3, V> {
  private final Map<String, SchemaValidator<V>> schemas;

  PropertiesValidator(final ValidationContext<OAI3, V> context, final JsonNode schemaNode, final JsonNode schemaParentNode, final SchemaValidator<V> parentSchema) {
    super(context, schemaNode, schemaParentNode, parentSchema);

    schemas = new HashMap<>();
    for (Iterator<String> it = schemaNode.fieldNames(); it.hasNext(); ) {
      String pname = it.next();
      schemas.put(pname, new SchemaValidator<>(context, new ValidationResults.CrumbInfo(pname, false), schemaNode.get(pname), schemaParentNode, parentSchema));
    }
  }

  @Override
  public boolean validate(final JsonNode valueNode, final ValidationData<V> validation) {
    validate(() -> {
      for (Map.Entry<String, SchemaValidator<V>> entry : schemas.entrySet()) {
        SchemaValidator<V> propertySchema = entry.getValue();
        JsonNode propertyNode = valueNode.get(entry.getKey());

        if (propertyNode != null) {
          propertySchema.validateWithContext(propertyNode, validation);
        }
      }
    });

    return false;
  }
}
