package org.openapi4j.schema.validator.v3;

import com.fasterxml.jackson.databind.JsonNode;

import org.openapi4j.core.model.reference.Reference;
import org.openapi4j.core.model.v3.OAI3;
import org.openapi4j.core.model.v3.OAI3SchemaKeywords;
import org.openapi4j.core.util.Json;
import org.openapi4j.core.validation.ValidationResults;
import org.openapi4j.schema.validator.BaseJsonValidator;
import org.openapi4j.schema.validator.ValidationContext;

import java.util.ArrayList;
import java.util.List;

import static org.openapi4j.core.model.v3.OAI3SchemaKeywords.ALLOF;
import static org.openapi4j.core.model.v3.OAI3SchemaKeywords.DISCRIMINATOR;
import static org.openapi4j.core.model.v3.OAI3SchemaKeywords.MAPPING;
import static org.openapi4j.core.model.v3.OAI3SchemaKeywords.PROPERTYNAME;

/**
 * Discriminator validator.
 * <p/>
 * {@see https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.2.md#discriminatorObject}
 */
abstract class DiscriminatorValidator extends BaseJsonValidator<OAI3> {
  private static final String INVALID_SCHEMA = "Schema selection can't be made for discriminator '%s'.";
  private static final String INVALID_PROPERTY = "Property name in schema is not set.";
  private static final String INVALID_PROPERTY_CONTENT = "Property name in content '%s' is not set.";
  private static final String SCHEMAS_PATH = "#/components/schemas/";

  final List<SchemaValidator> schemas = new ArrayList<>();
  private final String arrayType;
  private JsonNode discriminatorNode;
  private String discriminatorPropertyName;
  private JsonNode discriminatorMapping;

  DiscriminatorValidator(
    final ValidationContext<OAI3> context,
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
  public void validate(final JsonNode valueNode, final ValidationResults results) {
    if (discriminatorNode != null) {
      if (ALLOF.equals(arrayType)) {
        validateAllOf(valueNode, results);
      } else {
        validateOneAnyOf(valueNode, results);
      }
    } else {
      validateWithoutDiscriminator(valueNode, results);
    }
  }

  private void validateAllOf(final JsonNode valueNode, final ValidationResults results) {
    String discriminatorPropertyRefPath = getDiscriminatorPropertyRefPath(valueNode, results);
    if (discriminatorPropertyRefPath == null)
      return;

    for (SchemaValidator schema : schemas) {
      schema.validate(valueNode, results);
    }
  }

  private void validateOneAnyOf(final JsonNode valueNode, final ValidationResults results) {
    String discriminatorPropertyRefPath = getDiscriminatorPropertyRefPath(valueNode, results);
    if (discriminatorPropertyRefPath == null)
      return;

    for (SchemaValidator schema : schemas) {
      JsonNode refNode = schema.getSchemaNode().get(OAI3SchemaKeywords.$REF);
      if (refNode != null) { // inline schemas are not considered
        if (discriminatorPropertyRefPath.equals(refNode.textValue())) {
          schema.validate(valueNode, results);
          return;
        }
      }
    }

    results.addError(String.format(INVALID_SCHEMA, discriminatorPropertyRefPath), DISCRIMINATOR);
  }

  /**
   * Validate array keyword with default behaviour.
   *
   * @param valueNode The current value node.
   * @param results   The validation results.
   */
  abstract void validateWithoutDiscriminator(final JsonNode valueNode, final ValidationResults results);

  private void setupDiscriminator(
    final ValidationContext<OAI3> context,
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

  private void setupAllOfDiscriminatorSchemas(
    final ValidationContext<OAI3> context,
    final JsonNode schemaNode,
    final JsonNode schemaParentNode,
    final SchemaValidator parentSchema) {

    JsonNode allOfNode = getParentSchemaNode().get(ALLOF);

    for (int index = 0; index < allOfNode.size(); ++index) {
      for (JsonNode refNode : allOfNode.get(index).findValues(OAI3SchemaKeywords.$REF)) {
        Reference reference = context.getContext().getReferenceRegistry().getRef(refNode.textValue());
        if (reference == null) {
          schemas.add(new SchemaValidator(
            context,
            refNode.textValue(),
            Json.jsonMapper.createObjectNode().put(OAI3SchemaKeywords.$REF, refNode.textValue()), schemaParentNode, parentSchema));
          return;
        }

        discriminatorNode = reference.getContent().get(DISCRIMINATOR);
        if (discriminatorNode != null) {
          int size = schemaNode.size();
          for (int i = 0; i < size; i++) {
            JsonNode node = schemaNode.get(i);
            JsonNode refValueNode = node.get(OAI3SchemaKeywords.$REF);
            if (refValueNode != null && refValueNode.isTextual() && refValueNode.textValue().equals(refNode.textValue())) {
              // Add the parent schema
              schemas.add(new SchemaValidator(context, reference.getRef(), reference.getContent(), schemaParentNode, parentSchema));
            } else {
              // Add the other items
              schemas.add(new SchemaValidator(context, arrayType, node, schemaParentNode, parentSchema));
            }
          }
          return;
        }
      }
    }

    // Add default schemas
    int size = schemaNode.size();
    for (int i = 0; i < size; i++) {
      schemas.add(new SchemaValidator(context, arrayType, schemaNode.get(i), schemaParentNode, parentSchema));
    }
  }

  private void setupAnyOneOfDiscriminatorSchemas(
    final ValidationContext<OAI3> context,
    final JsonNode schemaNode,
    final JsonNode schemaParentNode,
    final SchemaValidator parentSchema) {

    discriminatorNode = getParentSchemaNode().get(DISCRIMINATOR);

    int size = schemaNode.size();
    for (int i = 0; i < size; i++) {
      schemas.add(new SchemaValidator(context, arrayType, schemaNode.get(i), schemaParentNode, parentSchema));
    }
  }

  private String getDiscriminatorPropertyRefPath(final JsonNode valueNode, final ValidationResults results) {
    // check discriminator definition
    if (discriminatorPropertyName == null) {
      results.addError(INVALID_PROPERTY, DISCRIMINATOR);
      return null;
    }
    // check discriminator in content
    JsonNode discriminatorPropertyNameNode = valueNode.get(discriminatorPropertyName);
    if (discriminatorPropertyNameNode == null) {
      results.addError(String.format(INVALID_PROPERTY_CONTENT, discriminatorPropertyName), DISCRIMINATOR);
      return null;
    }

    String discriminatorPropertyValue = discriminatorPropertyNameNode.textValue();

    if (discriminatorMapping != null) {
      // "Mapping / explicit" case, find the corresponding reference
      JsonNode mappingNode = discriminatorMapping.get(discriminatorPropertyValue);
      if (mappingNode != null) {
        String discriminatorPropertyRefPath = mappingNode.textValue();
        if (discriminatorPropertyRefPath.startsWith("#")) {
          return mappingNode.textValue();
        } else {
          return SCHEMAS_PATH + mappingNode.textValue();
        }
      }
    }

    // "Shortcut / implicit" case, the value must match exactly one of the schemas name
    return SCHEMAS_PATH + discriminatorPropertyValue;
  }
}
