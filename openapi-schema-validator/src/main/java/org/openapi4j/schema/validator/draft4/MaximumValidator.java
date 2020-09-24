package org.openapi4j.schema.validator.draft4;

import com.fasterxml.jackson.databind.JsonNode;
import org.openapi4j.core.model.v3.OAI3;
import org.openapi4j.core.validation.ValidationResult;
import org.openapi4j.core.validation.ValidationResults;
import org.openapi4j.schema.validator.common.BaseJsonValidator;
import org.openapi4j.schema.validator.common.ValidationContext;
import org.openapi4j.schema.validator.common.ValidationData;
import org.openapi4j.schema.validator.common.SchemaValidator;

import java.math.BigDecimal;

import static org.openapi4j.core.model.v3.OAI3SchemaKeywords.EXCLUSIVEMAXIMUM;
import static org.openapi4j.core.model.v3.OAI3SchemaKeywords.MAXIMUM;
import static org.openapi4j.core.validation.ValidationSeverity.ERROR;

/**
 * maximum keyword validator.
 * <p/>
 * <a href="https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.2.md#schemaObject" />
 * <p/>
 * <a href="https://tools.ietf.org/html/draft-wright-json-schema-validation-00#page-6" />
 */
class MaximumValidator extends BaseJsonValidator<OAI3> {
  private static final ValidationResult EXCLUSIVE_ERR = new ValidationResult(ERROR, 1009, "Excluded maximum is '%s', found '%s'.");
  private static final ValidationResult ERR = new ValidationResult(ERROR, 1010, "Maximum is '%s', found '%s'.");

  private static final ValidationResults.CrumbInfo CRUMB_INFO = new ValidationResults.CrumbInfo(MAXIMUM, true);

  private final BigDecimal maximum;
  private final boolean excludeEqual;

  MaximumValidator(final ValidationContext<OAI3> context, final JsonNode schemaNode, final JsonNode schemaParentNode, final SchemaValidator parentSchema) {
    super(context, schemaNode, schemaParentNode, parentSchema);

    maximum = schemaNode.isNumber() ? schemaNode.decimalValue() : null;

    JsonNode exclusiveMaximumNode = schemaParentNode.get(EXCLUSIVEMAXIMUM);
    if (exclusiveMaximumNode != null && exclusiveMaximumNode.isBoolean()) {
      excludeEqual = exclusiveMaximumNode.booleanValue();
    } else {
      excludeEqual = false;
    }
  }

  @Override
  public boolean validate(final JsonNode valueNode, final ValidationData<?> validation) {
    if (maximum == null || !valueNode.isNumber()) {
      return false;
    }

    final BigDecimal value = valueNode.decimalValue();
    final int compResult = value.compareTo(maximum);
    if (excludeEqual && compResult == 0) {
      validation.add(CRUMB_INFO, EXCLUSIVE_ERR, maximum, value);
    } else if (compResult > 0) {
      validation.add(CRUMB_INFO, ERR, maximum, value);
    }

    return false;
  }
}
