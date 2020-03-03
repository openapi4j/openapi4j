package org.openapi4j.parser.validation.v3;

import org.openapi4j.core.validation.ValidationResults;
import org.openapi4j.core.validation.ValidationSeverity;
import org.openapi4j.parser.model.v3.License;
import org.openapi4j.parser.model.v3.OpenApi3;
import org.openapi4j.parser.validation.ValidationContext;
import org.openapi4j.parser.validation.Validator;

import static org.openapi4j.parser.validation.v3.OAI3Keywords.EXTENSIONS;
import static org.openapi4j.parser.validation.v3.OAI3Keywords.NAME;
import static org.openapi4j.parser.validation.v3.OAI3Keywords.URL;

class LicenseValidator extends Validator3Base<OpenApi3, License> {
  private static final Validator<OpenApi3, License> INSTANCE = new LicenseValidator();

  private LicenseValidator() {
  }

  public static Validator<OpenApi3, License> instance() {
    return INSTANCE;
  }

  @Override
  public void validate(ValidationContext<OpenApi3> context, OpenApi3 api, License license, ValidationResults results) {
    validateMap(context, api, license.getExtensions(), results, false, EXTENSIONS, Regexes.EXT_REGEX, null);
    validateString(license.getName(), results, true, NAME);
    validateUrl(license.getUrl(), results, false, false, URL, ValidationSeverity.ERROR);
  }
}
