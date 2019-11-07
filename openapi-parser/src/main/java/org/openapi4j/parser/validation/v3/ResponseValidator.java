package org.openapi4j.parser.validation.v3;

import org.openapi4j.core.validation.ValidationResults;
import org.openapi4j.parser.model.v3.OpenApi3;
import org.openapi4j.parser.model.v3.Response;
import org.openapi4j.parser.validation.Validator;

import static org.openapi4j.parser.validation.v3.OAI3Keywords.$REF;
import static org.openapi4j.parser.validation.v3.OAI3Keywords.CONTENT;
import static org.openapi4j.parser.validation.v3.OAI3Keywords.DESCRIPTION;
import static org.openapi4j.parser.validation.v3.OAI3Keywords.EXTENSIONS;
import static org.openapi4j.parser.validation.v3.OAI3Keywords.HEADERS;
import static org.openapi4j.parser.validation.v3.OAI3Keywords.LINKS;

class ResponseValidator extends Validator3Base<OpenApi3, Response> {
  private static final Validator<OpenApi3, Response> INSTANCE = new ResponseValidator();

  private ResponseValidator() {
  }

  public static Validator<OpenApi3, Response> instance() {
    return INSTANCE;
  }

  @Override
  public void validate(OpenApi3 api, Response response, ValidationResults results) {
    if (response.isRef()) {
      validateReference(api, response.getRef(), results, $REF, ResponseValidator.instance(), Response.class);
    } else {
      validateRequired(response.getDescription(), results, true, DESCRIPTION);
      validateMap(api, response.getHeaders(), results, false, HEADERS, null, HeaderValidator.instance());
      validateMap(api, response.getContentMediaTypes(), results, false, CONTENT, Regexes.NOEXT_REGEX, MediaTypeValidator.instance());
      validateMap(api, response.getLinks(), results, false, LINKS, Regexes.NOEXT_NAME_REGEX, LinkValidator.instance());
      validateMap(api, response.getExtensions(), results, false, EXTENSIONS, Regexes.EXT_REGEX, null);
    }
  }
}
