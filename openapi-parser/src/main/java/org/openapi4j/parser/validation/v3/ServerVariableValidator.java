package org.openapi4j.parser.validation.v3;

import org.openapi4j.core.validation.ValidationResults;
import org.openapi4j.parser.model.v3.OpenApi3;
import org.openapi4j.parser.model.v3.ServerVariable;
import org.openapi4j.parser.validation.ValidationContext;
import org.openapi4j.parser.validation.Validator;

import static org.openapi4j.parser.validation.v3.OAI3Keywords.*;

public class ServerVariableValidator extends Validator3Base<OpenApi3, ServerVariable> {
  private static final Validator<OpenApi3, ServerVariable> INSTANCE = new ServerVariableValidator();

  private ServerVariableValidator() {
  }

  public static Validator<OpenApi3, ServerVariable> instance() {
    return INSTANCE;
  }

  @Override
  public void validate(ValidationContext<OpenApi3> context, OpenApi3 api, final ServerVariable variable, final ValidationResults results) {
    validateList(context, api, variable.getEnums(), results, false, 1, CRUMB_ENUM, null);
    validateString(variable.getDefault(), results, true, CRUMB_DEFAULT);
    validateString(variable.getDescription(), results, false, CRUMB_DESCRIPTION);
  }
}
