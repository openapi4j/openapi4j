package org.openapi4j.schema.validator.v3;

import com.fasterxml.jackson.databind.JsonNode;
import org.openapi4j.core.model.v3.OAI3;
import org.openapi4j.core.validation.ValidationResults;
import org.openapi4j.schema.validator.BaseJsonValidator;
import org.openapi4j.schema.validator.ValidationContext;

import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * patternProperties keyword validator.
 * <p/>
 * <a href="https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.2.md#schemaObject" />
 * <p/>
 * <a href="https://tools.ietf.org/html/draft-wright-json-schema-validation-00#page-9" />
 */
class PatternPropertiesValidator extends BaseJsonValidator<OAI3> {
  private final Map<Pattern, SchemaValidator> schemas = new IdentityHashMap<>();

  static PatternPropertiesValidator create(ValidationContext<OAI3> context, JsonNode schemaNode, JsonNode schemaParentNode, SchemaValidator parentSchema) {
    return new PatternPropertiesValidator(context, schemaNode, schemaParentNode, parentSchema);
  }

  private PatternPropertiesValidator(final ValidationContext<OAI3> context, final JsonNode schemaNode, final JsonNode schemaParentNode, final SchemaValidator parentSchema) {
    super(context, schemaNode, schemaParentNode, parentSchema);

    Iterator<String> names = schemaNode.fieldNames();
    while (names.hasNext()) {
      String name = names.next();
      schemas.put(Pattern.compile(name), new SchemaValidator(context, new ValidationResults.CrumbInfo(name, false), schemaNode.get(name), schemaParentNode, parentSchema));
    }
  }

  @Override
  public boolean validate(final JsonNode valueNode, final ValidationResults results) {
    if (!valueNode.isObject()) {
      return false;
    }

    final Iterator<String> names = valueNode.fieldNames();

    validate(() -> {
      while (names.hasNext()) {
        String name = names.next();
        for (Map.Entry<Pattern, SchemaValidator> entry : schemas.entrySet()) {
          Matcher m = entry.getKey().matcher(name);
          if (m.matches()) {
            entry.getValue().validateWithContext(valueNode.get(name), results);
          }
        }
      }
    });

    return false;
  }
}
