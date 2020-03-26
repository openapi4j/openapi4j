package org.openapi4j.parser.validation.v3;

import org.openapi4j.core.validation.ValidationResult;
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

import static org.openapi4j.core.model.v3.OAI3SchemaKeywords.*;
import static org.openapi4j.core.validation.ValidationSeverity.ERROR;
import static org.openapi4j.parser.validation.v3.OAI3Keywords.*;

class SchemaValidator extends Validator3Base<OpenApi3, Schema> {
  private static final Pattern TYPE_REGEX = Pattern.compile(
    String.join("|", TYPE_BOOLEAN, TYPE_OBJECT, TYPE_ARRAY, TYPE_NUMBER, TYPE_INTEGER, TYPE_STRING));

  private static final ValidationResult DISCRIM_ONLY_ONE = new ValidationResult(ERROR, 132, "The discriminator mapping '%s' MUST have only one of the composite keywords 'oneOf, anyOf, allOf'");
  private static final ValidationResult DISCRIM_CONSTRAINT_MISSING = new ValidationResult(ERROR, 133, "The discriminator '%s' is not required or not a property of the allOf schemas");
  private static final ValidationResult DISCRIM_PROP_MISSING = new ValidationResult(ERROR, 134, "The discriminator '%s' is not a property of this schema");
  private static final ValidationResult DISCRIM_REQUIRED_MISSING = new ValidationResult(ERROR, 135, "The discriminator '%s' is required in this schema");
  private static final ValidationResult READ_WRITE_ONLY_EXCLUSIVE = new ValidationResult(ERROR, 136, "Schema cannot be both ReadOnly and WriteOnly");
  private static final ValidationResult FORMAT_TYPE_MISMATCH = new ValidationResult(ERROR, 137, "Format '%s' is incompatible with schema type '%s'");
  private static final ValidationResult VALUE_TYPE_MISMATCH = new ValidationResult(ERROR, 138, "Value '%s' is incompatible with schema type '%s'");

  private static final ValidationResults.CrumbInfo CRUMB_ADDITIONALPROPERTIES = new ValidationResults.CrumbInfo(ADDITIONALPROPERTIES, false);
  private static final ValidationResults.CrumbInfo CRUMB_DISCRIMINATOR = new ValidationResults.CrumbInfo(DISCRIMINATOR, false);
  private static final ValidationResults.CrumbInfo CRUMB_MAXITEMS = new ValidationResults.CrumbInfo(MAXITEMS, false);
  private static final ValidationResults.CrumbInfo CRUMB_MINITEMS = new ValidationResults.CrumbInfo(MINITEMS, false);
  private static final ValidationResults.CrumbInfo CRUMB_MAXLENGTH = new ValidationResults.CrumbInfo(MAXLENGTH, false);
  private static final ValidationResults.CrumbInfo CRUMB_MINLENGTH = new ValidationResults.CrumbInfo(MINLENGTH, false);
  private static final ValidationResults.CrumbInfo CRUMB_MAXPROPERTIES = new ValidationResults.CrumbInfo(MAXPROPERTIES, false);
  private static final ValidationResults.CrumbInfo CRUMB_MINPROPERTIES = new ValidationResults.CrumbInfo(MINPROPERTIES, false);
  private static final ValidationResults.CrumbInfo CRUMB_MULTIPLEOF = new ValidationResults.CrumbInfo(MULTIPLEOF, false);
  private static final ValidationResults.CrumbInfo CRUMB_PATTERN = new ValidationResults.CrumbInfo(PATTERN, false);
  private static final ValidationResults.CrumbInfo CRUMB_PROPERTIES = new ValidationResults.CrumbInfo(PROPERTIES, false);
  private static final ValidationResults.CrumbInfo CRUMB_ALLOF = new ValidationResults.CrumbInfo(ALLOF, false);
  private static final ValidationResults.CrumbInfo CRUMB_ANYOF = new ValidationResults.CrumbInfo(ANYOF, false);
  private static final ValidationResults.CrumbInfo CRUMB_ONEOF = new ValidationResults.CrumbInfo(ONEOF, false);
  private static final ValidationResults.CrumbInfo CRUMB_FORMAT = new ValidationResults.CrumbInfo(FORMAT, false);

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
      validateReference(context, api, schema, results, CRUMB_$REF, SchemaValidator.instance(), Schema.class);
    } else {
      validateField(context, api, schema.getAdditionalProperties(), results, false, CRUMB_ADDITIONALPROPERTIES, SchemaValidator.instance());
      validateField(context, api, schema.getDiscriminator(), results, false, CRUMB_DISCRIMINATOR, DiscriminatorValidator.instance());
      checkDiscriminator(api, schema, results);
      validateDefaultType(schema.getDefault(), schema.getType(), results);
      validateList(context, api, schema.getEnums(), results, false, CRUMB_ENUM, null);
      validateMap(context, api, schema.getExtensions(), results, false, CRUMB_EXTENSIONS, Regexes.EXT_REGEX, null);
      validateField(context, api, schema.getExternalDocs(), results, false, CRUMB_EXTERNALDOCS, ExternalDocsValidator.instance());
      validateFormat(schema.getFormat(), schema.getType(), results);
      if (schema.getItemsSchema() != null) {
        context.validate(api, schema.getItemsSchema(), SchemaValidator.instance(), results/*, "items"*/);
      }
      validateNonNegative(schema.getMaxItems(), results, false, CRUMB_MAXITEMS);
      validateNonNegative(schema.getMinItems(), results, false, CRUMB_MINITEMS);
      validateNonNegative(schema.getMaxLength(), results, false, CRUMB_MAXLENGTH);
      validateNonNegative(schema.getMinLength(), results, false, CRUMB_MINLENGTH);
      validateNonNegative(schema.getMaxProperties(), results, false, CRUMB_MAXPROPERTIES);
      validateNonNegative(schema.getMinProperties(), results, false, CRUMB_MINPROPERTIES);
      validatePositive(schema.getMultipleOf(), results, false, CRUMB_MULTIPLEOF);
      if (schema.getNotSchema() != null) {
        context.validate(api, schema.getNotSchema(), SchemaValidator.instance(), results/*, "not"*/);
      }
      validatePattern(schema.getPattern(), results, false, CRUMB_PATTERN);
      validateMap(context, api, schema.getProperties(), results, false, CRUMB_PROPERTIES, null, this);
      validateList(context, api, schema.getRequiredFields(), results, false, CRUMB_REQUIRED, null);
      validateList(context, api, schema.getAllOfSchemas(), results, false, CRUMB_ALLOF, this);
      validateList(context, api, schema.getAnyOfSchemas(), results, false, CRUMB_ANYOF, this);
      validateList(context, api, schema.getOneOfSchemas(), results, false, CRUMB_ONEOF, this);
      checkReadWrite(schema, results);
      validateString(schema.getType(), results, false, TYPE_REGEX, CRUMB_TYPE);
      validateField(context, api, schema.getXml(), results, false, CRUMB_XML, XmlValidator.instance());
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
      results.add(CRUMB_DISCRIMINATOR, DISCRIM_ONLY_ONE, discriminator.getPropertyName());
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
        results.add(CRUMB_DISCRIMINATOR, DISCRIM_CONSTRAINT_MISSING, discriminator.getPropertyName());
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
        schema = getReferenceContent(api, schema, results, CRUMB_DISCRIMINATOR, Schema.class);
      }

      // Check for extended model with allOf
      if (schema.hasAllOfSchemas()) {
        if (!checkSchemaDiscriminator(api, discriminator, schema.getAllOfSchemas(), new ValidationResults())) {
          results.add(CRUMB_DISCRIMINATOR, DISCRIM_CONSTRAINT_MISSING, discriminator.getPropertyName());
          hasProperty = false;
        }
      } else {
        if (!schema.hasProperty(discriminator.getPropertyName())) {
          results.add(CRUMB_DISCRIMINATOR, DISCRIM_PROP_MISSING, discriminator.getPropertyName());
          hasProperty = false;
        }
        if (!schema.hasRequiredFields() || !schema.getRequiredFields().contains(discriminator.getPropertyName())) {
          results.add(CRUMB_DISCRIMINATOR, DISCRIM_REQUIRED_MISSING, discriminator.getPropertyName());
          hasProperty = false;
        }
      }

      break;
    }

    return hasProperty;
  }

  private void checkReadWrite(Schema schema, ValidationResults results) {
    if (schema.isReadOnly() && schema.isWriteOnly()) {
      results.add(READ_WRITE_ONLY_EXCLUSIVE);
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
        results.add(CRUMB_FORMAT, FORMAT_TYPE_MISMATCH, format, type);
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
        results.add(CRUMB_DEFAULT, VALUE_TYPE_MISMATCH, defaultValue, type);
      }
    }
  }
}
