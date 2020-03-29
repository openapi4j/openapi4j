package org.openapi4j.schema.validator.v3;

import com.fasterxml.jackson.databind.JsonNode;
import org.openapi4j.core.model.v3.OAI3;
import org.openapi4j.core.validation.ValidationException;
import org.openapi4j.core.validation.ValidationResult;
import org.openapi4j.core.validation.ValidationResults;
import org.openapi4j.schema.validator.BaseJsonValidator;
import org.openapi4j.schema.validator.ValidationContext;
import org.openapi4j.schema.validator.ValidationData;

import java.util.*;

import static org.openapi4j.core.model.v3.OAI3SchemaKeywords.DEPENDENCIES;
import static org.openapi4j.core.validation.ValidationSeverity.ERROR;

/**
 * dependencies keyword validator.
 * <p/>
 * <a href="https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.2.md#schemaObject" />
 * <p/>
 * <a href="https://tools.ietf.org/html/draft-wright-json-schema-validation-00#page-10" />
 */
class DependenciesValidator extends BaseJsonValidator<OAI3> {
  private static final ValidationResult ERR = new ValidationResult(ERROR, 1002, "Missing dependency '%s' from '%s' definition.");

  private static final ValidationResults.CrumbInfo CRUMB_INFO = new ValidationResults.CrumbInfo(DEPENDENCIES, true);

  private final Map<String, Collection<String>> arrayDependencies = new HashMap<>();
  private final Map<String, SchemaValidator> objectDependencies = new HashMap<>();

  DependenciesValidator(final ValidationContext<OAI3> context,
                        final JsonNode schemaNode,
                        final JsonNode schemaParentNode,
                        final SchemaValidator parentSchema) {

    super(context, schemaNode, schemaParentNode, parentSchema);

    Iterator<String> fieldNames = schemaNode.fieldNames();
    while (fieldNames.hasNext()) {
      final String fieldName = fieldNames.next();
      final JsonNode fieldSchemaVal = schemaNode.get(fieldName);

      if (fieldSchemaVal.isObject()) {
        objectDependencies.put(fieldName, new SchemaValidator(context, new ValidationResults.CrumbInfo(fieldName, false), fieldSchemaVal, schemaParentNode, parentSchema));

      } else if (fieldSchemaVal.isArray()) {
        Collection<String> values = arrayDependencies.computeIfAbsent(fieldName, k -> new ArrayList<>());
        for (int i = 0; i < fieldSchemaVal.size(); i++) {
          values.add(fieldSchemaVal.get(i).asText());
        }
      }
    }
  }

  @Override
  public boolean validate(final JsonNode valueNode, final ValidationData<?> validation) {
    final Iterator<String> fieldNames = valueNode.fieldNames();

    validate(() -> {
      while (fieldNames.hasNext()) {
        final String fieldName = fieldNames.next();

        final Collection<String> values = arrayDependencies.get(fieldName);
        if (values != null) {
          validateArray(valueNode, values, validation);
        } else {
          validateObject(valueNode, objectDependencies.get(fieldName), validation);
        }
      }
    });

    return false;
  }

  private void validateArray(final JsonNode valueNode,
                             final Collection<String> values,
                             final ValidationData<?> validation) {

    for (String field : values) {
      if (valueNode.get(field) == null) {
        validation.add(CRUMB_INFO, ERR, field, arrayDependencies.toString());
      }
    }
  }

  private void validateObject(final JsonNode valueNode,
                              final SchemaValidator schema,
                              final ValidationData<?> validation) throws ValidationException {

    if (schema != null) {
      schema.validateWithContext(valueNode, validation);
    }
  }
}
