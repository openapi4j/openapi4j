package org.openapi4j.parser.validation.v3;

import org.openapi4j.core.validation.ValidationResults;
import org.openapi4j.parser.model.v3.EncodingProperty;
import org.openapi4j.parser.model.v3.OpenApi3;
import org.openapi4j.parser.validation.ValidationContext;
import org.openapi4j.parser.validation.Validator;

import static org.openapi4j.parser.validation.v3.OAI3Keywords.CONTENTTYPE;
import static org.openapi4j.parser.validation.v3.OAI3Keywords.EXTENSIONS;
import static org.openapi4j.parser.validation.v3.OAI3Keywords.HEADERS;
import static org.openapi4j.parser.validation.v3.OAI3Keywords.STYLE;

class EncodingPropertyValidator extends Validator3Base<OpenApi3, EncodingProperty> {
  private static final Validator<OpenApi3, EncodingProperty> INSTANCE = new EncodingPropertyValidator();

  private EncodingPropertyValidator() {
  }

  public static Validator<OpenApi3, EncodingProperty> instance() {
    return INSTANCE;
  }

  @Override
  public void validate(ValidationContext<OpenApi3> context, OpenApi3 api, EncodingProperty encodingProperty, ValidationResults results) {
    // VALIDATION EXCLUSIONS :
    // explode
    validateString(encodingProperty.getContentType(), results, false, CONTENTTYPE);
    validateMap(context, api, encodingProperty.getExtensions(), results, false, EXTENSIONS, Regexes.EXT_REGEX, null);
    validateMap(context, api, encodingProperty.getHeaders(), results, false, HEADERS, Regexes.NOEXT_REGEX, null);
    validateString(encodingProperty.getStyle(), results, false, Regexes.STYLE_REGEX, STYLE);
  }
}
