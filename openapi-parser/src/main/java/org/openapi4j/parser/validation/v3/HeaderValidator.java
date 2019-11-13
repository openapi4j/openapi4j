package org.openapi4j.parser.validation.v3;

import org.openapi4j.core.validation.ValidationResults;
import org.openapi4j.parser.model.v3.Header;
import org.openapi4j.parser.model.v3.OpenApi3;
import org.openapi4j.parser.validation.Validator;

import static org.openapi4j.parser.validation.v3.OAI3Keywords.$REF;
import static org.openapi4j.parser.validation.v3.OAI3Keywords.CONTENT;
import static org.openapi4j.parser.validation.v3.OAI3Keywords.EXTENSIONS;
import static org.openapi4j.parser.validation.v3.OAI3Keywords.SCHEMA;
import static org.openapi4j.parser.validation.v3.OAI3Keywords.STYLE;

class HeaderValidator extends Validator3Base<OpenApi3, Header> {
  private static final String CONTENT_ONY_ONE_ERR_MSG = "content can only contain one media type.";

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
    if (header.isRef()) {
      validateReference(api, header.getRef(), results, $REF, HeaderValidator.instance(), Header.class);
    } else {
      validateString(header.getStyle(), results, false, "simple", STYLE); // Only simple is allowed.
      validateField(api, header.getSchema(), results, false, SCHEMA, SchemaValidator.instance());
      validateMap(api, header.getContentMediaTypes(), results, false, CONTENT, Regexes.NOEXT_REGEX, MediaTypeValidator.instance());
      validateMap(api, header.getExtensions(), results, false, EXTENSIONS, Regexes.EXT_REGEX, null);

      if (header.getContentMediaTypes() != null && header.getContentMediaTypes().size() > 1) {
        results.addError(CONTENT_ONY_ONE_ERR_MSG);
      }
    }
  }
}
