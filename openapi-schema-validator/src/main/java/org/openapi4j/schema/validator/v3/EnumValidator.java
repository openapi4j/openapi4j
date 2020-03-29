package org.openapi4j.schema.validator.v3;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.NumericNode;
import org.openapi4j.core.model.v3.OAI3;
import org.openapi4j.core.validation.ValidationResult;
import org.openapi4j.core.validation.ValidationResults;
import org.openapi4j.schema.validator.BaseJsonValidator;
import org.openapi4j.schema.validator.ValidationContext;
import org.openapi4j.schema.validator.ValidationData;

import java.util.Comparator;

import static org.openapi4j.core.model.v3.OAI3SchemaKeywords.ENUM;
import static org.openapi4j.core.validation.ValidationSeverity.ERROR;

/**
 * enum keyword validator.
 * <p/>
 * <a href="https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.2.md#schemaObject" />
 * <p/>
 * <a href="https://tools.ietf.org/html/draft-wright-json-schema-validation-00#page-10" />
 */
class EnumValidator extends BaseJsonValidator<OAI3> {
  private static final ValidationResult ERR = new ValidationResult(ERROR, 1006, "Value '%s' is not defined in the schema.");

  private static final ValidationResults.CrumbInfo CRUMB_INFO = new ValidationResults.CrumbInfo(ENUM, true);

  private final JsonNode schemaNode;
  private static final NodeComparator NODE_COMPARATOR = new NodeComparator();

  EnumValidator(final ValidationContext<OAI3> context, final JsonNode schemaNode, final JsonNode schemaParentNode, final SchemaValidator parentSchema) {
    super(context, schemaNode, schemaParentNode, parentSchema);

    this.schemaNode = schemaNode;
  }

  @Override
  public boolean validate(final JsonNode valueNode, final ValidationData<?> validation) {
    if (schemaNode.isArray()) {
      for (JsonNode enumNode : schemaNode) {
        if (enumNode.equals(NODE_COMPARATOR, valueNode)) {
          return false;
        }
      }

      validation.add(CRUMB_INFO, ERR, valueNode.asText());
    }

    return false;
  }

  private static class NodeComparator implements Comparator<JsonNode> {
    @Override
    public int compare(JsonNode n1, JsonNode n2) {
      if (n1.equals(n2)) {
        return 0;
      }

      if ((n1 instanceof NumericNode) && (n2 instanceof NumericNode)) {
        Double d1 = n1.asDouble();
        Double d2 = n2.asDouble();
        if (d1.compareTo(d2) == 0) {
          return 0;
        }
      }
      return 1;
    }
  }
}
