package org.openapi4j.schema.validator.v3;

import com.fasterxml.jackson.databind.JsonNode;
import org.openapi4j.core.model.v3.OAI3;
import org.openapi4j.schema.validator.JsonValidator;
import org.openapi4j.schema.validator.ValidationContext;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.openapi4j.core.model.v3.OAI3SchemaKeywords.*;

/**
 * The registry of known keywords with associated validator.
 * <p>
 * Also, this class manages additional validators if any.
 */
class ValidatorsRegistry {
  private static final ValidatorsRegistry INSTANCE = new ValidatorsRegistry();

  private ValidatorsRegistry() {
  }

  static ValidatorsRegistry instance() {
    return INSTANCE;
  }

  /**
   * Get the corresponding validators from the given keyword.
   * Core validator is always last in queue.
   *
   * @param context          The current validation context.
   * @param keyword          The given keyword.
   * @param schemaNode       The corresponding schema to validate against.
   * @param schemaParentNode The parent schema to validate against.
   * @param parentSchema     The corresponding schema to validate against.
   * @return The corresponding validators instances.
   */
  Collection<JsonValidator> getValidators(final ValidationContext<OAI3> context,
                                          final String keyword,
                                          final JsonNode schemaNode,
                                          final JsonNode schemaParentNode,
                                          final SchemaValidator parentSchema) {

    List<JsonValidator> validatorInstances = null;

    // Custom validators
    Collection<ValidatorInstance> additionalInstances = context.getValidators().get(keyword);
    // Core validator
    ValidatorInstance coreInstance = getCoreValidator(keyword);

    if (additionalInstances != null) {
      validatorInstances = new ArrayList<>();

      for (ValidatorInstance additionalInstance : additionalInstances) {
        validatorInstances.add(additionalInstance.apply(context, schemaNode, schemaParentNode, parentSchema));
      }

      if (coreInstance != null) {
        validatorInstances.add(coreInstance.apply(context, schemaNode, schemaParentNode, parentSchema));
      }
    } else if (coreInstance != null) {
      validatorInstances = new ArrayList<>();
      validatorInstances.add(coreInstance.apply(context, schemaNode, schemaParentNode, parentSchema));
    }

    return validatorInstances;
  }

  private ValidatorInstance getCoreValidator(final String keyword) {
    switch (keyword) {
      case ADDITIONALPROPERTIES:
        return AdditionalPropertiesValidator::new;
      case ALLOF:
        return AllOfValidator::new;
      case ANYOF:
        return AnyOfValidator::new;
      case DEPENDENCIES:
        return DependenciesValidator::new;
      case ENUM:
        return EnumValidator::new;
      case FORMAT:
        return FormatValidator::new;
      case ITEMS:
        return ItemsValidator::new;
      case MAXIMUM:
        return MaximumValidator::new;
      case MAXITEMS:
        return MaxItemsValidator::new;
      case MAXLENGTH:
        return MaxLengthValidator::new;
      case MAXPROPERTIES:
        return MaxPropertiesValidator::new;
      case MINIMUM:
        return MinimumValidator::new;
      case MINITEMS:
        return MinItemsValidator::new;
      case MINLENGTH:
        return MinLengthValidator::new;
      case MINPROPERTIES:
        return MinPropertiesValidator::new;
      case MULTIPLEOF:
        return MultipleOfValidator::new;
      case NOT:
        return NotValidator::new;
      case NULLABLE:
        return NullableValidator::new;
      case ONEOF:
        return OneOfValidator::new;
      case PATTERN:
        return PatternValidator::new;
      case PATTERNPROPERTIES:
        return PatternPropertiesValidator::new;
      case PROPERTIES:
        return PropertiesValidator::new;
      case $REF:
        return ReferenceValidator::new;
      case REQUIRED:
        return RequiredValidator::new;
      case TYPE:
        return TypeValidator::new;
      case UNIQUEITEMS:
        return UniqueItemsValidator::new;
      default:
        return null;
    }
  }
}
