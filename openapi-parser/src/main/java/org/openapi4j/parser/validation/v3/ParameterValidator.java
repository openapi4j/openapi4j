package org.openapi4j.parser.validation.v3;

import org.openapi4j.core.validation.ValidationResults;
import org.openapi4j.parser.model.v3.OpenApi3;
import org.openapi4j.parser.model.v3.Parameter;
import org.openapi4j.parser.validation.Validator;

import static org.openapi4j.parser.validation.v3.OAI3Keywords.$REF;
import static org.openapi4j.parser.validation.v3.OAI3Keywords.ALLOWRESERVED;
import static org.openapi4j.parser.validation.v3.OAI3Keywords.CONTENT;
import static org.openapi4j.parser.validation.v3.OAI3Keywords.COOKIE;
import static org.openapi4j.parser.validation.v3.OAI3Keywords.DEEPOBJECT;
import static org.openapi4j.parser.validation.v3.OAI3Keywords.EXTENSIONS;
import static org.openapi4j.parser.validation.v3.OAI3Keywords.FORM;
import static org.openapi4j.parser.validation.v3.OAI3Keywords.HEADER;
import static org.openapi4j.parser.validation.v3.OAI3Keywords.IN;
import static org.openapi4j.parser.validation.v3.OAI3Keywords.LABEL;
import static org.openapi4j.parser.validation.v3.OAI3Keywords.MATRIX;
import static org.openapi4j.parser.validation.v3.OAI3Keywords.NAME;
import static org.openapi4j.parser.validation.v3.OAI3Keywords.PATH;
import static org.openapi4j.parser.validation.v3.OAI3Keywords.PIPEDELIMITED;
import static org.openapi4j.parser.validation.v3.OAI3Keywords.QUERY;
import static org.openapi4j.parser.validation.v3.OAI3Keywords.SCHEMA;
import static org.openapi4j.parser.validation.v3.OAI3Keywords.SIMPLE;
import static org.openapi4j.parser.validation.v3.OAI3Keywords.SPACEDELIMITED;
import static org.openapi4j.parser.validation.v3.OAI3Keywords.STYLE;

class ParameterValidator extends Validator3Base<OpenApi3, Parameter> {
  private static final String ALLOWED_RESERVED_IGNORED = "AllowReserved is ignored for non-query parameter '%s'";
  private static final String STYLE_ONLY_IN = "Style '%s' is only allowed in %s";
  private static final String STYLE_ONLY_IN_AND = "Style '%s' is only allowed in %s and %s";
  private static final String CONTENT_ONY_ONE_ERR_MSG = "content can only contain one media type.";

  private static final Validator<OpenApi3, Parameter> INSTANCE = new ParameterValidator();

  private ParameterValidator() {
  }

  public static Validator<OpenApi3, Parameter> instance() {
    return INSTANCE;
  }

  @Override
  public void validate(OpenApi3 api, Parameter parameter, ValidationResults results) {
    // VALIDATION EXCLUSIONS :
    // allowReserved, deprecated, description, example, examples, explode, required

    // checks for path params are made with path validator
    if (parameter.isRef()) {
      validateReference(api, parameter.getRef(), results, $REF, ParameterValidator.instance(), Parameter.class);
    } else {
      validateString(parameter.getName(), results, true, NAME);
      validateString(parameter.getIn(), results, true, Regexes.PARAM_IN_REGEX, IN);
      validateString(parameter.getStyle(), results, false, Regexes.STYLE_REGEX, STYLE);
      checkStyleValues(parameter, results);
      checkAllowReserved(parameter, results);
      validateField(api, parameter.getSchema(), results, false, SCHEMA, SchemaValidator.instance());
      validateMap(api, parameter.getContentMediaTypes(), results, false, CONTENT, Regexes.NOEXT_REGEX, MediaTypeValidator.instance());
      validateMap(api, parameter.getExtensions(), results, false, EXTENSIONS, Regexes.EXT_REGEX, null);

      if (parameter.getContentMediaTypes() != null && parameter.getContentMediaTypes().size() > 1) {
        results.addError(CONTENT_ONY_ONE_ERR_MSG);
      }
    }
  }

  private void checkAllowReserved(Parameter parameter, ValidationResults results) {
    if (parameter.isAllowReserved() && !QUERY.equals(parameter.getIn())) {
      results.addWarning(String.format(ALLOWED_RESERVED_IGNORED, parameter.getName()), ALLOWRESERVED);
    }
  }

  // https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.2.md#style-values
  private void checkStyleValues(Parameter parameter, ValidationResults results) {
    String style = parameter.getStyle();
    if (style == null) {
      return;
    }

    String in = parameter.getIn();

    switch (style) {
      case MATRIX:
      case LABEL:
        if (!PATH.equals(in)) {
          results.addError(String.format(STYLE_ONLY_IN, style, PATH), STYLE);
        }
        break;
      case FORM:
        if (!QUERY.equals(in) && !COOKIE.equals(in)) {
          results.addError(String.format(STYLE_ONLY_IN_AND, style, QUERY, COOKIE), STYLE);
        }
        break;
      case SIMPLE:
        if (!PATH.equals(in) && !HEADER.equals(in)) {
          results.addError(String.format(STYLE_ONLY_IN_AND, style, PATH, HEADER), STYLE);
        }
        break;
      case SPACEDELIMITED:
      case PIPEDELIMITED:
      case DEEPOBJECT:
        if (!QUERY.equals(in)) {
          results.addError(String.format(STYLE_ONLY_IN, style, QUERY), STYLE);
        }
        break;
      default:
        break;
    }
  }
}
