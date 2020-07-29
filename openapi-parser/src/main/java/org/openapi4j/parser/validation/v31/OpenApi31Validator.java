package org.openapi4j.parser.validation.v31;

import org.openapi4j.core.validation.ValidationException;
import org.openapi4j.core.validation.ValidationResults;
import org.openapi4j.core.validation.ValidationSeverity;
import org.openapi4j.parser.model.v3.OpenApi3;
import org.openapi4j.parser.validation.ValidationContext;

public class OpenApi31Validator {
  private static final String VALIDATION_FAILURE = "OpenApi31 validation failure";

  private static final OpenApi31Validator INSTANCE = new OpenApi31Validator();

  private OpenApi31Validator() {
  }

  public static OpenApi31Validator instance() {
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
