package org.openapi4j.parser.model.v3;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.openapi4j.core.model.OAIContext;
import org.openapi4j.parser.model.AbsOpenApiSchema;

import java.util.ArrayList;
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
    if (parameters == null) {
      return null;
    }
    return parameters.get(index);
  }

  public SecurityParameter addParameter(String parameter) {
    if (parameters == null) {
      parameters = new ArrayList<>();
    }
    parameters.add(parameter);
    return this;
  }

  public SecurityParameter insertParameter(int index, String parameter) {
    if (parameters == null) {
      parameters = new ArrayList<>();
    }
    parameters.add(index, parameter);
    return this;
  }

  public SecurityParameter removeParameter(int index) {
    if (parameters != null) {
      parameters.remove(index);
    }
    return this;
  }

  @Override
  public SecurityParameter copy(OAIContext context, boolean followRefs) {
    SecurityParameter copy = new SecurityParameter();

    copy.setParameters(copyList(parameters));

    return copy;
  }
}
