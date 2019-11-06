package org.openapi4j.operation.validator.validation;

import com.fasterxml.jackson.databind.JsonNode;

import org.openapi4j.core.exception.EncodeException;
import org.openapi4j.core.exception.ResolutionException;
import org.openapi4j.core.validation.ValidationException;
import org.openapi4j.core.validation.ValidationResults;
import org.openapi4j.operation.validator.model.Request;
import org.openapi4j.parser.model.v3.OpenApi3;
import org.openapi4j.parser.model.v3.Operation;
import org.openapi4j.parser.model.v3.Path;

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
  private static final String PATH_REQUIRED_ERR_MSG = "Path is required.";
  private static final String OPERATION_REQUIRED_ERR_MSG = "Operation is required.";
  private static final String REQUEST_REQUIRED_ERR_MSG = "Request is required.";
  private static final String SETUP_ERR_MSG = "Unable to setup operation validators.";
  private static final String INVALID_REQUEST_ERR_MSG = "Invalid request";

  private final OpenApi3 openApi;
  private final Map<Operation, OperationValidator> operationValidators;

  /**
   * Construct a new request validator with the given open API.
   *
   * @param openApi The loaded open API model
   */
  public RequestValidator(final OpenApi3 openApi) {
    requireNonNull(openApi, OPENAPI_REQUIRED_ERR_MSG);

    this.openApi = openApi;
    operationValidators = new ConcurrentHashMap<>();
  }

  /**
   * The compile the given path/operation and fill the validators to associate with.
   *
   * @param path      The OAS path model of the operation.
   * @param operation The operation object from specification.
   * @return The generated validator for the operation.
   * @throws ResolutionException
   */
  public OperationValidator compile(Path path, Operation operation) throws ResolutionException {
    requireNonNull(path, PATH_REQUIRED_ERR_MSG);
    requireNonNull(operation, OPERATION_REQUIRED_ERR_MSG);

    OperationValidator opValidator = operationValidators.get(operation);
    if (opValidator == null) {
      try {
        opValidator = new OperationValidator(openApi, path, operation);
      } catch (EncodeException e) { // Should never happen
        throw new ResolutionException(SETUP_ERR_MSG, e);
      }
      operationValidators.put(operation, opValidator);
    }

    return opValidator;
  }

  /**
   * Validate the request against the given API operation
   *
   * @param request   The request to validate. Must be {@code nonnull}.
   * @param operation OpenAPI operation. Must be {@code nonnull}.
   * @throws ValidationException A validation report containing validation errors
   */
  public void validate(Request request, Path path, Operation operation) throws ValidationException {
    requireNonNull(request, REQUEST_REQUIRED_ERR_MSG);

    OperationValidator opValidator;
    try {
      opValidator = compile(path, operation);
    } catch (ResolutionException e) {
      // Should never happen
      ValidationException ex = new ValidationException(SETUP_ERR_MSG);
      ex.initCause(e);
      throw ex;
    }

    ValidationResults results = new ValidationResults();

    Map<String, JsonNode> pathParameters = opValidator.validatePath(request, results);
    Map<String, JsonNode> queryParameters = opValidator.validateQuery(request, results);
    Map<String, JsonNode> headerParameters = opValidator.validateHeaders(request, results);
    Map<String, JsonNode> cookieParameters = opValidator.validateCookies(request, results);
    opValidator.validateBody(request, results);

    if (!results.isValid()) {
      throw new ValidationException(INVALID_REQUEST_ERR_MSG, results);
    }
  }
}
