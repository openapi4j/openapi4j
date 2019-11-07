package org.openapi4j.parser.model.v3;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.openapi4j.core.model.OAIContext;
import org.openapi4j.parser.model.AbsRefOpenApiSchema;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unused")
public class RequestBody extends AbsRefOpenApiSchema<RequestBody> {
  @JsonProperty("content")
  private Map<String, MediaType> contentMediaTypes;
  private Map<String, Object> extensions;
  private String description;
  private Boolean required;

  // Description
  public String getDescription() {
    return description;
  }

  public RequestBody setDescription(String description) {
    this.description = description;
    return this;
  }

  // ContentMediaType
  public Map<String, MediaType> getContentMediaTypes() {
    return contentMediaTypes;
  }

  public RequestBody setContentMediaTypes(Map<String, MediaType> contentMediaTypes) {
    this.contentMediaTypes = contentMediaTypes;
    return this;
  }

  public boolean hasContentMediaType(String name) {
    return mapHas(contentMediaTypes, name);
  }

  public MediaType getContentMediaType(String name) {
    return mapGet(contentMediaTypes, name);
  }

  public RequestBody setContentMediaType(String name, MediaType contentMediaType) {
    if (contentMediaTypes == null) {
      contentMediaTypes = new HashMap<>();
    }
    contentMediaTypes.put(name, contentMediaType);
    return this;
  }

  public RequestBody removeContentMediaType(String name) {
    mapRemove(contentMediaTypes, name);
    return this;
  }

  // Required
  public Boolean getRequired() {
    return required;
  }

  public boolean isRequired() {
    return required != null ? required : false;
  }

  public RequestBody setRequired(Boolean required) {
    this.required = required;
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
  protected RequestBody copyReference(OAIContext context) {
    RequestBody copy = new RequestBody();
    copy.setRef(getRef());
    return copy;
  }

  @Override
  protected RequestBody copyContent(OAIContext context, boolean followRefs) {
    RequestBody copy = new RequestBody();

    copy.setDescription(getDescription());
    copy.setContentMediaTypes(copyMap(getContentMediaTypes(), context, followRefs));
    copy.setRequired(getRequired());
    copy.setExtensions(copyMap(getExtensions()));

    return copy;
  }
}
