package org.openapi4j.schema.validator.draft7;

import com.fasterxml.jackson.databind.JsonNode;

import org.openapi4j.core.model.v3.OAI3;
import org.openapi4j.core.validation.ValidationResults;
import org.openapi4j.schema.validator.common.BaseJsonValidator;
import org.openapi4j.schema.validator.common.ValidationContext;
import org.openapi4j.schema.validator.common.ValidationData;
import org.openapi4j.schema.validator.common.SchemaValidator;

import java.util.Arrays;
import java.util.List;

import static org.openapi4j.core.model.v3.OAI3SchemaKeywords.ELSE;
import static org.openapi4j.core.model.v3.OAI3SchemaKeywords.IF;
import static org.openapi4j.core.model.v3.OAI3SchemaKeywords.THEN;

/**
 * if / then / else validator.
 * <p/>
 * <a href="https://json-schema.org/draft/2019-09/json-schema-core.html#rfc.section.9.2.2" />
 */
public class IfValidator extends BaseJsonValidator<OAI3> {
  private static final ValidationResults.CrumbInfo CRUMB_INFO_IF = new ValidationResults.CrumbInfo(IF, true);
  private static final ValidationResults.CrumbInfo CRUMB_INFO_THEN = new ValidationResults.CrumbInfo(THEN, true);
  private static final ValidationResults.CrumbInfo CRUMB_INFO_ELSE = new ValidationResults.CrumbInfo(ELSE, true);
  private static final List<String> KEYWORDS = Arrays.asList(IF, THEN, ELSE);

  private SchemaValidator ifSchema;
  private SchemaValidator thenSchema;
  private SchemaValidator elseSchema;

  IfValidator(final ValidationContext<OAI3> context, final JsonNode schemaNode, final JsonNode schemaParentNode, final SchemaValidator parentSchema) {
    super(context, schemaNode, schemaParentNode, parentSchema);

    JsonNode ifNode = null;
    JsonNode thenNode = null;

    for (final String keyword : KEYWORDS) {
      if (keyword.equals(IF)) {
        ifNode = schemaNode.get(keyword);
        ifSchema = new SchemaValidator(context, CRUMB_INFO_IF, schemaNode, schemaParentNode, parentSchema);
      } else if (keyword.equals(THEN) && ifNode != null) {
        thenNode = schemaNode.get(keyword);
        thenSchema = new SchemaValidator(context, CRUMB_INFO_THEN, schemaNode, schemaParentNode, parentSchema);
      } else if (keyword.equals(ELSE) && thenNode != null) {
        elseSchema = new SchemaValidator(context, CRUMB_INFO_ELSE, schemaNode, schemaParentNode, parentSchema);
      }
    }
  }

  @Override
  public boolean validate(final JsonNode valueNode, final ValidationData<?> validation) {
    ValidationData<?> ifValidation = new ValidationData<>(validation.delegate());

    ifSchema.validate(valueNode, ifValidation);
    if (ifValidation.isValid()) {
      if (thenSchema != null) {
        thenSchema.validate(valueNode, validation);
      } else if (elseSchema != null) {
        elseSchema.validate(valueNode, validation);
      }
    }

    return false;
  }
}
