package org.openapi4j.parser.validation.v3;

import org.openapi4j.core.validation.ValidationResults;
import org.openapi4j.parser.model.v3.Callback;
import org.openapi4j.parser.model.v3.OpenApi3;

import static org.openapi4j.parser.validation.v3.OAI3Keywords.$REF;
import static org.openapi4j.parser.validation.v3.OAI3Keywords.EXTENSIONS;

class CallbackValidator extends ExpressionValidator<Callback> {
  private static final CallbackValidator INSTANCE = new CallbackValidator();

  private CallbackValidator() {
  }

  public static CallbackValidator instance() {
    return INSTANCE;
  }

  @Override
  public void validate(OpenApi3 api, Callback callback, ValidationResults results) {
    if (callback.isRef()) {
      validateReference(api, callback, results, $REF, CallbackValidator.instance(), Callback.class);
    } else {
      validateMap(api, callback.getCallbackPaths(), results, false, null, Regexes.NOEXT_REGEX, PathValidator.instance());
      validateMap(api, callback.getExtensions(), results, false, EXTENSIONS, Regexes.EXT_REGEX, null);
    }
  }
}
