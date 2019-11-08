package org.openapi4j.parser.model.v3;

import com.fasterxml.jackson.annotation.JsonProperty;

import org.openapi4j.core.model.OAIContext;

import java.util.List;

@SuppressWarnings({"unused", "UnusedReturnValue"})
public class ServerVariable extends AbsExtendedOpenApiSchema<ServerVariable> {
  @JsonProperty("enum")
  private List<String> enumValues;
  @JsonProperty("default")
  private String defaultValue;
  private String description;

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
