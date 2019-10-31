package org.openapi4j.parser.validation.v3;

import org.openapi4j.core.validation.ValidationResults;
import org.openapi4j.parser.model.v3.Extensions;
import org.openapi4j.parser.model.v3.OpenApi3;
import org.openapi4j.parser.validation.Validator;

import static org.openapi4j.parser.validation.v3.OAI3Keywords.EXTENSIONS;

class ExtensionsValidator extends Validator3Base<OpenApi3, Extensions> {
  private static final Validator<OpenApi3, Extensions> INSTANCE = new ExtensionsValidator();

  private ExtensionsValidator() {
  }

  public static Validator<OpenApi3, Extensions> instance() {
    return INSTANCE;
  }

  @Override
  public void validate(OpenApi3 api, Extensions extensions, ValidationResults results) {
    if (extensions != null) {
      validateMap(api, extensions.getExtensions(), results, false, EXTENSIONS, Regexes.EXT_REGEX, null);
    }
  }
}
