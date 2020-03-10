package org.openapi4j.parser.validation.v3;

import org.openapi4j.core.exception.DecodeException;
import org.openapi4j.core.model.reference.Reference;
import org.openapi4j.core.validation.ValidationResult;
import org.openapi4j.core.validation.ValidationResults;
import org.openapi4j.parser.model.v3.Link;
import org.openapi4j.parser.model.v3.OpenApi3;
import org.openapi4j.parser.model.v3.Operation;
import org.openapi4j.parser.model.v3.Parameter;
import org.openapi4j.parser.validation.ValidationContext;

import static org.openapi4j.core.validation.ValidationSeverity.ERROR;
import static org.openapi4j.parser.validation.v3.OAI3Keywords.EXTENSIONS;
import static org.openapi4j.parser.validation.v3.OAI3Keywords.HEADERS;
import static org.openapi4j.parser.validation.v3.OAI3Keywords.LINKS;
import static org.openapi4j.parser.validation.v3.OAI3Keywords.OPERATIONREF;
import static org.openapi4j.parser.validation.v3.OAI3Keywords.SERVER;

class LinkValidator extends ExpressionValidator<Link> {
  private static final ValidationResult OP_FIELD_MISSING_ERR = new ValidationResult(ERROR, 115, "'operationRef', 'operationId' or '$ref' field missing.");
  private static final ValidationResult OP_FIELD_EXCLUSIVE_ERR = new ValidationResult(ERROR, 116, "'operationRef' and 'operationId' fields are mutually exclusives.");
  private static final ValidationResult OP_NOT_FOUND_ERR = new ValidationResult(ERROR, 117, "'%s' not found.");
  private static final ValidationResult PARAM_NOT_FOUND_ERR = new ValidationResult(ERROR, 118, "Parameter name '%s' not found in target operation.");

  private static final LinkValidator INSTANCE = new LinkValidator();

  private LinkValidator() {
  }

  public static LinkValidator instance() {
    return INSTANCE;
  }

  @Override
  public void validate(ValidationContext<OpenApi3> context, OpenApi3 api, Link link, ValidationResults results) {
    // VALIDATION EXCLUSIONS :
    // description
    if (link.isRef()) {
      validateReference(context, api, link, results, LINKS, LinkValidator.instance(), Link.class);
    } else {
      validateMap(context, api, link.getHeaders(), results, false, HEADERS, Regexes.NOEXT_REGEX, HeaderValidator.instance());
      validateMap(context, api, link.getExtensions(), results, false, EXTENSIONS, Regexes.EXT_REGEX, null);
      validateField(context, api, link.getServer(), results, false, SERVER, ServerValidator.instance());
    }
  }

  // This called from operation validator
  void validateWithOperation(OpenApi3 api, Operation srcOperation, Link link, ValidationResults results) {
    if (link.isRef()) {
      link = getReferenceContent(api, link, results, LINKS, Link.class);
    }

    String operationRef = link.getOperationRef();
    String operationId = link.getOperationId();
    Operation targetOperation = null;

    if (operationId != null && operationRef != null) {
      results.add(OP_FIELD_EXCLUSIVE_ERR);
    } else if (operationRef != null) {
      targetOperation = getOperationRefContent(api, operationRef, results);
    } else if (operationId != null) {
      targetOperation = findOperationById(api, operationId, results);
    } else {
      results.add(OP_FIELD_MISSING_ERR);
    }

    if (targetOperation != null) {
      if (link.getParameters() == null) {
        return;
      }

      // Check expressions against current operation
      for (String expression : link.getParameters().values()) {
        validateExpression(api, srcOperation, expression, results);
      }
      // Check link parameter names are available in target operation
      checkTargetOperationParameters(targetOperation, link, results);
    }
  }

  private Operation findOperationById(OpenApi3 api, String operationId, ValidationResults results) {
    Operation operation = api.getOperationById(operationId);
    if (operation == null) {
      results.add(OPERATIONREF, OP_NOT_FOUND_ERR, operationId);
    }

    return operation;
  }

  private void checkTargetOperationParameters(Operation operation, Link link, ValidationResults results) {
    for (String paramName : link.getParameters().keySet()) {
      boolean hasParameter = false;

      if (operation.hasParameters()) {
        for (Parameter param : operation.getParameters()) {
          if (paramName.equals(param.getName())) {
            hasParameter = true;
            break;
          }
        }
      }

      if (!hasParameter) {
        results.add(PARAM_NOT_FOUND_ERR, paramName);
      }
    }
  }

  // Why didn't they used $ref ?
  Operation getOperationRefContent(final OpenApi3 api,
                                   final String operationRef,
                                   final ValidationResults results) {

    Reference reference = api.getContext().getReferenceRegistry().getRef(operationRef);

    if (reference == null) {
      results.add(OPERATIONREF, REF_MISSING, operationRef);
    } else {
      try {
        return reference.getMappedContent(Operation.class);
      } catch (DecodeException e) {
        results.add(OPERATIONREF, REF_CONTENT_UNREADABLE, operationRef);
      }
    }

    return null;
  }
}
