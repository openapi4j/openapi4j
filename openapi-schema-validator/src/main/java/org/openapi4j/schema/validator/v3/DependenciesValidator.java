package org.openapi4j.schema.validator.v3;

import com.fasterxml.jackson.databind.JsonNode;
import org.openapi4j.core.model.v3.OAI3;
import org.openapi4j.core.validation.ValidationResults;
import org.openapi4j.schema.validator.BaseJsonValidator;
import org.openapi4j.schema.validator.ValidationContext;

import java.util.*;

import static org.openapi4j.core.model.v3.OAI3SchemaKeywords.DEPENDENCIES;

/**
 * dependencies keyword validator.
 * <p/>
 * <a href="https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.2.md#schemaObject" />
 * <p/>
 * <a href="https://tools.ietf.org/html/draft-wright-json-schema-validation-00#page-10" />
 */
public class DependenciesValidator extends BaseJsonValidator<OAI3> {
  private final Map<String, Collection<String>> dependentProps = new HashMap<>();
  private Map<String, SchemaValidator> dependentSchemas = new HashMap<>();

  DependenciesValidator(final ValidationContext<OAI3> context, final JsonNode schemaNode, final JsonNode schemaParentNode, final SchemaValidator parentSchema) {
    super(context, schemaNode, schemaParentNode, parentSchema);

    Iterator<String> fieldNames = schemaNode.fieldNames();
    while (fieldNames.hasNext()) {
      final String fieldName = fieldNames.next();
      final JsonNode fieldSchemaVal = schemaNode.get(fieldName);

      if (fieldSchemaVal.isObject()) {
        dependentSchemas.put(fieldName, new SchemaValidator(context, fieldName, fieldSchemaVal, schemaParentNode, parentSchema));

      } else if (fieldSchemaVal.isArray()) {
        Collection<String> values = this.dependentProps.computeIfAbsent(fieldName, k -> new ArrayList<>());
        for (int i = 0; i < fieldSchemaVal.size(); i++) {
          values.add(fieldSchemaVal.get(i).asText());
        }
      }
    }
  }

  @Override
  public void validate(JsonNode valueNode, ValidationResults results) {
    Iterator<String> fieldNames = valueNode.fieldNames();
    while (fieldNames.hasNext()) {
      final String fieldName = fieldNames.next();
      Collection<String> deps = dependentProps.get(fieldName);

      if (deps != null) {
        for (String field : deps) {
          if (valueNode.get(field) == null) {
            results.addError(dependentProps.toString(), DEPENDENCIES);
          }
        }
      }

      SchemaValidator schema = dependentSchemas.get(fieldName);
      if (schema != null) {
        schema.validate(valueNode, results);
      }
    }
  }
}
