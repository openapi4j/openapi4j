package org.openapi4j.parser.validation.v3;

import org.openapi4j.core.model.reference.Reference;
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

import static org.openapi4j.core.validation.ValidationSeverity.ERROR;
import static org.openapi4j.parser.validation.v3.OAI3Keywords.$REF;
import static org.openapi4j.parser.validation.v3.OAI3Keywords.COMPONENTS;
import static org.openapi4j.parser.validation.v3.OAI3Keywords.EXTENSIONS;
import static org.openapi4j.parser.validation.v3.OAI3Keywords.EXTERNALDOCS;
import static org.openapi4j.parser.validation.v3.OAI3Keywords.IN;
import static org.openapi4j.parser.validation.v3.OAI3Keywords.INFO;
import static org.openapi4j.parser.validation.v3.OAI3Keywords.NAME;
import static org.openapi4j.parser.validation.v3.OAI3Keywords.OPENAPI;
import static org.openapi4j.parser.validation.v3.OAI3Keywords.PATH;
import static org.openapi4j.parser.validation.v3.OAI3Keywords.PATHS;
import static org.openapi4j.parser.validation.v3.OAI3Keywords.REQUIRED;
import static org.openapi4j.parser.validation.v3.OAI3Keywords.SECURITY;
import static org.openapi4j.parser.validation.v3.OAI3Keywords.SERVERS;
import static org.openapi4j.parser.validation.v3.OAI3Keywords.TAGS;

class OpenApiValidator extends Validator3Base<OpenApi3, OpenApi3> {
  private static final ValidationResult REQUIRED_PATH_PARAM = new ValidationResult(ERROR, 122, "Parameter '%s' in path '%s' must have 'required' property set to true");
  private static final ValidationResult UNEXPECTED_PATH_PARAM = new ValidationResult(ERROR, 123, "Path parameter '%s' in path '%s' is unexpected");
  private static final ValidationResult MISMATCH_PATH_PARAM = new ValidationResult(ERROR, 124, "Path parameter '%s' in path '%s' is expected but undefined");

  private static final Pattern PATTERN_PATH_PARAM = Pattern.compile("/\\{(\\w+)\\}");
  private static final Pattern PATTERN_OAI3 = Pattern.compile("3\\.\\d+(\\.\\d+.*)?");

  private static final Validator<OpenApi3, OpenApi3> INSTANCE = new OpenApiValidator();

  private OpenApiValidator() {
  }

  public static Validator<OpenApi3, OpenApi3> instance() {
    return INSTANCE;
  }

  @Override
  public void validate(ValidationContext<OpenApi3> context, OpenApi3 root, OpenApi3 api, ValidationResults results) {
    validateString(api.getOpenapi(), results, true, PATTERN_OAI3, OPENAPI);
    validateField(context, api, api.getInfo(), results, true, INFO, InfoValidator.instance());
    validateList(context, api, api.getServers(), results, false, SERVERS, ServerValidator.instance());
    validateMap(context, api, api.getPaths(), results, true, PATHS, Regexes.PATH_REGEX, PathValidator.instance());
    validateField(context, api, api.getComponents(), results, false, COMPONENTS, ComponentsValidator.instance());
    validateList(context, api, api.getSecurityRequirements(), results, false, SECURITY, SecurityRequirementValidator.instance());
    validateList(context, api, api.getTags(), results, false, TAGS, TagValidator.instance());
    validateField(context, api, api.getExternalDocs(), results, false, EXTERNALDOCS, ExternalDocsValidator.instance());
    validateMap(context, api, api.getExtensions(), results, false, EXTENSIONS, Regexes.EXT_REGEX, null);

    checkOperationsParams(api, api.getPaths(), results);
  }

  private void checkOperationsParams(OpenApi3 api, Map<String, Path> paths, ValidationResults results) {
    if (paths == null) {
      return;
    }

    for (Map.Entry<String, Path> pathEntry : paths.entrySet()) {
      String path = pathEntry.getKey();
      final List<String> pathParams = getPathParams(path);

      Path pathItem = pathEntry.getValue();
      if (pathItem.isRef()) pathItem = getReferenceContent(api, pathItem, results, $REF, Path.class);
      for (Operation operation : pathItem.getOperations().values()) {
        discoverAndCheckParams(api, path, pathParams, operation.getParameters(), results);
      }
    }
  }

  private void discoverAndCheckParams(OpenApi3 api,
                                      String path,
                                      List<String> pathParams,
                                      Collection<Parameter> parameters,
                                      ValidationResults results) {

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

  private String checkPathParam(OpenApi3 api, String path, List<String> pathParams, Parameter parameter, ValidationResults results) {
    String in = null;
    boolean required = true;
    String name = null;

    if (parameter.isRef()) {
      Reference reference = parameter.getReference(api.getContext());
      if (reference != null && reference.getContent() != null) {
        in = reference.getContent().path(IN).textValue();
        required = reference.getContent().path(REQUIRED).booleanValue();
        name = reference.getContent().path(NAME).textValue();
      }
    } else {
      in = parameter.getIn();
      required = parameter.isRequired();
      name = parameter.getName();
    }

    return checkPathParam(path, in, required, name, pathParams, results);
  }

  private String checkPathParam(String path, String in, boolean required, String name, List<String> pathParams, ValidationResults results) {
    if (!PATH.equals(in)) {
      return null;
    }

    if (!required) {
      results.add(REQUIRED, REQUIRED_PATH_PARAM, name, path);
    }

    // Name is required but could be missing in spec definition
    if (name == null) {
      return null;
    }

    if (!pathParams.contains(name)) {
      results.add(NAME, UNEXPECTED_PATH_PARAM, name, path);
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
        results.add(NAME, MISMATCH_PATH_PARAM, name, path);
      }
    }
  }
}
