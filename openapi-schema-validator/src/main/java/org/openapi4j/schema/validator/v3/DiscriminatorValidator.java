package org.openapi4j.schema.validator.v3;

import com.fasterxml.jackson.databind.JsonNode;
import org.openapi4j.core.model.reference.Reference;
import org.openapi4j.core.model.reference.ReferenceRegistry;
import org.openapi4j.core.model.v3.OAI3;
import org.openapi4j.core.model.v3.OAI3SchemaKeywords;
import org.openapi4j.core.validation.ValidationResult;
import org.openapi4j.core.validation.ValidationResults;
import org.openapi4j.schema.validator.BaseJsonValidator;
import org.openapi4j.schema.validator.ValidationContext;
import org.openapi4j.schema.validator.ValidationData;

import java.util.ArrayList;
import java.util.List;

import static org.openapi4j.core.model.reference.Reference.ABS_REF_FIELD;
import static org.openapi4j.core.model.v3.OAI3SchemaKeywords.*;
import static org.openapi4j.core.validation.ValidationSeverity.ERROR;

/**
 * discriminator keyword validator.
 * <p/>
 * <a href="https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.2.md#discriminatorObject" />
 */
abstract class DiscriminatorValidator<V> extends BaseJsonValidator<OAI3, V> {
  private static final ValidationResult INVALID_SCHEMA_ERR = new ValidationResult(ERROR, 1003, "Schema selection can't be made for discriminator '%s'.");
  private static final ValidationResult INVALID_PROPERTY_ERR = new ValidationResult(ERROR, 1004, "Property name in schema is not set.");
  private static final ValidationResult INVALID_PROPERTY_CONTENT_ERR = new ValidationResult(ERROR, 1005, "Property name in content '%s' is not set.");

  private static final ValidationResults.CrumbInfo CRUMB_INFO = new ValidationResults.CrumbInfo(DISCRIMINATOR, true);

  private static final String SCHEMAS_PATH = "#/components/schemas/";

  final List<SchemaValidator<V>> schemas = new ArrayList<>();
  private final String arrayType;
  private JsonNode discriminatorNode;
  private String discriminatorPropertyName;
  private JsonNode discriminatorMapping;
  private final ValidationResults.CrumbInfo crumbInfo;

  DiscriminatorValidator(final ValidationContext<OAI3, V> context,
                         final JsonNode schemaNode,
                         final JsonNode schemaParentNode,
                         final SchemaValidator<V> parentSchema,
                         final String arrayType) {

    super(context, schemaNode, schemaParentNode, parentSchema);

    this.arrayType = arrayType;
    crumbInfo = new ValidationResults.CrumbInfo(arrayType, true);

    // Setup discriminator behaviour for anyOf, oneOf or allOf
    setupDiscriminator(context, schemaNode, schemaParentNode, parentSchema);
  }

  @Override
  public boolean validate(final JsonNode valueNode, final ValidationData<V> validation) {
    if (discriminatorNode != null) {
      String discriminatorPropertyRefPath = getDiscriminatorPropertyRefPath(valueNode, validation);
      if (discriminatorPropertyRefPath == null) {
        validation.add(CRUMB_INFO, INVALID_SCHEMA_ERR, discriminatorPropertyName);
        return false;
      }

      if (ALLOF.equals(arrayType)) {
        validateAllOf(valueNode, validation);
      } else {
        validateOneAnyOf(valueNode, discriminatorPropertyRefPath, validation);
      }
    } else {
      validateWithoutDiscriminator(valueNode, validation);
    }

    return false;
  }

  private void validateAllOf(final JsonNode valueNode, final ValidationData<V> validation) {
    validate(() -> {
      for (SchemaValidator<V> schema : schemas) {
        schema.validateWithContext(valueNode, validation);
      }
    });
  }

  private void validateOneAnyOf(final JsonNode valueNode, final String discriminatorPropertyRefPath, final ValidationData<V> validation) {
    for (SchemaValidator<V> schema : schemas) {
      JsonNode refNode = schema.getSchemaNode().get(OAI3SchemaKeywords.$REF);
      if (discriminatorPropertyRefPath.equals(refNode.textValue())) {
        validate(() -> schema.validateWithContext(valueNode, validation));
        return;
      }
    }

    validation.add(CRUMB_INFO, INVALID_SCHEMA_ERR, discriminatorPropertyName);
  }

  /**
   * Validate array keyword with default behaviour.
   *
   * @param valueNode  The current value node.
   * @param validation The validation results.
   */
  abstract void validateWithoutDiscriminator(final JsonNode valueNode, final ValidationData<V> validation);

  private void setupDiscriminator(final ValidationContext<OAI3, V> context,
                                  final JsonNode schemaNode,
                                  final JsonNode schemaParentNode,
                                  final SchemaValidator<V> parentSchema) {

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

  private void setupAllOfDiscriminatorSchemas(final ValidationContext<OAI3, V> context,
                                              final JsonNode schemaNode,
                                              final JsonNode schemaParentNode,
                                              final SchemaValidator<V> parentSchema) {

    JsonNode allOfNode = getParentSchemaNode().get(ALLOF);
    ReferenceRegistry refRegistry = context.getContext().getReferenceRegistry();

    for (JsonNode allOfNodeItem : allOfNode) {
      List<JsonNode> refNodes = getReferences(allOfNodeItem);

      for (JsonNode refNode : refNodes) {
        Reference reference = refRegistry.getRef(refNode.textValue());
        discriminatorNode = reference.getContent().get(DISCRIMINATOR);
        if (discriminatorNode != null) {
          setupAllOfDiscriminatorSchemas(schemaNode, refNode, reference, schemaParentNode, parentSchema);
          return;
        }
      }
    }

    // Add default schemas
    for (JsonNode node : schemaNode) {
      schemas.add(new SchemaValidator<>(context, crumbInfo, node, schemaParentNode, parentSchema));
    }
  }

  private void setupAnyOneOfDiscriminatorSchemas(final ValidationContext<OAI3, V> context,
                                                 final JsonNode schemaNode,
                                                 final JsonNode schemaParentNode,
                                                 final SchemaValidator<V> parentSchema) {

    discriminatorNode = getParentSchemaNode().get(DISCRIMINATOR);

    for (JsonNode node : schemaNode) {
      schemas.add(new SchemaValidator<>(context, crumbInfo, node, schemaParentNode, parentSchema));
    }
  }

  private void setupAllOfDiscriminatorSchemas(final JsonNode schemaNode,
                                              final JsonNode refNode,
                                              final Reference reference,
                                              final JsonNode schemaParentNode,
                                              final SchemaValidator<V> parentSchema) {

    for (JsonNode node : schemaNode) {
      JsonNode refValueNode = getReference(node);

      if (refNode.equals(refValueNode)) { // Add the parent schema
        ValidationResults.CrumbInfo refCrumbInfo = new ValidationResults.CrumbInfo(reference.getRef(), true);
        schemas.add(new SchemaValidator<>(context, refCrumbInfo, reference.getContent(), schemaParentNode, parentSchema));
      } else { // Add the other items
        schemas.add(new SchemaValidator<>(context, crumbInfo, node, schemaParentNode, parentSchema));
      }
    }
  }

  private List<JsonNode> getReferences(JsonNode allOfNodeItem) {
    // Prefer absolute reference value
    List<JsonNode> refNodes = allOfNodeItem.findValues(ABS_REF_FIELD);
    if (refNodes.isEmpty()) {
      refNodes = allOfNodeItem.findValues(OAI3SchemaKeywords.$REF);
    }

    return refNodes;
  }

  private JsonNode getReference(JsonNode node) {
    // Prefer absolute reference value
    JsonNode refNode = node.get(ABS_REF_FIELD);
    if (refNode == null) {
      refNode = node.get(OAI3SchemaKeywords.$REF);
    }

    return refNode;
  }

  private String getDiscriminatorPropertyRefPath(final JsonNode valueNode, final ValidationData<V> validation) {
    // check discriminator definition
    if (discriminatorPropertyName == null) {
      validation.add(CRUMB_INFO, INVALID_PROPERTY_ERR);
      return null;
    }
    // check discriminator in content
    JsonNode discriminatorPropertyNameNode = valueNode.get(discriminatorPropertyName);
    if (discriminatorPropertyNameNode == null) {
      validation.add(CRUMB_INFO, INVALID_PROPERTY_CONTENT_ERR, discriminatorPropertyName);
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
    return (context.getContext().getReferenceRegistry().getRef(ref) != null) ? ref : null;
  }
}
