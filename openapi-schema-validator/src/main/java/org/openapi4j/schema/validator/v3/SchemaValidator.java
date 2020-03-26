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

import java.net.URI;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static org.openapi4j.core.model.v3.OAI3SchemaKeywords.ADDITIONALPROPERTIES;
import static org.openapi4j.core.model.v3.OAI3SchemaKeywords.NULLABLE;
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
   * A new context will be created with '/' as base URI.
   *
   * @param propertyName The property or root name of the schema.
   * @param schemaNode   The schema specification.
   * @throws ResolutionException for wrong references.
   */
  public SchemaValidator(final String propertyName, final JsonNode schemaNode) throws ResolutionException {
    this(
      new ValidationContext<>(new OAI3Context(URI.create("/"), schemaNode)),
      new ValidationResults.CrumbInfo(propertyName, false),
      schemaNode, null, null);
  }

  /**
   * Create a new Schema Object validator with the given context.
   * {@code schemaNode} should be populated from {@code context} if you have JSON-references
   * as references are searched with absolute values.
   *
   * @param context      The context to use for validation.
   * @param propertyName The property or root name of the schema.
   * @param schemaNode   The schema specification.
   */
  public SchemaValidator(final ValidationContext<OAI3> context,
                         final String propertyName,
                         final JsonNode schemaNode) {

    this(context, new ValidationResults.CrumbInfo(propertyName, false), schemaNode, null, null);
  }

  /**
   * Create a new Schema Object validator with the given context.
   * {@code schemaNode} should be populated from {@code context} if you have JSON-references
   * as references are searched with absolute values.
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
   * Get the context of validation.
   */
  public ValidationContext<OAI3> getContext() {
    return context;
  }

  SchemaValidator findParent() {
    return (getParentSchema() != null) ? getParentSchema().findParent() : this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean validate(final JsonNode valueNode, final ValidationResults results) {
    try {
      validateWithContext(valueNode, results);
    } catch (ValidationException ignored) {
      // results are already populated
    }

    return true;
  }

  final void validateWithContext(JsonNode valueNode, final ValidationResults results) throws ValidationException {
    if (valueNode == null) {
      valueNode = JsonNodeFactory.instance.nullNode();
    }

    if (context.isFastFail()) {
      fastFailValidate(valueNode, results);
    } else {
      defaultValidate(valueNode, results);
    }
  }

  private void fastFailValidate(final JsonNode valueNode, final ValidationResults results) throws ValidationException {
    results.withCrumb(crumbInfo, () -> {
      for (Collection<JsonValidator> keywordValidators : validators.values()) {
        for (JsonValidator validator : keywordValidators) {
          boolean shouldChain = validator.validate(valueNode, results);

          if (!results.isValid()) {
            return;
          }

          if (!shouldChain) {
            break;
          }
        }
      }
    });

    if (!results.isValid()) {
      throw new ValidationException(null, results);
    }
  }

  private void defaultValidate(final JsonNode valueNode, final ValidationResults results) {
    results.withCrumb(crumbInfo, () -> {
      for (Collection<JsonValidator> keywordValidators : validators.values()) {
        for (JsonValidator validator : keywordValidators) {
          if (!validator.validate(valueNode, results)) {
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

    return validatorMap;
  }
}
