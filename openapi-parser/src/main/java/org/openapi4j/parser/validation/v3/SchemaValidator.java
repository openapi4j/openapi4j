package org.openapi4j.parser.validation.v3;

import org.openapi4j.core.exception.DecodeException;
import org.openapi4j.core.model.reference.Reference;
import org.openapi4j.core.validation.ValidationResults;
import org.openapi4j.parser.model.v3.Discriminator;
import org.openapi4j.parser.model.v3.OpenApi3;
import org.openapi4j.parser.model.v3.Schema;
import org.openapi4j.parser.validation.Validator;

import java.util.regex.Pattern;

import static org.openapi4j.core.model.v3.OAI3SchemaKeywords.$REF;
import static org.openapi4j.core.model.v3.OAI3SchemaKeywords.ADDITIONALPROPERTIES;
import static org.openapi4j.core.model.v3.OAI3SchemaKeywords.ALLOF;
import static org.openapi4j.core.model.v3.OAI3SchemaKeywords.ANYOF;
import static org.openapi4j.core.model.v3.OAI3SchemaKeywords.DEFAULT;
import static org.openapi4j.core.model.v3.OAI3SchemaKeywords.DISCRIMINATOR;
import static org.openapi4j.core.model.v3.OAI3SchemaKeywords.ENUM;
import static org.openapi4j.core.model.v3.OAI3SchemaKeywords.FORMAT;
import static org.openapi4j.core.model.v3.OAI3SchemaKeywords.MAXITEMS;
import static org.openapi4j.core.model.v3.OAI3SchemaKeywords.MAXLENGTH;
import static org.openapi4j.core.model.v3.OAI3SchemaKeywords.MAXPROPERTIES;
import static org.openapi4j.core.model.v3.OAI3SchemaKeywords.MINITEMS;
import static org.openapi4j.core.model.v3.OAI3SchemaKeywords.MINLENGTH;
import static org.openapi4j.core.model.v3.OAI3SchemaKeywords.MINPROPERTIES;
import static org.openapi4j.core.model.v3.OAI3SchemaKeywords.MULTIPLEOF;
import static org.openapi4j.core.model.v3.OAI3SchemaKeywords.ONEOF;
import static org.openapi4j.core.model.v3.OAI3SchemaKeywords.PATTERN;
import static org.openapi4j.core.model.v3.OAI3SchemaKeywords.PROPERTIES;
import static org.openapi4j.core.model.v3.OAI3SchemaKeywords.REQUIRED;
import static org.openapi4j.core.model.v3.OAI3SchemaKeywords.TYPE;
import static org.openapi4j.core.model.v3.OAI3SchemaKeywords.TYPE_ARRAY;
import static org.openapi4j.core.model.v3.OAI3SchemaKeywords.TYPE_BOOLEAN;
import static org.openapi4j.core.model.v3.OAI3SchemaKeywords.TYPE_INTEGER;
import static org.openapi4j.core.model.v3.OAI3SchemaKeywords.TYPE_NUMBER;
import static org.openapi4j.core.model.v3.OAI3SchemaKeywords.TYPE_OBJECT;
import static org.openapi4j.core.model.v3.OAI3SchemaKeywords.TYPE_STRING;
import static org.openapi4j.parser.validation.v3.OAI3Keywords.EXTENSIONS;
import static org.openapi4j.parser.validation.v3.OAI3Keywords.EXTERNALDOCS;
import static org.openapi4j.parser.validation.v3.OAI3Keywords.XML;

class SchemaValidator extends Validator3Base<OpenApi3, Schema> {
  private static final Pattern TYPE_REGEX = Pattern.compile(
    String.join("|", TYPE_BOOLEAN, TYPE_OBJECT, TYPE_ARRAY, TYPE_NUMBER, TYPE_INTEGER, TYPE_STRING));

  private static final String DISCRIM_ONLY_ONE = "The discriminator mapping '%s' MUST have only one of the composite keywords 'oneOf, anyOf, allOf'";
  private static final String DISCRIM_CONSTRAINT_MISSING = "The discriminator '%s' is not required or not a property of the allOf schemas";
  private static final String DISCRIM_REF_MAPPING = "Unable to map reference '%s' to schema content";
  private static final String DISCRIM_PROP_MISSING = "The discriminator '%s' is not a property of this schema";
  private static final String DISCRIM_REQUIRED_MISSING = "The discriminator '%s' is required in this schema";
  private static final String READ_WRITE_ONLY_EXCLUSIVE = "Schema cannot be both ReadOnly and WriteOnly";

  private static final Validator<OpenApi3, Schema> INSTANCE = new SchemaValidator();

  private SchemaValidator() {
  }

  public static Validator<OpenApi3, Schema> instance() {
    return INSTANCE;
  }

  @Override
  public void validate(OpenApi3 api, Schema schema, ValidationResults results) {
    // VALIDATION EXCLUSIONS :
    // additionalPropertiesAllowed, description, deprecated,
    // example, title, exclusiveMaximum, exclusiveMinimum, nullable, uniqueItems

    if (schema.is$ref()) {
      validateReference(api, schema.get$ref(), results, $REF, SchemaValidator.instance(), Schema.class);
    } else {
      validateField(api, schema.getAdditionalProperties(), results, false, ADDITIONALPROPERTIES, SchemaValidator.instance());
      validateField(api, schema.getDiscriminator(), results, false, DISCRIMINATOR, DiscriminatorValidator.instance());
      checkDiscriminator(api, schema, results);
      validateType(schema.getDefault(), schema.getType(), results, DEFAULT);
      validateList(api, schema.getEnums(), results, false, ENUM, null);
      validateField(api, schema.getExtensions(), results, false, EXTENSIONS, ExtensionsValidator.instance());
      validateField(api, schema.getExternalDocs(), results, false, EXTERNALDOCS, ExternalDocsValidator.instance());
      validateFormat(schema.getFormat(), schema.getType(), results, FORMAT);
      if (schema.getItemsSchema() != null) {
        validate(api, schema.getItemsSchema(), results/*, "items"*/);
      }
      validateNonNegative(schema.getMaxItems(), results, false, MAXITEMS);
      validateNonNegative(schema.getMinItems(), results, false, MINITEMS);
      validateNonNegative(schema.getMaxLength(), results, false, MAXLENGTH);
      validateNonNegative(schema.getMinLength(), results, false, MINLENGTH);
      validateNonNegative(schema.getMaxProperties(), results, false, MAXPROPERTIES);
      validateNonNegative(schema.getMinProperties(), results, false, MINPROPERTIES);
      validatePositive(schema.getMultipleOf(), results, false, MULTIPLEOF);
      if (schema.getNotSchema() != null) {
        validate(api, schema.getNotSchema(), results/*, "not"*/);
      }
      validatePattern(schema.getPattern(), results, false, PATTERN);
      validateMap(api, schema.getProperties(), results, false, PROPERTIES, null, this);
      validateList(api, schema.getRequiredFields(), results, false, REQUIRED, null);
      validateList(api, schema.getAllOfSchemas(), results, false, ALLOF, this);
      validateList(api, schema.getAnyOfSchemas(), results, false, ANYOF, this);
      validateList(api, schema.getOneOfSchemas(), results, false, ONEOF, this);
      checkReadWrite(schema, results);
      validateString(schema.getType(), results, false, TYPE_REGEX, TYPE);
      validateField(api, schema.getXml(), results, false, XML, XmlValidator.instance());
    }
  }

  private void checkDiscriminator(OpenApi3 api, Schema schema, ValidationResults results) {
    Discriminator discriminator = schema.getDiscriminator();
    if (discriminator != null) {
      int count = 0;
      count += schema.hasAllOfSchemas() ? 1 : 0;
      count += schema.hasAnyOfSchemas() ? 1 : 0;
      count += schema.hasOneOfSchemas() ? 1 : 0;

      if (count > 1) {
        results.addError(String.format(DISCRIM_ONLY_ONE, discriminator.getPropertyName()), DISCRIMINATOR);
      } else if (count == 0) {
        // discriminator is located aside properties
        checkSchemaDiscriminator(api, discriminator, schema, results);
      } else /*(count == 1)*/ {
        // discriminator is aside xxxOf
        // check properties for the schemas
        if (schema.hasAllOfSchemas()) {
          boolean hasProperty = false;
          for (Schema subSchema : schema.getAllOfSchemas()) {
            hasProperty |= checkSchemaDiscriminator(api, discriminator, subSchema, new ValidationResults());
          }
          if (!hasProperty) {
            results.addError(String.format(DISCRIM_CONSTRAINT_MISSING, discriminator.getPropertyName()), DISCRIMINATOR);
          }
        } else if (schema.hasAnyOfSchemas()) {
          for (Schema subSchema : schema.getAnyOfSchemas()) {
            checkSchemaDiscriminator(api, discriminator, subSchema, results);
          }
        } else {
          for (Schema subSchema : schema.getOneOfSchemas()) {
            checkSchemaDiscriminator(api, discriminator, subSchema, results);
          }
        }
      }
    }
  }

  private boolean checkSchemaDiscriminator(OpenApi3 api, Discriminator discriminator, Schema schema, ValidationResults results) {
    boolean hasProperty = true;

    if (schema.is$ref()) {
      Reference reference = api.getContext().getReferenceRegistry().getRef(schema.get$ref());
      if (reference != null) {
        try {
          schema = reference.getMappedContent(Schema.class);
        } catch (DecodeException e) {
          results.addError(String.format(DISCRIM_REF_MAPPING, schema.get$ref()), DISCRIMINATOR);
          return false;
        }
      }
    }

    if (!schema.getProperties().containsKey(discriminator.getPropertyName())) {
      results.addError(String.format(DISCRIM_PROP_MISSING, discriminator.getPropertyName()), DISCRIMINATOR);
      hasProperty = false;
    }
    if (!schema.getRequiredFields().contains(discriminator.getPropertyName())) {
      results.addError(String.format(DISCRIM_REQUIRED_MISSING, discriminator.getPropertyName()), DISCRIMINATOR);
      hasProperty = false;
    }

    return hasProperty;
  }

  private void checkReadWrite(Schema schema, ValidationResults results) {
    if (schema.isReadOnly() && schema.isWriteOnly()) {
      results.addError(READ_WRITE_ONLY_EXCLUSIVE);
    }
  }
}
