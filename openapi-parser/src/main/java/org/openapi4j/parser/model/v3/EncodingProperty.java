package org.openapi4j.parser.model.v3;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;

import org.openapi4j.core.model.OAIContext;
import org.openapi4j.parser.model.AbsOpenApiSchema;

import java.util.HashMap;
import java.util.Map;

public class EncodingProperty extends AbsOpenApiSchema<EncodingProperty> {
  private String contentType;
  private Boolean explode;
  private Map<String, Object> extensions;
  private Map<String, String> headers;
  private String style;

  // ContentType
  public String getContentType() {
    return contentType;
  }

  public EncodingProperty setContentType(String contentType) {
    this.contentType = contentType;
    return this;
  }

  // Header
  public Map<String, String> getHeaders() {
    return headers;
  }

  public EncodingProperty setHeaders(Map<String, String> headers) {
    this.headers = headers;
    return this;
  }

  public boolean hasHeader(String name) {
    return mapHas(headers, name);
  }

  public String getHeader(String name) {
    return mapGet(headers, name);
  }

  public EncodingProperty setHeader(String name, String header) {
    if (headers == null) {
      headers = new HashMap<>();
    }
    headers.put(name, header);
    return this;
  }

  public EncodingProperty removeHeader(String name) {
    mapRemove(headers, name);
    return this;
  }

  // Style
  public String getStyle() {
    return style;
  }

  public void setStyle(String style) {
    this.style = style;
  }

  // Explode
  public Boolean getExplode() {
    return explode;
  }

  public boolean isExplode() {
    return explode != null ? explode : false;
  }

  public EncodingProperty setExplode(Boolean explode) {
    this.explode = explode;
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
  public EncodingProperty copy(OAIContext context, boolean followRefs) {
    EncodingProperty copy = new EncodingProperty();

    copy.setContentType(getContentType());
    copy.setHeaders(copyMap(getHeaders()));
    copy.setStyle(getStyle());
    copy.setExplode(getExplode());
    copy.setExtensions(copyMap(getExtensions()));

    return copy;
  }
}
