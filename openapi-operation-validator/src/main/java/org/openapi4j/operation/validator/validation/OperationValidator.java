package org.openapi4j.operation.validator.validation;

import com.fasterxml.jackson.databind.JsonNode;

import org.openapi4j.core.exception.ResolutionException;
import org.openapi4j.core.model.v3.OAI3;
import org.openapi4j.core.validation.ValidationResults;
import org.openapi4j.operation.validator.model.Request;
import org.openapi4j.operation.validator.model.impl.Body;
import org.openapi4j.operation.validator.util.ContentType;
import org.openapi4j.operation.validator.util.PathResolver;
import org.openapi4j.operation.validator.util.parameter.ParameterConverter;
import org.openapi4j.parser.model.v3.AbsParameter;
import org.openapi4j.parser.model.v3.Header;
import org.openapi4j.parser.model.v3.MediaType;
import org.openapi4j.parser.model.v3.OpenApi3;
import org.openapi4j.parser.model.v3.Operation;
import org.openapi4j.parser.model.v3.Parameter;
import org.openapi4j.parser.model.v3.Path;
import org.openapi4j.parser.model.v3.Response;
import org.openapi4j.schema.validator.ValidationContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;

/**
 * Validator for OpenAPI Operation.
 * It validates all aspects of interaction between request and response.
 */
public class OperationValidator {
  // Error messages
  private static final String VALIDATION_CTX_REQUIRED_ERR_MSG = "Validation context is required.";
  private static final String OAI_REQUIRED_ERR_MSG = "OpenAPI is required.";
  private static final String PATH_REQUIRED_ERR_MSG = "Path is required.";
  private static final String OPERATION_REQUIRED_ERR_MSG = "Operation is required.";
  private static final String BODY_CONTENT_TYPE_ERR_MSG = "Body content type cannot be determined. No 'Content-Type' header available.";
  private static final String BODY_WRONG_CONTENT_TYPE_ERR_MSG = "Content type '%s' is not allowed in body.";
  private static final String RESPONSE_STATUS_NOT_FOUND_ERR_MSG = "Response status '%s', ranged or default has not been found.";
  // Parameter specifics
  private static final String IN_PATH = "path";
  private static final String IN_QUERY = "query";
  private static final String IN_HEADER = "header";
  private static final String IN_COOKIE = "cookie";
  private static final String DEFAULT_RESPONSE_CODE = "default";
  // Validators
  private final ParameterValidator<Parameter> specRequestPathValidator;
  private final ParameterValidator<Parameter> specRequestQueryValidator;
  private final ParameterValidator<Parameter> specRequestHeaderValidator;
  private final ParameterValidator<Parameter> specRequestCookieValidator;
  // Map<content type, validator>
  private final Map<String, BodyValidator> specRequestBodyValidators;
  // Map<status code, Map<content type, validator>>
  private final Map<String, Map<String, BodyValidator>> specResponseBodyValidators;
  // Map<status code, validator>
  private final Map<String, ParameterValidator<Header>> specResponseHeaderValidators;
  private final ValidationContext<OAI3> context;
  private final OpenApi3 openApi;
  private final Operation operation;
  private final Pattern pathPattern;

  public OperationValidator(final OpenApi3 openApi, final Path path, final Operation operation) {
    this(new ValidationContext<>(openApi.getContext()), openApi, path, operation);
  }

  @SuppressWarnings("WeakerAccess")
  public OperationValidator(final ValidationContext<OAI3> context, final OpenApi3 openApi, final Path path, final Operation operation) {
    this.context = requireNonNull(context, VALIDATION_CTX_REQUIRED_ERR_MSG);
    this.openApi = requireNonNull(openApi, OAI_REQUIRED_ERR_MSG);
    String specPath = openApi.getPathFrom(requireNonNull(path, PATH_REQUIRED_ERR_MSG));
    requireNonNull(operation, OPERATION_REQUIRED_ERR_MSG);

    // Clone operation and get the flatten content
    this.operation = operation.copy(openApi.getContext(), true);

    // Merge parameters with default parameters
    // https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.2.md#operationObject
    mergePathToOperationParameters(path);

    // Request path parameters
    specRequestPathValidator = createParameterValidator(IN_PATH);
    pathPattern = initPathPattern(specPath);
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
   * @param request The request to validate. Path MUST MATCH exactly the pattern defined in specification.
   * @param results The results.
   * @return The mapped parameters with their values.
   */
  public Map<String, JsonNode> validatePath(final Request request, final ValidationResults results) {
    if (specRequestPathValidator == null) return null;

    Map<String, JsonNode> mappedValues = ParameterConverter.pathToNode(
      specRequestPathValidator.getParameters(),
      pathPattern,
      request.getPath());

    specRequestPathValidator.validate(mappedValues, results);

    return mappedValues;
  }

  /**
   * Validate query parameters from the given request.
   *
   * @param request The request to validate.
   * @param results The results.
   * @return The mapped parameters with their values.
   */
  public Map<String, JsonNode> validateQuery(final Request request, final ValidationResults results) {
    if (specRequestQueryValidator == null) return null;

    Map<String, JsonNode> mappedValues = ParameterConverter.queryToNode(
      specRequestQueryValidator.getParameters(),
      request.getQuery());

    specRequestQueryValidator.validate(mappedValues, results);

    return mappedValues;
  }

  /**
   * Validate header parameters from the given request.
   *
   * @param request The request to validate.
   * @param results The results.
   * @return The mapped parameters with their values.
   */
  public Map<String, JsonNode> validateHeaders(final Request request, final ValidationResults results) {
    if (specRequestHeaderValidator == null) return null;

    Map<String, JsonNode> mappedValues = ParameterConverter.headersToNode(
      specRequestHeaderValidator.getParameters(),
      request.getHeaders());

    specRequestHeaderValidator.validate(mappedValues, results);

    return mappedValues;
  }

  /**
   * Validate cookie parameters from the given request.
   *
   * @param request The request to validate.
   * @param results The results.
   * @return The mapped parameters with their values.
   */
  public Map<String, JsonNode> validateCookies(final Request request, final ValidationResults results) {
    if (specRequestCookieValidator == null) return null;

    final Map<String, JsonNode> mappedValues = ParameterConverter.cookiesToNode(
      specRequestCookieValidator.getParameters(),
      request.getCookies());

    specRequestCookieValidator.validate(mappedValues, results);

    return mappedValues;
  }

  /**
   * Validate body content from the given request.
   *
   * @param request The request to validate.
   * @param results The results.
   */
  public void validateBody(final Request request, final ValidationResults results) {
    if (specRequestBodyValidators == null) return;

    validateBody(
      specRequestBodyValidators,
      request.getContentType(),
      request.getBody(),
      operation.getRequestBody().isRequired(),
      results);
  }

  /**
   * Validate body content from the given response.
   *
   * @param response The response to validate.
   * @param results  The results.
   */
  @SuppressWarnings("WeakerAccess")
  public void validateBody(final org.openapi4j.operation.validator.model.Response response,
                           final ValidationResults results) {

    Map<String, BodyValidator> validators = getResponseValidator(specResponseBodyValidators, response, results);

    if (validators == null) return;

    validateBody(
      validators,
      response.getContentType(),
      response.getBody(),
      true,
      results);
  }

  private void validateBody(final Map<String, BodyValidator> validators,
                            final String rawContentType,
                            final Body body,
                            final boolean isRequired,
                            final ValidationResults results) {

    final String contentType = ContentType.getTypeOnly(rawContentType);

    if (contentType == null) {
      results.addError(BODY_CONTENT_TYPE_ERR_MSG);
      return;
    }

    BodyValidator validator = validators.get(contentType);
    if (validator == null) {
      results.addError(String.format(BODY_WRONG_CONTENT_TYPE_ERR_MSG, contentType));
      return;
    }

    validator.validate(body,
      rawContentType,
      isRequired,
      results);
  }

  /**
   * Validate header parameters from the given response.
   *
   * @param response The response to validate.
   * @param results  The results.
   */
  @SuppressWarnings("WeakerAccess")
  public void validateHeaders(final org.openapi4j.operation.validator.model.Response response,
                              final ValidationResults results) {

    ParameterValidator<Header> validator = getResponseValidator(specResponseHeaderValidators, response, results);

    if (validator == null) return;

    Map<String, JsonNode> mappedValues =
      ParameterConverter.headersToNode(validator.getParameters(), response.getHeaders());

    validator.validate(mappedValues, results);
  }

  private ParameterValidator<Parameter> createParameterValidator(final String in) {
    List<Parameter> specParameters = operation.getParametersIn(in);

    Map<String, AbsParameter<Parameter>> parameters = specParameters
      .stream()
      .collect(Collectors.toMap(Parameter::getName, parameter -> parameter));

    return
      parameters.size() != 0
        ? new ParameterValidator<>(context, openApi, parameters)
        : null;
  }

  private Map<String, BodyValidator> createRequestBodyValidators() {
    if (operation.getRequestBody() == null) {
      return null;
    }

    return createBodyValidators(operation.getRequestBody().getContentMediaTypes());
  }

  private Map<String, Map<String, BodyValidator>> createResponseBodyValidators() {
    if (operation.getResponses() == null) {
      return null;
    }

    final Map<String, Map<String, BodyValidator>> validators = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

    final Map<String, Response> responses = operation.getResponses();

    for (Map.Entry<String, Response> entryStatusCode : responses.entrySet()) {
      final String statusCode = entryStatusCode.getKey();
      final Response response = entryStatusCode.getValue();

      validators.put(statusCode, createBodyValidators(response.getContentMediaTypes()));
    }

    return validators;
  }

  private Map<String, BodyValidator> createBodyValidators(final Map<String, MediaType> mediaTypes) {
    if (mediaTypes == null) {
      return null;
    }

    final Map<String, BodyValidator> validators = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

    for (Map.Entry<String, MediaType> entry : mediaTypes.entrySet()) {
      validators.put(entry.getKey(), new BodyValidator(context, openApi, entry.getValue()));
    }

    return validators;
  }

  private Map<String, ParameterValidator<Header>> createResponseHeaderValidators() {
    final Map<String, ParameterValidator<Header>> validators = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

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
                                     final ValidationResults results) {

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
      results.addError(String.format(RESPONSE_STATUS_NOT_FOUND_ERR_MSG, response.getStatus()));
    }

    return validator;
  }

  private Pattern initPathPattern(String specPath) {
    if (specRequestPathValidator == null) {
      return null;
    }

    // fill path regex for validation
    Optional<String> optRegEx;
    try {
      optRegEx = PathResolver.instance().solve(specPath, this.operation.getParametersIn(IN_PATH));
      return optRegEx.map(Pattern::compile).orElse(null);
    } catch (ResolutionException e) {
      return null;
    }
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
