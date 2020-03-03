package org.openapi4j.parser.validation.v3;

import org.openapi4j.core.validation.ValidationResults;
import org.openapi4j.parser.model.v3.EncodingProperty;
import org.openapi4j.parser.model.v3.MediaType;
import org.openapi4j.parser.model.v3.OpenApi3;
import org.openapi4j.parser.model.v3.Schema;
import org.openapi4j.parser.validation.ValidationContext;
import org.openapi4j.parser.validation.Validator;

import java.util.Map;
import java.util.Set;

import static org.openapi4j.parser.validation.v3.OAI3Keywords.ENCODING;
import static org.openapi4j.parser.validation.v3.OAI3Keywords.EXTENSIONS;
import static org.openapi4j.parser.validation.v3.OAI3Keywords.SCHEMA;

class MediaTypeValidator extends Validator3Base<OpenApi3, MediaType> {
  private static final String ENCODING_MISMATCH = "Encoding property '%s' is not a corresponding schema property";

  private static final Validator<OpenApi3, MediaType> INSTANCE = new MediaTypeValidator();

  private MediaTypeValidator() {
  }

  public static Validator<OpenApi3, MediaType> instance() {
    return INSTANCE;
  }

  @Override
  public void validate(ValidationContext<OpenApi3> context, OpenApi3 api, MediaType mediaType, ValidationResults results) {
    // VALIDATION EXCLUSIONS :
    // example, examples
    validateMap(context, api, mediaType.getEncodings(), results, false, ENCODING, Regexes.NOEXT_NAME_REGEX, EncodingPropertyValidator.instance());
    validateMap(context, api, mediaType.getExtensions(), results, false, EXTENSIONS, Regexes.EXT_REGEX, null);
    validateField(context, api, mediaType.getSchema(), results, false, SCHEMA, SchemaValidator.instance());
    checkEncodingProperties(mediaType, results);
  }

  private void checkEncodingProperties(MediaType mediaType, ValidationResults results) {
    Schema schema = mediaType.getSchema();
    if (schema == null) {
      return;
    }

    Map<String, Schema> schemaProps = schema.getProperties();
    Map<String, EncodingProperty> mediaEncodings = mediaType.getEncodings();

    if (schemaProps != null && mediaEncodings != null) {
      Set<String> propNames = schemaProps.keySet();
      for (String encodingPropertyName : mediaEncodings.keySet()) {
        if (!propNames.contains(encodingPropertyName)) {
          results.addError(String.format(ENCODING_MISMATCH, encodingPropertyName), encodingPropertyName);
        }
      }
    }
  }
}
