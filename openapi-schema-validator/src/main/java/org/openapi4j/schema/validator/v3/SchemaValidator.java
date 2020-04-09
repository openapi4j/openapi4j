package org.openapi4j.schema.validator.v3;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import org.openapi4j.core.exception.ResolutionException;
import org.openapi4j.core.model.v3.OAI3;
import org.openapi4j.core.model.v3.OAI3Context;
import org.openapi4j.core.validation.ValidationException;
import org.openapi4j.core.validation.ValidationResults;
import org.openapi4j.schema.validator.BaseJsonValidator;
import org.openapi4j.schema.validator.JsonValidator;
import org.openapi4j.schema.validator.ValidationContext;
import org.openapi4j.schema.validator.ValidationData;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static org.openapi4j.core.model.v3.OAI3SchemaKeywords.*;
import static org.openapi4j.schema.validator.v3.ValidationOptions.ADDITIONAL_PROPS_RESTRICT;

/**
 * Schema validation implementation.
 * This is the entry point of all validators.
 */
public class SchemaValidator extends BaseJsonValidator<OAI3> {
  private static final JsonNode FALSE_NODE = JsonNodeFactory.instance.booleanNode(false);

  private final ValidationResults.CrumbInfo crumbInfo;
  private final Map<String, Collection<JsonValidator>> validators;

  /**
   * Create a new Schema Object validator.
   * A new context will be created with 'file:/' as base URL.
   *
   * @param propertyName The property or root name of the schema. Can be {@code null}.
   * @param schemaNode   The schema specification.
   * @throws ResolutionException for wrong references.
   */
  public SchemaValidator(final String propertyName, final JsonNode schemaNode) throws ResolutionException {
    this(
      new ValidationContext<>(new OAI3Context(getDefaultBaseUrl(), schemaNode)),
      new ValidationResults.CrumbInfo(propertyName, false),
      schemaNode, null, null);
  }

  /**
   * Create a new Schema Object validator with the given context.
   * Warning: {@code schemaNode} must be associated with {@code context} via
   * {@code new OAI3Context(URL, schemaNode)} in case of JSON-references.
   *
   * @param context      The context to use for validation.
   * @param propertyName The property or root name of the schema. Can be {@code null}.
   * @param schemaNode   The schema specification.
   */
  public SchemaValidator(final ValidationContext<OAI3> context,
                         final String propertyName,
                         final JsonNode schemaNode) {

    this(context, new ValidationResults.CrumbInfo(propertyName, false), schemaNode, null, null);
  }

  /**
   * Create a new Schema Object validator with the given context.
   *
   * @param context          The context to use for validation.
   * @param crumbInfo        The property or root name of the schema.
   * @param schemaNode       The schema specification.
   * @param schemaParentNode The tree node of the parent schema.
   * @param parentSchema     The parent schema model.
   */
  SchemaValidator(final ValidationContext<OAI3> context,
                  final ValidationResults.CrumbInfo crumbInfo,
                  final JsonNode schemaNode,
                  final JsonNode schemaParentNode,
                  final SchemaValidator parentSchema) {

    super(context, schemaNode, schemaParentNode, parentSchema);

    this.crumbInfo = crumbInfo;
    validators = read(this.context, schemaNode);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean validate(final JsonNode valueNode, final ValidationData<?> validation) {
    try {
      validateWithContext(valueNode, validation);
    } catch (ValidationException ignored) {
      // results are already populated
    }

    return true;
  }

  /**
   * Get the context of validation.
   */
  public ValidationContext<OAI3> getContext() {
    return context;
  }

  SchemaValidator findParent() {
    return (getParentSchema() != null) ? getParentSchema().findParent() : this;
  }

  final void validateWithContext(JsonNode valueNode, final ValidationData<?> validation) throws ValidationException {
    if (valueNode == null) {
      valueNode = JsonNodeFactory.instance.nullNode();
    }

    if (context.isFastFail()) {
      fastFailValidate(valueNode, validation);
    } else {
      defaultValidate(valueNode, validation);
    }
  }

  private void fastFailValidate(final JsonNode valueNode, final ValidationData<?> validation) throws ValidationException {
    validation.results().withCrumb(crumbInfo, () -> {
      for (Collection<JsonValidator> keywordValidators : validators.values()) {
        for (JsonValidator validator : keywordValidators) {
          boolean shouldChain = validator.validate(valueNode, validation);

          if (!validation.isValid()) {
            return;
          }

          if (!shouldChain) {
            break;
          }
        }
      }
    });

    if (!validation.isValid()) {
      throw new ValidationException(null, validation.results());
    }
  }

  private void defaultValidate(final JsonNode valueNode, final ValidationData<?> validation) {
    validation.results().withCrumb(crumbInfo, () -> {
      for (Collection<JsonValidator> keywordValidators : validators.values()) {
        for (JsonValidator validator : keywordValidators) {
          if (!validator.validate(valueNode, validation)) {
            break;
          }
        }
      }
    });
  }

  /**
   * Read the schema and create dedicated validators from keywords.
   */
  private Map<String, Collection<JsonValidator>> read(final ValidationContext<OAI3> context, final JsonNode schemaNode) {
    Map<String, Collection<JsonValidator>> validatorMap = new HashMap<>();

    Iterator<String> fieldNames = schemaNode.fieldNames();
    while (fieldNames.hasNext()) {
      final String keyword = fieldNames.next();
      final JsonNode keywordSchemaNode = schemaNode.get(keyword);

      Collection<JsonValidator> keywordValidators = ValidatorsRegistry.instance().getValidators(context, keyword, keywordSchemaNode, schemaNode, this);
      if (keywordValidators != null) {
        validatorMap.put(keyword, keywordValidators);
      }
    }

    applyAdditionalValidators(validatorMap, schemaNode);

    return validatorMap;
  }

  private void applyAdditionalValidators(final Map<String, Collection<JsonValidator>> validatorMap,
                                         final JsonNode schemaNode) {

    // Apply additional validators if not JSON-Reference
    if (validatorMap.containsKey($REF)) {
      return;
    }

    // Setup optional restriction for additional properties
    if (validatorMap.get(ADDITIONALPROPERTIES) == null && context.getOption(ADDITIONAL_PROPS_RESTRICT)) {
      validatorMap.put(
        ADDITIONALPROPERTIES,
        ValidatorsRegistry.instance().getValidators(context, ADDITIONALPROPERTIES, FALSE_NODE, schemaNode, this));
    }

    // Setup default nullable schema to false
    // https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.2.md#schemaNullable
    validatorMap.computeIfAbsent(
      NULLABLE,
      s -> ValidatorsRegistry.instance().getValidators(context, s, FALSE_NODE, schemaNode, this));
  }

  /**
   * Make quietly a default base URL for context.
   *
   * @return default base URL for context.
   */
  private static URL getDefaultBaseUrl() {
    try {
      return new URL("file:/");
    } catch (MalformedURLException ignored) {
      // Will never happen
      return null;
    }
  }
}
