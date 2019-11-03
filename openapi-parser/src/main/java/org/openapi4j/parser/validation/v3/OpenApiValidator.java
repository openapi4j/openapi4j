package org.openapi4j.parser.validation.v3;

import org.openapi4j.core.model.reference.Reference;
import org.openapi4j.core.validation.ValidationResults;
import org.openapi4j.parser.model.v3.OpenApi3;
import org.openapi4j.parser.model.v3.Operation;
import org.openapi4j.parser.model.v3.Parameter;
import org.openapi4j.parser.model.v3.Path;
import org.openapi4j.parser.validation.Validator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.openapi4j.parser.validation.v3.OAI3Keywords.*;

class OpenApiValidator extends Validator3Base<OpenApi3, OpenApi3> {
  private static final String MISSING_REF_IN_PATH = "Missing $ref '%s' for parameter in path '%s'";
  private static final String REQUIRED_PATH_PARAM = "Parameter '%s' in path '%s' must have 'required' property set to true";
  private static final String UNEXPECTED_PATH_PARAM = "Path parameter '%s' in path '%s' is unexpected";
  private static final String MISMATCH_PATH_PARAM = "Path parameter '%s' in path '%s' is expected but undefined";

  private static final Pattern PATTERN_PATH_PARAM = Pattern.compile("/\\{(\\w+)\\}");
  private static final Pattern PATTERN_OAI3 = Pattern.compile("3\\.\\d+(\\.\\d+.*)?");

  private static final Validator<OpenApi3, OpenApi3> INSTANCE = new OpenApiValidator();

  private OpenApiValidator() {
  }

  public static Validator<OpenApi3, OpenApi3> instance() {
    return INSTANCE;
  }

  @Override
  public void validate(OpenApi3 root, OpenApi3 api, ValidationResults results) {
    validateString(api.getOpenapi(), results, true, PATTERN_OAI3, OPENAPI);
    validateField(api, api.getInfo(), results, true, INFO, InfoValidator.instance());
    validateList(api, api.getServers(), results, false, SERVERS, ServerValidator.instance());
    validateMap(api, api.getPaths(), results, true, PATHS, Regexes.PATH_REGEX, PathValidator.instance());
    validateField(api, api.getComponents(), results, false, COMPONENTS, ComponentsValidator.instance());
    validateField(api, api.getSecurity(), results, false, SECURITY, SecurityRequirementValidator.instance());
    validateList(api, api.getTags(), results, false, TAGS, TagValidator.instance());
    validateField(api, api.getExternalDocs(), results, false, EXTERNALDOCS, ExternalDocsValidator.instance());
    validateField(api, api.getExtensions(), results, false, EXTENSIONS, ExtensionsValidator.instance());

    checkOperationsParams(api, api.getPaths(), results);
  }

  private void checkOperationsParams(OpenApi3 api, Map<String, Path> paths, ValidationResults results) {
    if (paths == null) {
      return;
    }

    for (Map.Entry<String, Path> pathEntry : paths.entrySet()) {
      String path = pathEntry.getKey();

      final List<String> pathParams = getPathParams(path);

      for (Operation operation : pathEntry.getValue().getOperations().values()) {
        Collection<Parameter> parameters = operation.getParameters();

        List<String> discoveredPathParameters = new ArrayList<>();
        if (parameters != null) {
          for (Parameter parameter : parameters) {
            String paramName = checkPathParam(api, path, pathParams, parameter, results);
            if (paramName != null) {
              discoveredPathParameters.add(paramName);
            }
          }
        }

        // Check that all path parameters are in the path
        validatePathParametersMatching(path, pathParams, discoveredPathParameters, results);
      }
    }
  }

  private String checkPathParam(OpenApi3 api, String path, List<String> pathParams, Parameter parameter, ValidationResults results) {
    String in;
    Boolean required;
    String name;
    if (parameter.isRef()) {
      Reference reference = api.getContext().getReferenceRegistry().getRef(parameter.getRef());
      if (reference == null) {
        results.addError(String.format(MISSING_REF_IN_PATH, parameter.getRef(), path), $REF);
        return null;
      }

      in = reference.getContent().path(IN).textValue();
      required = reference.getContent().path(REQUIRED).booleanValue();
      name = reference.getContent().path(NAME).textValue();
    } else {
      in = parameter.getIn();
      required = parameter.getRequired();
      name = parameter.getName();
    }

    return checkPathParam(path, in, required, name, pathParams, results);
  }

  private String checkPathParam(String path, String in, Boolean required, String name, List<String> pathParams, ValidationResults results) {
    if (!PATH.equals(in)) {
      return null;
    }

    if (!Boolean.TRUE.equals(required)) {
      results.addError(String.format(REQUIRED_PATH_PARAM, name, path), REQUIRED);
    }

    // Name is required but could be missing in spec definition
    if (name == null) {
      return null;
    }

    if (!pathParams.contains(name)) {
      results.addError(String.format(UNEXPECTED_PATH_PARAM, name, path), NAME);
    }

    return name;
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
        results.addError(String.format(MISMATCH_PATH_PARAM, name, path), NAME);
      }
    }
  }
}
