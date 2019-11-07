package org.openapi4j.parser.validation.v3;

import org.openapi4j.core.validation.ValidationResults;
import org.openapi4j.parser.model.v3.Callback;
import org.openapi4j.parser.model.v3.OpenApi3;
import org.openapi4j.parser.model.v3.Operation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.openapi4j.parser.validation.v3.OAI3Keywords.$REF;
import static org.openapi4j.parser.validation.v3.OAI3Keywords.CALLBACKS;
import static org.openapi4j.parser.validation.v3.OAI3Keywords.EXTENSIONS;

class CallbackValidator extends ExpressionValidator<Callback> {
  private static final String PARAM_NOT_FOUND_ERR_MSG = "Parameter with path '%s' not found on operation.";

  private static final Pattern PATTERN_PARAM = Pattern.compile("\\{(.*?)}");

  private static final CallbackValidator INSTANCE = new CallbackValidator();

  private CallbackValidator() {
  }

  public static CallbackValidator instance() {
    return INSTANCE;
  }

  @Override
  public void validate(OpenApi3 api, Callback callback, ValidationResults results) {
    if (callback.isRef()) {
      validateReference(api, callback.getRef(), results, $REF, CallbackValidator.instance(), Callback.class);
    } else {
      validateMap(api, callback.getCallbackPaths(), results, false, null, Regexes.NOEXT_REGEX, PathValidator.instance());
      validateMap(api, callback.getExtensions(), results, false, EXTENSIONS, Regexes.EXT_REGEX, null);
    }
  }

  // This called from operation validator
  void validateWithOperation(OpenApi3 api, Operation operation, String expression, ValidationResults results) {
    Matcher matcher = PATTERN_PARAM.matcher(expression);
    while (matcher.find()) {
      if (
        !checkRequestParameter(api, operation, matcher.group(1), CALLBACKS, results) &&
        !checkResponseParameter(api, operation, matcher.group(1), CALLBACKS, results)
      ) {
        results.addError(String.format(PARAM_NOT_FOUND_ERR_MSG, matcher.group(1)), CALLBACKS);
      }
    }
  }
}
