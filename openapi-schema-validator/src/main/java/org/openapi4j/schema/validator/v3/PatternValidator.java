package org.openapi4j.schema.validator.v3;

import com.fasterxml.jackson.databind.JsonNode;

import org.openapi4j.core.model.v3.OAI3;
import org.openapi4j.core.validation.ValidationResult;
import org.openapi4j.core.validation.ValidationResults;
import org.openapi4j.schema.validator.BaseJsonValidator;
import org.openapi4j.schema.validator.ValidationContext;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.openapi4j.core.model.v3.OAI3SchemaKeywords.PATTERN;
import static org.openapi4j.core.validation.ValidationSeverity.ERROR;

/**
 * pattern keyword validator.
 * <p/>
 * <a href="https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.2.md#schemaObject" />
 * <p/>
 * <a href="https://tools.ietf.org/html/draft-wright-json-schema-validation-00#page-7" />
 */
class PatternValidator extends BaseJsonValidator<OAI3> {
  private static final ValidationResult PATTERN_DEF_ERR = new ValidationResult(ERROR, 1024, "Wrong pattern definition '%s'.");
  private static final ValidationResult ERR = new ValidationResult(ERROR, 1025, "'%s' does not respect pattern '%s'.");

  private final String patternStr;
  private final Pattern pattern;

  static PatternValidator create(ValidationContext<OAI3> context, JsonNode schemaNode, JsonNode schemaParentNode, SchemaValidator parentSchema) {
    return new PatternValidator(context, schemaNode, schemaParentNode, parentSchema);
  }

  private PatternValidator(final ValidationContext<OAI3> context, final JsonNode schemaNode, final JsonNode schemaParentNode, final SchemaValidator parentSchema) {
    super(context, schemaNode, schemaParentNode, parentSchema);

    patternStr = schemaNode.asText();
    pattern = schemaNode.isTextual() ? Pattern.compile(schemaNode.textValue()) : null;
  }

  @Override
  public void validate(final JsonNode valueNode, final ValidationResults results) {
    if (pattern == null) {
      results.add(PATTERN, PATTERN_DEF_ERR, patternStr);
      return;
    } else if (!valueNode.isTextual()) {
      return;
    }

    String value = valueNode.textValue();
    Matcher m = pattern.matcher(value);
    if (!m.find()) {
      results.add(PATTERN, ERR, value, patternStr);
    }
  }
}
