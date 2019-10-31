package org.openapi4j.parser.validation.v3;

import org.openapi4j.core.validation.ValidationResults;
import org.openapi4j.parser.model.v3.Link;
import org.openapi4j.parser.model.v3.OpenApi3;
import org.openapi4j.parser.model.v3.Operation;
import org.openapi4j.parser.model.v3.Response;
import org.openapi4j.parser.validation.Validator;

import static org.openapi4j.parser.validation.v3.OAI3Keywords.CALLBACKS;
import static org.openapi4j.parser.validation.v3.OAI3Keywords.EXTENSIONS;
import static org.openapi4j.parser.validation.v3.OAI3Keywords.EXTERNALDOCS;
import static org.openapi4j.parser.validation.v3.OAI3Keywords.OPERATIONID;
import static org.openapi4j.parser.validation.v3.OAI3Keywords.PARAMETERS;
import static org.openapi4j.parser.validation.v3.OAI3Keywords.REQUESTBODY;
import static org.openapi4j.parser.validation.v3.OAI3Keywords.RESPONSES;
import static org.openapi4j.parser.validation.v3.OAI3Keywords.SECURITY;
import static org.openapi4j.parser.validation.v3.OAI3Keywords.SERVERS;

class OperationValidator extends Validator3Base<OpenApi3, Operation> {
  private static final Validator<OpenApi3, Operation> INSTANCE = new OperationValidator();

  private OperationValidator() {
  }

  public static Validator<OpenApi3, Operation> instance() {
    return INSTANCE;
  }

  @Override
  public void validate(OpenApi3 api, Operation operation, ValidationResults results) {
    // VALIDATION EXCLUSIONS :
    // summary, description, deprecated, tags
    validateField(api, operation.getExternalDocs(), results, false, EXTERNALDOCS, ExternalDocsValidator.instance());
    validateString(operation.getOperationId(), results, false, OPERATIONID);
    validateList(api, operation.getParameters(), results, false, PARAMETERS, ParameterValidator.instance());
    validateField(api, operation.getRequestBody(), results, false, REQUESTBODY, RequestBodyValidator.instance());
    validateMap(api, operation.getResponses(), results, true, RESPONSES, Regexes.RESPONSE_REGEX, ResponseValidator.instance());
    validateMap(api, operation.getCallbacks(), results, false, CALLBACKS, Regexes.NOEXT_REGEX, CallbackValidator.instance());
    validateList(api, operation.getSecurityRequirements(), results, false, SECURITY, SecurityRequirementValidator.instance());
    validateList(api, operation.getServers(), results, false, SERVERS, ServerValidator.instance());
    validateField(api, operation.getExtensions(), results, false, EXTENSIONS, ExtensionsValidator.instance());

    validateCallbacks(api, operation, results);
    validateResponseLinks(api, operation, results);
  }

  private void validateCallbacks(OpenApi3 api, Operation operation, ValidationResults results) {
    if (operation.getCallbacks() == null) return;

    for (String expression : operation.getCallbacks().keySet()) {
      CallbackValidator.instance().validateWithOperation(api, operation, expression, results);
    }
  }

  private void validateResponseLinks(OpenApi3 api, Operation operation, ValidationResults results) {
    if (operation.getResponses() == null) return;

    for (Response response : operation.getResponses().values()) {
      if (response.getLinks() != null) {
        for (Link link : response.getLinks().values()) {
          LinkValidator.instance().validateWithOperation(api, operation, link, results);
        }
      }
    }
  }
}
