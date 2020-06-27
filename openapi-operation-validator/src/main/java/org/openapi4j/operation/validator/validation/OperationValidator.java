package org.openapi4j.operation.validator.validation;

import com.fasterxml.jackson.databind.JsonNode;
import org.openapi4j.core.exception.DecodeException;
import org.openapi4j.core.model.v3.OAI3;
import org.openapi4j.core.validation.ValidationResult;
import org.openapi4j.core.validation.ValidationResults;
import org.openapi4j.operation.validator.model.Request;
import org.openapi4j.operation.validator.model.impl.Body;
import org.openapi4j.operation.validator.model.impl.MediaTypeContainer;
import org.openapi4j.operation.validator.util.PathResolver;
import org.openapi4j.operation.validator.util.convert.ParameterConverter;
import org.openapi4j.parser.model.AbsRefOpenApiSchema;
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
public class OperationValidator {
  // Error messages
  private static final String VALIDATION_CTX_REQUIRED_ERR_MSG = "Validation context is required.";
  private static final String PATH_REQUIRED_ERR_MSG = "Path is required.";
  private static final String OPERATION_REQUIRED_ERR_MSG = "Operation is required.";
  private static final ValidationResult BODY_CONTENT_TYPE_ERR = new ValidationResult(ERROR, 202, "Body content type cannot be determined. No 'Content-Type' header available.");
  private static final ValidationResult BODY_WRONG_CONTENT_TYPE_ERR = new ValidationResult(ERROR, 203, "Content type '%s' is not allowed for body content.");
  private static final ValidationResult PATH_NOT_FOUND_ERR = new ValidationResult(ERROR, 205, "Path template '%s' has not been found from value '%s'.");

  // Parameter specifics
  private static final String IN_PATH = "path";
  private static final String IN_QUERY = "query";
  private static final String IN_HEADER = "header";
  private static final String IN_COOKIE = "cookie";
  private static final String DEFAULT_RESPONSE_CODE = "default";
  private static final ValidationResults.CrumbInfo CRUMB_PATH = new ValidationResults.CrumbInfo(IN_PATH, true);
  // Validators
  private final ParameterValidator<Parameter> specRequestPathValidator;
  private final ParameterValidator<Parameter> specRequestQueryValidator;
  private final ParameterValidator<Parameter> specRequestHeaderValidator;
  private final ParameterValidator<Parameter> specRequestCookieValidator;
  // Map<content type, validator>
  private final Map<MediaTypeContainer, BodyValidator> specRequestBodyValidators;
  // Map<status code, Map<content type, validator>>
  private final Map<String, Map<MediaTypeContainer, BodyValidator>> specResponseBodyValidators;
  // Map<status code, validator>
  private final Map<String, ParameterValidator<Header>> specResponseHeaderValidators;
  private final ValidationContext<OAI3> context;
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
  public OperationValidator(final ValidationContext<OAI3> context,
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
  OperationValidator(final ValidationContext<OAI3> context,
                     final List<Pattern> pathPatterns,
                     final OpenApi3 openApi,
                     final Path path,
                     final Operation operation) {

    this.context = requireNonNull(context, VALIDATION_CTX_REQUIRED_ERR_MSG);
    requireNonNull(operation, OPERATION_REQUIRED_ERR_MSG);
    this.templatePath = openApi.getPathFrom(requireNonNull(path, PATH_REQUIRED_ERR_MSG));

    // Clone operation
    this.operation = buildFlatOperation(operation);

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
  public Map<String, JsonNode> validatePath(final Request request, final ValidationData<?> validation) {
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
  Map<String, JsonNode> validatePath(final Request request, Pattern pathPattern, final ValidationData<?> validation) {
    if (specRequestPathValidator == null) return null;

    Map<String, JsonNode> mappedValues = ParameterConverter.pathToNode(
      context.getContext(),
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
  public Map<String, JsonNode> validateQuery(final Request request, final ValidationData<?> validation) {
    if (specRequestQueryValidator == null) return null;

    Map<String, JsonNode> mappedValues = ParameterConverter.queryToNode(
      context.getContext(),
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
  public Map<String, JsonNode> validateHeaders(final Request request, final ValidationData<?> validation) {
    if (specRequestHeaderValidator == null) return null;

    Map<String, JsonNode> mappedValues = ParameterConverter.headersToNode(
      context.getContext(),
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
  public Map<String, JsonNode> validateCookies(final Request request, final ValidationData<?> validation) {
    if (specRequestCookieValidator == null) return null;

    final Map<String, JsonNode> mappedValues = ParameterConverter.cookiesToNode(
      context.getContext(),
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
  public void validateBody(final Request request, final ValidationData<?> validation) {
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
                           final ValidationData<?> validation) {

    Map<MediaTypeContainer, BodyValidator> validators = getResponseValidator(specResponseBodyValidators, response);

    if (validators == null) return;

    validateBody(
      validators,
      response.getContentType(),
      response.getBody(),
      true,
      validation);
  }

  private void validateBody(final Map<MediaTypeContainer, BodyValidator> validators,
                            final String rawContentType,
                            final Body body,
                            final boolean isRequired,
                            final ValidationData<?> validation) {

    final MediaTypeContainer contentType = MediaTypeContainer.create(rawContentType);

    if (contentType == null) {
      validation.add(BODY_CONTENT_TYPE_ERR);
      return;
    }

    BodyValidator validator = null;
    for (Map.Entry<MediaTypeContainer, BodyValidator> mediaType : validators.entrySet()) {
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
                              final ValidationData<?> validation) {

    ParameterValidator<Header> validator = getResponseValidator(specResponseHeaderValidators, response);

    if (validator == null) return;

    Map<String, JsonNode> mappedValues = ParameterConverter.headersToNode(
      context.getContext(),
      validator.getParameters(),
      response.getHeaders());

    validator.validate(mappedValues, validation);
  }

  private ParameterValidator<Parameter> createParameterValidator(final String in) {
    List<Parameter> specParameters = operation.getParametersIn(context.getContext(), in);

    Map<String, AbsParameter<Parameter>> parameters = specParameters
      .stream()
      .collect(Collectors.toMap(Parameter::getName, parameter -> parameter));

    return
      parameters.size() != 0
        ? new ParameterValidator<>(context, parameters)
        : null;
  }

  private Map<MediaTypeContainer, BodyValidator> createRequestBodyValidators() {
    if (operation.getRequestBody() == null) {
      return null;
    }

    return createBodyValidators(operation.getRequestBody().getContentMediaTypes());
  }

  private Map<String, Map<MediaTypeContainer, BodyValidator>> createResponseBodyValidators() {
    if (operation.getResponses() == null) {
      return null;
    }

    final Map<String, Map<MediaTypeContainer, BodyValidator>> validators = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

    final Map<String, Response> responses = operation.getResponses();

    for (Map.Entry<String, Response> entryStatusCode : responses.entrySet()) {
      final String statusCode = entryStatusCode.getKey();
      final Response response = entryStatusCode.getValue();

      validators.put(statusCode, createBodyValidators(response.getContentMediaTypes()));
    }

    return validators;
  }

  private Map<MediaTypeContainer, BodyValidator> createBodyValidators(final Map<String, MediaType> mediaTypes) {
    final Map<MediaTypeContainer, BodyValidator> validators = new HashMap<>();

    if (mediaTypes == null) {
      validators.put(MediaTypeContainer.create(null), new BodyValidator(context, null));
    } else {
      for (Map.Entry<String, MediaType> entry : mediaTypes.entrySet()) {
        validators.put(MediaTypeContainer.create(entry.getKey()), new BodyValidator(context, entry.getValue()));
      }
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
          validators.put(statusCode, new ParameterValidator<>(context, headers));
        }
      }
    }

    return validators.size() != 0 ? validators : null;
  }

  private <T> T getResponseValidator(final Map<String, T> validators,
                                     final org.openapi4j.operation.validator.model.Response response) {

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
        parentParameters.add(parentParam.copy());
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

  /**
   * Create operation from original by avoiding recursion.
   * Flatten the content for direct access to attributes.
   *
   * @param operation the given operation to rebuild.
   * @return The flatten operation.
   */
  private Operation buildFlatOperation(Operation operation) {
    final Operation result = new Operation();

    // Parameters
    if (operation.hasParameters()) {
      for (Parameter parameter : operation.getParameters()) {
        Parameter flatParam = getFlatModel(parameter, Parameter.class);
        flatParam.setSchema(getFlatSchema(flatParam.getSchema()));
        getFlatMediaTypes(flatParam.getContentMediaTypes());
        result.addParameter(flatParam);
      }
    }

    // Request body
    RequestBody rqBody = operation.getRequestBody();
    if (rqBody != null) {
      RequestBody flatBody = getFlatModel(rqBody, RequestBody.class);
      getFlatMediaTypes(flatBody.getContentMediaTypes());
      result.setRequestBody(flatBody);
    }

    // Responses
    Map<String, Response> responses = operation.getResponses();
    if (responses != null) {
      for (Map.Entry<String, Response> entry : responses.entrySet()) {
        Response flatResponse = getFlatModel(entry.getValue(), Response.class);
        if (flatResponse.getHeaders() != null) {
          for (Map.Entry<String, Header> entryHeader : flatResponse.getHeaders().entrySet()) {
            Header flatHeader = getFlatModel(entryHeader.getValue(), Header.class);
            flatHeader.setSchema(getFlatSchema(flatHeader.getSchema()));
            flatResponse.setHeader(entryHeader.getKey(), flatHeader);

            getFlatMediaTypes(entryHeader.getValue().getContentMediaTypes());
          }
        }
        getFlatMediaTypes(flatResponse.getContentMediaTypes());
        result.setResponse(entry.getKey(), flatResponse);
      }
    }

    // Callbacks
    if (operation.getCallbacks() != null) {
      for (Map.Entry<String, Callback> entry : operation.getCallbacks().entrySet()) {
        Callback flatCallback = getFlatModel(entry.getValue(), Callback.class);
        result.setCallback(entry.getKey(), flatCallback);
      }
    }

    // All others without $ref
    result.setOperationId(operation.getOperationId());
    result.setSecurityRequirements(operation.getSecurityRequirements());
    result.setExtensions(operation.getExtensions());
    result.setTags(operation.getTags());
    result.setDescription(operation.getDescription());
    result.setExternalDocs(operation.getExternalDocs());
    result.setDeprecated(operation.getDeprecated());
    result.setServers(operation.getServers());

    return result;
  }

  private Schema getFlatSchema(Schema schema) {
    if (schema != null) {
      return getFlatModel(schema, Schema.class);
    }
    return null;
  }

  private <M extends AbsRefOpenApiSchema<M>> M getFlatModel(M model, Class<M> clazz) {
    try {
      if (model.isRef()) {
        return model.getReference(context.getContext()).getMappedContent(clazz);
      }
    } catch (DecodeException ex) {
      // Will never happen
    }

    return model.copy();
  }

  private void getFlatMediaTypes(Map<String, MediaType> mediaTypes) {
    if (mediaTypes != null) {
      for (Map.Entry<String, MediaType> entry : mediaTypes.entrySet()) {
        MediaType mediaType = entry.getValue();
        if (mediaType.getSchema() != null) {
          mediaType.setSchema(
            getFlatModel(mediaType.getSchema(), Schema.class));
        }
      }
    }
  }
}
