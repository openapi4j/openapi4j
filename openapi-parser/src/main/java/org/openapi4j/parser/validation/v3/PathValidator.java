package org.openapi4j.parser.validation.v3;

import org.openapi4j.core.validation.ValidationResults;
import org.openapi4j.parser.model.v3.OpenApi3;
import org.openapi4j.parser.model.v3.Path;
import org.openapi4j.parser.validation.ValidationContext;
import org.openapi4j.parser.validation.Validator;

import static org.openapi4j.parser.validation.v3.OAI3Keywords.*;

class PathValidator extends Validator3Base<OpenApi3, Path> {
  private static final Validator<OpenApi3, Path> INSTANCE = new PathValidator();

  private PathValidator() {
  }

  public static Validator<OpenApi3, Path> instance() {
    return INSTANCE;
  }

  @Override
  public void validate(ValidationContext<OpenApi3> context, OpenApi3 api, Path path, ValidationResults results) {
    if (path.isRef()) {
      validateReference(context, api, path, results, CRUMB_$REF, PathValidator.instance(), Path.class);
    } else {
      // VALIDATION EXCLUSIONS :
      // description, summary
      validateMap(context, api, path.getExtensions(), results, false, CRUMB_EXTENSIONS, Regexes.EXT_REGEX, null);
      validateMap(context, api, path.getOperations(), results, false, null, Regexes.METHOD_REGEX, OperationValidator.instance());
      validateList(context, api, path.getParameters(), results, false, 0, CRUMB_PARAMETERS, ParameterValidator.instance());
      validateList(context, api, path.getServers(), results, false, 0, CRUMB_SERVERS, ServerValidator.instance());
    }
  }
}
