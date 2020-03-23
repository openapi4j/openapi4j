package org.openapi4j.schema.validator.v3;

import com.fasterxml.jackson.databind.JsonNode;

import org.openapi4j.core.model.reference.Reference;
import org.openapi4j.core.model.v3.OAI3;
import org.openapi4j.core.model.v3.OAI3SchemaKeywords;
import org.openapi4j.core.validation.ValidationResult;
import org.openapi4j.core.validation.ValidationResults;
import org.openapi4j.schema.validator.BaseJsonValidator;
import org.openapi4j.schema.validator.ValidationContext;

import java.util.ArrayList;
import java.util.List;

import static org.openapi4j.core.model.reference.Reference.ABS_REF_FIELD;
import static org.openapi4j.core.model.v3.OAI3SchemaKeywords.ALLOF;
import static org.openapi4j.core.model.v3.OAI3SchemaKeywords.DISCRIMINATOR;
import static org.openapi4j.core.model.v3.OAI3SchemaKeywords.MAPPING;
import static org.openapi4j.core.model.v3.OAI3SchemaKeywords.PROPERTYNAME;
import static org.openapi4j.core.validation.ValidationSeverity.ERROR;

/**
 * discriminator keyword validator.
 * <p/>
 * <a href="https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.2.md#discriminatorObject" />
 */
abstract class DiscriminatorValidator extends BaseJsonValidator<OAI3> {
  private static final ValidationResult INVALID_SCHEMA_ERR = new ValidationResult(ERROR, 1003, "Schema selection can't be made for discriminator '%s'.");
  private static final ValidationResult INVALID_PROPERTY_ERR = new ValidationResult(ERROR, 1004, "Property name in schema is not set.");
  private static final ValidationResult INVALID_PROPERTY_CONTENT_ERR = new ValidationResult(ERROR, 1005, "Property name in content '%s' is not set.");

  private static final String SCHEMAS_PATH = "#/components/schemas/";

  final List<SchemaValidator> schemas = new ArrayList<>();
  private final String arrayType;
  private JsonNode discriminatorNode;
  private String discriminatorPropertyName;
  private JsonNode discriminatorMapping;

  DiscriminatorValidator(final ValidationContext<OAI3> context,
                         final JsonNode schemaNode,
                         final JsonNode schemaParentNode,
                         final SchemaValidator parentSchema,
                         final String arrayType) {

    super(context, schemaNode, schemaParentNode, parentSchema);

    this.arrayType = arrayType;

    // Setup discriminator behaviour for anyOf, oneOf or allOf
    setupDiscriminator(context, schemaNode, schemaParentNode, parentSchema);
  }

  @Override
  public boolean validate(final JsonNode valueNode, final ValidationResults results) {
    if (discriminatorNode != null) {
      if (ALLOF.equals(arrayType)) {
        validateAllOf(valueNode, results);
      } else {
        validateOneAnyOf(valueNode, results);
      }
    } else {
      validateWithoutDiscriminator(valueNode, results);
    }

    return false;
  }

  private void validateAllOf(final JsonNode valueNode, final ValidationResults results) {
    String discriminatorPropertyRefPath = getDiscriminatorPropertyRefPath(valueNode, results);
    if (discriminatorPropertyRefPath == null) {
      results.add(DISCRIMINATOR, INVALID_SCHEMA_ERR, discriminatorPropertyName);
      return;
    }

    validate(() -> {
      for (SchemaValidator schema : schemas) {
        schema.validateWithContext(valueNode, results);
      }
    });
  }

  private void validateOneAnyOf(final JsonNode valueNode, final ValidationResults results) {
    String discriminatorPropertyRefPath = getDiscriminatorPropertyRefPath(valueNode, results);
    if (discriminatorPropertyRefPath == null) {
      results.add(DISCRIMINATOR, INVALID_SCHEMA_ERR, discriminatorPropertyName);
      return;
    }

    for (SchemaValidator schema : schemas) {
      JsonNode refNode = schema.getSchemaNode().get(OAI3SchemaKeywords.$REF);
      if (discriminatorPropertyRefPath.equals(refNode.textValue())) {
        validate(() -> schema.validateWithContext(valueNode, results));
        return;
      }
    }

    results.add(DISCRIMINATOR, INVALID_SCHEMA_ERR, discriminatorPropertyName);
  }

  /**
   * Validate array keyword with default behaviour.
   *
   * @param valueNode The current value node.
   * @param results   The validation results.
   */
  abstract void validateWithoutDiscriminator(final JsonNode valueNode, final ValidationResults results);

  private void setupDiscriminator(final ValidationContext<OAI3> context,
                                  final JsonNode schemaNode,
                                  final JsonNode schemaParentNode,
                                  final SchemaValidator parentSchema) {

    if (ALLOF.equals(arrayType)) {
      setupAllOfDiscriminatorSchemas(context, schemaNode, schemaParentNode, parentSchema);
    } else {
      setupAnyOneOfDiscriminatorSchemas(context, schemaNode, schemaParentNode, parentSchema);
    }

    if (discriminatorNode != null) {
      JsonNode propertyNameNode = discriminatorNode.get(PROPERTYNAME);
      if (propertyNameNode == null) {
        // Property name in schema is not set. This will result in error on validate call.
        return;
      }

      discriminatorPropertyName = propertyNameNode.textValue();
      discriminatorMapping = discriminatorNode.get(MAPPING);
    }
  }

  private void setupAllOfDiscriminatorSchemas(final ValidationContext<OAI3> context,
                                              final JsonNode schemaNode,
                                              final JsonNode schemaParentNode,
                                              final SchemaValidator parentSchema) {

    JsonNode allOfNode = getParentSchemaNode().get(ALLOF);

    for (int index = 0; index < allOfNode.size(); ++index) {
      JsonNode allOfNodeItem = allOfNode.get(index);
      // Prefer absolute reference value
      List<JsonNode> refNodes = allOfNodeItem.findValues(ABS_REF_FIELD);
      if (refNodes.isEmpty()) {
        refNodes = allOfNodeItem.findValues(OAI3SchemaKeywords.$REF);
      }

      for (JsonNode refNode : refNodes) {
        Reference reference = context.getContext().getReferenceRegistry().getRef(refNode.textValue());
        discriminatorNode = reference.getContent().get(DISCRIMINATOR);
        if (discriminatorNode != null) {
          setupAllOfDiscriminatorSchemas(schemaNode, refNode, reference, schemaParentNode, parentSchema);
          return;
        }
      }
    }

    // Add default schemas
    int size = schemaNode.size();
    for (int i = 0; i < size; i++) {
      schemas.add(new SchemaValidator(context, arrayType, schemaNode.get(i), schemaParentNode, parentSchema, true));
    }
  }

  private void setupAnyOneOfDiscriminatorSchemas(final ValidationContext<OAI3> context,
                                                 final JsonNode schemaNode,
                                                 final JsonNode schemaParentNode,
                                                 final SchemaValidator parentSchema) {

    discriminatorNode = getParentSchemaNode().get(DISCRIMINATOR);

    int size = schemaNode.size();
    for (int i = 0; i < size; i++) {
      schemas.add(new SchemaValidator(context, arrayType, schemaNode.get(i), schemaParentNode, parentSchema, true));
    }
  }

  private void setupAllOfDiscriminatorSchemas(final JsonNode schemaNode,
                                              final JsonNode refNode,
                                              final Reference reference,
                                              final JsonNode schemaParentNode,
                                              final SchemaValidator parentSchema) {

    int size = schemaNode.size();
    for (int i = 0; i < size; i++) {
      JsonNode node = schemaNode.get(i);
      JsonNode refValueNode = node.get(OAI3SchemaKeywords.$REF);
      if (refNode.equals(refValueNode)) {
        // Add the parent schema
        schemas.add(new SchemaValidator(context, reference.getRef(), reference.getContent(), schemaParentNode, parentSchema, true));
      } else {
        // Add the other items
        schemas.add(new SchemaValidator(context, arrayType, node, schemaParentNode, parentSchema, true));
      }
    }
  }

  private String getDiscriminatorPropertyRefPath(final JsonNode valueNode, final ValidationResults results) {
    // check discriminator definition
    if (discriminatorPropertyName == null) {
      results.add(DISCRIMINATOR, INVALID_PROPERTY_ERR);
      return null;
    }
    // check discriminator in content
    JsonNode discriminatorPropertyNameNode = valueNode.get(discriminatorPropertyName);
    if (discriminatorPropertyNameNode == null) {
      results.add(DISCRIMINATOR, INVALID_PROPERTY_CONTENT_ERR, discriminatorPropertyName);
      return null;
    }

    String discriminatorPropertyValue = discriminatorPropertyNameNode.textValue();
    // "Shortcut / implicit" case, the value must match exactly one of the schemas name
    String ref = SCHEMAS_PATH + discriminatorPropertyValue;

    if (discriminatorMapping != null) {
      // "Mapping / explicit" case, find the corresponding reference
      JsonNode mappingNode = discriminatorMapping.get(discriminatorPropertyValue);
      if (mappingNode != null) {
        ref = mappingNode.textValue();
      }
    }

    // Check if Schema Object exists
    if (context.getContext().getReferenceRegistry().getRef(ref) != null) {
      return ref;
    } else {
      return null;
    }
  }
}
