package org.openapi4j.parser.model.v3;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;

import org.openapi4j.core.model.OAIContext;
import org.openapi4j.parser.model.AbsOpenApiSchema;

import java.util.HashMap;
import java.util.Map;

public class Contact extends AbsOpenApiSchema<Contact> {
  private String email;
  private Map<String, Object> extensions;
  private String name;
  private String url;

  // Name
  public String getName() {
    return name;
  }

  public Contact setName(String name) {
    this.name = name;
    return this;
  }

  // Url
  public String getUrl() {
    return url;
  }

  public Contact setUrl(String url) {
    this.url = url;
    return this;
  }

  // Email
  public String getEmail() {
    return email;
  }

  public Contact setEmail(String email) {
    this.email = email;
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
  public Contact copy(OAIContext context, boolean followRefs) {
    Contact copy = new Contact();

    copy.setName(getName());
    copy.setUrl(getUrl());
    copy.setEmail(getEmail());
    copy.setExtensions(copyMap(getExtensions()));

    return copy;
  }
}
