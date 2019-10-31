package org.openapi4j.schema.validator.v3;

import com.fasterxml.jackson.databind.JsonNode;

import org.openapi4j.core.model.v3.OAI3;
import org.openapi4j.core.validation.ValidationResults;
import org.openapi4j.schema.validator.BaseJsonValidator;
import org.openapi4j.schema.validator.ValidationContext;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.openapi4j.core.model.v3.OAI3SchemaKeywords.PATTERN;

class PatternValidator extends BaseJsonValidator<OAI3> {
  private static final String PATTERN_DEF_ERR_MSG = "Wrong pattern definition '%s'.";
  private static final String CONTENT_TYPE_ERR_MSG = "'%s' is not a string.";
  private static final String ERR_MSG = "'%s' does not respect pattern '%s'.";

  private final String patternStr;
  private final Pattern pattern;

  PatternValidator(final ValidationContext<OAI3> context, final JsonNode schemaNode, final JsonNode schemaParentNode, final SchemaValidator parentSchema) {
    super(context, schemaNode, schemaParentNode, parentSchema);

    patternStr = schemaNode.asText();
    pattern = schemaNode.isTextual() ? Pattern.compile(schemaNode.textValue()) : null;
  }

  @Override
  public void validate(final JsonNode valueNode, final ValidationResults results) {
    if (pattern == null) {
      results.addError(String.format(PATTERN_DEF_ERR_MSG, patternStr), PATTERN);
      return;
    } else if (!valueNode.isTextual()) {
      results.addError(String.format(CONTENT_TYPE_ERR_MSG, valueNode), PATTERN);
      return;
    }

    String value = valueNode.textValue();
    Matcher m = pattern.matcher(value);
    if (!m.find()) {
      results.addError(String.format(ERR_MSG, value, patternStr), PATTERN);
    }
  }
}
