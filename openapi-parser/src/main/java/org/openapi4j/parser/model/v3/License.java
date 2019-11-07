package org.openapi4j.parser.model.v3;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;

import org.openapi4j.core.model.OAIContext;
import org.openapi4j.parser.model.AbsOpenApiSchema;

import java.util.HashMap;
import java.util.Map;

public class License extends AbsOpenApiSchema<License> {
  private Map<String, Object> extensions;
  private String name;
  private String url;

  // Name
  public String getName() {
    return name;
  }

  public License setName(String name) {
    this.name = name;
    return this;
  }

  // Url
  public String getUrl() {
    return url;
  }

  public License setUrl(String url) {
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
  public License copy(OAIContext context, boolean followRefs) {
    License copy = new License();

    copy.setName(getName());
    copy.setUrl(getUrl());
    copy.setExtensions(copyMap(getExtensions()));

    return copy;
  }
}
