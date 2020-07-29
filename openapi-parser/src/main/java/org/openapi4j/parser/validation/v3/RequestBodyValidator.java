package org.openapi4j.parser.validation.v3;

import org.openapi4j.core.validation.ValidationResult;
import org.openapi4j.core.validation.ValidationResults;
import org.openapi4j.parser.model.v3.MediaType;
import org.openapi4j.parser.model.v3.OpenApi3;
import org.openapi4j.parser.model.v3.RequestBody;
import org.openapi4j.parser.validation.ValidationContext;
import org.openapi4j.parser.validation.Validator;

import java.util.Map;

import static org.openapi4j.core.validation.ValidationSeverity.ERROR;
import static org.openapi4j.parser.validation.v3.OAI3Keywords.*;

public class RequestBodyValidator extends Validator3Base<OpenApi3, RequestBody> {
  private static final ValidationResult ENCODING_MISMATCH = new ValidationResult(ERROR, 131, "The encoding object SHALL only apply to requestBody objects when the media type is multipart or application/x-www-form-urlencoded");

  private static final String MULTIPART = "multipart/";
  private static final String FORM_URL_ENCODED = "application/x-www-form-urlencoded";

  private static final Validator<OpenApi3, RequestBody> INSTANCE = new RequestBodyValidator();

  private RequestBodyValidator() {
  }

  public static Validator<OpenApi3, RequestBody> instance() {
    return INSTANCE;
  }

  @Override
  public void validate(ValidationContext<OpenApi3> context, OpenApi3 api, RequestBody requestBody, ValidationResults results) {
    // VALIDATION EXCLUSIONS :
    // description, required
    if (requestBody.isRef()) {
      validateReference(context, api, requestBody, results, CRUMB_$REF, RequestBodyValidator.instance(), RequestBody.class);
    } else {
      validateMap(context, api, requestBody.getContentMediaTypes(), results, false, CRUMB_CONTENT, Regexes.NOEXT_REGEX, MediaTypeValidator.instance());
      validateMap(context, api, requestBody.getExtensions(), results, false, CRUMB_EXTENSIONS, Regexes.EXT_REGEX, null);
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
        mediaType.getEncodings() != null
          && !entry.getKey().startsWith(MULTIPART)
          && !entry.getKey().equals(FORM_URL_ENCODED)) {
        results.add(ENCODING_MISMATCH, entry.getKey());
      }
    }
  }
}
