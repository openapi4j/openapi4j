package org.openapi4j.parser.model.v3;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

import org.openapi4j.core.model.OAIContext;
import org.openapi4j.parser.model.AbsOpenApiSchema;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Example extends AbsOpenApiSchema<Example> {
  private String summary;
  private String description;
  private Object value;
  private String externalValue;
  @JsonUnwrapped
  private Extensions extensions;

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
  public Extensions getExtensions() {
    return extensions;
  }

  public Example setExtensions(Extensions extensions) {
    this.extensions = extensions;
    return this;
  }

  @Override
  public Example copy(OAIContext context, boolean followRefs) {
    Example copy = new Example();

    copy.setSummary(summary);
    copy.setDescription(description);
    copy.setValue(value);
    copy.setExternalValue(externalValue);
    copy.setExtensions(copyField(extensions, context, followRefs));

    return copy;
  }
}
