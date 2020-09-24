package org.openapi4j.schema.validator.common;

import com.fasterxml.jackson.databind.JsonNode;
import org.openapi4j.core.model.reference.Reference;
import org.openapi4j.core.model.v3.OAI3;
import org.openapi4j.core.validation.ValidationResults;

import static org.openapi4j.core.model.reference.Reference.ABS_REF_FIELD;

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

  ReferenceValidator(final ValidationContext<OAI3> context,
                     final JsonNode schemaNode,
                     final JsonNode schemaParentNode,
                     final SchemaValidator parentSchema) {

    super(context, schemaNode, schemaParentNode, parentSchema);

    if (HASH.equals(schemaNode.textValue())) {
      refValue = schemaNode.textValue();
      schemaValidator = parentSchema.findParent();
    } else {
      // Prefer absolute reference value
      JsonNode refNode = schemaParentNode.has(ABS_REF_FIELD) ? schemaParentNode.get(ABS_REF_FIELD) : schemaNode;
      refValue = refNode.textValue();
      Reference reference = context.getContext().getReferenceRegistry().getRef(refValue);
      // Check visited references to break infinite looping
      JsonValidator validator = context.getReference(refValue);
      if (validator == null) {
        ReferenceValidator refValidator = new ReferenceValidator(context, refValue, schemaNode, schemaParentNode, parentSchema);
        refValidator.setSchemaValidator(new SchemaValidator(context, new ValidationResults.CrumbInfo(schemaNode.textValue(), true), reference.getContent(), schemaParentNode, parentSchema));
        schemaValidator = refValidator;
      } else {
        schemaValidator = validator;
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
  private ReferenceValidator(final ValidationContext<OAI3> context,
                             final String refValue,
                             final JsonNode schemaNode,
                             final JsonNode schemaParentNode,
                             final SchemaValidator parentSchema) {

    super(context, schemaNode, schemaParentNode, parentSchema);

    this.refValue = refValue;
    context.addReference(refValue, this);
  }

  private void setSchemaValidator(JsonValidator schemaValidator) {
    this.schemaValidator = schemaValidator;
  }

  @Override
  public boolean validate(final JsonNode valueNode, final ValidationData<?> validation) {
    schemaValidator.validate(valueNode, validation);

    return false;
  }
}
