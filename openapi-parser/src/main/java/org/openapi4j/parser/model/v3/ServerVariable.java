package org.openapi4j.parser.model.v3;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.openapi4j.core.model.OAIContext;
import org.openapi4j.parser.model.AbsOpenApiSchema;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"unused", "UnusedReturnValue"})
public class ServerVariable extends AbsOpenApiSchema<ServerVariable> {
  @JsonProperty("enum")
  private List<String> enumValues;
  @JsonProperty("default")
  private String defaultValue;
  private String description;
  private Map<String, Object> extensions;

  // EnumValue
  public List<String> getEnumValues() {
    return enumValues;
  }

  public ServerVariable setEnumValues(List<String> enumValues) {
    this.enumValues = enumValues;
    return this;
  }

  public boolean hasEnumValues() {
    return enumValues != null;
  }

  public ServerVariable addEnumValue(String enumValue) {
    enumValues = listAdd(enumValues, enumValue);
    return this;
  }

  public ServerVariable insertEnumValue(int index, String enumValue) {
    enumValues = listAdd(enumValues, index, enumValue);
    return this;
  }

  public ServerVariable removeEnumValue(String enumValue) {
    listRemove(enumValues, enumValue);
    return this;
  }

  // Default
  public String getDefault() {
    return defaultValue;
  }

  public ServerVariable setDefault(String defaultValue) {
    this.defaultValue = defaultValue;
    return this;
  }

  // Description
  public String getDescription() {
    return description;
  }

  public ServerVariable setDescription(String description) {
    this.description = description;
    return this;
  }

  // Extensions
  @JsonAnyGetter
  public Map<String, Object> getExtensions() {
    return extensions;
  }

  public void setExtensions(Map<String, Object> extensions) {
    this.extensions = extensions;
  }

  @JsonAnySetter
  public void setExtension(String name, Object value) {
    if (extensions == null) {
      extensions = new HashMap<>();
    }
    extensions.put(name, value);
  }

  @Override
  public ServerVariable copy(OAIContext context, boolean followRefs) {
    ServerVariable copy = new ServerVariable();

    copy.setEnumValues(copyList(getEnumValues()));
    copy.setDefault(getDefault());
    copy.setDescription(getDescription());
    copy.setExtensions(copyMap(getExtensions()));

    return copy;
  }
}
