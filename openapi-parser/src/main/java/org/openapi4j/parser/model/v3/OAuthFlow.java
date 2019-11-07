package org.openapi4j.parser.model.v3;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import org.openapi4j.core.model.OAIContext;
import org.openapi4j.parser.model.AbsOpenApiSchema;

import java.util.HashMap;
import java.util.Map;

public class OAuthFlow extends AbsOpenApiSchema<OAuthFlow> {
  private String authorizationUrl;
  private String tokenUrl;
  private String refreshUrl;
  private Map<String, String> scopes;
  @JsonIgnore
  private String configuration;
  private Map<String, Object> extensions;

  // AuthorizationUrl
  public String getAuthorizationUrl() {
    return authorizationUrl;
  }

  public OAuthFlow setAuthorizationUrl(String authorizationUrl) {
    this.authorizationUrl = authorizationUrl;
    return this;
  }

  // TokenUrl
  public String getTokenUrl() {
    return tokenUrl;
  }

  public OAuthFlow setTokenUrl(String tokenUrl) {
    this.tokenUrl = tokenUrl;
    return this;
  }

  // RefreshUrl
  public String getRefreshUrl() {
    return refreshUrl;
  }

  public OAuthFlow setRefreshUrl(String refreshUrl) {
    this.refreshUrl = refreshUrl;
    return this;
  }

  // configuration
  public String getConfiguration() {
    return configuration;
  }

  public OAuthFlow setConfiguration(String configuration) {
    this.configuration = configuration;
    return this;
  }

  // Scope
  public Map<String, String> getScopes() {
    return scopes;
  }

  public OAuthFlow setScopes(Map<String, String> scopes) {
    this.scopes = scopes;
    return this;
  }

  public boolean hasScope(String name) {
    return mapHas(scopes, name);
  }

  public String getScope(String name) {
    return mapGet(scopes, name);
  }

  public OAuthFlow setScope(String name, String scope) {
    if (scopes == null) {
      scopes = new HashMap<>();
    }
    scopes.put(name, scope);
    return this;
  }

  public OAuthFlow removeScope(String name) {
    mapRemove(scopes, name);
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
  public OAuthFlow copy(OAIContext context, boolean followRefs) {
    OAuthFlow copy = new OAuthFlow();

    copy.setAuthorizationUrl(getAuthorizationUrl());
    copy.setTokenUrl(getTokenUrl());
    copy.setRefreshUrl(getRefreshUrl());
    copy.setScopes(copyMap(getScopes()));
    copy.setConfiguration(getConfiguration());
    copy.setExtensions(copyMap(getExtensions()));

    return copy;
  }
}
