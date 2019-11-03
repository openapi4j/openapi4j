package org.openapi4j.parser.model.v3;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import org.openapi4j.core.model.OAIContext;
import org.openapi4j.parser.model.AbsOpenApiSchema;

import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Header extends AbsOpenApiSchema<Header> {
  private Boolean allowReserved;
  @JsonProperty("content")
  private Map<String, MediaType> contentMediaTypes;
  private Boolean deprecated;
  private String description;
  private Object example;
  private Map<String, Example> examples;
  private Boolean explode;
  @JsonUnwrapped
  private Extensions extensions;
  private Boolean required;
  private Schema schema;
  private String style;

  // Description
  public String getDescription() {
    return description;
  }

  public Header setDescription(String description) {
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

  public Header setRequired(Boolean required) {
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

  public Header setDeprecated(Boolean deprecated) {
    this.deprecated = deprecated;
    return this;
  }

  // Style
  public String getStyle() {
    return style;
  }

  public Header setStyle(String style) {
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

  public Header setExplode(Boolean explode) {
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

  public Header setAllowReserved(Boolean allowReserved) {
    this.allowReserved = allowReserved;
    return this;
  }

  // Schema
  public Schema getSchema() {
    return schema;
  }

  public Header setSchema(Schema schema) {
    this.schema = schema;
    return this;
  }

  // Example
  public Object getExample() {
    return example;
  }

  public Header setExample(Object example) {
    this.example = example;
    return this;
  }

  // Example
  public Map<String, Example> getExamples() {
    return examples;
  }

  public Header setExamples(Map<String, Example> examples) {
    this.examples = examples;
    return this;
  }

  public boolean hasExample(String name) {
    return has(examples, name);
  }

  public Example getExample(String name) {
    return get(examples, name);
  }

  public Header setExample(String name, Example example) {
    if (examples == null) {
      examples = new HashMap<>();
    }
    examples.put(name, example);
    return this;
  }

  public Header removeExample(String name) {
    remove(examples, name);
    return this;
  }

  // ContentMediaType
  public Map<String, MediaType> getContentMediaTypes() {
    return contentMediaTypes;
  }

  public Header setContentMediaTypes(Map<String, MediaType> contentMediaTypes) {
    this.contentMediaTypes = contentMediaTypes;
    return this;
  }

  public boolean hasContentMediaType(String name) {
    return has(contentMediaTypes, name);
  }

  public MediaType getContentMediaType(String name) {
    return get(contentMediaTypes, name);
  }

  public Header setContentMediaType(String name, MediaType contentMediaType) {
    if (contentMediaTypes == null) {
      contentMediaTypes = new HashMap<>();
    }
    contentMediaTypes.put(name, contentMediaType);
    return this;
  }

  public Header removeContentMediaType(String name) {
    remove(contentMediaTypes, name);
    return this;
  }

  // Extensions
  public Extensions getExtensions() {
    return extensions;
  }

  public void setExtensions(Extensions extensions) {
    this.extensions = extensions;
  }

  @Override
  public Header copy(OAIContext context, boolean followRefs) {
    Header copy = new Header();

    copy.setDescription(description);
    copy.setRequired(required);
    copy.setDeprecated(deprecated);
    copy.setStyle(style);
    copy.setExplode(explode);
    copy.setAllowReserved(allowReserved);
    copy.setSchema(copyField(schema, context, followRefs));
    copy.setExample(example);
    copy.setExamples(copyMap(examples, context, followRefs));
    copy.setContentMediaTypes(copyMap(contentMediaTypes, context, followRefs));
    copy.setExtensions(copyField(extensions, context, followRefs));

    return copy;
  }
}
