package org.openapi4j.parser.validation.v3;

import org.openapi4j.core.exception.DecodeException;
import org.openapi4j.core.validation.ValidationResults;
import org.openapi4j.parser.model.v3.MediaType;
import org.openapi4j.parser.model.v3.OpenApi3;
import org.openapi4j.parser.model.v3.Operation;
import org.openapi4j.parser.model.v3.Parameter;
import org.openapi4j.parser.model.v3.RequestBody;
import org.openapi4j.parser.model.v3.Response;
import org.openapi4j.parser.model.v3.Schema;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.openapi4j.core.model.v3.OAI3SchemaKeywords.TYPE_ARRAY;

abstract class ExpressionValidator<M> extends Validator3Base<OpenApi3, M> {
  private static final String PARAM_NOT_FOUND_ERR_MSG = "Parameter '%s' not found in operation.";
  private static final String PARAM_PATH_EXCEPTION_ERR_MSG = "Path '%s' is malformed.\n'%s'";

  private static final Pattern PARAM_PATTERN = Pattern.compile("\\{(.*?)}");
  private static final Pattern PATTERN_REQUEST_PARAM = Pattern.compile("^(\\$request)(?:\\.)(query(?=\\.)|path(?=\\.)|header(?=\\.)|body(?=#/))(?:\\.|#/)(.+)");
  private static final Pattern PATTERN_RESPONSE_PARAM = Pattern.compile("^(\\$response)(?:\\.)(header(?=\\.)|body(?=#/))(?:\\.|#/)(.+)");

  void validateExpression(OpenApi3 api, Operation operation, String expression, ValidationResults results) {
    // Check against expression fragments
    boolean paramFound = false;
    Matcher matcher = PARAM_PATTERN.matcher(expression);
    while (matcher.find()) {
      paramFound = true;
      if (!checkRequestParameter(api, operation, matcher.group(1), results)) {
        checkResponseParameter(api, operation, matcher.group(1), results);
      }
    }

    // Check against full expression
    if (!paramFound && !checkRequestParameter(api, operation, expression, results)) {
      checkResponseParameter(api, operation, expression, results);
    }
  }

  @SuppressWarnings("BooleanMethodIsAlwaysInverted")
  private boolean checkRequestParameter(OpenApi3 api, Operation operation, String propValue, ValidationResults results) {
    Matcher matcher = PATTERN_REQUEST_PARAM.matcher(propValue);
    boolean matches = matcher.matches();

    // group 1 : $request, group 2 : in, group 3 : value (JSON pointer for body)
    if (matches) {
      if (matcher.group(2).equals("body")) {
        RequestBody reqBody = operation.getRequestBody();
        if (reqBody != null && hasBodyProperty(api, matcher.group(3), reqBody.getContentMediaTypes(), results)) {
          return true;
        }

        results.addError(String.format(PARAM_NOT_FOUND_ERR_MSG, propValue));
      } else {
        if (checkParameterIn(matcher.group(2), matcher.group(3), operation, results)) {
          return true;
        }
      }
    }

    return matches;
  }

  @SuppressWarnings("UnusedReturnValue")
  private boolean checkResponseParameter(OpenApi3 api, Operation operation, String propValue, ValidationResults results) {
    if (operation.getResponses() == null) return false;

    Matcher matcher = PATTERN_RESPONSE_PARAM.matcher(propValue);
    boolean matches = matcher.matches();

    // group 1 : $request, group 2 : in, group 3 : value (JSON pointer for body)
    if (matches) {
      if (matcher.group(2).equals("body")) {
        for (Response response : operation.getResponses().values()) {
          if (hasBodyProperty(api, matcher.group(3), response.getContentMediaTypes(), results)) {
            return true;
          }
        }

        results.addError(String.format(PARAM_NOT_FOUND_ERR_MSG, propValue));
      } else {
        for (Response response : operation.getResponses().values()) {
          if (response.getHeaders() != null) {
            for (String header : response.getHeaders().keySet()) {
              if (header.equalsIgnoreCase(matcher.group(3))) {
                return true;
              }
            }
          }
        }
        results.addError(String.format(PARAM_NOT_FOUND_ERR_MSG, propValue));
        return false;
      }
    }

    return matches;
  }

  private boolean hasBodyProperty(OpenApi3 api, String propValue, Map<String, MediaType> contentMediaTypes, ValidationResults results) {
    if (contentMediaTypes == null) return false;

    String[] pathFragments = propValue.split("/");
    for (Map.Entry<String, MediaType> entry : contentMediaTypes.entrySet()) {
      try {
        if (hasBodyProperty(api, entry.getValue().getSchema(), pathFragments, 0)) {
          return true;
        }
      } catch (DecodeException e) {
        results.addError(String.format(PARAM_PATH_EXCEPTION_ERR_MSG, propValue, e.getMessage()));
      }
    }

    return false;
  }

  private boolean hasBodyProperty(OpenApi3 api, Schema schema, String[] pathFragments, int index) throws DecodeException {
    if (schema == null) {
      return false;
    }

    if (pathFragments.length > index) {
      if (schema.isRef()) {
        schema = schema.getReference(api.getContext()).getMappedContent(Schema.class);
      }

      if (TYPE_ARRAY.equals(schema.getType())) {
        return hasBodyProperty(api, schema.getItemsSchema(), pathFragments, index);
      }

      Schema subSchema = schema.getProperty(pathFragments[index]);
      if (subSchema == null) {
        return false;
      }

      index++;
      return (pathFragments.length == index) || hasBodyProperty(api, subSchema, pathFragments, index);
    }

    return false;
  }

  private boolean checkParameterIn(String in, String propName, Operation operation, ValidationResults results) {
    for (Parameter param : operation.getParametersIn(in)) {
      if (param.getName().equals(propName)) {
        return true;
      }
    }
    results.addError(String.format(PARAM_NOT_FOUND_ERR_MSG, propName));
    return false;
  }
}
