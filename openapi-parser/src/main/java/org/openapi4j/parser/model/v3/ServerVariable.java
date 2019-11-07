package org.openapi4j.parser.model.v3;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

import org.openapi4j.core.model.OAIContext;
import org.openapi4j.parser.model.AbsOpenApiSchema;

import java.util.List;

@SuppressWarnings({"unused", "UnusedReturnValue"})
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ServerVariable extends AbsOpenApiSchema<ServerVariable> {
  @JsonProperty("enum")
  private List<String> enumValues;
  @JsonProperty("default")
  private String defaultValue;
  private String description;
  @JsonUnwrapped
  private Extensions extensions;

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

  public String getEnumValue(int index) {
    return listGet(enumValues, index);
  }

  public ServerVariable addEnumValue(String enumValue) {
    enumValues = listAdd(enumValues, enumValue);
    return this;
  }

  public ServerVariable insertEnumValue(int index, String enumValue) {
    enumValues = listAdd(enumValues, index, enumValue);
    return this;
  }

  public ServerVariable removeEnumValue(int index) {
    listRemove(enumValues, index);
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
  public Extensions getExtensions() {
    return extensions;
  }

  public ServerVariable setExtensions(Extensions extensions) {
    this.extensions = extensions;
    return this;
  }

  @Override
  public ServerVariable copy(OAIContext context, boolean followRefs) {
    ServerVariable copy = new ServerVariable();

    copy.setEnumValues(copyList(enumValues));
    copy.setDefault(defaultValue);
    copy.setDescription(description);
    copy.setExtensions(copyField(extensions, context, followRefs));

    return copy;
  }
}
