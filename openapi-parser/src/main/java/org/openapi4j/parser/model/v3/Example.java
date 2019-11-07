package org.openapi4j.parser.model.v3;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;

import org.openapi4j.core.model.OAIContext;
import org.openapi4j.parser.model.AbsOpenApiSchema;

import java.util.HashMap;
import java.util.Map;

public class Example extends AbsOpenApiSchema<Example> {
  private String summary;
  private String description;
  private Object value;
  private String externalValue;
  private Map<String, Object> extensions;

  // Summary
  public String getSummary() {
    return summary;
  }

  public Example setSummary(String summary) {
    this.summary = summary;
    return this;
  }

  // Description
  public String getDescription() {
    return description;
  }

  public Example setDescription(String description) {
    this.description = description;
    return this;
  }

  // Value
  public Object getValue() {
    return value;
  }

  public Example setValue(Object value) {
    this.value = value;
    return this;
  }

  // ExternalValue
  public String getExternalValue() {
    return externalValue;
  }

  public Example setExternalValue(String externalValue) {
    this.externalValue = externalValue;
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
  public Example copy(OAIContext context, boolean followRefs) {
    Example copy = new Example();

    copy.setSummary(getSummary());
    copy.setDescription(getDescription());
    copy.setValue(getValue());
    copy.setExternalValue(getExternalValue());
    copy.setExtensions(copyMap(getExtensions()));

    return copy;
  }
}
