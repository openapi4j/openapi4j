package org.openapi4j.parser.model.v3;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

import org.openapi4j.core.model.OAIContext;
import org.openapi4j.core.model.v3.OAI3;
import org.openapi4j.parser.model.AbsOpenApiSchema;

import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class OAuthFlow extends AbsOpenApiSchema<OAI3, OAuthFlow> {
  private String authorizationUrl;
  private String tokenUrl;
  private String refreshUrl;
  private Map<String, String> scopes;
  @JsonIgnore
  private String configuration;
  @JsonUnwrapped
  private Extensions extensions;

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
    return has(scopes, name);
  }

  public String getScope(String name) {
    return get(scopes, name);
  }

  public OAuthFlow setScope(String name, String scope) {
    if (scopes == null) {
      scopes = new HashMap<>();
    }
    scopes.put(name, scope);
    return this;
  }

  public OAuthFlow removeScope(String name) {
    remove(scopes, name);
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
  public OAuthFlow copy(OAIContext<OAI3> context, boolean followRefs) {
    OAuthFlow copy = new OAuthFlow();

    copy.setAuthorizationUrl(authorizationUrl);
    copy.setTokenUrl(tokenUrl);
    copy.setRefreshUrl(refreshUrl);
    copy.setScopes(copyMap(scopes));
    copy.setConfiguration(configuration);
    copy.setExtensions(copyField(extensions, context, followRefs));

    return copy;
  }
}
