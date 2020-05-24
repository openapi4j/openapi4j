package org.openapi4j.parser.model.v3;

public class Example extends AbsExtendedOpenApiSchema<Example> {
  private String summary;
  private String description;
  private Object value;
  private String externalValue;

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

  @Override
  public Example copy() {
    Example copy = new Example();

    copy.setSummary(getSummary());
    copy.setDescription(getDescription());
    copy.setValue(getValue());
    copy.setExternalValue(getExternalValue());
    copy.setExtensions(copySimpleMap(getExtensions()));

    return copy;
  }
}
