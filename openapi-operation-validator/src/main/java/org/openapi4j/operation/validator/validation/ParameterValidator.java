package org.openapi4j.operation.validator.validation;

import com.fasterxml.jackson.databind.JsonNode;
import org.openapi4j.core.model.v3.OAI3;
import org.openapi4j.core.util.TreeUtil;
import org.openapi4j.core.validation.ValidationResult;
import org.openapi4j.parser.model.OpenApiSchema;
import org.openapi4j.parser.model.v3.AbsParameter;
import org.openapi4j.parser.model.v3.OpenApi3;
import org.openapi4j.schema.validator.JsonValidator;
import org.openapi4j.schema.validator.ValidationContext;
import org.openapi4j.schema.validator.ValidationData;
import org.openapi4j.schema.validator.v3.SchemaValidator;

import java.util.HashMap;
import java.util.Map;

import static org.openapi4j.core.validation.ValidationSeverity.ERROR;

class ParameterValidator<M extends OpenApiSchema<M>> {
  private static final ValidationResult PARAM_REQUIRED_ERR = new ValidationResult(ERROR, 206, "Parameter '%s' is required.");

  private final ValidationContext<OAI3> context;
  private final OpenApi3 openApi;
  private final Map<String, JsonValidator> specValidators;
  private final Map<String, AbsParameter<M>> specParameters;

  ParameterValidator(ValidationContext<OAI3> context, OpenApi3 openApi, Map<String, AbsParameter<M>> specParameters) {
    this.context = context;
    this.openApi = openApi;
    this.specParameters = specParameters;
    specValidators = initValidators(specParameters);
  }

  Map<String, AbsParameter<M>> getParameters() {
    return specParameters;
  }

  void validate(final Map<String, JsonNode> values,
                final ValidationData<?> validation) {

    if (specValidators == null) return;

    for (Map.Entry<String, JsonValidator> entry : specValidators.entrySet()) {
      String paramName = entry.getKey();

      if (checkRequired(paramName, specParameters.get(paramName), values, validation)) {
        JsonNode paramValue = values.get(paramName);
        entry.getValue().validate(paramValue, validation);
      }
    }
  }

  private Map<String, JsonValidator> initValidators(Map<String, AbsParameter<M>> specParameters) {
    if (specParameters == null || specParameters.isEmpty()) {
      return null;
    }

    Map<String, JsonValidator> validators = new HashMap<>();

    for (Map.Entry<String, AbsParameter<M>> paramEntry : specParameters.entrySet()) {
      String paramName = paramEntry.getKey();
      AbsParameter<M> parameter = paramEntry.getValue();

      if (parameter.getSchema() != null) { // Schema is not mandatory
        SchemaValidator validator = new SchemaValidator(
          context,
          paramName,
          TreeUtil.json.convertValue(parameter.getSchema().copy(openApi.getContext(), true), JsonNode.class));

        validators.put(paramName, validator);
      }
    }

    return validators.size() != 0 ? validators : null;
  }

  private boolean checkRequired(final String paramName,
                                final AbsParameter<?> parameter,
                                final Map<String, JsonNode> paramValues,
                                final ValidationData<?> validation) {

    if (!paramValues.containsKey(paramName)) {
      if (parameter.isRequired()) {
        validation.add(PARAM_REQUIRED_ERR, paramName);
      }
      return false;
    }

    return true;
  }
}
