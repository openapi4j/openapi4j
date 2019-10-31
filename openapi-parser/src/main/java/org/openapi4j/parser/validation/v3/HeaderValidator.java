package org.openapi4j.parser.validation.v3;

import org.openapi4j.core.validation.ValidationResults;
import org.openapi4j.parser.model.v3.Header;
import org.openapi4j.parser.model.v3.OpenApi3;
import org.openapi4j.parser.validation.Validator;

import static org.openapi4j.parser.validation.v3.OAI3Keywords.CONTENT;
import static org.openapi4j.parser.validation.v3.OAI3Keywords.EXTENSIONS;
import static org.openapi4j.parser.validation.v3.OAI3Keywords.SCHEMA;
import static org.openapi4j.parser.validation.v3.OAI3Keywords.STYLE;

class HeaderValidator extends Validator3Base<OpenApi3, Header> {
  private static final Validator<OpenApi3, Header> INSTANCE = new HeaderValidator();

  private HeaderValidator() {
  }

  public static Validator<OpenApi3, Header> instance() {
    return INSTANCE;
  }

  @Override
  public void validate(OpenApi3 api, Header header, ValidationResults results) {
    // VALIDATION EXCLUSIONS :
    // allowReserved, deprecated, description, example, examples, explode, required
    validateMap(api, header.getContentMediaTypes(), results, false, CONTENT, Regexes.NOEXT_REGEX, MediaTypeValidator.instance());
    validateField(api, header.getExtensions(), results, false, EXTENSIONS, ExtensionsValidator.instance());
    validateField(api, header.getSchema(), results, false, SCHEMA, SchemaValidator.instance());
    validateString(header.getStyle(), results, false, Regexes.STYLE_REGEX, STYLE);
  }
}
