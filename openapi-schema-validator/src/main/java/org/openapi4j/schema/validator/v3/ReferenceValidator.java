package org.openapi4j.schema.validator.v3;

import com.fasterxml.jackson.databind.JsonNode;

import org.openapi4j.core.model.reference.Reference;
import org.openapi4j.core.model.v3.OAI3;
import org.openapi4j.core.validation.ValidationResults;
import org.openapi4j.schema.validator.BaseJsonValidator;
import org.openapi4j.schema.validator.JsonValidator;
import org.openapi4j.schema.validator.ValidationContext;

/**
 * JSON Reference Schema Object validator.
 * This validator is not truly a keyword validator,
 * it only traverse all the references to reach known keywords.
 * <p>
 * Reaching a validator is guaranteed by the
 * JSON reference registry of the context which is our guard for correct definitions.
 */
class ReferenceValidator extends BaseJsonValidator<OAI3> {
  private static final String HASH = "#";

  private final String refValue;
  private JsonValidator schemaValidator;

  static ReferenceValidator create(ValidationContext<OAI3> context, JsonNode schemaNode, JsonNode schemaParentNode, SchemaValidator parentSchema) {
    return new ReferenceValidator(context, schemaNode, schemaParentNode, parentSchema);
  }

  private ReferenceValidator(final ValidationContext<OAI3> context, final JsonNode schemaNode, final JsonNode schemaParentNode, final SchemaValidator parentSchema) {
    super(context, schemaNode, schemaParentNode, parentSchema);

    refValue = schemaNode.textValue();
    if (HASH.equals(refValue)) {
      schemaValidator = parentSchema.findParent();
    } else {
      Reference reference = context.getContext().getReferenceRegistry().getRef(refValue);
      // Check visited references to avoid infinite loops
      JsonValidator validator = context.getReference(refValue);
      if (validator == null) {
        ReferenceValidator refValidator = new ReferenceValidator(context, refValue, schemaNode, schemaParentNode, parentSchema);
        context.addReference(refValue, refValidator);
        refValidator.setSchemaValidator(new SchemaValidator(context, refValue, reference.getContent(), schemaParentNode, parentSchema));
        schemaValidator = refValidator;
      }
    }
  }

  /**
   * private constructor to avoid infinite JSON Reference looping
   *
   * @param context          The current validation context
   * @param refValue         The JSON Reference string to store
   * @param schemaNode       The Schema Object node
   * @param schemaParentNode The parent Schema Object node
   * @param parentSchema     The parent Schema Object
   */
  private ReferenceValidator(
    final ValidationContext<OAI3> context,
    final String refValue,
    final JsonNode schemaNode,
    final JsonNode schemaParentNode,
    final SchemaValidator parentSchema) {
    super(context, schemaNode, schemaParentNode, parentSchema);

    this.refValue = refValue;
  }

  private void setSchemaValidator(JsonValidator schemaValidator) {
    this.schemaValidator = schemaValidator;
  }

  @Override
  public void validate(final JsonNode valueNode, final ValidationResults results) {
    schemaValidator.validate(valueNode, results);
  }
}
