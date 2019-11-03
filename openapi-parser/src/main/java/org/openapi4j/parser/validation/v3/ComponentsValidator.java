package org.openapi4j.parser.validation.v3;

import org.openapi4j.core.validation.ValidationResults;
import org.openapi4j.parser.model.v3.Components;
import org.openapi4j.parser.model.v3.OpenApi3;
import org.openapi4j.parser.validation.Validator;

import static org.openapi4j.parser.validation.v3.OAI3Keywords.*;

class ComponentsValidator extends Validator3Base<OpenApi3, Components> {
  private static final Validator<OpenApi3, Components> INSTANCE = new ComponentsValidator();

  private ComponentsValidator() {
  }

  public static Validator<OpenApi3, Components> instance() {
    return INSTANCE;
  }

  @Override
  public void validate(OpenApi3 api, Components components, ValidationResults results) {
    // VALIDATION EXCLUSIONS :
    // examples
    validateMap(api, components.getCallbacks(), results, false, CALLBACKS, Regexes.NOEXT_NAME_REGEX, CallbackValidator.instance());
    validateField(api, components.getExtensions(), results, false, EXTENSIONS, ExtensionsValidator.instance());
    validateMap(api, components.getHeaders(), results, false, HEADERS, Regexes.NOEXT_NAME_REGEX, HeaderValidator.instance());
    validateMap(api, components.getLinks(), results, false, LINKS, Regexes.NOEXT_NAME_REGEX, LinkValidator.instance());
    validateMap(api, components.getParameters(), results, false, PARAMETERS, Regexes.NOEXT_NAME_REGEX, ParameterValidator.instance());
    validateMap(api, components.getRequestBodies(), results, false, REQUESTBODIES, Regexes.NOEXT_NAME_REGEX, RequestBodyValidator.instance());
    validateMap(api, components.getResponses(), results, false, RESPONSES, Regexes.NOEXT_NAME_REGEX, ResponseValidator.instance());
    validateMap(api, components.getSchemas(), results, false, SCHEMAS, Regexes.NOEXT_NAME_REGEX, SchemaValidator.instance());
    validateMap(api, components.getSecuritySchemes(), results, false, SECURITYSCHEMES, Regexes.NOEXT_NAME_REGEX, SecuritySchemeValidator.instance());
  }
}
