package org.openapi4j.schema.validator.v3;

import com.fasterxml.jackson.databind.JsonNode;
import org.openapi4j.core.model.v3.OAI3;
import org.openapi4j.core.validation.ValidationResults;
import org.openapi4j.schema.validator.BaseJsonValidator;
import org.openapi4j.schema.validator.ValidationContext;
import org.openapi4j.schema.validator.ValidationData;

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
class PatternPropertiesValidator<V> extends BaseJsonValidator<OAI3, V> {
  private final Map<Pattern, SchemaValidator<V>> schemas = new IdentityHashMap<>();

  PatternPropertiesValidator(final ValidationContext<OAI3, V> context, final JsonNode schemaNode, final JsonNode schemaParentNode, final SchemaValidator<V> parentSchema) {
    super(context, schemaNode, schemaParentNode, parentSchema);

    Iterator<String> names = schemaNode.fieldNames();
    while (names.hasNext()) {
      String name = names.next();
      schemas.put(Pattern.compile(name), new SchemaValidator<>(context, new ValidationResults.CrumbInfo(name, false), schemaNode.get(name), schemaParentNode, parentSchema));
    }
  }

  @Override
  public boolean validate(final JsonNode valueNode, final ValidationData<V> validation) {
    if (!valueNode.isObject()) {
      return false;
    }

    final Iterator<String> names = valueNode.fieldNames();

    validate(() -> {
      while (names.hasNext()) {
        String name = names.next();
        for (Map.Entry<Pattern, SchemaValidator<V>> entry : schemas.entrySet()) {
          Matcher m = entry.getKey().matcher(name);
          if (m.matches()) {
            entry.getValue().validateWithContext(valueNode.get(name), validation);
          }
        }
      }
    });

    return false;
  }
}
