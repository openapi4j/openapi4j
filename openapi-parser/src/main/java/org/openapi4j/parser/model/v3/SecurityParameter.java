package org.openapi4j.parser.model.v3;

import org.openapi4j.core.model.OAIContext;
import org.openapi4j.parser.model.AbsOpenApiSchema;

import java.util.List;

@SuppressWarnings("unused")
public class SecurityParameter extends AbsOpenApiSchema<SecurityParameter> {
  private List<String> parameters;

  // Parameter
  public List<String> getParameters() {
    return parameters;
  }

  public SecurityParameter setParameters(List<String> parameters) {
    this.parameters = parameters;
    return this;
  }

  public boolean hasParameters() {
    return parameters != null;
  }

  public SecurityParameter addParameter(String parameter) {
    parameters = listAdd(parameters, parameter);
    return this;
  }

  public SecurityParameter insertParameter(int index, String parameter) {
    parameters = listAdd(parameters, index, parameter);
    return this;
  }

  public SecurityParameter removeParameter(String parameter) {
    listRemove(parameters, parameter);
    return this;
  }

  @Override
  public SecurityParameter copy(OAIContext context, boolean followRefs) {
    SecurityParameter copy = new SecurityParameter();

    copy.setParameters(copyList(getParameters()));

    return copy;
  }
}
