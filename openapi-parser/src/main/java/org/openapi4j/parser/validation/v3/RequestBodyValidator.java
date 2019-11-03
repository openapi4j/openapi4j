package org.openapi4j.parser.validation.v3;

import org.openapi4j.core.validation.ValidationResults;
import org.openapi4j.parser.model.v3.MediaType;
import org.openapi4j.parser.model.v3.OpenApi3;
import org.openapi4j.parser.model.v3.RequestBody;
import org.openapi4j.parser.validation.Validator;

import java.util.Map;

import static org.openapi4j.parser.validation.v3.OAI3Keywords.*;

class RequestBodyValidator extends Validator3Base<OpenApi3, RequestBody> {
  private static final String MULTIPART = "multipart/";
  private static final String FORM_URL_ENCODED = "application/x-www-form-urlencoded";
  private static final String ENCODING_MISMATCH = "The encoding object SHALL only apply to requestBody objects when the media type is multipart or application/x-www-form-urlencoded";

  private static final Validator<OpenApi3, RequestBody> INSTANCE = new RequestBodyValidator();

  private RequestBodyValidator() {
  }

  public static Validator<OpenApi3, RequestBody> instance() {
    return INSTANCE;
  }

  @Override
  public void validate(OpenApi3 api, RequestBody requestBody, ValidationResults results) {
    // VALIDATION EXCLUSIONS :
    // description, required
    if (requestBody.isRef()) {
      validateReference(api, requestBody.getRef(), results, $REF, RequestBodyValidator.instance(), RequestBody.class);
    } else {
      validateMap(api, requestBody.getContentMediaTypes(), results, false, CONTENT, Regexes.NOEXT_REGEX, MediaTypeValidator.instance());
      validateField(api, requestBody.getExtensions(), results, false, EXTENSIONS, ExtensionsValidator.instance());
      checkAllowedEncoding(requestBody, results);
    }
  }

  private void checkAllowedEncoding(RequestBody requestBody, ValidationResults results) {
    Map<String, MediaType> mediaTypes = requestBody.getContentMediaTypes();
    if (mediaTypes == null) {
      return;
    }

    for (Map.Entry<String, MediaType> entry : mediaTypes.entrySet()) {
      MediaType mediaType = entry.getValue();

      if (
        mediaType.getEncoding() != null
          && !entry.getKey().startsWith(MULTIPART)
          && !entry.getKey().equals(FORM_URL_ENCODED)) {
        results.addWarning(ENCODING_MISMATCH, entry.getKey());
      }
    }
  }
}
