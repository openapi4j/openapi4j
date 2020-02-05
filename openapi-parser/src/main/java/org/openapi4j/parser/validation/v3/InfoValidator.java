package org.openapi4j.parser.validation.v3;

import org.openapi4j.core.validation.ValidationResults;
import org.openapi4j.core.validation.ValidationSeverity;
import org.openapi4j.parser.model.v3.Info;
import org.openapi4j.parser.model.v3.OpenApi3;
import org.openapi4j.parser.validation.Validator;

import static org.openapi4j.parser.validation.v3.OAI3Keywords.CONTACT;
import static org.openapi4j.parser.validation.v3.OAI3Keywords.EXTENSIONS;
import static org.openapi4j.parser.validation.v3.OAI3Keywords.LICENSE;
import static org.openapi4j.parser.validation.v3.OAI3Keywords.TERMSOFSERVICE;
import static org.openapi4j.parser.validation.v3.OAI3Keywords.TITLE;
import static org.openapi4j.parser.validation.v3.OAI3Keywords.VERSION;

class InfoValidator extends Validator3Base<OpenApi3, Info> {
  private static final Validator<OpenApi3, Info> INSTANCE = new InfoValidator();

  private InfoValidator() {
  }

  public static Validator<OpenApi3, Info> instance() {
    return INSTANCE;
  }

  @Override
  public void validate(OpenApi3 api, Info info, ValidationResults results) {
    // VALIDATION EXCLUSIONS :
    // description
    validateField(api, info.getContact(), results, false, CONTACT, ContactValidator.instance());
    validateMap(api, info.getExtensions(), results, false, EXTENSIONS, Regexes.EXT_REGEX, null);
    validateField(api, info.getLicense(), results, false, LICENSE, LicenseValidator.instance());
    validateUrl(info.getTermsOfService(), results, false, false, TERMSOFSERVICE, ValidationSeverity.WARNING);
    validateString(info.getTitle(), results, true, TITLE);
    validateString(info.getVersion(), results, true, VERSION);
  }
}
