package org.openapi4j.parser.validation.v3;

import org.openapi4j.core.validation.ValidationResults;
import org.openapi4j.parser.model.v3.OpenApi3;
import org.openapi4j.parser.model.v3.Tag;
import org.openapi4j.parser.validation.Validator;

import static org.openapi4j.parser.validation.v3.OAI3Keywords.EXTENSIONS;
import static org.openapi4j.parser.validation.v3.OAI3Keywords.EXTERNALDOCS;
import static org.openapi4j.parser.validation.v3.OAI3Keywords.NAME;

class TagValidator extends Validator3Base<OpenApi3, Tag> {
  private static final Validator<OpenApi3, Tag> INSTANCE = new TagValidator();

  private TagValidator() {
  }

  public static Validator<OpenApi3, Tag> instance() {
    return INSTANCE;
  }

  @Override
  public void validate(OpenApi3 api, Tag tag, ValidationResults results) {
    validateString(tag.getName(), results, true, NAME);
    validateField(api, tag.getExternalDocs(), results, false, EXTERNALDOCS, ExternalDocsValidator.instance());
    validateField(api, tag.getExtensions(), results, false, EXTENSIONS, ExtensionsValidator.instance());
  }
}
