package org.openapi4j.parser.validation.v3;

import org.openapi4j.core.validation.ValidationResults;
import org.openapi4j.parser.model.v3.Discriminator;
import org.openapi4j.parser.model.v3.OpenApi3;
import org.openapi4j.parser.model.v3.Schema;
import org.openapi4j.parser.validation.ValidationContext;
import org.openapi4j.parser.validation.Validator;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static org.openapi4j.core.model.v3.OAI3SchemaKeywords.$REF;
import static org.openapi4j.core.model.v3.OAI3SchemaKeywords.ADDITIONALPROPERTIES;
import static org.openapi4j.core.model.v3.OAI3SchemaKeywords.ALLOF;
import static org.openapi4j.core.model.v3.OAI3SchemaKeywords.ANYOF;
import static org.openapi4j.core.model.v3.OAI3SchemaKeywords.DEFAULT;
import static org.openapi4j.core.model.v3.OAI3SchemaKeywords.DISCRIMINATOR;
import static org.openapi4j.core.model.v3.OAI3SchemaKeywords.ENUM;
import static org.openapi4j.core.model.v3.OAI3SchemaKeywords.FORMAT;
import static org.openapi4j.core.model.v3.OAI3SchemaKeywords.FORMAT_DOUBLE;
import static org.openapi4j.core.model.v3.OAI3SchemaKeywords.FORMAT_FLOAT;
import static org.openapi4j.core.model.v3.OAI3SchemaKeywords.FORMAT_INT32;
import static org.openapi4j.core.model.v3.OAI3SchemaKeywords.FORMAT_INT64;
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
  private static final String DISCRIM_PROP_MISSING = "The discriminator '%s' is not a property of this schema";
  private static final String DISCRIM_REQUIRED_MISSING = "The discriminator '%s' is required in this schema";
  private static final String READ_WRITE_ONLY_EXCLUSIVE = "Schema cannot be both ReadOnly and WriteOnly";
  private static final String FORMAT_TYPE_MISMATCH = "Format '%s' is incompatible with schema type '%s'";
  private static final String VALUE_TYPE_MISMATCH = "Value '%s' is incompatible with schema type '%s'";

  private static final Validator<OpenApi3, Schema> INSTANCE = new SchemaValidator();

  private SchemaValidator() {
  }

  public static Validator<OpenApi3, Schema> instance() {
    return INSTANCE;
  }

  @Override
  public void validate(ValidationContext<OpenApi3> context, OpenApi3 api, Schema schema, ValidationResults results) {
    // VALIDATION EXCLUSIONS :
    // additionalPropertiesAllowed, description, deprecated,
    // example, title, exclusiveMaximum, exclusiveMinimum, nullable, uniqueItems

    if (schema.isRef()) {
      validateReference(context, api, schema, results, $REF, SchemaValidator.instance(), Schema.class);
    } else {
      validateField(context, api, schema.getAdditionalProperties(), results, false, ADDITIONALPROPERTIES, SchemaValidator.instance());
      validateField(context, api, schema.getDiscriminator(), results, false, DISCRIMINATOR, DiscriminatorValidator.instance());
      checkDiscriminator(api, schema, results);
      validateDefaultType(schema.getDefault(), schema.getType(), results);
      validateList(context, api, schema.getEnums(), results, false, ENUM, null);
      validateMap(context, api, schema.getExtensions(), results, false, EXTENSIONS, Regexes.EXT_REGEX, null);
      validateField(context, api, schema.getExternalDocs(), results, false, EXTERNALDOCS, ExternalDocsValidator.instance());
      validateFormat(schema.getFormat(), schema.getType(), results);
      if (schema.getItemsSchema() != null) {
        context.validate(api, schema.getItemsSchema(), SchemaValidator.instance(), results/*, "items"*/);
      }
      validateNonNegative(schema.getMaxItems(), results, false, MAXITEMS);
      validateNonNegative(schema.getMinItems(), results, false, MINITEMS);
      validateNonNegative(schema.getMaxLength(), results, false, MAXLENGTH);
      validateNonNegative(schema.getMinLength(), results, false, MINLENGTH);
      validateNonNegative(schema.getMaxProperties(), results, false, MAXPROPERTIES);
      validateNonNegative(schema.getMinProperties(), results, false, MINPROPERTIES);
      validatePositive(schema.getMultipleOf(), results, false, MULTIPLEOF);
      if (schema.getNotSchema() != null) {
        context.validate(api, schema.getNotSchema(), SchemaValidator.instance(), results/*, "not"*/);
      }
      validatePattern(schema.getPattern(), results, false, PATTERN);
      validateMap(context, api, schema.getProperties(), results, false, PROPERTIES, null, this);
      validateList(context, api, schema.getRequiredFields(), results, false, REQUIRED, null);
      validateList(context, api, schema.getAllOfSchemas(), results, false, ALLOF, this);
      validateList(context, api, schema.getAnyOfSchemas(), results, false, ANYOF, this);
      validateList(context, api, schema.getOneOfSchemas(), results, false, ONEOF, this);
      checkReadWrite(schema, results);
      validateString(schema.getType(), results, false, TYPE_REGEX, TYPE);
      validateField(context, api, schema.getXml(), results, false, XML, XmlValidator.instance());
    }
  }

  private void checkDiscriminator(OpenApi3 api, Schema schema, ValidationResults results) {
    Discriminator discriminator = schema.getDiscriminator();

    if (discriminator == null) {
      return;
    }

    int count = 0;
    count += schema.hasAllOfSchemas() ? 1 : 0;
    count += schema.hasAnyOfSchemas() ? 1 : 0;
    count += schema.hasOneOfSchemas() ? 1 : 0;

    if (count > 1) {
      results.addError(String.format(DISCRIM_ONLY_ONE, discriminator.getPropertyName()), DISCRIMINATOR);
    } else if (count == 0) {
      // discriminator is located aside properties
      checkSchemaDiscriminator(api, discriminator, Collections.singletonList(schema), results);
    } else {
      // discriminator is aside xxxOf
      // check properties for the schemas
      checkSchemaCollections(api, schema, discriminator, results);
    }
  }

  private void checkSchemaCollections(OpenApi3 api, Schema schema, Discriminator discriminator, ValidationResults results) {
    if (schema.hasAllOfSchemas()) {
      if (!checkSchemaDiscriminator(api, discriminator, schema.getAllOfSchemas(), new ValidationResults())) {
        results.addError(String.format(DISCRIM_CONSTRAINT_MISSING, discriminator.getPropertyName()), DISCRIMINATOR);
      }
    } else if (schema.hasAnyOfSchemas()) {
      checkSchemaDiscriminator(api, discriminator, schema.getAnyOfSchemas(), results);
    } else {
      checkSchemaDiscriminator(api, discriminator, schema.getOneOfSchemas(), results);
    }
  }

  private boolean checkSchemaDiscriminator(OpenApi3 api, Discriminator discriminator, List<Schema> schemas, ValidationResults results) {
    boolean hasProperty = true;

    for (Schema schema : schemas) {
      if (schema.isRef()) {
        schema = getReferenceContent(api, schema, results, DISCRIMINATOR, Schema.class);
      }

      // Check for extended model with allOf
      if (schema.hasAllOfSchemas()) {
        if (!checkSchemaDiscriminator(api, discriminator, schema.getAllOfSchemas(), new ValidationResults())) {
          results.addError(String.format(DISCRIM_CONSTRAINT_MISSING, discriminator.getPropertyName()), DISCRIMINATOR);
          hasProperty = false;
        }
      } else {
        if (!schema.hasProperty(discriminator.getPropertyName())) {
          results.addError(String.format(DISCRIM_PROP_MISSING, discriminator.getPropertyName()), DISCRIMINATOR);
          hasProperty = false;
        }
        if (!schema.hasRequiredFields() || !schema.getRequiredFields().contains(discriminator.getPropertyName())) {
          results.addError(String.format(DISCRIM_REQUIRED_MISSING, discriminator.getPropertyName()), DISCRIMINATOR);
          hasProperty = false;
        }
      }

      break;
    }

    return hasProperty;
  }

  private void checkReadWrite(Schema schema, ValidationResults results) {
    if (schema.isReadOnly() && schema.isWriteOnly()) {
      results.addError(READ_WRITE_ONLY_EXCLUSIVE);
    }
  }

  private void validateFormat(final String format,
                              final String type,
                              final ValidationResults results) {

    if (format != null) {
      String expectedType;
      switch (format) {
        case FORMAT_INT32:
        case FORMAT_INT64:
          expectedType = TYPE_INTEGER;
          break;
        case FORMAT_FLOAT:
        case FORMAT_DOUBLE:
          expectedType = TYPE_NUMBER;
          break;
        default:
          expectedType = TYPE_STRING;
          break;
      }

      if (type != null && !type.equals(expectedType)) {
        results.addError(String.format(FORMAT_TYPE_MISMATCH, format, type), FORMAT);
      }
    }
  }

  private void validateDefaultType(final Object defaultValue,
                                   final String type,
                                   final ValidationResults results) {

    if (defaultValue != null && type != null) {
      boolean ok;
      switch (type) {
        case TYPE_STRING:
          ok = defaultValue instanceof String;
          break;
        case TYPE_NUMBER:
          ok = defaultValue instanceof Number;
          break;
        case TYPE_INTEGER:
          ok = defaultValue instanceof Integer;
          break;
        case TYPE_BOOLEAN:
          ok = defaultValue instanceof Boolean;
          break;
        case TYPE_OBJECT:
          ok = defaultValue instanceof Map;
          break;
        case TYPE_ARRAY:
        default:
          ok = defaultValue instanceof Collection;
          break;
      }
      if (!ok) {
        results.addError(String.format(VALUE_TYPE_MISMATCH, defaultValue, type), DEFAULT);
      }
    }
  }
}
