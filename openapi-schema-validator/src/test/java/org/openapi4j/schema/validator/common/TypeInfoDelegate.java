package org.openapi4j.schema.validator.common;

import org.openapi4j.core.validation.ValidationResult;
import org.openapi4j.core.validation.ValidationResults;
import org.openapi4j.core.validation.ValidationSeverity;

import static org.openapi4j.core.model.v3.OAI3SchemaKeywords.TYPE;

public class TypeInfoDelegate {
  private static final ValidationResult INFO = new ValidationResult(ValidationSeverity.INFO, null, "%s");
  private static final ValidationResults.CrumbInfo CRUMB_INFO = new ValidationResults.CrumbInfo(TYPE, true);

  public final boolean isRequest;

  public TypeInfoDelegate(boolean isRequest) {
    this.isRequest = isRequest;
  }

  public void log(ValidationData<?> validation, boolean value) {
    validation.add(CRUMB_INFO, INFO, value);
  }
}
