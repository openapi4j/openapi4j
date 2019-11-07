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
  private static final String PARAM_NOT_FOUND_ERR_MSG = "Parameter with path '%s' not found in operation.";
  private static final String PARAM_PATH_EXCEPTION_ERR_MSG = "Path '%s' is malformed.\n'%s'";

  private static final Pattern PATTERN_REQUEST_PARAM = Pattern.compile("^(\\$request)(?:\\.)(query(?=\\.)|path(?=\\.)|header(?=\\.)|body(?=#/))(?:\\.|#/)(.+)");
  private static final Pattern PATTERN_RESPONSE_PARAM = Pattern.compile("^(\\$response)(?:\\.)(header(?=\\.)|body(?=#/))(?:\\.|#/)(.+)");

  @SuppressWarnings("BooleanMethodIsAlwaysInverted")
  boolean checkRequestParameter(OpenApi3 api, Operation operation, String propValue, String validatorCrumb, ValidationResults results) {
    Matcher matcher = PATTERN_REQUEST_PARAM.matcher(propValue);
    boolean matches = matcher.matches();

    // group 1 : $request, group 2 : in, group 3 : value (JSON pointer for body)
    if (matches) {
      if (matcher.group(2).equals("body")) {
        RequestBody reqBody = operation.getRequestBody();
        if (reqBody != null && hasBodyProperty(api, matcher.group(3), reqBody.getContentMediaTypes(), validatorCrumb, results)) {
          return true;
        }

        results.addError(String.format(PARAM_NOT_FOUND_ERR_MSG, propValue), validatorCrumb);
      } else {
        if (checkParameterIn(matcher.group(2), matcher.group(3), operation, validatorCrumb, results)) {
          return true;
        }
      }
    }

    return matches;
  }

  @SuppressWarnings("BooleanMethodIsAlwaysInverted")
  boolean checkResponseParameter(OpenApi3 api, Operation operation, String propValue, String validatorCrumb, ValidationResults results) {
    Matcher matcher = PATTERN_RESPONSE_PARAM.matcher(propValue);
    boolean matches = matcher.matches();

    // group 1 : $request, group 2 : in, group 3 : value (JSON pointer for body)
    if (matches) {
      if (matcher.group(2).equals("body")) {
        for (Response response : operation.getResponses().values()) {
          if (hasBodyProperty(api, matcher.group(3), response.getContentMediaTypes(), validatorCrumb, results)) {
            return true;
          }
        }

        results.addError(String.format(PARAM_NOT_FOUND_ERR_MSG, propValue), validatorCrumb);
      } else {
        if (checkParameterIn(matcher.group(2), matcher.group(3), operation, validatorCrumb, results)) {
          return true;
        }
      }
    }

    return matches;
  }

  private boolean hasBodyProperty(OpenApi3 api, String propValue, Map<String, MediaType> contentMediaTypes, String validatorCrumb, ValidationResults results) {
    String[] pathFragments = propValue.split("/");
    for (Map.Entry<String, MediaType> entry : contentMediaTypes.entrySet()) {
      try {
        if (hasBodyProperty(api, entry.getValue().getSchema(), pathFragments, 0)) {
          return true;
        }
      } catch (DecodeException e) {
        results.addError(String.format(PARAM_PATH_EXCEPTION_ERR_MSG, propValue, e.getMessage()), validatorCrumb);
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
        schema = api.getContext().getReferenceRegistry().getRef(schema.getRef()).getMappedContent(Schema.class);
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

  private boolean checkParameterIn(String in, String propName, Operation operation, String validatorCrumb, ValidationResults results) {
    for (Parameter param : operation.getParametersIn(in)) {
      if (param.getName().equals(propName)) {
        return true;
      }
    }
    results.addError(String.format(PARAM_NOT_FOUND_ERR_MSG, propName), validatorCrumb);
    return false;
  }
}
