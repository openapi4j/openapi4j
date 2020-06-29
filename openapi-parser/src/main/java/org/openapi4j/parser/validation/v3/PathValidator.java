package org.openapi4j.parser.validation.v3;

import org.openapi4j.core.validation.ValidationResult;
import org.openapi4j.core.validation.ValidationResults;
import org.openapi4j.parser.model.v3.OpenApi3;
import org.openapi4j.parser.model.v3.Operation;
import org.openapi4j.parser.model.v3.Parameter;
import org.openapi4j.parser.model.v3.Path;
import org.openapi4j.parser.validation.ValidationContext;
import org.openapi4j.parser.validation.Validator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.openapi4j.core.validation.ValidationSeverity.ERROR;
import static org.openapi4j.parser.validation.v3.OAI3Keywords.*;

class PathValidator extends Validator3Base<OpenApi3, Path> {
  private static final Validator<OpenApi3, Path> INSTANCE = new PathValidator();

  private static final ValidationResult REQUIRED_PATH_PARAM = new ValidationResult(ERROR, 122, "Parameter '%s' in path '%s' must have 'required' property set to true");
  private static final ValidationResult UNEXPECTED_PATH_PARAM = new ValidationResult(ERROR, 123, "Path parameter '%s' in path '%s' is unexpected");
  private static final ValidationResult MISMATCH_PATH_PARAM = new ValidationResult(ERROR, 124, "Path parameter '%s' in path '%s' is expected but undefined");

  private static final Pattern PATTERN_PATH_PARAM = Pattern.compile("/\\{(\\w+)}");

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

      checkPathsParams(api, api.getPaths(), results);
    }
  }

  private void checkPathsParams(OpenApi3 api, Map<String, Path> paths, ValidationResults results) {
    for (Map.Entry<String, Path> pathEntry : paths.entrySet()) {
      String path = pathEntry.getKey();
      final List<String> pathParams = getPathParams(path);

      Path pathItem = pathEntry.getValue();
      if (pathItem.isRef()) pathItem = getReferenceContent(api, pathItem, results, CRUMB_$REF, Path.class);

      final Map<String, Operation> operations = pathItem.getOperations();

      if (operations == null) {
        // Check parameters from path only
        discoverAndCheckParams(path, pathParams, pathItem.getParameters(), results);
      } else {
        // Check parameters from both path & operation
        for (Operation operation : operations.values()) {
          final List<Parameter> params = mergePathParameters(api, pathItem, operation);
          discoverAndCheckParams(path, pathParams, params, results);
        }
      }
    }
  }

  private List<Parameter> mergePathParameters(final OpenApi3 api,
                                              final Path path,
                                              final Operation operation) {

    final List<Parameter> opParams = operation.getParametersIn(api.getContext(), PATH);
    if (opParams.isEmpty()) {
      return path.getParameters();
    } else {
      final List<Parameter> pathParams = path.getParametersIn(api.getContext(), PATH);
      return Stream
        .concat(opParams.stream(), pathParams.stream())
        .distinct()
        .collect(Collectors.toList());
    }
  }

  private void discoverAndCheckParams(String path,
                                      List<String> pathParams,
                                      Collection<Parameter> parameters,
                                      ValidationResults results) {

    List<String> discoveredPathParameters = new ArrayList<>();
    if (parameters != null) {
      for (Parameter parameter : parameters) {
        String paramName = checkPathParam(path, parameter, pathParams, results);
        if (paramName != null) {
          discoveredPathParameters.add(paramName);
        }
      }
    }

    // Check that all path parameters are in the path
    validatePathParametersMatching(path, pathParams, discoveredPathParameters, results);
  }

  private String checkPathParam(String path, Parameter parameter, List<String> pathParams, ValidationResults results) {
    if (!parameter.isRequired()) {
      results.add(CRUMB_REQUIRED, REQUIRED_PATH_PARAM, parameter.getName(), path);
    }

    // Name is required but could be missing in spec definition
    if (parameter.getName() == null) {
      return null;
    }

    if (!pathParams.contains(parameter.getName())) {
      results.add(CRUMB_NAME, UNEXPECTED_PATH_PARAM, parameter.getName(), path);
    }

    return parameter.getName();
  }

  private List<String> getPathParams(String path) {
    Matcher matcher = PATTERN_PATH_PARAM.matcher(path);

    final List<String> pathParams = new ArrayList<>();
    while (matcher.find()) {
      pathParams.add(matcher.group(1));
    }

    return pathParams;
  }

  private void validatePathParametersMatching(String path, List<String> refParams, List<String> discoveredParams, ValidationResults results) {
    for (String name : refParams) {
      if (!discoveredParams.contains(name)) {
        results.add(CRUMB_NAME, MISMATCH_PATH_PARAM, name, path);
      }
    }
  }
}
