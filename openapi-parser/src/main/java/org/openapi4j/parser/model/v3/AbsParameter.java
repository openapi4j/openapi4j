package org.openapi4j.parser.model.v3;

import com.fasterxml.jackson.annotation.JsonProperty;

import org.openapi4j.core.model.OAIContext;
import org.openapi4j.parser.model.OpenApiSchema;

import java.util.HashMap;
import java.util.Map;

public abstract class AbsParameter<M extends OpenApiSchema<M>> extends AbsExtendedRefOpenApiSchema<M> {
  private Boolean allowReserved;
  @JsonProperty("content")
  private Map<String, MediaType> contentMediaTypes;
  private Boolean deprecated;
  private String description;
  private Object example;
  private Map<String, Example> examples;
  private Boolean explode;
  private Boolean required;
  private Schema schema;
  private String style;

  // Description
  public String getDescription() {
    return description;
  }

  public AbsParameter<M> setDescription(String description) {
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

  public AbsParameter<M> setRequired(Boolean required) {
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

  public AbsParameter<M> setDeprecated(Boolean deprecated) {
    this.deprecated = deprecated;
    return this;
  }

  // Style
  public String getStyle() {
    return style;
  }

  public AbsParameter<M> setStyle(String style) {
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

  public AbsParameter<M> setExplode(Boolean explode) {
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

  public AbsParameter<M> setAllowReserved(Boolean allowReserved) {
    this.allowReserved = allowReserved;
    return this;
  }

  // Schema
  public Schema getSchema() {
    return schema;
  }

  public AbsParameter<M> setSchema(Schema schema) {
    this.schema = schema;
    return this;
  }

  // Example
  public Object getExample() {
    return example;
  }

  public AbsParameter<M> setExample(Object example) {
    this.example = example;
    return this;
  }

  // Example
  public Map<String, Example> getExamples() {
    return examples;
  }

  public AbsParameter<M> setExamples(Map<String, Example> examples) {
    this.examples = examples;
    return this;
  }

  public boolean hasExample(String name) {
    return mapHas(examples, name);
  }

  public Example getExample(String name) {
    return mapGet(examples, name);
  }

  public AbsParameter<M> setExample(String name, Example example) {
    if (examples == null) {
      examples = new HashMap<>();
    }
    examples.put(name, example);
    return this;
  }

  public AbsParameter<M> removeExample(String name) {
    mapRemove(examples, name);
    return this;
  }

  // ContentMediaType
  public Map<String, MediaType> getContentMediaTypes() {
    return contentMediaTypes;
  }

  public AbsParameter<M> setContentMediaTypes(Map<String, MediaType> contentMediaTypes) {
    this.contentMediaTypes = contentMediaTypes;
    return this;
  }

  public boolean hasContentMediaType(String name) {
    return mapHas(contentMediaTypes, name);
  }

  public MediaType getContentMediaType(String name) {
    return mapGet(contentMediaTypes, name);
  }

  public AbsParameter<M> setContentMediaType(String name, MediaType contentMediaType) {
    if (contentMediaTypes == null) {
      contentMediaTypes = new HashMap<>();
    }
    contentMediaTypes.put(name, contentMediaType);
    return this;
  }

  public AbsParameter<M> removeContentMediaType(String name) {
    mapRemove(contentMediaTypes, name);
    return this;
  }

  void copyReference(AbsParameter<M> copy) {
    copy.setRef(getRef());
  }

  void copyContent(OAIContext context, AbsParameter<M> copy, boolean followRefs) {
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
  }
}
