package org.openapi4j.parser.validation.v3;

import org.openapi4j.core.validation.ValidationResults;
import org.openapi4j.parser.model.v3.Components;
import org.openapi4j.parser.model.v3.OpenApi3;
import org.openapi4j.parser.validation.ValidationContext;
import org.openapi4j.parser.validation.Validator;

import static org.openapi4j.parser.validation.v3.OAI3Keywords.*;

public class ComponentsValidator extends Validator3Base<OpenApi3, Components> {
  private static final Validator<OpenApi3, Components> INSTANCE = new ComponentsValidator();

  private ComponentsValidator() {
  }

  public static Validator<OpenApi3, Components> instance() {
    return INSTANCE;
  }

  @Override
  public void validate(ValidationContext<OpenApi3> context, OpenApi3 api, Components components, ValidationResults results) {
    // VALIDATION EXCLUSIONS :
    // examples
    validateMap(context, api, components.getCallbacks(), results, false, CRUMB_CALLBACKS, Regexes.NOEXT_NAME_REGEX, CallbackValidator.instance());
    validateMap(context, api, components.getExtensions(), results, false, CRUMB_EXTENSIONS, Regexes.EXT_REGEX, null);
    validateMap(context, api, components.getHeaders(), results, false, CRUMB_HEADERS, Regexes.NOEXT_NAME_REGEX, HeaderValidator.instance());
    validateMap(context, api, components.getLinks(), results, false, CRUMB_LINKS, Regexes.NOEXT_NAME_REGEX, LinkValidator.instance());
    validateMap(context, api, components.getParameters(), results, false, CRUMB_PARAMETERS, Regexes.NOEXT_NAME_REGEX, ParameterValidator.instance());
    validateMap(context, api, components.getRequestBodies(), results, false, CRUMB_REQUESTBODIES, Regexes.NOEXT_NAME_REGEX, RequestBodyValidator.instance());
    validateMap(context, api, components.getResponses(), results, false, CRUMB_RESPONSES, Regexes.NOEXT_NAME_REGEX, ResponseValidator.instance());
    validateMap(context, api, components.getSchemas(), results, false, CRUMB_SCHEMAS, Regexes.NOEXT_NAME_REGEX, SchemaValidator.instance());
    validateMap(context, api, components.getSecuritySchemes(), results, false, CRUMB_SECURITYSCHEMES, Regexes.NOEXT_NAME_REGEX, SecuritySchemeValidator.instance());
  }
}
