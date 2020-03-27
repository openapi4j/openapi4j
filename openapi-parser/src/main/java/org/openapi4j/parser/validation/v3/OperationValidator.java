package org.openapi4j.parser.validation.v3;

import org.openapi4j.core.validation.ValidationResults;
import org.openapi4j.parser.model.v3.*;
import org.openapi4j.parser.validation.ValidationContext;
import org.openapi4j.parser.validation.Validator;

import java.util.Map;

import static org.openapi4j.parser.validation.v3.OAI3Keywords.*;

class OperationValidator extends Validator3Base<OpenApi3, Operation> {
  private static final Validator<OpenApi3, Operation> INSTANCE = new OperationValidator();

  private OperationValidator() {
  }

  public static Validator<OpenApi3, Operation> instance() {
    return INSTANCE;
  }

  @Override
  public void validate(ValidationContext<OpenApi3> context, OpenApi3 api, Operation operation, ValidationResults results) {
    // VALIDATION EXCLUSIONS :
    // summary, description, deprecated, tags
    validateField(context, api, operation.getExternalDocs(), results, false, CRUMB_EXTERNALDOCS, ExternalDocsValidator.instance());
    validateString(operation.getOperationId(), results, false, CRUMB_OPERATIONID);
    validateList(context, api, operation.getParameters(), results, false, 0, CRUMB_PARAMETERS, ParameterValidator.instance());
    validateField(context, api, operation.getRequestBody(), results, false, CRUMB_REQUESTBODY, RequestBodyValidator.instance());
    validateMap(context, api, operation.getResponses(), results, true, CRUMB_RESPONSES, Regexes.RESPONSE_REGEX, ResponseValidator.instance());
    validateMap(context, api, operation.getCallbacks(), results, false, CRUMB_CALLBACKS, Regexes.NOEXT_REGEX, CallbackValidator.instance());
    validateList(context, api, operation.getSecurityRequirements(), results, false, 0, CRUMB_SECURITY, SecurityRequirementValidator.instance());
    validateList(context, api, operation.getServers(), results, false, 0, CRUMB_SERVERS, ServerValidator.instance());
    validateMap(context, api, operation.getExtensions(), results, false, CRUMB_EXTENSIONS, Regexes.EXT_REGEX, null);

    validateCallbacks(api, operation, results);
    validateResponseLinks(api, operation, results);
  }

  private void validateCallbacks(OpenApi3 api, Operation operation, ValidationResults results) {
    if (operation.getCallbacks() == null) return;

    for (Callback callback : operation.getCallbacks().values()) {
      if (callback.isRef()) {
        callback = getReferenceContent(api, callback, results, CRUMB_CALLBACKS, Callback.class);
      }

      if (callback.getCallbackPaths() != null) {
        for (String path : callback.getCallbackPaths().keySet()) {
          CallbackValidator.instance().validateExpression(api, operation, path, results);
        }
      }
    }
  }

  private void validateResponseLinks(OpenApi3 api, Operation operation, ValidationResults results) {
    if (operation.getResponses() == null) return;

    for (Response response : operation.getResponses().values()) {
      if (response.getLinks() != null) {
        for (Map.Entry<String, Link> entry : response.getLinks().entrySet()) {
          results.withCrumb(
            new ValidationResults.CrumbInfo(LINKS + "." + entry.getKey(), false),
            () -> LinkValidator.instance().validateWithOperation(api, operation, entry.getValue(), results));
        }
      }
    }
  }
}
