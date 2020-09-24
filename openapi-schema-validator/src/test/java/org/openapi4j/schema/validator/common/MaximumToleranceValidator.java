package org.openapi4j.schema.validator.common;

import com.fasterxml.jackson.databind.JsonNode;
import org.openapi4j.core.model.v3.OAI3;
import org.openapi4j.core.validation.ValidationResult;
import org.openapi4j.core.validation.ValidationResults;

import java.math.BigDecimal;

import static org.openapi4j.core.model.v3.OAI3SchemaKeywords.EXCLUSIVEMAXIMUM;
import static org.openapi4j.core.model.v3.OAI3SchemaKeywords.MAXIMUM;
import static org.openapi4j.core.validation.ValidationSeverity.ERROR;

/**
 * Foo maximum keyword override for testing purpose.
 */
public class MaximumToleranceValidator extends BaseJsonValidator<OAI3> {
  private static final ValidationResult EXCLUSIVE_ERR_MSG = new ValidationResult(ERROR, 1, "'%s' must be lower than '%s'.");
  private static final ValidationResult ERR_MSG = new ValidationResult(ERROR, 2, "'%s' is greater than '%s'");

  private static final ValidationResults.CrumbInfo CRUMB_INFO = new ValidationResults.CrumbInfo(MAXIMUM, true);

  private static final String TOLERANCE_KEYWORD = "x-tolerance";

  private final BigDecimal maximum;
  private final boolean excludeEqual;

  public MaximumToleranceValidator(final ValidationContext<OAI3> context,
                                   final JsonNode schemaNode,
                                   final JsonNode schemaParentNode,
                                   final SchemaValidator parentSchema) {

    super(context, schemaNode, schemaParentNode, parentSchema);

    double tolerance = schemaParentNode.get(TOLERANCE_KEYWORD).doubleValue();

    BigDecimal tempMaximum = schemaNode.isNumber() ? schemaNode.decimalValue() : new BigDecimal(0);
    maximum = BigDecimal.valueOf(tempMaximum.doubleValue() + tolerance);

    JsonNode exclusiveMaximumNode = schemaParentNode.get(EXCLUSIVEMAXIMUM);
    if (exclusiveMaximumNode != null && exclusiveMaximumNode.isBoolean()) {
      excludeEqual = exclusiveMaximumNode.booleanValue();
    } else {
      excludeEqual = false;
    }
  }

  @Override
  public boolean validate(final JsonNode valueNode, final ValidationData<?> validation) {
    if (valueNode.isNumber()) {
      final BigDecimal value = valueNode.decimalValue();
      final int compResult = value.compareTo(maximum);
      if (excludeEqual && compResult == 0) {
        validation.add(CRUMB_INFO, EXCLUSIVE_ERR_MSG, value, maximum);
      } else if (compResult > 0) {
        validation.add(CRUMB_INFO, ERR_MSG, value, maximum);
      }

      return false;
    }

    return true;
  }
}
