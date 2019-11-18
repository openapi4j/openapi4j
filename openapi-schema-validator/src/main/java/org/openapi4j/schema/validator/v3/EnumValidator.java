package org.openapi4j.schema.validator.v3;

import com.fasterxml.jackson.databind.JsonNode;

import com.fasterxml.jackson.databind.node.NumericNode;
import org.openapi4j.core.model.v3.OAI3;
import org.openapi4j.core.validation.ValidationResults;
import org.openapi4j.schema.validator.BaseJsonValidator;
import org.openapi4j.schema.validator.ValidationContext;

import java.util.Comparator;

import static org.openapi4j.core.model.v3.OAI3SchemaKeywords.ENUM;

/**
 * enum keyword validator.
 * <p/>
 * <a href="https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.2.md#schemaObject" />
 * <p/>
 * <a href="https://tools.ietf.org/html/draft-wright-json-schema-validation-00#page-10" />
 */
class EnumValidator extends BaseJsonValidator<OAI3> {
  private static final String ERR_MSG = "Value '%s' is not defined in the schema.";

  private final JsonNode schemaNode;
  private static final NodeComparator NODE_COMPARATOR = new NodeComparator();

  static EnumValidator create(ValidationContext<OAI3> context, JsonNode schemaNode, JsonNode schemaParentNode, SchemaValidator parentSchema) {
    return new EnumValidator(context, schemaNode, schemaParentNode, parentSchema);
  }

  private EnumValidator(final ValidationContext<OAI3> context, final JsonNode schemaNode, final JsonNode schemaParentNode, final SchemaValidator parentSchema) {
    super(context, schemaNode, schemaParentNode, parentSchema);

    this.schemaNode = schemaNode;
  }

  @Override
  public void validate(final JsonNode valueNode, final ValidationResults results) {
    if (schemaNode.isArray()) {
      for (JsonNode enumNode : schemaNode) {
        if (enumNode.equals(NODE_COMPARATOR, valueNode)) {
          return;
        }
      }

      results.addError(String.format(ERR_MSG, valueNode.asText()), ENUM);
    }
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
