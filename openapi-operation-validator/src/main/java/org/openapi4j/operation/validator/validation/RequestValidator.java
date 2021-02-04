package org.openapi4j.operation.validator.validation;

import com.fasterxml.jackson.databind.JsonNode;
import org.openapi4j.core.model.v3.OAI3;
import org.openapi4j.core.validation.ValidationException;
import org.openapi4j.operation.validator.model.Request;
import org.openapi4j.operation.validator.model.Response;
import org.openapi4j.operation.validator.model.impl.RequestParameters;
import org.openapi4j.operation.validator.util.PathResolver;
import org.openapi4j.parser.model.v3.OpenApi3;
import org.openapi4j.parser.model.v3.Operation;
import org.openapi4j.parser.model.v3.Path;
import org.openapi4j.schema.validator.ValidationContext;
import org.openapi4j.schema.validator.ValidationData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

import static java.util.Objects.requireNonNull;

/**
 * Validate a request data against a given OpenAPI Operation defined in the Spec.
 * Be sure to re-use it each time you need validation since the Operation validators are cached
 * for the given Open API.
 */
public class RequestValidator {
  private static final String OAI_REQUIRED_ERR_MSG = "OpenAPI is required.";
  private static final String VALIDATION_CTX_REQUIRED_ERR_MSG = "Validation context is required.";
  private static final String PATHS_REQUIRED_ERR_MSG = "Paths Object is required in Document Description.";
  private static final String PATH_REQUIRED_ERR_MSG = "Path is required.";
  private static final String OPERATION_REQUIRED_ERR_MSG = "Operation is required.";
  private static final String REQUEST_REQUIRED_ERR_MSG = "Request is required.";
  private static final String RESPONSE_REQUIRED_ERR_MSG = "Response is required.";
  private static final String INVALID_REQUEST_ERR_MSG = "Invalid request.";
  private static final String INVALID_RESPONSE_ERR_MSG = "Invalid response.";
  private static final String INVALID_OP_ERR_MSG = "Operation not found from URL '%s' with method '%s'.";
  private static final String INVALID_OP_PATH_ERR_MSG = "Operation path not found from URL '%s'.";

  private final OpenApi3 openApi;
  private final ValidationContext<OAI3> context;
  private final Map<Operation, OperationValidator> operationValidators;
  private final Map<Pattern, Path> pathPatterns;

  /**
   * Construct a new request validator with the given open API.
   *
   * @param openApi The loaded open API model
   */
  public RequestValidator(final OpenApi3 openApi) {
    this(new ValidationContext<>(openApi.getContext()), openApi);
  }

  /**
   * Construct a new request validator with the given open API.
   *
   * @param context The validation context to attach options and keyword overrides.
   * @param openApi The loaded open API model
   */
  public RequestValidator(final ValidationContext<OAI3> context, final OpenApi3 openApi) {
    requireNonNull(openApi, OAI_REQUIRED_ERR_MSG);
    requireNonNull(context, VALIDATION_CTX_REQUIRED_ERR_MSG);
    requireNonNull(openApi.getPaths(), PATHS_REQUIRED_ERR_MSG);

    this.openApi = openApi;
    this.context = context;
    this.operationValidators = new ConcurrentHashMap<>();
    this.pathPatterns = buildPathPatterns();
  }

  /**
   * Compile the given request and fill the validators to associate with.
   * <p/>
   * In no server URL has been defined in the Document,
   * any path fragment from the request prior to the
   * path operation template will lead to a failure in path lookup.<br/>
   * Thus, a {@code ValidationException} will be thrown.
   *
   * @param request The request to validate. Must be {@code nonnull}.
   * @return The generated validator for the operation or cached version.
   * @throws ValidationException A validation report containing validation errors
   */
  public OperationValidator getValidator(final Request request) throws ValidationException {
    requireNonNull(request, REQUEST_REQUIRED_ERR_MSG);

    final Pattern pathPattern = getRequiredPathPattern(request);
    final Path path = getRequiredPath(request, pathPattern);
    final Operation operation = getRequiredOperation(request, path);

    return getValidator(path, operation);
  }

  /**
   * Compile the given path/operation and fill the validators to associate with.
   *
   * @param path      The OAS path model of the operation.
   * @param operation The operation object from specification.
   * @return The generated validator for the operation or cached version.
   */
  public OperationValidator getValidator(final Path path, final Operation operation) {
    requireNonNull(path, PATH_REQUIRED_ERR_MSG);
    requireNonNull(operation, OPERATION_REQUIRED_ERR_MSG);

    return operationValidators.computeIfAbsent(
      operation,
      op -> {
        // extract resolved path patterns for the given path
        List<Pattern> patterns = new ArrayList<>();
        for (Map.Entry<Pattern, Path> patternPathEntry : pathPatterns.entrySet()) {
          if (patternPathEntry.getValue().equals(path)) {
            patterns.add(patternPathEntry.getKey());
          }
        }
        return new OperationValidator(context, patterns, openApi, path, op);
      });
  }

  /**
   * Validate the request from its given URL.
   * <p/>
   * In no server URL has been defined in the Document,
   * any path fragment from the request prior to the
   * path operation template will lead to a failure in path lookup.<br/>
   * Thus, a {@code ValidationException} will be thrown.
   *
   * @param request The request to validate. Must be {@code nonnull}.
   * @throws ValidationException A validation report containing validation errors
   */
  public RequestParameters validate(final Request request) throws ValidationException {
    return validate(request, new ValidationData<>());
  }

  /**
   * Validate the response from the validators associated with the request.
   * The request is not validated except for the path and operation,
   * use {@link #validate(Request)} instead to validate the request.
   * <p/>
   * In no server URL has been defined in the Document,
   * any path fragment from the request prior to the
   * path operation template will lead to a failure in path lookup.<br/>
   * Thus, a {@code ValidationException} will be thrown.
   *
   * @param response The response to validate. Must be {@code nonnull}.
   * @param request  The request to validate. Must be {@code nonnull}.
   * @throws ValidationException A validation report containing validation errors
   * @see #getValidator(Request)
   */
  public void validate(final Response response,
                       final Request request) throws ValidationException {
    requireNonNull(response, RESPONSE_REQUIRED_ERR_MSG);

    final OperationValidator validator = getValidator(request);
    final ValidationData<?> validation = new ValidationData<>();

    validateResponse(response, validator, validation);
  }

  /**
   * {@link #validate(Request)}
   *
   * @param request    The request to validate. Must be non {@code null}.
   * @param validation The validation results with your own data/delegates. Must be non {@code null}.
   * @throws ValidationException A validation report containing validation errors
   */
  public RequestParameters validate(final Request request,
                                    final ValidationData<?> validation) throws ValidationException {
    final Pattern pathPattern = getRequiredPathPattern(request);
    final Path path = getRequiredPath(request, pathPattern);
    final Operation operation = getRequiredOperation(request, path);

    return validate(request, pathPattern, path, operation, validation);
  }

  /**
   * Validate the request against the given API operation.
   *
   * @param request   The request to validate. Must be {@code nonnull}.
   * @param path      The OAS path. Must be {@code nonnull}.
   * @param operation OpenAPI operation. Must be {@code nonnull}.
   * @throws ValidationException A validation report containing validation errors
   */
  public RequestParameters validate(final Request request,
                                    final Path path,
                                    final Operation operation) throws ValidationException {
    return validate(request, path, operation, new ValidationData<>());
  }

  /**
   * Validate the request against the given API operation.
   *
   * @param request    The request to validate. Must be {@code nonnull}.
   * @param path       The OAS path. Must be {@code nonnull}.
   * @param operation  OpenAPI operation. Must be {@code nonnull}.
   * @param validation The validation results with your own data/delegates. Must be non {@code null}.
   * @throws ValidationException A validation report containing validation errors
   */
  public RequestParameters validate(final Request request,
                                    final Path path,
                                    final Operation operation,
                                    final ValidationData<?> validation) throws ValidationException {
    return validate(request, null, path, operation, validation);
  }

  /**
   * Validate the response against the given API operation
   *
   * @param response  The response to validate. Must be {@code nonnull}.
   * @param path      The OAS path. Must be {@code nonnull}.
   * @param operation OpenAPI operation. Must be {@code nonnull}.
   * @throws ValidationException A validation report containing validation errors.
   */
  public void validate(final Response response,
                       final Path path,
                       final Operation operation) throws ValidationException {
    validate(response, path, operation, new ValidationData<>());
  }

  /**
   * Validate the response against the given API operation.
   *
   * @param response   The response to validate. Must be {@code nonnull}.
   * @param path       The OAS path. Must be {@code nonnull}.
   * @param operation  OpenAPI operation. Must be {@code nonnull}.
   * @param validation The validation results with your own data/delegates. Must be non {@code null}.
   * @throws ValidationException A validation report containing validation errors.
   */
  public void validate(final Response response,
                       final Path path,
                       final Operation operation,
                       final ValidationData<?> validation) throws ValidationException {

    requireNonNull(response, RESPONSE_REQUIRED_ERR_MSG);

    final OperationValidator opValidator = getValidator(path, operation);

    validateResponse(response, opValidator, validation);
  }

  /**
   * Validate the request against the given API operation
   *
   * @param request     The request to validate. Must be {@code nonnull}.
   * @param pathPattern The path pattern for the current path operation.
   * @param path        The OAS path. Must be {@code nonnull}.
   * @param operation   OpenAPI operation. Must be {@code nonnull}.
   * @param validation  The validation results with your own data/delegates. Must be non {@code null}.
   * @throws ValidationException A validation report containing validation errors
   */
  private RequestParameters validate(final Request request,
                                     final Pattern pathPattern,
                                     final Path path,
                                     final Operation operation,
                                     final ValidationData<?> validation) throws ValidationException {

    requireNonNull(request, REQUEST_REQUIRED_ERR_MSG);

    final OperationValidator opValidator = getValidator(path, operation);

    final Map<String, JsonNode> pathParameters
      = (pathPattern != null)
      ? opValidator.validatePath(request, pathPattern, validation)
      : opValidator.validatePath(request, validation);

    final Map<String, JsonNode> queryParameters = opValidator.validateQuery(request, validation);
    final Map<String, JsonNode> headerParameters = opValidator.validateHeaders(request, validation);
    final Map<String, JsonNode> cookieParameters = opValidator.validateCookies(request, validation);
    opValidator.validateBody(request, validation);

    if (!validation.isValid()) {
      throw new ValidationException(INVALID_REQUEST_ERR_MSG, validation.results());
    }

    return new RequestParameters(
      pathParameters,
      queryParameters,
      headerParameters,
      cookieParameters
    );
  }

  private Operation getRequiredOperation(final Request request,
                                         final Path path) throws ValidationException {
    final Operation operation = path.getOperation(request.getMethod().name().toLowerCase());
    if (operation == null) {
      throw new ValidationException(String.format(INVALID_OP_ERR_MSG, request.getURL(), request.getMethod().name()));
    }
    return operation;
  }

  private Pattern getRequiredPathPattern(final Request request) throws ValidationException {
    final Pattern pathPattern = PathResolver.instance().findPathPattern(pathPatterns.keySet(), request.getPath());
    if (pathPattern == null) {
      throw new ValidationException(String.format(INVALID_OP_PATH_ERR_MSG, request.getURL()));
    }
    return pathPattern;
  }

  private Path getRequiredPath(Request request, Pattern pathPattern) throws ValidationException {
    final Path path = pathPatterns.get(pathPattern);
    if (path == null) {
      throw new ValidationException(String.format(INVALID_OP_PATH_ERR_MSG, request.getURL()));
    }
    return path;
  }

  private void validateResponse(final Response response,
                                final OperationValidator opValidator,
                                final ValidationData<?> validation) throws ValidationException {
    opValidator.validateResponse(response, validation);
    if (!validation.isValid()) {
      throw new ValidationException(INVALID_RESPONSE_ERR_MSG, validation.results());
    }
  }

  private Map<Pattern, Path> buildPathPatterns() {
    Map<Pattern, Path> patterns = new HashMap<>();

    for (Map.Entry<String, Path> pathEntry : openApi.getPaths().entrySet()) {
      List<Pattern> builtPathPatterns = PathResolver.instance().buildPathPatterns(
        openApi.getContext(),
        openApi.getServers(),
        pathEntry.getKey());

      for (Pattern pathPattern : builtPathPatterns) {
        patterns.put(pathPattern, pathEntry.getValue());
      }
    }

    return patterns;
  }
}
