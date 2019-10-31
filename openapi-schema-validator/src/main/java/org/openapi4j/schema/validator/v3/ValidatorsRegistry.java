package org.openapi4j.schema.validator.v3;

import com.fasterxml.jackson.databind.JsonNode;

import org.openapi4j.core.model.v3.OAI3;
import org.openapi4j.core.model.v3.OAI3SchemaKeywords;
import org.openapi4j.schema.validator.JsonValidator;
import org.openapi4j.schema.validator.ValidationContext;
import org.openapi4j.schema.validator.util.ExtValidatorInstance;

import java.util.HashMap;
import java.util.Map;

/**
 * The registry of known keywords with associated validator.
 * <p>
 * Also, this class manages the replacement with additional validators if any.
 */
class ValidatorsRegistry {
  private static final ValidatorsRegistry INSTANCE = new ValidatorsRegistry();
  private final Map<String, Class<? extends JsonValidator<OAI3>>> validators = new HashMap<>();

  private ValidatorsRegistry() {
    // Keywords are not ported directly by validators
    // to allow breadcrumb flexibility (i.e. validator extensions)
    validators.put(OAI3SchemaKeywords.ADDITIONALPROPERTIES, AdditionalPropertiesValidator.class);
    validators.put(OAI3SchemaKeywords.ALLOF, AllOfValidator.class);
    validators.put(OAI3SchemaKeywords.ANYOF, AnyOfValidator.class);
    validators.put(OAI3SchemaKeywords.DEPENDENCIES, DependenciesValidator.class);
    validators.put(OAI3SchemaKeywords.ENUM, EnumValidator.class);
    validators.put(OAI3SchemaKeywords.FORMAT, FormatValidator.class);
    validators.put(OAI3SchemaKeywords.ITEMS, ItemsValidator.class);
    validators.put(OAI3SchemaKeywords.MAXIMUM, MaximumValidator.class);
    validators.put(OAI3SchemaKeywords.MAXITEMS, MaxItemsValidator.class);
    validators.put(OAI3SchemaKeywords.MAXLENGTH, MaxLengthValidator.class);
    validators.put(OAI3SchemaKeywords.MAXPROPERTIES, MaxPropertiesValidator.class);
    validators.put(OAI3SchemaKeywords.MINIMUM, MinimumValidator.class);
    validators.put(OAI3SchemaKeywords.MINITEMS, MinItemsValidator.class);
    validators.put(OAI3SchemaKeywords.MINLENGTH, MinLengthValidator.class);
    validators.put(OAI3SchemaKeywords.MINPROPERTIES, MinPropertiesValidator.class);
    validators.put(OAI3SchemaKeywords.MULTIPLEOF, MultipleOfValidator.class);
    validators.put(OAI3SchemaKeywords.NOT, NotValidator.class);
    validators.put(OAI3SchemaKeywords.NULLABLE, NullableValidator.class);
    validators.put(OAI3SchemaKeywords.ONEOF, OneOfValidator.class);
    validators.put(OAI3SchemaKeywords.PATTERN, PatternValidator.class);
    validators.put(OAI3SchemaKeywords.PATTERNPROPERTIES, PatternPropertiesValidator.class);
    validators.put(OAI3SchemaKeywords.PROPERTIES, PropertiesValidator.class);
    validators.put(OAI3SchemaKeywords.$REF, ReferenceValidator.class);
    validators.put(OAI3SchemaKeywords.REQUIRED, RequiredValidator.class);
    validators.put(OAI3SchemaKeywords.TYPE, TypeValidator.class);
    validators.put(OAI3SchemaKeywords.UNIQUEITEMS, UniqueItemsValidator.class);
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
   * @return The corresponding validator.
   */
  JsonValidator<OAI3> getValidator(final ValidationContext<OAI3> context,
                                   final String keyword,
                                   final JsonNode schemaNode,
                                   final JsonNode schemaParentNode,
                                   final SchemaValidator parentSchema) {

    ExtValidatorInstance<OAI3> evi = context.getValidators().get(keyword);

    return (evi == null)
      ? getDefaultValidator(context, keyword, schemaNode, schemaParentNode, parentSchema)
      : evi.apply(context, schemaNode, schemaParentNode, parentSchema);
  }

  private JsonValidator<OAI3> getDefaultValidator(final ValidationContext<OAI3> context,
                                                  final String keyword,
                                                  final JsonNode schemaNode,
                                                  final JsonNode schemaParentNode,
                                                  final SchemaValidator parentSchema) {

    final Class<? extends JsonValidator<OAI3>> validatorClass = validators.get(keyword);
    if (validatorClass == null) {
      return null;
    }

    if (validatorClass == AdditionalPropertiesValidator.class) {
      return new AdditionalPropertiesValidator(context, schemaNode, schemaParentNode, parentSchema);
    } else if (validatorClass == AllOfValidator.class) {
      return new AllOfValidator(context, schemaNode, schemaParentNode, parentSchema);
    } else if (validatorClass == AnyOfValidator.class) {
      return new AnyOfValidator(context, schemaNode, schemaParentNode, parentSchema);
    } else if (validatorClass == DependenciesValidator.class) {
      return new DependenciesValidator(context, schemaNode, schemaParentNode, parentSchema);
    } else if (validatorClass == EnumValidator.class) {
      return new EnumValidator(context, schemaNode, schemaParentNode, parentSchema);
    } else if (validatorClass == FormatValidator.class) {
      return new FormatValidator(context, schemaNode, schemaParentNode, parentSchema);
    } else if (validatorClass == ItemsValidator.class) {
      return new ItemsValidator(context, schemaNode, schemaParentNode, parentSchema);
    } else if (validatorClass == MaximumValidator.class) {
      return new MaximumValidator(context, schemaNode, schemaParentNode, parentSchema);
    } else if (validatorClass == MaxItemsValidator.class) {
      return new MaxItemsValidator(context, schemaNode, schemaParentNode, parentSchema);
    } else if (validatorClass == MaxLengthValidator.class) {
      return new MaxLengthValidator(context, schemaNode, schemaParentNode, parentSchema);
    } else if (validatorClass == MaxPropertiesValidator.class) {
      return new MaxPropertiesValidator(context, schemaNode, schemaParentNode, parentSchema);
    } else if (validatorClass == MinimumValidator.class) {
      return new MinimumValidator(context, schemaNode, schemaParentNode, parentSchema);
    } else if (validatorClass == MinItemsValidator.class) {
      return new MinItemsValidator(context, schemaNode, schemaParentNode, parentSchema);
    } else if (validatorClass == MinLengthValidator.class) {
      return new MinLengthValidator(context, schemaNode, schemaParentNode, parentSchema);
    } else if (validatorClass == MinPropertiesValidator.class) {
      return new MinPropertiesValidator(context, schemaNode, schemaParentNode, parentSchema);
    } else if (validatorClass == MultipleOfValidator.class) {
      return new MultipleOfValidator(context, schemaNode, schemaParentNode, parentSchema);
    } else if (validatorClass == NotValidator.class) {
      return new NotValidator(context, schemaNode, schemaParentNode, parentSchema);
    } else if (validatorClass == NullableValidator.class) {
      return new NullableValidator(context, schemaNode, schemaParentNode, parentSchema);
    } else if (validatorClass == OneOfValidator.class) {
      return new OneOfValidator(context, schemaNode, schemaParentNode, parentSchema);
    } else if (validatorClass == PatternValidator.class) {
      return new PatternValidator(context, schemaNode, schemaParentNode, parentSchema);
    } else if (validatorClass == PatternPropertiesValidator.class) {
      return new PatternPropertiesValidator(context, schemaNode, schemaParentNode, parentSchema);
    } else if (validatorClass == PropertiesValidator.class) {
      return new PropertiesValidator(context, schemaNode, schemaParentNode, parentSchema);
    } else if (validatorClass == ReferenceValidator.class) {
      return new ReferenceValidator(context, schemaNode, schemaParentNode, parentSchema);
    } else if (validatorClass == RequiredValidator.class) {
      return new RequiredValidator(context, schemaNode, schemaParentNode, parentSchema);
    } else if (validatorClass == TypeValidator.class) {
      return new TypeValidator(context, schemaNode, schemaParentNode, parentSchema);
    } else /*if (validatorClass == UniqueItemsValidator.class)*/ {
      return new UniqueItemsValidator(context, schemaNode, schemaParentNode, parentSchema);
    }
  }
}
