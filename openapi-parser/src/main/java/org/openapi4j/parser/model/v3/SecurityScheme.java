package org.openapi4j.parser.model.v3;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;

import org.openapi4j.core.model.OAIContext;
import org.openapi4j.parser.model.AbsOpenApiSchema;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings({"unused", "UnusedReturnValue"})
public class SecurityScheme extends AbsOpenApiSchema<SecurityScheme> {
  private String type;
  private String description;
  private String name;
  private String in;
  private String scheme;
  private String bearerFormat;
  private OAuthFlows flows;
  private String openIdConnectUrl;
  private Map<String, Object> extensions;

  // Type
  public String getType() {
    return type;
  }

  public SecurityScheme setType(String type) {
    this.type = type;
    return this;
  }

  // Description
  public String getDescription() {
    return description;
  }

  public SecurityScheme setDescription(String description) {
    this.description = description;
    return this;
  }

  // Name
  public String getName() {
    return name;
  }

  public SecurityScheme setName(String name) {
    this.name = name;
    return this;
  }

  // In
  public String getIn() {
    return in;
  }

  public SecurityScheme setIn(String in) {
    this.in = in;
    return this;
  }

  // Scheme
  public String getScheme() {
    return scheme;
  }

  public SecurityScheme setScheme(String scheme) {
    this.scheme = scheme;
    return this;
  }

  // BearerFormat
  public String getBearerFormat() {
    return bearerFormat;
  }

  public SecurityScheme setBearerFormat(String bearerFormat) {
    this.bearerFormat = bearerFormat;
    return this;
  }

  // Flows
  public OAuthFlows getFlows() {
    return flows;
  }

  public SecurityScheme setFlows(OAuthFlows flows) {
    this.flows = flows;
    return this;
  }

  // OpenIdConnectUrl
  public String getOpenIdConnectUrl() {
    return openIdConnectUrl;
  }

  public SecurityScheme setOpenIdConnectUrl(String openIdConnectUrl) {
    this.openIdConnectUrl = openIdConnectUrl;
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
  public SecurityScheme copy(OAIContext context, boolean followRefs) {
    SecurityScheme copy = new SecurityScheme();

    copy.setType(getType());
    copy.setDescription(getDescription());
    copy.setName(getName());
    copy.setIn(getIn());
    copy.setScheme(getScheme());
    copy.setBearerFormat(getBearerFormat());
    copy.setFlows(copyField(getFlows(), context, followRefs));
    copy.setOpenIdConnectUrl(getOpenIdConnectUrl());
    copy.setExtensions(copyMap(getExtensions()));

    return copy;
  }
}
