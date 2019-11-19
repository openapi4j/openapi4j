package org.openapi4j.schema.validator.v3;

import com.fasterxml.jackson.databind.JsonNode;

import org.openapi4j.core.model.v3.OAI3;
import org.openapi4j.core.validation.ValidationException;
import org.openapi4j.core.validation.ValidationResults;
import org.openapi4j.schema.validator.BaseJsonValidator;
import org.openapi4j.schema.validator.ValidationContext;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static org.openapi4j.core.model.v3.OAI3SchemaKeywords.DEPENDENCIES;

/**
 * dependencies keyword validator.
 * <p/>
 * <a href="https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.2.md#schemaObject" />
 * <p/>
 * <a href="https://tools.ietf.org/html/draft-wright-json-schema-validation-00#page-10" />
 */
class DependenciesValidator extends BaseJsonValidator<OAI3> {
  private static final String MISSING_DEP_ERR_MSG = "Missing dependency '%s' from '%s' definition.";

  private final Map<String, Collection<String>> arrayDependencies = new HashMap<>();
  private final Map<String, SchemaValidator> objectDependencies = new HashMap<>();

  static DependenciesValidator create(final ValidationContext<OAI3> context,
                                      final JsonNode schemaNode,
                                      final JsonNode schemaParentNode,
                                      final SchemaValidator parentSchema) {

    return new DependenciesValidator(context, schemaNode, schemaParentNode, parentSchema);
  }

  private DependenciesValidator(final ValidationContext<OAI3> context,
                                final JsonNode schemaNode,
                                final JsonNode schemaParentNode,
                                final SchemaValidator parentSchema) {

    super(context, schemaNode, schemaParentNode, parentSchema);

    Iterator<String> fieldNames = schemaNode.fieldNames();
    while (fieldNames.hasNext()) {
      final String fieldName = fieldNames.next();
      final JsonNode fieldSchemaVal = schemaNode.get(fieldName);

      if (fieldSchemaVal.isObject()) {
        objectDependencies.put(fieldName, new SchemaValidator(context, fieldName, fieldSchemaVal, schemaParentNode, parentSchema));

      } else if (fieldSchemaVal.isArray()) {
        Collection<String> values = arrayDependencies.computeIfAbsent(fieldName, k -> new ArrayList<>());
        for (int i = 0; i < fieldSchemaVal.size(); i++) {
          values.add(fieldSchemaVal.get(i).asText());
        }
      }
    }
  }

  @Override
  public void validate(final JsonNode valueNode, final ValidationResults results) {
    final Iterator<String> fieldNames = valueNode.fieldNames();

    validate(() -> {
      while (fieldNames.hasNext()) {
        final String fieldName = fieldNames.next();

        final Collection<String> values = arrayDependencies.get(fieldName);
        if (values != null) {
          validateArray(valueNode, values, results);
        } else {
          validateObject(valueNode, objectDependencies.get(fieldName), results);
        }
      }
    });
  }

  private void validateArray(final JsonNode valueNode,
                             final Collection<String> values,
                             final ValidationResults results) {

    for (String field : values) {
      if (valueNode.get(field) == null) {
        results.addError(String.format(MISSING_DEP_ERR_MSG, field, arrayDependencies.toString()), DEPENDENCIES);
      }
    }
  }

  private void validateObject(final JsonNode valueNode,
                              final SchemaValidator schema,
                              final ValidationResults results) throws ValidationException {

    if (schema != null) {
      schema.validateWithContext(valueNode, results);
    }
  }
}
