package org.openapi4j.operation.validator.validation;

import com.fasterxml.jackson.databind.JsonNode;

import org.openapi4j.core.exception.EncodeException;
import org.openapi4j.core.exception.ResolutionException;
import org.openapi4j.core.model.v3.OAI3;
import org.openapi4j.core.validation.ValidationResults;
import org.openapi4j.operation.validator.model.Request;
import org.openapi4j.operation.validator.model.impl.Body;
import org.openapi4j.operation.validator.util.ContentType;
import org.openapi4j.operation.validator.util.parameter.ParameterConverter;
import org.openapi4j.parser.model.SerializationFlag;
import org.openapi4j.parser.model.v3.MediaType;
import org.openapi4j.parser.model.v3.OpenApi3;
import org.openapi4j.parser.model.v3.Operation;
import org.openapi4j.parser.model.v3.Parameter;
import org.openapi4j.parser.model.v3.Path;
import org.openapi4j.parser.model.v3.Response;
import org.openapi4j.parser.model.v3.Schema;
import org.openapi4j.schema.validator.JsonValidator;
import org.openapi4j.schema.validator.ValidationContext;
import org.openapi4j.schema.validator.v3.SchemaValidator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;

/**
 * Validator for OpenAPI Operation.
 * It validates all aspects of the interaction between the request and a response.
 */
public class OperationValidator {
  private static final String VALIDATION_CTX_REQUIRED_ERR_MSG = "Validation context is required.";
  private static final String OAI_REQUIRED_ERR_MSG = "OpenAPI is required.";
  private static final String PATH_REQUIRED_ERR_MSG = "Path is required.";
  private static final String OPERATION_REQUIRED_ERR_MSG = "Operation is required.";
  private static final String PARAM_REQUIRED_ERR_MSG = "Parameter '%s' in '%s' is required.";
  private static final String BODY_REQUIRED_ERR_MSG = "Body is required but none provided.";
  private static final String BODY_CONTENT_TYPE_ERR_MSG = "Body content type cannot be determined. No 'Content-Type' header available.";
  private static final String BODY_WRONG_CONTENT_TYPE_ERR_MSG = "Content type '%s' is not allowed in body.";
  private static final String BODY_CONTENT_ERR_MSG = "An error occurred when getting the body content from type '%s'.%n%s";
  private static final String BODY = "body";

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
  private final String specPath;
  private final ValidationContext<OAI3> context;

  public OperationValidator(final OpenApi3 openApi, final Path path, final Operation operation) {
    this(new ValidationContext<>(openApi.getContext()), openApi, path, operation);
  }

  public OperationValidator(final ValidationContext<OAI3> context, final OpenApi3 openApi, final Path path, final Operation operation) {
    this.context = requireNonNull(context, VALIDATION_CTX_REQUIRED_ERR_MSG);
    requireNonNull(openApi, OAI_REQUIRED_ERR_MSG);
    requireNonNull(path, PATH_REQUIRED_ERR_MSG);
    requireNonNull(operation, OPERATION_REQUIRED_ERR_MSG);

    // Clone this and get the flatten content
    this.operation = operation.copy(openApi.getContext(), true);
    this.specPath = openApi.getPathFrom(path);

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
    if (this.operation.getRequestBody() != null) {
      fillBodyValidators(openApi, this.operation.getRequestBody().getContentMediaTypes(), specRequestBodyValidators);
    }
    // response
    fillResponseBodyValidators(openApi, this.operation, specResponseValidators);
  }

  public Operation getOperation() {
    return operation;
  }

  // If any request path parameter is declared, the parameter is required
  public Map<String, JsonNode> validatePath(final Request request, final ValidationResults results) {
    if (specPathValidators == null) return null;

    Map<String, JsonNode> mappedValues =
      ParameterConverter.pathToNode(specPath, request.getPath(), specPathValidators.keySet());

    validateParameters(specPathValidators, mappedValues, results);

    return mappedValues;
  }

  public Map<String, JsonNode> validateQuery(final Request request, final ValidationResults results) {
    if (specQueryValidators == null) return null;

    Map<String, JsonNode> mappedValues;

    try {
      mappedValues = ParameterConverter.queryToNode(request.getQuery(), specQueryValidators.keySet());

      validateParameters(specQueryValidators, mappedValues, results);
    } catch (ResolutionException e) {
      results.addError(e.getMessage());
      return null;
    }

    return mappedValues;
  }

  public Map<String, JsonNode> validateHeaders(final Request request, final ValidationResults results) {
    if (specHeaderValidators == null) return null;

    Map<String, JsonNode> mappedValues =
      ParameterConverter.headersToNode(request.getHeaders(), specHeaderValidators.keySet());

    validateParameters(specHeaderValidators, mappedValues, results);

    return mappedValues;
  }

  public Map<String, JsonNode> validateCookies(final Request request, final ValidationResults results) {
    if (specCookieValidators == null) return null;

    Map<String, JsonNode> mappedValues =
      ParameterConverter.cookiesToNode(request.getCookies(), specCookieValidators.keySet());

    validateParameters(specCookieValidators, mappedValues, results);

    return mappedValues;
  }

  public void validateBody(final Request request, final ValidationResults results) {
    if (operation.getRequestBody() == null) return;

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

    if (body == null) {
      if (isBodyRequired) results.addError(BODY_REQUIRED_ERR_MSG);

    } else if (contentType == null) {
      results.addError(BODY_CONTENT_TYPE_ERR_MSG);

    } else if (bodyValidators.get(contentType) == null) {
      results.addError(String.format(BODY_WRONG_CONTENT_TYPE_ERR_MSG, contentType));

    } else {
      MediaType mediaType = operation.getRequestBody().getContentMediaType(contentType);
      try {
        JsonNode jsonBody = body.getContentAsJson(
          mediaType.getSchema(),
          rawContentType);

        bodyValidators.get(contentType).validate(jsonBody, results);
      } catch (IOException ex) {
        results.addError(String.format(BODY_CONTENT_ERR_MSG, contentType, ex));
      }
    }
  }

  private Map<Parameter, JsonValidator> fillParametersValidators(OpenApi3 openApi,
                                        Operation operation,
                                        String in) {

    Collection<Parameter> parameters = operation.getParametersIn(in);

    if (!parameters.isEmpty()) {
      Map<Parameter, JsonValidator> validators = new HashMap<>();

      for (Parameter param : parameters) {
        if (param.getSchema() != null) { // Schema in not mandatory
          try {
            SchemaValidator validator = new SchemaValidator(
              context,
              param.getName(),
              param.getSchema().toJson(openApi.getContext(), EnumSet.of(SerializationFlag.FOLLOW_REFS)));

            validators.put(param, validator);
          } catch (EncodeException ex) {
            // Will never happen
          }
        }
      }

      return validators;
    }

    return null;
  }

  private void fillResponseBodyValidators(OpenApi3 openApi,
                                          Operation operation,
                                          Map<String, Map<String, JsonValidator>> validators) {

    Map<String, Response> responses = operation.getResponses();

    for (Map.Entry<String, Response> entryStatusCode : responses.entrySet()) {
      Map<String, JsonValidator> responseValidators = new HashMap<>();
      fillBodyValidators(openApi, entryStatusCode.getValue().getContentMediaTypes(), responseValidators);
      validators.put(entryStatusCode.getKey(), responseValidators);
    }
  }

  private void fillBodyValidators(OpenApi3 openApi,
                                  Map<String, MediaType> mediaTypes,
                                  Map<String, JsonValidator> validators) {

    if (mediaTypes == null) return;

    for (Map.Entry<String, MediaType> entry : mediaTypes.entrySet()) {
      Schema bodySchema = entry.getValue().getSchema();

      if (bodySchema != null) {
        try {
          SchemaValidator validator = new SchemaValidator(
            context,
            BODY,
            bodySchema.toJson(openApi.getContext(), EnumSet.of(SerializationFlag.FOLLOW_REFS)));

          validators.put(entry.getKey(), validator);
        } catch (EncodeException ex) {
          // Will never happen
        }
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
      results.addError(String.format(PARAM_REQUIRED_ERR_MSG, parameter.getName(), parameter.getIn()));
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
