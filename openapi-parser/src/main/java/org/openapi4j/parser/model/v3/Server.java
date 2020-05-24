package org.openapi4j.parser.model.v3;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unused")
public class Server extends AbsExtendedOpenApiSchema<Server> {
  private String url;
  private String description;
  private Map<String, ServerVariable> variables;

  // Url
  public String getUrl() {
    return url;
  }

  public Server setUrl(String url) {
    this.url = url;
    return this;
  }

  // Description
  public String getDescription() {
    return description;
  }

  public Server setDescription(String description) {
    this.description = description;
    return this;
  }

  // ServerVariable
  public Map<String, ServerVariable> getVariables() {
    return variables;
  }

  public Server setVariables(Map<String, ServerVariable> variables) {
    this.variables = variables;
    return this;
  }

  public boolean hasVariable(String name) {
    return mapHas(variables, name);
  }

  public ServerVariable getVariable(String name) {
    return mapGet(variables, name);
  }

  public Server setVariable(String name, ServerVariable serverVariable) {
    if (variables == null) {
      variables = new HashMap<>();
    }
    variables.put(name, serverVariable);
    return this;
  }

  public Server removeVariable(String name) {
    mapRemove(variables, name);
    return this;
  }

  @Override
  public Server copy() {
    Server copy = new Server();

    copy.setUrl(getUrl());
    copy.setDescription(getDescription());
    copy.setVariables(copyMap(getVariables()));
    copy.setExtensions(copySimpleMap(getExtensions()));

    return copy;
  }
}
