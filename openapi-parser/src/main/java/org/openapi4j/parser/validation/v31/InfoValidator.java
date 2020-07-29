package org.openapi4j.parser.validation.v31;

import org.openapi4j.core.validation.ValidationResults;
import org.openapi4j.parser.model.v3.Info;
import org.openapi4j.parser.model.v3.OpenApi3;
import org.openapi4j.parser.validation.ValidationContext;
import org.openapi4j.parser.validation.Validator;
import org.openapi4j.parser.validation.v3.ContactValidator;
import org.openapi4j.parser.validation.v3.Regexes;
import org.openapi4j.parser.validation.v3.Validator3Base;

import static org.openapi4j.parser.validation.v3.OAI3Keywords.CRUMB_CONTACT;
import static org.openapi4j.parser.validation.v3.OAI3Keywords.CRUMB_EXTENSIONS;
import static org.openapi4j.parser.validation.v3.OAI3Keywords.CRUMB_LICENSE;
import static org.openapi4j.parser.validation.v3.OAI3Keywords.CRUMB_TERMSOFSERVICE;
import static org.openapi4j.parser.validation.v3.OAI3Keywords.CRUMB_TITLE;
import static org.openapi4j.parser.validation.v3.OAI3Keywords.CRUMB_VERSION;
import static org.openapi4j.parser.validation.v31.OAI31Keywords.CRUMB_SUMMARY;

public class InfoValidator extends Validator3Base<OpenApi3, Info> {
  private static final Validator<OpenApi3, Info> INSTANCE = new InfoValidator();

  private InfoValidator() {
  }

  public static Validator<OpenApi3, Info> instance() {
    return INSTANCE;
  }

  @Override
  public void validate(ValidationContext<OpenApi3> context, OpenApi3 api, Info info, ValidationResults results) {
    // VALIDATION EXCLUSIONS :
    // description
    validateField(context, api, info.getContact(), results, false, CRUMB_CONTACT, ContactValidator.instance());
    validateMap(context, api, info.getExtensions(), results, false, CRUMB_EXTENSIONS, Regexes.EXT_REGEX, null);
    validateField(context, api, info.getLicense(), results, false, CRUMB_LICENSE, LicenseValidator.instance()); // 3.1
    validateUrl(api, info.getTermsOfService(), results, false, CRUMB_TERMSOFSERVICE);
    validateString(info.getTitle(), results, true, CRUMB_TITLE);
    validateString(info.getVersion(), results, true, CRUMB_VERSION);
    validateString(info.getSummary(), results, false, CRUMB_SUMMARY); // 3.1
  }
}
