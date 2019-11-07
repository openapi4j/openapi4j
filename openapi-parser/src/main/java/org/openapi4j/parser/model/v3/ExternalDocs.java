package org.openapi4j.parser.model.v3;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;

import org.openapi4j.core.model.OAIContext;
import org.openapi4j.parser.model.AbsOpenApiSchema;

import java.util.HashMap;
import java.util.Map;

public class ExternalDocs extends AbsOpenApiSchema<ExternalDocs> {
  private String description;
  private Map<String, Object> extensions;
  private String url;

  // Description
  public String getDescription() {
    return description;
  }

  public ExternalDocs setDescription(String description) {
    this.description = description;
    return this;
  }

  // Url
  public String getUrl() {
    return url;
  }

  public ExternalDocs setUrl(String url) {
    this.url = url;
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
  public ExternalDocs copy(OAIContext context, boolean followRefs) {
    ExternalDocs copy = new ExternalDocs();

    copy.setDescription(getDescription());
    copy.setUrl(getUrl());
    copy.setExtensions(copyMap(getExtensions()));

    return copy;
  }
}
