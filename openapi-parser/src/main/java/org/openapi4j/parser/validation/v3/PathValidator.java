package org.openapi4j.parser.validation.v3;

import org.openapi4j.core.validation.ValidationResults;
import org.openapi4j.parser.model.v3.OpenApi3;
import org.openapi4j.parser.model.v3.Path;
import org.openapi4j.parser.validation.Validator;

import static org.openapi4j.parser.validation.v3.OAI3Keywords.EXTENSIONS;
import static org.openapi4j.parser.validation.v3.OAI3Keywords.PARAMETERS;
import static org.openapi4j.parser.validation.v3.OAI3Keywords.SERVERS;

class PathValidator extends Validator3Base<OpenApi3, Path> {
  private static final Validator<OpenApi3, Path> INSTANCE = new PathValidator();

  private PathValidator() {
  }

  public static Validator<OpenApi3, Path> instance() {
    return INSTANCE;
  }

  @Override
  public void validate(OpenApi3 api, Path path, ValidationResults results) {
    // VALIDATION EXCLUSIONS :
    // description, summary
    validateField(api, path.getExtensions(), results, false, EXTENSIONS, ExtensionsValidator.instance());
    validateMap(api, path.getOperations(), results, false, null, Regexes.METHOD_REGEX, OperationValidator.instance());
    validateList(api, path.getParameters(), results, false, PARAMETERS, ParameterValidator.instance());
    validateList(api, path.getServers(), results, false, SERVERS, ServerValidator.instance());
  }
}
