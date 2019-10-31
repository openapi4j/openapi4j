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

class PatternPropertiesValidator extends BaseJsonValidator<OAI3> {
  private final Map<Pattern, SchemaValidator> schemas = new IdentityHashMap<>();

  PatternPropertiesValidator(final ValidationContext<OAI3> context, final JsonNode schemaNode, final JsonNode schemaParentNode, final SchemaValidator parentSchema) {
    super(context, schemaNode, schemaParentNode, parentSchema);

    Iterator<String> names = schemaNode.fieldNames();
    while (names.hasNext()) {
      String name = names.next();
      schemas.put(Pattern.compile(name), new SchemaValidator(context, name, schemaNode.get(name), schemaParentNode, parentSchema));
    }
  }

  @Override
  public void validate(final JsonNode valueNode, final ValidationResults results) {
    if (!valueNode.isObject()) {
      return;
    }

    Iterator<String> names = valueNode.fieldNames();
    while (names.hasNext()) {
      String name = names.next();
      for (Map.Entry<Pattern, SchemaValidator> entry : schemas.entrySet()) {
        Matcher m = entry.getKey().matcher(name);
        if (m.matches()) {
          entry.getValue().validate(valueNode.get(name), results);
        }
      }
    }
  }
}
