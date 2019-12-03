package org.openapi4j.parser.validation.v3;

import org.openapi4j.core.validation.ValidationResults;
import org.openapi4j.parser.model.v3.Discriminator;
import org.openapi4j.parser.model.v3.OpenApi3;
import org.openapi4j.parser.validation.Validator;

import static org.openapi4j.parser.validation.v3.OAI3Keywords.PROPERTYNAME;

class DiscriminatorValidator extends Validator3Base<OpenApi3, Discriminator> {
  private static final Validator<OpenApi3, Discriminator> INSTANCE = new DiscriminatorValidator();

  private DiscriminatorValidator() {
  }

  public static Validator<OpenApi3, Discriminator> instance() {
    return INSTANCE;
  }

  @Override
  public void validate(OpenApi3 api, Discriminator discriminator, ValidationResults results) {
    // mapping references are checked in parsing phase.
    validateString(discriminator.getPropertyName(), results, true, PROPERTYNAME);
  }
}
