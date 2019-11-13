package org.openapi4j.operation.validator.validation;

import com.fasterxml.jackson.databind.JsonNode;

import org.openapi4j.core.model.v3.OAI3;
import org.openapi4j.core.validation.ValidationException;
import org.openapi4j.core.validation.ValidationResults;
import org.openapi4j.operation.validator.model.Request;
import org.openapi4j.operation.validator.model.Response;
import org.openapi4j.operation.validator.model.impl.RequestParameters;
import org.openapi4j.parser.model.v3.OpenApi3;
import org.openapi4j.parser.model.v3.Operation;
import org.openapi4j.parser.model.v3.Path;
import org.openapi4j.schema.validator.ValidationContext;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Objects.requireNonNull;

/**
 * Validate a request data against a given OpenAPI Operation defined in the Spec.
 * Be sure to re-use it each time you need validation since the Operation validators are cached
 * for the given Open API.
 */
public class RequestValidator {
  private static final String OPENAPI_REQUIRED_ERR_MSG = "OpenAPI is required.";
  private static final String VALIDATION_CTX_REQUIRED_ERR_MSG = "Validation context is required.";
  private static final String PATH_REQUIRED_ERR_MSG = "Path is required.";
  private static final String OPERATION_REQUIRED_ERR_MSG = "Operation is required.";
  private static final String REQUEST_REQUIRED_ERR_MSG = "Request is required.";
  private static final String RESPONSE_REQUIRED_ERR_MSG = "Response is required.";
  private static final String INVALID_REQUEST_ERR_MSG = "Invalid request";
  private static final String INVALID_RESPONSE_ERR_MSG = "Invalid response";

  private final OpenApi3 openApi;
  private final ValidationContext<OAI3> context;
  private final Map<Operation, OperationValidator> operationValidators;

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
  @SuppressWarnings("WeakerAccess")
  public RequestValidator(final ValidationContext<OAI3> context, final OpenApi3 openApi) {
    requireNonNull(openApi, OPENAPI_REQUIRED_ERR_MSG);
    requireNonNull(context, VALIDATION_CTX_REQUIRED_ERR_MSG);

    this.openApi = openApi;
    this.context = context;
    operationValidators = new ConcurrentHashMap<>();
  }

  /**
   * The compile the given path/operation and fill the validators to associate with.
   *
   * @param path      The OAS path model of the operation.
   * @param operation The operation object from specification.
   * @return The generated validator for the operation.
   */
  public OperationValidator compile(final Path path, final Operation operation) {
    requireNonNull(path, PATH_REQUIRED_ERR_MSG);
    requireNonNull(operation, OPERATION_REQUIRED_ERR_MSG);

    return operationValidators.computeIfAbsent(
      operation,
      op -> new OperationValidator(context, openApi, path, op));
  }

  /**
   * Validate the request against the given API operation
   *
   * @param request   The request to validate. Must be {@code nonnull}.
   * @param path      The OAS path. Must be {@code nonnull}.
   * @param operation OpenAPI operation. Must be {@code nonnull}.
   * @throws ValidationException A validation report containing validation errors
   */
  public RequestParameters validate(final Request request,
                                    final Path path,
                                    final Operation operation) throws ValidationException {

    requireNonNull(request, REQUEST_REQUIRED_ERR_MSG);

    final OperationValidator opValidator = compile(path, operation);

    final ValidationResults results = new ValidationResults();
    final Map<String, JsonNode> pathParameters = opValidator.validatePath(request, results);
    final Map<String, JsonNode> queryParameters = opValidator.validateQuery(request, results);
    final Map<String, JsonNode> headerParameters = opValidator.validateHeaders(request, results);
    final Map<String, JsonNode> cookieParameters = opValidator.validateCookies(request, results);
    opValidator.validateBody(request, results);

    if (!results.isValid()) {
      throw new ValidationException(INVALID_REQUEST_ERR_MSG, results);
    }

    return new RequestParameters(
      pathParameters,
      queryParameters,
      headerParameters,
      cookieParameters
    );
  }

  /**
   * Validate the response against the given API operation
   *
   * @param response  The response to validate. Must be {@code nonnull}.
   * @param path      The OAS path. Must be {@code nonnull}.
   * @param operation OpenAPI operation. Must be {@code nonnull}.
   * @throws ValidationException A validation report containing validation errors
   */
  public void validate(final Response response,
                       final Path path,
                       final Operation operation) throws ValidationException {

    requireNonNull(response, RESPONSE_REQUIRED_ERR_MSG);

    final OperationValidator opValidator = compile(path, operation);

    final ValidationResults results = new ValidationResults();
    opValidator.validateBody(response, results);

    if (!results.isValid()) {
      throw new ValidationException(INVALID_RESPONSE_ERR_MSG, results);
    }
  }
}
