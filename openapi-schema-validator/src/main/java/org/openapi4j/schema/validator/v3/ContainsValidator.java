package org.openapi4j.schema.validator.v3;

import com.fasterxml.jackson.databind.JsonNode;

import org.openapi4j.core.model.v3.OAI3;
import org.openapi4j.core.validation.ValidationResult;
import org.openapi4j.core.validation.ValidationResults;
import org.openapi4j.schema.validator.BaseJsonValidator;
import org.openapi4j.schema.validator.ValidationContext;
import org.openapi4j.schema.validator.ValidationData;

import static org.openapi4j.core.model.v3.OAI3SchemaKeywords.CONTAINS;
import static org.openapi4j.core.validation.ValidationSeverity.ERROR;


/**
 * contains validator.
 * <p/>
 * <a href="https://json-schema.org/draft/2019-09/json-schema-core.html#rfc.section.9.3.1.4" />
 */
public class ContainsValidator extends BaseJsonValidator<OAI3> {
  private static final ValidationResults.CrumbInfo CRUMB_INFO = new ValidationResults.CrumbInfo(CONTAINS, true);
  private static final ValidationResult ERR = new ValidationResult(ERROR, 1016, "At least one element should be valid against the given schema.");
  private final SchemaValidator schema;

  ContainsValidator(final ValidationContext<OAI3> context, final JsonNode schemaNode, final JsonNode schemaParentNode, final SchemaValidator parentSchema) {
    super(context, schemaNode, schemaParentNode, parentSchema);

    schema = new SchemaValidator(context, CRUMB_INFO, schemaNode, schemaParentNode, parentSchema);
  }

  @Override
  public boolean validate(final JsonNode valueNode, final ValidationData<?> validation) {
    if (!valueNode.isArray()) {
      return false;
    }

    boolean hasValidResult = false;

    for (int idx = 0; idx < valueNode.size(); ++idx) {
      JsonNode itemNode = valueNode.get(idx);
      ValidationData<?> schemaValidation = new ValidationData<>(validation.delegate());

      validation.results().withCrumb(
        new ValidationResults.CrumbInfo(Integer.toString(idx), false),
        () -> schema.validate(itemNode, schemaValidation));

      if (schemaValidation.isValid()) {
        hasValidResult = true;
        // Append potential results from sub validation (INFO / WARN)
        validation.add(validation.results().crumbs(), schemaValidation.results());
      }
    }

    if (!hasValidResult) {
      validation.add(CRUMB_INFO, ERR);
    }

    return false;
  }
}
