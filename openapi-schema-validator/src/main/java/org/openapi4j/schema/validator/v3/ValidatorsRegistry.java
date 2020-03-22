package org.openapi4j.schema.validator.v3;

import com.fasterxml.jackson.databind.JsonNode;
import org.openapi4j.core.model.v3.OAI3;
import org.openapi4j.core.model.v3.OAI3SchemaKeywords;
import org.openapi4j.schema.validator.JsonValidator;
import org.openapi4j.schema.validator.ValidationContext;

import java.util.*;

/**
 * The registry of known keywords with associated validator.
 * <p>
 * Also, this class manages the replacement with additional validators if any.
 */
class ValidatorsRegistry {
  private static final ValidatorsRegistry INSTANCE = new ValidatorsRegistry();
  private final Map<String, ValidatorInstance> validators = new HashMap<>();

  private ValidatorsRegistry() {
    // Keywords are not ported directly by validators
    // to allow breadcrumb flexibility (i.e. validator extensions)
    validators.put(OAI3SchemaKeywords.ADDITIONALPROPERTIES, AdditionalPropertiesValidator::create);
    validators.put(OAI3SchemaKeywords.ALLOF, AllOfValidator::create);
    validators.put(OAI3SchemaKeywords.ANYOF, AnyOfValidator::create);
    validators.put(OAI3SchemaKeywords.DEPENDENCIES, DependenciesValidator::create);
    validators.put(OAI3SchemaKeywords.ENUM, EnumValidator::create);
    validators.put(OAI3SchemaKeywords.FORMAT, FormatValidator::create);
    validators.put(OAI3SchemaKeywords.ITEMS, ItemsValidator::create);
    validators.put(OAI3SchemaKeywords.MAXIMUM, MaximumValidator::create);
    validators.put(OAI3SchemaKeywords.MAXITEMS, MaxItemsValidator::create);
    validators.put(OAI3SchemaKeywords.MAXLENGTH, MaxLengthValidator::create);
    validators.put(OAI3SchemaKeywords.MAXPROPERTIES, MaxPropertiesValidator::create);
    validators.put(OAI3SchemaKeywords.MINIMUM, MinimumValidator::create);
    validators.put(OAI3SchemaKeywords.MINITEMS, MinItemsValidator::create);
    validators.put(OAI3SchemaKeywords.MINLENGTH, MinLengthValidator::create);
    validators.put(OAI3SchemaKeywords.MINPROPERTIES, MinPropertiesValidator::create);
    validators.put(OAI3SchemaKeywords.MULTIPLEOF, MultipleOfValidator::create);
    validators.put(OAI3SchemaKeywords.NOT, NotValidator::create);
    validators.put(OAI3SchemaKeywords.NULLABLE, NullableValidator::create);
    validators.put(OAI3SchemaKeywords.ONEOF, OneOfValidator::create);
    validators.put(OAI3SchemaKeywords.PATTERN, PatternValidator::create);
    validators.put(OAI3SchemaKeywords.PATTERNPROPERTIES, PatternPropertiesValidator::create);
    validators.put(OAI3SchemaKeywords.PROPERTIES, PropertiesValidator::create);
    validators.put(OAI3SchemaKeywords.$REF, ReferenceValidator::create);
    validators.put(OAI3SchemaKeywords.REQUIRED, RequiredValidator::create);
    validators.put(OAI3SchemaKeywords.TYPE, TypeValidator::create);
    validators.put(OAI3SchemaKeywords.UNIQUEITEMS, UniqueItemsValidator::create);
  }

  static ValidatorsRegistry instance() {
    return INSTANCE;
  }

  /**
   * Get the corresponding validator from the given keyword.
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

    // Custom or override validators
    Collection<ValidatorInstance> cvi = context.getValidators().get(keyword);
    if (cvi != null) {
      validatorInstances = new ArrayList<>();

      for (ValidatorInstance validatorInstance : cvi) {
        validatorInstances.add(validatorInstance.apply(context, schemaNode, schemaParentNode, parentSchema));
      }
    }

    // Core validator
    ValidatorInstance coreValidatorInstance = validators.get(keyword);
    if (coreValidatorInstance != null) {
      if (validatorInstances == null) {
        validatorInstances = new ArrayList<>();
      }
      validatorInstances.add(coreValidatorInstance.apply(context, schemaNode, schemaParentNode, parentSchema));
    }

    return validatorInstances;
  }
}
