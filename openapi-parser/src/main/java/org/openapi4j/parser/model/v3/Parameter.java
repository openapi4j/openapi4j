package org.openapi4j.parser.model.v3;

import com.fasterxml.jackson.annotation.JsonProperty;

import org.openapi4j.core.model.OAIContext;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@SuppressWarnings("unused")
public class Parameter extends AbsExtendedRefOpenApiSchema<Parameter> {
  private Boolean allowReserved;
  @JsonProperty("content")
  private Map<String, MediaType> contentMediaTypes;
  private Boolean deprecated;
  private String description;
  private Object example;
  private Map<String, Example> examples;
  private Boolean explode;
  private String in;
  private String name;
  private Boolean required;
  private Schema schema;
  private String style;

  // Name
  public String getName() {
    return name;
  }

  public Parameter setName(String name) {
    this.name = name;
    return this;
  }

  // In
  public String getIn() {
    return in;
  }

  public Parameter setIn(String in) {
    this.in = in;
    return this;
  }

  // Description
  public String getDescription() {
    return description;
  }

  public Parameter setDescription(String description) {
    this.description = description;
    return this;
  }

  // Required
  public Boolean getRequired() {
    return required;
  }

  public boolean isRequired() {
    return required != null ? required : false;
  }

  public Parameter setRequired(Boolean required) {
    this.required = required;
    return this;
  }

  // Deprecated
  public Boolean getDeprecated() {
    return deprecated;
  }

  public boolean isDeprecated() {
    return deprecated != null ? deprecated : false;
  }

  public Parameter setDeprecated(Boolean deprecated) {
    this.deprecated = deprecated;
    return this;
  }

  // Style
  public String getStyle() {
    return style;
  }

  public Parameter setStyle(String style) {
    this.style = style;
    return this;
  }

  // Explode
  public Boolean getExplode() {
    return explode;
  }

  public boolean isExplode() {
    return explode != null ? explode : false;
  }

  public Parameter setExplode(Boolean explode) {
    this.explode = explode;
    return this;
  }

  // AllowReserved
  public Boolean getAllowReserved() {
    return allowReserved;
  }

  public boolean isAllowReserved() {
    return allowReserved != null ? allowReserved : false;
  }

  public Parameter setAllowReserved(Boolean allowReserved) {
    this.allowReserved = allowReserved;
    return this;
  }

  // Schema
  public Schema getSchema() {
    return schema;
  }

  public Parameter setSchema(Schema schema) {
    this.schema = schema;
    return this;
  }

  // Example
  public Object getExample() {
    return example;
  }

  public Parameter setExample(Object example) {
    this.example = example;
    return this;
  }

  // Example
  public Map<String, Example> getExamples() {
    return examples;
  }

  public Parameter setExamples(Map<String, Example> examples) {
    this.examples = examples;
    return this;
  }

  public boolean hasExample(String name) {
    return mapHas(examples, name);
  }

  public Example getExample(String name) {
    return mapGet(examples, name);
  }

  public Parameter setExample(String name, Example example) {
    if (examples == null) {
      examples = new HashMap<>();
    }
    examples.put(name, example);
    return this;
  }

  public Parameter removeExample(String name) {
    mapRemove(examples, name);
    return this;
  }

  // ContentMediaType
  public Map<String, MediaType> getContentMediaTypes() {
    return contentMediaTypes;
  }

  public Parameter setContentMediaTypes(Map<String, MediaType> contentMediaTypes) {
    this.contentMediaTypes = contentMediaTypes;
    return this;
  }

  public boolean hasContentMediaType(String name) {
    return mapHas(contentMediaTypes, name);
  }

  public MediaType getContentMediaType(String name) {
    return mapGet(contentMediaTypes, name);
  }

  public Parameter setContentMediaType(String name, MediaType contentMediaType) {
    if (contentMediaTypes == null) {
      contentMediaTypes = new HashMap<>();
    }
    contentMediaTypes.put(name, contentMediaType);
    return this;
  }

  public Parameter removeContentMediaType(String name) {
    mapRemove(contentMediaTypes, name);
    return this;
  }

  @Override
  protected Parameter copyReference(OAIContext context) {
    Parameter copy = new Parameter();
    copy.setRef(getRef());
    return copy;
  }

  @Override
  protected Parameter copyContent(OAIContext context, boolean followRefs) {
    Parameter copy = new Parameter();

    copy.setName(getName());
    copy.setIn(getIn());
    copy.setDescription(getDescription());
    copy.setRequired(getRequired());
    copy.setDeprecated(getDeprecated());
    copy.setStyle(getStyle());
    copy.setExplode(getExplode());
    copy.setAllowReserved(getAllowReserved());
    copy.setSchema(copyField(getSchema(), context, followRefs));
    copy.setExample(getExample());
    copy.setExamples(copyMap(getExamples(), context, followRefs));
    copy.setContentMediaTypes(getContentMediaTypes());
    copy.setExtensions(copyMap(getExtensions()));

    return copy;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Parameter parameter = (Parameter) o;

    if (isRef()) {
      return Objects.equals(getRef(), parameter.getRef());
    } else {
      if (!Objects.equals(name, parameter.name)) return false;
      return Objects.equals(in, parameter.in);
    }
  }

  @Override
  public int hashCode() {
    if (isRef()) {
      return getRef().hashCode();
    } else {
      int result = name != null ? name.hashCode() : 0;
      result = 31 * result + (in != null ? in.hashCode() : 0);
      return result;
    }
  }
}
