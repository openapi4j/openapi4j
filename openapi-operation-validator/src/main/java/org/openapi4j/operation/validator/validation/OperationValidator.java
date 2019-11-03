package org.openapi4j.operation.validator.validation;

import com.fasterxml.jackson.databind.JsonNode;
import org.openapi4j.core.exception.EncodeException;
import org.openapi4j.core.exception.ResolutionException;
import org.openapi4j.core.validation.ValidationResults;
import org.openapi4j.operation.validator.model.Request;
import org.openapi4j.operation.validator.model.impl.Body;
import org.openapi4j.operation.validator.util.ContentType;
import org.openapi4j.operation.validator.util.parameter.ParameterConverter;
import org.openapi4j.parser.model.SerializationFlag;
import org.openapi4j.parser.model.v3.*;
import org.openapi4j.schema.validator.JsonValidator;
import org.openapi4j.schema.validator.v3.SchemaValidator;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;

/**
 * Validator for OpenAPI Operation.
 * It validates all aspects of the interaction between the request and a response.
 */
public class OperationValidator {
  private static final String REQUIRED_PARAM_ERR_MSG = "Parameter '%s' in '%s' is required.";

  private static final String IN_PATH = "path";
  private static final String IN_QUERY = "query";
  private static final String IN_HEADER = "header";
  private static final String IN_COOKIE = "cookie";
  private static final String DEFAULT_RESPONSE_CODE = "default";
  private final Map<Parameter, JsonValidator> specPathValidators;
  private final Map<Parameter, JsonValidator> specQueryValidators;
  private final Map<Parameter, JsonValidator> specHeaderValidators;
  private final Map<Parameter, JsonValidator> specCookieValidators;
  // Map<content type, validator>
  private final Map<String, JsonValidator> specRequestBodyValidators = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
  // Map<status code, Map<content type, validator>>
  private final Map<String, Map<String, JsonValidator>> specResponseValidators = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
  private final Operation operation;
  private final String path;

  public OperationValidator(final OpenApi3 openApi, final Path path, final Operation operation) throws EncodeException {
    requireNonNull(openApi, "OpenAPI is required");
    requireNonNull(operation, "Path is required");
    requireNonNull(operation, "Operation is required");
    // Clone this and get the flatten content
    this.operation = operation.copy(openApi.getContext(), true);
    this.path = null;

    // Merge parameters with default parameters
    // https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.2.md#operationObject
    mergeParameters(openApi, this.operation, path);

    // Path parameters
    specPathValidators = fillParametersValidators(openApi, this.operation, IN_PATH);
    // Query parameters
    specQueryValidators = fillParametersValidators(openApi, this.operation, IN_QUERY);
    // headers
    specHeaderValidators = fillParametersValidators(openApi, this.operation, IN_HEADER);
    // Cookies
    specCookieValidators = fillParametersValidators(openApi, this.operation, IN_COOKIE);
    // request body
    fillBodyValidators(openApi, this.operation.getRequestBody().getContentMediaTypes(), specRequestBodyValidators);
    // response
    fillResponseBodyValidators(openApi, this.operation, specResponseValidators);
  }

  public Operation getOperation() {
    return operation;
  }

  // If any request path parameter is declared, the parameter is required
  public void validatePath(final Request request, final ValidationResults results) {
    if (specPathValidators == null) return;

    validateParameters(
      specPathValidators,
      ParameterConverter.pathToNode(path, request.getPath(), specPathValidators.keySet()),
      results);
  }

  public void validateQuery(final Request request, final ValidationResults results) {
    if (specQueryValidators == null) return;

    try {
      validateParameters(
        specQueryValidators,
        ParameterConverter.queryToNode(request.getQuery(), specQueryValidators.keySet()),
        results);
    } catch (ResolutionException e) {
      results.addError(e.getMessage());
    }
  }

  public void validateHeaders(final Request request, final ValidationResults results) {
    if (specHeaderValidators == null) return;

    validateParameters(
      specHeaderValidators,
      ParameterConverter.headersToNode(request.getHeaders(), specHeaderValidators.keySet()),
      results);
  }

  public void validateCookies(final Request request, final ValidationResults results) {
    if (specCookieValidators == null) return;

    validateParameters(
      specCookieValidators,
      ParameterConverter.cookiesToNode(request.getCookies(), specCookieValidators.keySet()),
      results);
  }

  public void validateBody(final Request request, final ValidationResults results) {
    String rawContentType = request.getContentType().orElse(null);

    validateBody(
      request.getBody(),
      rawContentType,
      operation.getRequestBody().isRequired(),
      specRequestBodyValidators,
      results);
  }

  public void validateBody(final org.openapi4j.operation.validator.model.Response response,
                           final ValidationResults results) {

    Map<String, JsonValidator> validatorsForStatus = specResponseValidators.get(String.valueOf(response.getStatus()));
    if (validatorsForStatus == null) { // Check default response if any
      validatorsForStatus = specResponseValidators.get(DEFAULT_RESPONSE_CODE);
    }
    if (validatorsForStatus == null) {
      return;
    }

    String rawContentType = response.getContentType().orElse(null);

    validateBody(
      response.getBody(),
      rawContentType,
      false,
      validatorsForStatus,
      results);
  }

  private void validateBody(final Body body,
                            final String rawContentType,
                            final boolean isBodyRequired,
                            final Map<String, JsonValidator> bodyValidators,
                            final ValidationResults results) {

    if (bodyValidators.size() == 0) {
      return; // No schema specified for body
    }

    String contentType = ContentType.getTypeOnly(rawContentType);

    if ((body == null || body.isNull())) {
      if (isBodyRequired) results.addError("Body is required but none provided.");

    } else if (contentType == null) {
      results.addError("Body content type cannot be determined. No 'Content-Type' header available.");

    } else if (bodyValidators.get(contentType) == null) {
      results.addError(String.format("Content type '%s' is not allowed in body.", contentType));

    } else {
      MediaType mediaType = operation.getRequestBody().getContentMediaType(contentType);
      try {
        JsonNode jsonBody = body.getContentAsJson(
          mediaType.getSchema(),
          rawContentType);

        bodyValidators.get(contentType).validate(jsonBody, results);
      } catch (IOException ex) {
        results.addError(String.format("An error occurred when getting the body content from type '%s'.%n%s", contentType, ex));
      }
    }
  }

  private Map<Parameter, JsonValidator> fillParametersValidators(OpenApi3 openApi,
                                        Operation operation,
                                        String in) throws EncodeException {

    Collection<Parameter> parameters = operation.getParametersIn(in);

    if (!parameters.isEmpty()) {
      Map<Parameter, JsonValidator> validators = new HashMap<>();

      for (Parameter param : parameters) {
        if (param.getSchema() != null) { // Schema in not mandatory
          SchemaValidator validator = new SchemaValidator(
            param.getName(),
            param.getSchema().toJson(openApi.getContext(), EnumSet.of(SerializationFlag.FOLLOW_REFS)));

          validators.put(param, validator);
        }
      }

      return validators;
    }

    return null;
  }

  private void fillResponseBodyValidators(OpenApi3 openApi,
                                          Operation operation,
                                          Map<String, Map<String, JsonValidator>> validators) throws EncodeException {

    Map<String, Response> responses = operation.getResponses();

    for (Map.Entry<String, Response> entryStatusCode : responses.entrySet()) {
      Map<String, JsonValidator> responseValidators = new HashMap<>();
      fillBodyValidators(openApi, entryStatusCode.getValue().getContentMediaTypes(), responseValidators);
      validators.put(entryStatusCode.getKey(), responseValidators);
    }
  }

  private void fillBodyValidators(OpenApi3 openApi,
                                  Map<String, MediaType> mediaTypes,
                                  Map<String, JsonValidator> validators) throws EncodeException {

    for (Map.Entry<String, MediaType> entry : mediaTypes.entrySet()) {
      Schema bodySchema = entry.getValue().getSchema();

      if (bodySchema != null) {
        SchemaValidator validator = new SchemaValidator(
          "body",
          bodySchema.toJson(openApi.getContext(), EnumSet.of(SerializationFlag.FOLLOW_REFS)));

        validators.put(entry.getKey(), validator);
      }
    }
  }

  private void validateParameters(final Map<Parameter, JsonValidator> paramValidators,
                                  final Map<String, JsonNode> paramValues,
                                  final ValidationResults results) {

    for (Map.Entry<Parameter, JsonValidator> entry : paramValidators.entrySet()) {
      Parameter parameter = entry.getKey();

      if (checkRequired(parameter, paramValues, results)) {
        JsonNode paramValue = paramValues.get(parameter.getName());
        entry.getValue().validate(paramValue, results);
      }
    }
  }

  private boolean checkRequired(final Parameter parameter,
                                final Map<String, JsonNode> paramValues,
                                final ValidationResults results) {

    if (parameter.isRequired() && !paramValues.containsKey(parameter.getName())) {
      results.addError(String.format(REQUIRED_PARAM_ERR_MSG, parameter.getName(), parameter.getIn()));
      return false;
    }

    return true;
  }

  private void mergeParameters(final OpenApi3 openApi,
                               final Operation operation,
                               final Path path) {

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
