package org.openapi4j.parser.model.v3;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

import org.openapi4j.core.model.OAIContext;
import org.openapi4j.parser.model.AbsOpenApiSchema;

import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class EncodingProperty extends AbsOpenApiSchema<EncodingProperty> {
  private String contentType;
  private Boolean explode;
  @JsonUnwrapped
  private Extensions extensions;
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
  public Extensions getExtensions() {
    return extensions;
  }

  public EncodingProperty setExtensions(Extensions extensions) {
    this.extensions = extensions;
    return this;
  }

  @Override
  public EncodingProperty copy(OAIContext context, boolean followRefs) {
    EncodingProperty copy = new EncodingProperty();

    copy.setContentType(contentType);
    copy.setHeaders(copyMap(headers));
    copy.setStyle(style);
    copy.setExplode(explode);
    copy.setExtensions(copyField(extensions, context, followRefs));

    return copy;
  }
}
