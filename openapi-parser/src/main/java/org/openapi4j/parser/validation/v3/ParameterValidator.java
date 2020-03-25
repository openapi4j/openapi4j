package org.openapi4j.parser.validation.v3;

import org.openapi4j.core.validation.ValidationResult;
import org.openapi4j.core.validation.ValidationResults;
import org.openapi4j.parser.model.v3.OpenApi3;
import org.openapi4j.parser.model.v3.Parameter;
import org.openapi4j.parser.validation.ValidationContext;
import org.openapi4j.parser.validation.Validator;

import static org.openapi4j.core.validation.ValidationSeverity.ERROR;
import static org.openapi4j.core.validation.ValidationSeverity.WARNING;
import static org.openapi4j.parser.validation.v3.OAI3Keywords.*;

class ParameterValidator extends Validator3Base<OpenApi3, Parameter> {
  private static final ValidationResult ALLOWED_RESERVED_IGNORED = new ValidationResult(WARNING, 125, "AllowReserved is ignored for non-query parameter '%s'");
  private static final ValidationResult STYLE_ONLY_IN = new ValidationResult(ERROR, 126, "Style '%s' is only allowed in %s");
  private static final ValidationResult STYLE_ONLY_IN_AND = new ValidationResult(ERROR, 127, "Style '%s' is only allowed in %s and %s");
  private static final ValidationResult CONTENT_ONY_ONE_ERR = new ValidationResult(ERROR, 128, "content can only contain one media type.");
  private static final ValidationResult CONTENT_SCHEMA_EXCLUSIVE_ERR = new ValidationResult(ERROR, 129, "Content and schema are mutually exclusive.");
  private static final ValidationResult STYLE_UNKNOWN = new ValidationResult(ERROR, 130, "Style '%s' is unknown.");

  private static final Validator<OpenApi3, Parameter> INSTANCE = new ParameterValidator();

  private ParameterValidator() {
  }

  public static Validator<OpenApi3, Parameter> instance() {
    return INSTANCE;
  }

  @Override
  public void validate(ValidationContext<OpenApi3> context, OpenApi3 api, Parameter parameter, ValidationResults results) {
    // VALIDATION EXCLUSIONS :
    // allowReserved, deprecated, description, example, examples, explode, required

    // checks for path params are made with path validator
    if (parameter.isRef()) {
      validateReference(context, api, parameter, results, CRUMB_$REF, ParameterValidator.instance(), Parameter.class);
    } else {
      validateString(parameter.getName(), results, true, CRUMB_NAME);
      validateString(parameter.getIn(), results, true, Regexes.PARAM_IN_REGEX, CRUMB_IN);
      validateString(parameter.getStyle(), results, false, Regexes.STYLE_REGEX, CRUMB_STYLE);
      checkStyleValues(parameter, results);
      checkAllowReserved(parameter, results);
      validateField(context, api, parameter.getSchema(), results, false, CRUMB_SCHEMA, SchemaValidator.instance());
      validateMap(context, api, parameter.getContentMediaTypes(), results, false, CRUMB_CONTENT, Regexes.NOEXT_REGEX, MediaTypeValidator.instance());
      validateMap(context, api, parameter.getExtensions(), results, false, CRUMB_EXTENSIONS, Regexes.EXT_REGEX, null);

      // Content or schema, not both
      if (parameter.getContentMediaTypes() != null && parameter.getSchema() != null) {
        results.add(CONTENT_SCHEMA_EXCLUSIVE_ERR);
      }
      // only one content type
      if (parameter.getContentMediaTypes() != null && parameter.getContentMediaTypes().size() > 1) {
        results.add(CONTENT_ONY_ONE_ERR);
      }
    }
  }

  private void checkAllowReserved(Parameter parameter, ValidationResults results) {
    if (parameter.isAllowReserved() && !QUERY.equals(parameter.getIn())) {
      results.add(CRUMB_ALLOWRESERVED, ALLOWED_RESERVED_IGNORED, parameter.getName());
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
          results.add(CRUMB_STYLE, STYLE_ONLY_IN, style, PATH);
        }
        break;
      case FORM:
        if (!QUERY.equals(in) && !COOKIE.equals(in)) {
          results.add(CRUMB_STYLE, STYLE_ONLY_IN_AND, style, QUERY, COOKIE);
        }
        break;
      case SIMPLE:
        if (!PATH.equals(in) && !HEADER.equals(in)) {
          results.add(CRUMB_STYLE, STYLE_ONLY_IN_AND, style, PATH, HEADER);
        }
        break;
      case SPACEDELIMITED:
      case PIPEDELIMITED:
      case DEEPOBJECT:
        if (!QUERY.equals(in)) {
          results.add(CRUMB_STYLE, STYLE_ONLY_IN, style, QUERY);
        }
        break;
      default:
        results.add(CRUMB_STYLE, STYLE_UNKNOWN, style);
        break;
    }
  }
}
