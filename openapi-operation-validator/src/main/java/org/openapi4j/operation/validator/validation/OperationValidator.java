package org.openapi4j.operation.validator.validation;

import com.fasterxml.jackson.databind.JsonNode;
import org.openapi4j.core.model.v3.OAI3;
import org.openapi4j.core.validation.ValidationResult;
import org.openapi4j.core.validation.ValidationResults;
import org.openapi4j.operation.validator.model.Request;
import org.openapi4j.operation.validator.model.impl.Body;
import org.openapi4j.operation.validator.model.impl.MediaTypeContainer;
import org.openapi4j.operation.validator.util.PathResolver;
import org.openapi4j.operation.validator.util.convert.ParameterConverter;
import org.openapi4j.parser.model.v3.*;
import org.openapi4j.schema.validator.ValidationContext;
import org.openapi4j.schema.validator.ValidationData;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;
import static org.openapi4j.core.validation.ValidationSeverity.ERROR;

/**
 * Validator for OpenAPI Operation.
 * It validates all aspects of interaction between request and response for a given Operation.
 */
public class OperationValidator<V> {
  // Error messages
  private static final String VALIDATION_CTX_REQUIRED_ERR_MSG = "Validation context is required.";
  private static final String OAI_REQUIRED_ERR_MSG = "OpenAPI is required.";
  private static final String PATH_REQUIRED_ERR_MSG = "Path is required.";
  private static final String OPERATION_REQUIRED_ERR_MSG = "Operation is required.";
  private static final ValidationResult BODY_CONTENT_TYPE_ERR = new ValidationResult(ERROR, 202, "Body content type cannot be determined. No 'Content-Type' header available.");
  private static final ValidationResult BODY_WRONG_CONTENT_TYPE_ERR = new ValidationResult(ERROR, 203, "Content type '%s' is not allowed for body content.");
  private static final ValidationResult RESPONSE_STATUS_NOT_FOUND_ERR = new ValidationResult(ERROR, 204, "Response status '%s', ranged or default has not been found.");
  private static final ValidationResult PATH_NOT_FOUND_ERR = new ValidationResult(ERROR, 205, "Path template '%s' has not been found from value '%s'.");

  // Parameter specifics
  private static final String IN_PATH = "path";
  private static final String IN_QUERY = "query";
  private static final String IN_HEADER = "header";
  private static final String IN_COOKIE = "cookie";
  private static final String DEFAULT_RESPONSE_CODE = "default";
  private static final ValidationResults.CrumbInfo CRUMB_PATH = new ValidationResults.CrumbInfo(IN_PATH, true);
  // Validators
  private final ParameterValidator<Parameter, V> specRequestPathValidator;
  private final ParameterValidator<Parameter, V> specRequestQueryValidator;
  private final ParameterValidator<Parameter, V> specRequestHeaderValidator;
  private final ParameterValidator<Parameter, V> specRequestCookieValidator;
  // Map<content type, validator>
  private final Map<MediaTypeContainer, BodyValidator<V>> specRequestBodyValidators;
  // Map<status code, Map<content type, validator>>
  private final Map<String, Map<MediaTypeContainer, BodyValidator<V>>> specResponseBodyValidators;
  // Map<status code, validator>
  private final Map<String, ParameterValidator<Header, V>> specResponseHeaderValidators;
  private final ValidationContext<OAI3, V> context;
  private final OpenApi3 openApi;
  private final Operation operation;
  private final String templatePath;
  private final List<Pattern> pathPatterns;

  /**
   * Creates a validator for the given operation.
   *
   * @param openApi   The full Document Description where the Operation is located.
   * @param path      The Path of the Operation.
   * @param operation The Operation to validate.
   */
  public OperationValidator(final OpenApi3 openApi, final Path path, final Operation operation) {
    this(new ValidationContext<>(openApi.getContext()), openApi, path, operation);
  }

  /**
   * Creates a validator for the given operation.
   *
   * @param context   The validation context for additional or changing behaviours.
   * @param openApi   The full Document Description where the Operation is located.
   * @param path      The Path of the Operation.
   * @param operation The Operation to validate.
   */
  @SuppressWarnings("WeakerAccess")
  public OperationValidator(final ValidationContext<OAI3, V> context,
                            final OpenApi3 openApi,
                            final Path path,
                            final Operation operation) {
    this(context, null, openApi, path, operation);
  }

  /**
   * Creates a validator for the given operation.
   *
   * @param context      The validation context for additional or changing behaviours.
   * @param pathPatterns Pattern for the current path related to servers or OAI Document origin.
   * @param openApi      The full Document Description where the Operation is located.
   * @param path         The Path of the Operation.
   * @param operation    The Operation to validate.
   */
  OperationValidator(final ValidationContext<OAI3, V> context,
                     final List<Pattern> pathPatterns,
                     final OpenApi3 openApi,
                     final Path path,
                     final Operation operation) {

    this.context = requireNonNull(context, VALIDATION_CTX_REQUIRED_ERR_MSG);
    this.openApi = requireNonNull(openApi, OAI_REQUIRED_ERR_MSG);
    requireNonNull(operation, OPERATION_REQUIRED_ERR_MSG);
    this.templatePath = openApi.getPathFrom(requireNonNull(path, PATH_REQUIRED_ERR_MSG));

    // Clone operation and get the flatten content
    this.operation = operation.copy(openApi.getContext(), true);

    // Merge parameters with default parameters
    // https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.2.md#operationObject
    mergePathToOperationParameters(path);

    // Request path parameters
    specRequestPathValidator = createParameterValidator(IN_PATH);
    this.pathPatterns
      = pathPatterns == null
      ? PathResolver.instance().buildPathPatterns(openApi.getContext(), openApi.getServers(), templatePath)
      : pathPatterns;

    // Request query parameters
    specRequestQueryValidator = createParameterValidator(IN_QUERY);
    // Request header parameters
    specRequestHeaderValidator = createParameterValidator(IN_HEADER);
    // Request cookie parameters
    specRequestCookieValidator = createParameterValidator(IN_COOKIE);
    // Request body
    specRequestBodyValidators = createRequestBodyValidators();
    // Response headers
    specResponseHeaderValidators = createResponseHeaderValidators();
    // Response body
    specResponseBodyValidators = createResponseBodyValidators();
  }

  public Operation getOperation() {
    return operation;
  }

  /**
   * Validate path parameters from the given request.
   *
   * @param request    The request to validate. Path MUST MATCH exactly the pattern defined in specification.
   * @param validation The validation data delegate and results.
   * @return The mapped parameters with their values.
   */
  public Map<String, JsonNode> validatePath(final Request request, final ValidationData<V> validation) {
    // Check paths are matching before trying to map values
    Pattern pathPattern = PathResolver.instance().findPathPattern(pathPatterns, request.getPath());
    if (pathPattern == null) {
      validation.add(CRUMB_PATH, PATH_NOT_FOUND_ERR, templatePath, request.getPath());
      return null;
    }

    return validatePath(request, pathPattern, validation);
  }

  /**
   * Validate path parameters from the given request.
   *
   * @param request    The request to validate. Path MUST MATCH exactly the pattern defined in specification.
   * @param validation The validation data delegate and results.
   * @return The mapped parameters with their values.
   */
  Map<String, JsonNode> validatePath(final Request request, Pattern pathPattern, final ValidationData<V> validation) {
    if (specRequestPathValidator == null) return null;

    Map<String, JsonNode> mappedValues = ParameterConverter.pathToNode(
      specRequestPathValidator.getParameters(),
      pathPattern,
      request.getPath());

    specRequestPathValidator.validate(mappedValues, validation);

    return mappedValues;
  }

  /**
   * Validate query parameters from the given request.
   *
   * @param request    The request to validate.
   * @param validation The validation data delegate and results.
   * @return The mapped parameters with their values.
   */
  public Map<String, JsonNode> validateQuery(final Request request, final ValidationData<V> validation) {
    if (specRequestQueryValidator == null) return null;

    Map<String, JsonNode> mappedValues = ParameterConverter.queryToNode(
      specRequestQueryValidator.getParameters(),
      request.getQuery(),
      "UTF-8");

    specRequestQueryValidator.validate(mappedValues, validation);

    return mappedValues;
  }

  /**
   * Validate header parameters from the given request.
   *
   * @param request    The request to validate.
   * @param validation The validation data delegate and results.
   * @return The mapped parameters with their values.
   */
  public Map<String, JsonNode> validateHeaders(final Request request, final ValidationData<V> validation) {
    if (specRequestHeaderValidator == null) return null;

    Map<String, JsonNode> mappedValues = ParameterConverter.headersToNode(
      specRequestHeaderValidator.getParameters(),
      request.getHeaders());

    specRequestHeaderValidator.validate(mappedValues, validation);

    return mappedValues;
  }

  /**
   * Validate cookie parameters from the given request.
   *
   * @param request    The request to validate.
   * @param validation The validation data delegate and results.
   * @return The mapped parameters with their values.
   */
  public Map<String, JsonNode> validateCookies(final Request request, final ValidationData<V> validation) {
    if (specRequestCookieValidator == null) return null;

    final Map<String, JsonNode> mappedValues = ParameterConverter.cookiesToNode(
      specRequestCookieValidator.getParameters(),
      request.getCookies());

    specRequestCookieValidator.validate(mappedValues, validation);

    return mappedValues;
  }

  /**
   * Validate body content from the given request.
   *
   * @param request    The request to validate.
   * @param validation The validation data delegate and results.
   */
  public void validateBody(final Request request, final ValidationData<V> validation) {
    if (specRequestBodyValidators == null) return;

    validateBody(
      specRequestBodyValidators,
      request.getContentType(),
      request.getBody(),
      operation.getRequestBody().isRequired(),
      validation);
  }

  /**
   * Validate body content from the given response.
   *
   * @param response   The response to validate.
   * @param validation The validation data delegate and results.
   */
  public void validateBody(final org.openapi4j.operation.validator.model.Response response,
                           final ValidationData<V> validation) {

    Map<MediaTypeContainer, BodyValidator<V>> validators = getResponseValidator(specResponseBodyValidators, response, validation);

    if (validators == null) return;

    validateBody(
      validators,
      response.getContentType(),
      response.getBody(),
      true,
      validation);
  }

  private void validateBody(final Map<MediaTypeContainer, BodyValidator<V>> validators,
                            final String rawContentType,
                            final Body body,
                            final boolean isRequired,
                            final ValidationData<V> validation) {

    final MediaTypeContainer contentType = MediaTypeContainer.create(rawContentType);

    if (contentType == null) {
      validation.add(BODY_CONTENT_TYPE_ERR);
      return;
    }

    BodyValidator<V> validator = null;
    for (Map.Entry<MediaTypeContainer, BodyValidator<V>> mediaType : validators.entrySet()) {
      if (mediaType.getKey().match(contentType)) {
        validator = mediaType.getValue();
        break;
      }
    }
    if (validator == null) {
      validation.add(BODY_WRONG_CONTENT_TYPE_ERR, rawContentType);
      return;
    }

    validator.validate(body,
      rawContentType,
      isRequired,
      validation);
  }

  /**
   * Validate header parameters from the given response.
   *
   * @param response   The response to validate.
   * @param validation The validation data delegate and results.
   */
  public void validateHeaders(final org.openapi4j.operation.validator.model.Response response,
                              final ValidationData<V> validation) {

    ParameterValidator<Header, V> validator = getResponseValidator(specResponseHeaderValidators, response, validation);

    if (validator == null) return;

    Map<String, JsonNode> mappedValues = ParameterConverter.headersToNode(
      validator.getParameters(),
      response.getHeaders());

    validator.validate(mappedValues, validation);
  }

  private ParameterValidator<Parameter, V> createParameterValidator(final String in) {
    List<Parameter> specParameters = operation.getParametersIn(in);

    Map<String, AbsParameter<Parameter>> parameters = specParameters
      .stream()
      .collect(Collectors.toMap(Parameter::getName, parameter -> parameter));

    return
      parameters.size() != 0
        ? new ParameterValidator<>(context, openApi, parameters)
        : null;
  }

  private Map<MediaTypeContainer, BodyValidator<V>> createRequestBodyValidators() {
    if (operation.getRequestBody() == null) {
      return null;
    }

    return createBodyValidators(operation.getRequestBody().getContentMediaTypes());
  }

  private Map<String, Map<MediaTypeContainer, BodyValidator<V>>> createResponseBodyValidators() {
    if (operation.getResponses() == null) {
      return null;
    }

    final Map<String, Map<MediaTypeContainer, BodyValidator<V>>> validators = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

    final Map<String, Response> responses = operation.getResponses();

    for (Map.Entry<String, Response> entryStatusCode : responses.entrySet()) {
      final String statusCode = entryStatusCode.getKey();
      final Response response = entryStatusCode.getValue();

      validators.put(statusCode, createBodyValidators(response.getContentMediaTypes()));
    }

    return validators;
  }

  private Map<MediaTypeContainer, BodyValidator<V>> createBodyValidators(final Map<String, MediaType> mediaTypes) {
    if (mediaTypes == null) {
      return null;
    }

    final Map<MediaTypeContainer, BodyValidator<V>> validators = new HashMap<>();

    for (Map.Entry<String, MediaType> entry : mediaTypes.entrySet()) {
      validators.put(MediaTypeContainer.create(entry.getKey()), new BodyValidator<>(context, openApi, entry.getValue()));
    }

    return validators;
  }

  private Map<String, ParameterValidator<Header, V>> createResponseHeaderValidators() {
    final Map<String, ParameterValidator<Header, V>> validators = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

    final Map<String, Response> responses = operation.getResponses();

    if (responses != null) {
      for (Map.Entry<String, Response> entryStatusCode : responses.entrySet()) {
        final String statusCode = entryStatusCode.getKey();
        final Response response = entryStatusCode.getValue();

        if (response.getHeaders() != null) {
          Map<String, AbsParameter<Header>> headers = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
          headers.putAll(response.getHeaders());
          validators.put(statusCode, new ParameterValidator<>(context, openApi, headers));
        }
      }
    }

    return validators.size() != 0 ? validators : null;
  }

  private <T> T getResponseValidator(final Map<String, T> validators,
                                     final org.openapi4j.operation.validator.model.Response response,
                                     final ValidationData<V> validation) {

    if (validators == null) return null;

    String statusCode = String.valueOf(response.getStatus());

    // Check explicit status code
    T validator = validators.get(statusCode);
    // Check ranged status code
    if (validator == null) {
      validator = validators.get(statusCode.charAt(0) + "XX");
    }
    // Check default
    if (validator == null) {
      validator = validators.get(DEFAULT_RESPONSE_CODE);
    }
    // Well, we tried...
    if (validator == null) {
      validation.add(RESPONSE_STATUS_NOT_FOUND_ERR, response.getStatus());
    }

    return validator;
  }

  private void mergePathToOperationParameters(final Path path) {
    if (path.getParameters() == null) {
      return; // Nothing to do
    }

    // Clone this and get the flatten content
    List<Parameter> parentParameters = new ArrayList<>(path.getParameters().size());
    for (Parameter parentParam : path.getParameters()) {
      if (parentParam.isRef()) {
        parentParameters.add(parentParam.copy(openApi.getContext(), true));
      } else {
        parentParameters.add(parentParam);
      }
    }

    if (operation.getParameters() == null) {
      // Setup path item parameters as operation parameters
      operation.setParameters(parentParameters);
    } else {
      // Merge
      // Jackson uses array lists (i.e. ordered streams), so this operation is safe, only
      // the element appearing first in the encounter order is preserved.
      List<Parameter> result = Stream
        .concat(operation.getParameters().stream(), parentParameters.stream())
        .distinct()
        .collect(Collectors.toList());

      operation.setParameters(result);
    }
  }
}
