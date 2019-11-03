package org.openapi4j.schema.validator.v3;

import com.fasterxml.jackson.databind.JsonNode;
import org.openapi4j.core.model.v3.OAI3;
import org.openapi4j.core.model.v3.OAI3SchemaKeywords;
import org.openapi4j.core.validation.ValidationResults;
import org.openapi4j.schema.validator.BaseJsonValidator;
import org.openapi4j.schema.validator.JsonValidator;
import org.openapi4j.schema.validator.ValidationContext;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.openapi4j.core.model.v3.OAI3SchemaKeywords.ADDITIONALPROPERTIES;

/**
 * additionalProperties keyword validator.
 * <p/>
 * <a href="https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.2.md#schemaObject" />
 * <p/>
 * <a href="https://tools.ietf.org/html/draft-wright-json-schema-validation-00#page-10" />
 */
class AdditionalPropertiesValidator extends BaseJsonValidator<OAI3> {
  private static final String ERR_MSG = "Additional property '%s' is not allowed.";

  private final Set<String> allowedProperties;
  private final Set<Pattern> allowedPatternProperties;
  private final Boolean additionalPropertiesAllowed;
  private final SchemaValidator additionalPropertiesSchema;

  static JsonValidator create(ValidationContext<OAI3> context, JsonNode schemaNode, JsonNode schemaParentNode, SchemaValidator parentSchema) {
    return new AdditionalPropertiesValidator(context, schemaNode, schemaParentNode, parentSchema);
  }

  AdditionalPropertiesValidator(final ValidationContext<OAI3> context, final JsonNode schemaNode, final JsonNode schemaParentNode, final SchemaValidator parentSchema) {
    super(context, schemaNode, schemaParentNode, parentSchema);

    if (schemaNode.isBoolean()) {
      additionalPropertiesAllowed = schemaNode.booleanValue();
      additionalPropertiesSchema = null;
    } else /*if (schemaNode.isObject())*/ {
      additionalPropertiesAllowed = false;
      additionalPropertiesSchema = new SchemaValidator(context, ADDITIONALPROPERTIES, schemaNode, schemaParentNode, parentSchema);
    }

    if (additionalPropertiesAllowed) {
      allowedProperties = null;
      allowedPatternProperties = null;

    } else {
      JsonNode propertiesNode = schemaParentNode.get(OAI3SchemaKeywords.PROPERTIES);
      if (propertiesNode != null) {
        allowedProperties = new HashSet<>();
        for (Iterator<String> it = propertiesNode.fieldNames(); it.hasNext(); ) {
          allowedProperties.add(it.next());
        }
      } else {
        allowedProperties = null;
      }

      JsonNode patternPropertiesNode = schemaParentNode.get(OAI3SchemaKeywords.PATTERNPROPERTIES);
      if (patternPropertiesNode != null) {
        allowedPatternProperties = new HashSet<>();
        for (Iterator<String> it = patternPropertiesNode.fieldNames(); it.hasNext(); ) {
          allowedPatternProperties.add(Pattern.compile(it.next()));
        }
      } else {
        allowedPatternProperties = null;
      }
    }
  }

  @Override
  public void validate(final JsonNode valueNode, final ValidationResults results) {
    if (additionalPropertiesAllowed) return;

    for (Iterator<String> it = valueNode.fieldNames(); it.hasNext(); ) {
      String fieldName = it.next();

      if (!checkAgainstPatternProperties(fieldName) && !checkAgainstProperties(fieldName)) {
        if (additionalPropertiesSchema != null) {
          additionalPropertiesSchema.validate(valueNode.get(fieldName), results);
        } else {
          results.addError(String.format(ERR_MSG, fieldName), ADDITIONALPROPERTIES);
        }
      }
    }
  }

  private boolean checkAgainstPatternProperties(final String fieldName) {
    if (allowedPatternProperties != null) {
      for (Pattern pattern : allowedPatternProperties) {
        Matcher m = pattern.matcher(fieldName);
        if (m.find()) {
          return true;
        }
      }
    }

    return false;
  }

  private boolean checkAgainstProperties(final String fieldName) {
    return allowedProperties != null && allowedProperties.contains(fieldName);
  }
}
