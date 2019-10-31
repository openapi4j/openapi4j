package org.openapi4j.parser.validation.v3;

import org.openapi4j.core.exception.DecodeException;
import org.openapi4j.core.model.reference.Reference;
import org.openapi4j.core.validation.ValidationResults;
import org.openapi4j.parser.model.v3.OpenApi3;
import org.openapi4j.parser.validation.Validator;
import org.openapi4j.parser.validation.ValidatorBase;

import java.util.Collection;
import java.util.Map;

import static org.openapi4j.core.model.v3.OAI3SchemaKeywords.FORMAT_DOUBLE;
import static org.openapi4j.core.model.v3.OAI3SchemaKeywords.FORMAT_FLOAT;
import static org.openapi4j.core.model.v3.OAI3SchemaKeywords.FORMAT_INT32;
import static org.openapi4j.core.model.v3.OAI3SchemaKeywords.FORMAT_INT64;
import static org.openapi4j.core.model.v3.OAI3SchemaKeywords.TYPE_ARRAY;
import static org.openapi4j.core.model.v3.OAI3SchemaKeywords.TYPE_BOOLEAN;
import static org.openapi4j.core.model.v3.OAI3SchemaKeywords.TYPE_INTEGER;
import static org.openapi4j.core.model.v3.OAI3SchemaKeywords.TYPE_NUMBER;
import static org.openapi4j.core.model.v3.OAI3SchemaKeywords.TYPE_OBJECT;
import static org.openapi4j.core.model.v3.OAI3SchemaKeywords.TYPE_STRING;

abstract class Validator3Base<O extends OpenApi3, T> extends ValidatorBase<O, T> {
  private static final String FORMAT_TYPE_MISMATCH = "Format '%s' is incompatible with schema type '%s'";
  private static final String VALUE_TYPE_MISMATCH = "Value '%s' is incompatible with schema type '%s'";
  private static final String REF_MISSING = "Missing $ref '%s'";
  private static final String REF_CONTENT_UNREADABLE = "Unable to read $ref content at '%s'.";
  private static final String EMAIL_INVALID = "Invalid email address '%s'";

  void validateFormat(final String format,
                      final String type,
                      final ValidationResults results,
                      final String crumb) {

    if (format != null && type != null) {
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
      if (!type.equals(expectedType)) {
        results.addError(String.format(FORMAT_TYPE_MISMATCH, format, type), crumb);
      }
    }
  }

  void validateType(final Object defaultValue,
                    final String type,
                    final ValidationResults results,
                    final String crumb) {

    if (defaultValue != null && type != null) {
      boolean ok = false;
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
          ok = defaultValue instanceof Map<?, ?>;
          break;
        case TYPE_ARRAY:
          ok = defaultValue instanceof Collection<?>;
          break;
      }
      if (!ok) {
        results.addError(String.format(VALUE_TYPE_MISMATCH, defaultValue, type), crumb);
      }
    }
  }

  <F> void validateReference(final OpenApi3 api,
                             final String $ref,
                             final ValidationResults results,
                             final String crumb,
                             final Validator<OpenApi3, F> validator,
                             final Class<F> clazz) {

    Reference reference = api.getContext().getReferenceRegistry().getRef($ref);

    if (reference == null || reference.getContent().isMissingNode()) {
      results.addError(String.format(REF_MISSING, $ref), crumb);
    } else {
      try {
        F value = reference.getMappedContent(clazz);
        validator.validate(api, value, results);
      } catch (DecodeException e) {
        results.addError(String.format(REF_CONTENT_UNREADABLE, $ref), crumb);
      }
    }
  }

  @Override
  protected void checkEmail(String email, ValidationResults results, String crumb) {
    if (!Regexes.EMAIL_REGEX.matcher(email).matches()) {
      results.addError(String.format(EMAIL_INVALID, email), crumb);
    }
  }
}
