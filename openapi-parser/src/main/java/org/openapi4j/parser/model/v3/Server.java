package org.openapi4j.parser.model.v3;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import org.openapi4j.core.model.OAIContext;
import org.openapi4j.parser.model.AbsOpenApiSchema;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unused")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Server extends AbsOpenApiSchema<Server> {
  private String url;
  private String description;
  private Map<String, ServerVariable> variables;
  @JsonUnwrapped
  private Extensions extensions;

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

  public boolean hasServerVariable(String name) {
    return has(variables, name);
  }

  public ServerVariable getServerVariable(String name) {
    return get(variables, name);
  }

  public Server setServerVariable(String name, ServerVariable serverVariable) {
    if (variables == null) {
      variables = new HashMap<>();
    }
    variables.put(name, serverVariable);
    return this;
  }

  public Server removeServerVariable(String name) {
    remove(variables, name);
    return this;
  }

  // Extensions
  public Extensions getExtensions() {
    return extensions;
  }

  public Server setExtensions(Extensions extensions) {
    this.extensions = extensions;
    return this;
  }

  @Override
  public Server copy(OAIContext context, boolean followRefs) {
    Server copy = new Server();

    copy.setUrl(url);
    copy.setDescription(description);
    copy.setVariables(copyMap(variables, context, followRefs));
    copy.setExtensions(copyField(extensions, context, followRefs));

    return copy;
  }
}
