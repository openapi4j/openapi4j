package org.openapi4j.parser.validation.v3;

import org.openapi4j.core.validation.ValidationResults;
import org.openapi4j.parser.model.v3.ExternalDocs;
import org.openapi4j.parser.model.v3.OpenApi3;
import org.openapi4j.parser.validation.ValidationContext;
import org.openapi4j.parser.validation.Validator;

import static org.openapi4j.parser.validation.v3.OAI3Keywords.CRUMB_EXTENSIONS;
import static org.openapi4j.parser.validation.v3.OAI3Keywords.CRUMB_EXTERNALDOCS;

class ExternalDocsValidator extends Validator3Base<OpenApi3, ExternalDocs> {
  private static final Validator<OpenApi3, ExternalDocs> INSTANCE = new ExternalDocsValidator();

  private ExternalDocsValidator() {
  }

  public static Validator<OpenApi3, ExternalDocs> instance() {
    return INSTANCE;
  }

  @Override
  public void validate(ValidationContext<OpenApi3> context, OpenApi3 api, ExternalDocs externalDocs, ValidationResults results) {
    // VALIDATION EXCLUSIONS :
    // description
    validateMap(context, api, externalDocs.getExtensions(), results, false, CRUMB_EXTENSIONS, Regexes.EXT_REGEX, null);
    validateUrl(api, externalDocs.getUrl(), results, true, true, CRUMB_EXTERNALDOCS);
  }
}
