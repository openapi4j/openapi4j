package org.openapi4j.operation.validator.validation;

import com.fasterxml.jackson.databind.JsonNode;

import org.openapi4j.core.exception.EncodeException;
import org.openapi4j.core.model.v3.OAI3;
import org.openapi4j.core.validation.ValidationResult;
import org.openapi4j.core.validation.ValidationResults;
import org.openapi4j.operation.validator.model.impl.Body;
import org.openapi4j.parser.model.v3.MediaType;
import org.openapi4j.parser.model.v3.OpenApi3;
import org.openapi4j.schema.validator.JsonValidator;
import org.openapi4j.schema.validator.ValidationContext;
import org.openapi4j.schema.validator.v3.SchemaValidator;

import java.io.IOException;

import static org.openapi4j.core.validation.ValidationSeverity.ERROR;

class BodyValidator {
  private static final ValidationResult BODY_REQUIRED_ERR = new ValidationResult(ERROR, 200, "Body is required but none provided.");
  private static final ValidationResult BODY_CONTENT_ERR = new ValidationResult(ERROR, 201, "An error occurred when getting the body content from type '%s'.%n%s");

  private static final String BODY = "body";

  private final ValidationContext<OAI3> context;
  private final OpenApi3 openApi;
  private final MediaType mediaType;
  private final JsonValidator validator;

  BodyValidator(ValidationContext<OAI3> context, OpenApi3 openApi, MediaType mediaType) {
    this.context = context;
    this.openApi = openApi;
    this.mediaType = mediaType;

    validator = initValidator();
  }

  void validate(final Body body,
                final String rawContentType,
                final boolean isBodyRequired,
                final ValidationResults results) {

    if (validator == null) {
      return; // No schema specified for body
    } else if (body == null) {
      if (isBodyRequired) {
        results.add(BODY_REQUIRED_ERR);
      }
      return;
    }

    try {
      JsonNode jsonBody = body.getContentAsNode(mediaType.getSchema(), rawContentType);
      validator.validate(jsonBody, results);
    } catch (IOException ex) {
      results.add(BODY_CONTENT_ERR, rawContentType, ex);
    }
  }

  private JsonValidator initValidator() {
    if (mediaType == null || mediaType.getSchema() == null) {
      return null;
    }

    try {
      return new SchemaValidator(
        context,
        BODY,
        mediaType.getSchema().toNode(openApi.getContext(), true));
    } catch (EncodeException ex) {
      // Will never happen
      return null;
    }
  }
}
