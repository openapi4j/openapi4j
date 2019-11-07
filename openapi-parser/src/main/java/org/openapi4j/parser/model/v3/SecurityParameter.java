package org.openapi4j.parser.model.v3;

import com.fasterxml.jackson.annotation.JsonInclude;

import org.openapi4j.core.model.OAIContext;
import org.openapi4j.parser.model.AbsOpenApiSchema;

import java.util.List;

@SuppressWarnings("unused")
@JsonInclude(JsonInclude.Include.NON_NULL)
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

  public String getParameter(int index) {
    return listGet(parameters, index);
  }

  public SecurityParameter addParameter(String parameter) {
    parameters = listAdd(parameters, parameter);
    return this;
  }

  public SecurityParameter insertParameter(int index, String parameter) {
    parameters = listAdd(parameters, index, parameter);
    return this;
  }

  public SecurityParameter removeParameter(int index) {
    listRemove(parameters, index);
    return this;
  }

  @Override
  public SecurityParameter copy(OAIContext context, boolean followRefs) {
    SecurityParameter copy = new SecurityParameter();

    copy.setParameters(copyList(parameters));

    return copy;
  }
}
