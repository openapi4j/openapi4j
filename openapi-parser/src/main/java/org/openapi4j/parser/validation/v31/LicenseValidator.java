package org.openapi4j.parser.validation.v31;

import org.openapi4j.core.validation.ValidationResults;
import org.openapi4j.parser.model.v3.License;
import org.openapi4j.parser.model.v3.OpenApi3;
import org.openapi4j.parser.validation.ValidationContext;
import org.openapi4j.parser.validation.Validator;
import org.openapi4j.parser.validation.v3.Regexes;
import org.openapi4j.parser.validation.v3.Validator3Base;

import static org.openapi4j.parser.validation.v3.OAI3Keywords.CRUMB_EXTENSIONS;
import static org.openapi4j.parser.validation.v3.OAI3Keywords.CRUMB_NAME;
import static org.openapi4j.parser.validation.v3.OAI3Keywords.CRUMB_URL;
import static org.openapi4j.parser.validation.v31.OAI31Keywords.CRUMB_IDENTIFIER;

public class LicenseValidator extends Validator3Base<OpenApi3, License> {
  private static final Validator<OpenApi3, License> INSTANCE = new LicenseValidator();

  private LicenseValidator() {
  }

  public static Validator<OpenApi3, License> instance() {
    return INSTANCE;
  }

  @Override
  public void validate(ValidationContext<OpenApi3> context, OpenApi3 api, License license, ValidationResults results) {
    validateMap(context, api, license.getExtensions(), results, false, CRUMB_EXTENSIONS, Regexes.EXT_REGEX, null);
    validateString(license.getName(), results, true, CRUMB_NAME);
    validateUrl(api, license.getUrl(), results, false, CRUMB_URL);
    // 3.1
    validateUrl(api, license.getIdentifier(), results, false, CRUMB_IDENTIFIER);
  }
}
