package org.openapi4j.parser.validation.v3;

import org.openapi4j.core.validation.ValidationResults;
import org.openapi4j.parser.model.v3.Contact;
import org.openapi4j.parser.model.v3.OpenApi3;
import org.openapi4j.parser.validation.ValidationContext;
import org.openapi4j.parser.validation.Validator;

import static org.openapi4j.parser.validation.v3.OAI3Keywords.*;

public class ContactValidator extends Validator3Base<OpenApi3, Contact> {
  private static final Validator<OpenApi3, Contact> INSTANCE = new ContactValidator();

  private ContactValidator() {
  }

  public static Validator<OpenApi3, Contact> instance() {
    return INSTANCE;
  }

  @Override
  public void validate(ValidationContext<OpenApi3> context, OpenApi3 api, Contact contact, ValidationResults results) {
    validateString(contact.getEmail(), results, false, EMAIL_REGEX, CRUMB_EMAIL);
    validateMap(context, api, contact.getExtensions(), results, false, CRUMB_EXTENSIONS, Regexes.EXT_REGEX, null);
    validateString(contact.getName(), results, false, CRUMB_NAME);
    validateUrl(api, contact.getUrl(), results, false, CRUMB_URL);
  }
}
