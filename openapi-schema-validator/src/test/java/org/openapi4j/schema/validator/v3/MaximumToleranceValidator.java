package org.openapi4j.schema.validator.v3;

import com.fasterxml.jackson.databind.JsonNode;

import org.openapi4j.core.model.v3.OAI3;
import org.openapi4j.core.validation.ValidationResults;
import org.openapi4j.schema.validator.BaseJsonValidator;
import org.openapi4j.schema.validator.JsonValidator;
import org.openapi4j.schema.validator.ValidationContext;

import java.math.BigDecimal;

import static org.openapi4j.core.model.v3.OAI3SchemaKeywords.EXCLUSIVEMAXIMUM;
import static org.openapi4j.core.model.v3.OAI3SchemaKeywords.MAXIMUM;

/**
 * Foo maximum keyword override for testing purpose.
 */
public class MaximumToleranceValidator extends BaseJsonValidator<OAI3> {
  private static final String EXCLUSIVE_ERR_MSG = "'%s' must be lower than '%s'.";
  private static final String ERR_MSG = "'%s' is greater than '%s'";

  private final BigDecimal maximum;
  private final boolean excludeEqual;

  private MaximumToleranceValidator(final ValidationContext<OAI3> context,
                                    final JsonNode schemaNode,
                                    final JsonNode schemaParentNode,
                                    final SchemaValidator parentSchema,
                                    final double tolerance) {

    super(context, schemaNode, schemaParentNode, parentSchema);

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
  public void validate(final JsonNode valueNode, final ValidationResults results) {
    if (!valueNode.isNumber()) {
      return;
    }

    final BigDecimal value = valueNode.decimalValue();
    final int compResult = value.compareTo(maximum);
    if (excludeEqual && compResult == 0) {
      results.addError(String.format(EXCLUSIVE_ERR_MSG, value, maximum), MAXIMUM);
    } else if (compResult > 0) {
      results.addError(String.format(ERR_MSG, value, maximum), MAXIMUM);
    }
  }

  public static JsonValidator create(ValidationContext<OAI3> context, JsonNode schemaNode, JsonNode schemaParentNode, SchemaValidator parentSchema) {
    return new MaximumToleranceValidator(context, schemaNode, schemaParentNode, parentSchema, 0.1);
  }
}
