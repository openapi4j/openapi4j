package org.openapi4j.parser.validation.v3;

import org.openapi4j.core.exception.DecodeException;
import org.openapi4j.core.model.reference.Reference;
import org.openapi4j.core.validation.ValidationResults;
import org.openapi4j.parser.model.v3.OpenApi3;
import org.openapi4j.parser.validation.Validator;
import org.openapi4j.parser.validation.ValidatorBase;

abstract class Validator3Base<O extends OpenApi3, T> extends ValidatorBase<O, T> {
  private static final String REF_MISSING = "Missing $ref '%s'";
  private static final String REF_CONTENT_UNREADABLE = "Unable to read $ref content for '%s' pointer.";
  private static final String EMAIL_INVALID = "Invalid email address '%s'";

  <F> F getReferenceContent(final OpenApi3 api,
                            final String $ref,
                            final ValidationResults results,
                            final String crumb,
                            final Class<F> clazz) {

    Reference reference = api.getContext().getReferenceRegistry().getRef($ref);

    if (reference == null || reference.getContent().isMissingNode()) {
      results.addError(String.format(REF_MISSING, $ref), crumb);
    } else {
      try {
        return reference.getMappedContent(clazz);
      } catch (DecodeException e) {
        results.addError(String.format(REF_CONTENT_UNREADABLE, $ref), crumb);
      }
    }

    return null;
  }

  <F> void validateReference(final OpenApi3 api,
                             final String $ref,
                             final ValidationResults results,
                             final String crumb,
                             final Validator<OpenApi3, F> validator,
                             final Class<F> clazz) {

    F content = getReferenceContent(api, $ref, results, crumb, clazz);
    if (content != null) {
      validator.validate(api, content, results);
    }
  }

  @Override
  protected void checkEmail(String email, ValidationResults results, String crumb) {
    if (!Regexes.EMAIL_REGEX.matcher(email).matches()) {
      results.addError(String.format(EMAIL_INVALID, email), crumb);
    }
  }
}
