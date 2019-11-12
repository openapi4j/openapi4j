package org.openapi4j.parser.validation.v3;

import org.openapi4j.core.exception.DecodeException;
import org.openapi4j.core.model.reference.Reference;
import org.openapi4j.core.validation.ValidationResults;
import org.openapi4j.parser.model.v3.Link;
import org.openapi4j.parser.model.v3.OpenApi3;
import org.openapi4j.parser.model.v3.Operation;
import org.openapi4j.parser.model.v3.Parameter;
import org.openapi4j.parser.model.v3.Path;

import java.util.Map;

import static org.openapi4j.parser.validation.v3.OAI3Keywords.EXTENSIONS;
import static org.openapi4j.parser.validation.v3.OAI3Keywords.HEADERS;
import static org.openapi4j.parser.validation.v3.OAI3Keywords.LINKS;
import static org.openapi4j.parser.validation.v3.OAI3Keywords.OPERATIONID;
import static org.openapi4j.parser.validation.v3.OAI3Keywords.OPERATIONREF;
import static org.openapi4j.parser.validation.v3.OAI3Keywords.PATH;
import static org.openapi4j.parser.validation.v3.OAI3Keywords.QUERY;
import static org.openapi4j.parser.validation.v3.OAI3Keywords.SERVER;

class LinkValidator extends ExpressionValidator<Link> {
  private static final String OP_FIELD_MISSING_ERR_MSG = "'operationRef', 'operationId' or '$ref' field missing.";
  private static final String OP_FIELD_EXCLUSIVE_ERR_MSG = "'operationRef' and 'operationId' fields are mutually exclusives.";
  private static final String OP_NOT_FOUND_ERR_MSG = "'%s' not found.";
  private static final String PARAM_PATH_ERR_MSG = "Path '%s' on parameter '%s' is malformed.";
  private static final String PARAM_NOT_FOUND_ERR_MSG = "Parameter name '%s' not found in target operation.";
  private static final String REF_CONTENT_UNREADABLE = "Unable to read $ref content at '%s'.";

  private static final LinkValidator INSTANCE = new LinkValidator();

  private LinkValidator() {
  }

  public static LinkValidator instance() {
    return INSTANCE;
  }

  @Override
  public void validate(OpenApi3 api, Link link, ValidationResults results) {
    // VALIDATION EXCLUSIONS :
    // description
    if (link.isRef()) {
      validateReference(api, link.getRef(), results, LINKS, LinkValidator.instance(), Link.class);
    } else {
      validateMap(api, link.getHeaders(), results, false, HEADERS, Regexes.NOEXT_REGEX, HeaderValidator.instance());
      validateMap(api, link.getExtensions(), results, false, EXTENSIONS, Regexes.EXT_REGEX, null);
      validateField(api, link.getServer(), results, false, SERVER, ServerValidator.instance());
    }
  }

  // This called from operation validator
  void validateWithOperation(OpenApi3 api, Operation srcOperation, Link link, ValidationResults results) {
    if (link.isRef()) {
      try {
        link = api.getContext().getReferenceRegistry().getRef(link.getRef()).getMappedContent(Link.class);
      } catch (DecodeException e) {
        results.addError(String.format(REF_CONTENT_UNREADABLE, link.getRef()), LINKS);
        return;
      }
    }

    String operationRef = link.getOperationRef();
    String operationId = link.getOperationId();
    Operation targetOperation = null;

    if (operationId != null && operationRef != null) {
      results.addError(OP_FIELD_EXCLUSIVE_ERR_MSG);
    } else if (operationRef != null) {
      targetOperation = findOperationByPath(api, operationRef, results);
    } else if (operationId != null) {
      targetOperation = findOperationById(api, operationId, results);
    } else {
      results.addError(OP_FIELD_MISSING_ERR_MSG);
    }

    if (targetOperation != null) {
      // Check expression against current operation
      checkSourceOperationParameters(api, srcOperation, link, results);
      // Check link parameter names are available in target operation
      checkTargetOperationParameters(targetOperation, link, results);
    }
  }

  private Operation findOperationById(OpenApi3 api, String operationId, ValidationResults results) {
    for (Path path : api.getPaths().values()) {
      for (Operation op : path.getOperations().values()) {
        if (operationId.equals(op.getOperationId())) {
          return op;
        }
      }
    }

    results.addError(String.format(OP_NOT_FOUND_ERR_MSG, operationId), OPERATIONID);
    return null;
  }

  private Operation findOperationByPath(OpenApi3 api, String operationRef, ValidationResults results) {
    Reference reference = api.getContext().getReferenceRegistry().getRef(operationRef);
    if (reference != null) {
      try {
        return reference.getMappedContent(Operation.class);
      } catch (DecodeException e) {
        results.addError(e.getMessage(), OPERATIONREF);
      }
    }

    results.addError(String.format(OP_NOT_FOUND_ERR_MSG, operationRef), OPERATIONREF);
    return null;
  }

  private void checkSourceOperationParameters(OpenApi3 api, Operation operation, Link link, ValidationResults results) {
    for (Map.Entry<String, String> entry : link.getParameters().entrySet()) {
      if (!checkRequestParameter(api, operation, entry.getValue(), results) &&
          !checkResponseParameter(api, operation, entry.getValue(), results)) {
        results.addError(String.format(PARAM_PATH_ERR_MSG, entry.getValue(), entry.getKey()));
      }
    }
  }

  private void checkTargetOperationParameters(Operation operation, Link link, ValidationResults results) {
    for (String paramName : link.getParameters().keySet()) {
      boolean hasParameter = false;
      for (Parameter param : operation.getParameters()) {
        if ((PATH.equals(param.getIn()) || QUERY.equals(param.getIn())) && paramName.equals(param.getName())) {
          hasParameter = true;
          break;
        }
      }

      if (!hasParameter) {
        results.addError(String.format(PARAM_NOT_FOUND_ERR_MSG, paramName));
      }
    }
  }
}
