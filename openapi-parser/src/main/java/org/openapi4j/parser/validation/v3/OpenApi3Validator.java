package org.openapi4j.parser.validation.v3;

import org.openapi4j.core.validation.ValidationException;
import org.openapi4j.core.validation.ValidationResults;
import org.openapi4j.core.validation.ValidationSeverity;
import org.openapi4j.parser.model.v3.OpenApi3;
import org.openapi4j.parser.validation.ValidationContext;

public class OpenApi3Validator {
  private static final String VALIDATION_FAILURE = "OpenApi3 validation failure";

  private static final OpenApi3Validator INSTANCE = new OpenApi3Validator();

  private OpenApi3Validator() {
  }

  public static OpenApi3Validator instance() {
    return INSTANCE;
  }

  public ValidationResults validate(OpenApi3 api) throws ValidationException {
    final ValidationContext<OpenApi3> context = new ValidationContext<>();
    final ValidationResults results = new ValidationResults();

    context.validate(api, api, OpenApiValidator.instance(), results);

    if (results.severity() == ValidationSeverity.ERROR) {
      throw new ValidationException(VALIDATION_FAILURE, results);
    }

    return results;
  }
}
