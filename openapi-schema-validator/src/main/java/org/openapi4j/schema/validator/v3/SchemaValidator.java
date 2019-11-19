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

  private final String propertyName;
  private final Map<String, JsonValidator> validators;

  /**
   * Create a new Schema validator this default values.
   * A new context will be created.
   *
   * @param propertyName The property or root name of the schema.
   * @param schemaNode   The schema specification.
   * @throws ResolutionException for wrong references.
   */
  public SchemaValidator(final String propertyName, final JsonNode schemaNode) throws ResolutionException {
    super(null, schemaNode, null, null);

    OAI3Context apiContext = new OAI3Context(URI.create("/"), schemaNode);
    this.context = new ValidationContext<>(apiContext);

    this.propertyName = propertyName;
    validators = read(this.context, schemaNode);
  }

  /**
   * Create a new Schema validator with the given context.
   *
   * @param context      The context to use for validation.
   * @param propertyName The property or root name of the schema.
   * @param schemaNode   The schema specification.
   */
  public SchemaValidator(final ValidationContext<OAI3> context,
                         final String propertyName,
                         final JsonNode schemaNode) {

    this(context, propertyName, schemaNode, null, null);
  }

  /**
   * Create a new Schema validator with the given context.
   *
   * @param context          The context to use for validation.
   * @param propertyName     The property or root name of the schema.
   * @param schemaNode       The schema specification.
   * @param schemaParentNode The tree node of the parent schema.
   * @param parentSchema     The parent schema model.
   */
  public SchemaValidator(final ValidationContext<OAI3> context,
                         final String propertyName,
                         final JsonNode schemaNode,
                         final JsonNode schemaParentNode,
                         final SchemaValidator parentSchema) {

    super(context, schemaNode, schemaParentNode, parentSchema);

    this.propertyName = propertyName;
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
  public void validate(final JsonNode valueNode, final ValidationResults results) {
    try {
      validateWithContext(valueNode, results);
    } catch (ValidationException ignored) {
      // results are already populated
    }
  }

  final void validateWithContext(final JsonNode valueNode, final ValidationResults results) throws ValidationException {
    if (context.isFastFail()) {
      fastFailValidate(valueNode, results);
    } else {
      defaultValidate(valueNode, results);
    }
  }

  private void fastFailValidate(final JsonNode valueNode, final ValidationResults results) throws ValidationException {
    results.withCrumb(propertyName, () -> {
      for (JsonValidator validator : validators.values()) {
        validator.validate(valueNode, results);

        if (!results.isValid()) {
          return;
        }
      }
    });

    if (!results.isValid()) {
      throw new ValidationException(null, results);
    }
  }

  private void defaultValidate(final JsonNode valueNode, final ValidationResults results) {
    results.withCrumb(propertyName, () -> {
      for (JsonValidator validator : validators.values()) {
        validator.validate(valueNode, results);
      }
    });
  }

  /**
   * Read the schema and create dedicated validators from keywords.
   */
  private Map<String, JsonValidator> read(final ValidationContext<OAI3> context, final JsonNode schemaNode) {
    Map<String, JsonValidator> validatorMap = new HashMap<>();

    Iterator<String> fieldNames = schemaNode.fieldNames();
    while (fieldNames.hasNext()) {
      final String keyword = fieldNames.next();
      final JsonNode keywordSchemaNode = schemaNode.get(keyword);

      JsonValidator validator = ValidatorsRegistry.instance().getValidator(context, keyword, keywordSchemaNode, schemaNode, this);
      if (validator != null) {
        validatorMap.put(keyword, validator);
      }
    }

    if (validatorMap.get(ADDITIONALPROPERTIES) == null && context.getOption(ADDITIONAL_PROPS_RESTRICT)) {
      validatorMap.put(
        ADDITIONALPROPERTIES,
        ValidatorsRegistry.instance().getValidator(context, ADDITIONALPROPERTIES, FALSE_NODE, schemaNode, this));
    }

    // Setup default nullable schema to false
    // https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.2.md#schemaNullable
    validatorMap.computeIfAbsent(
      NULLABLE,
      s -> ValidatorsRegistry.instance().getValidator(context, s, FALSE_NODE, schemaNode, this));

    return validatorMap;
  }
}
