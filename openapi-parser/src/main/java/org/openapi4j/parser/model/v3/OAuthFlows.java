package org.openapi4j.parser.model.v3;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;

import org.openapi4j.core.model.OAIContext;
import org.openapi4j.parser.model.AbsOpenApiSchema;

import java.util.HashMap;
import java.util.Map;

public class OAuthFlows extends AbsOpenApiSchema<OAuthFlows> {
  private OAuthFlow implicit;
  private OAuthFlow password;
  private OAuthFlow clientCredentials;
  private OAuthFlow authorizationCode;
  private Map<String, Object> extensions;

  // ImplicitOAuthFlow
  public OAuthFlow getImplicit() {
    return implicit;
  }

  public OAuthFlows setImplicit(OAuthFlow implicit) {
    this.implicit = implicit;
    if (this.implicit != null) {
      this.implicit.setConfiguration("implicit");
    }
    return this;
  }

  // PasswordOAuthFlow
  public OAuthFlow getPassword() {
    return password;
  }

  public OAuthFlows setPassword(OAuthFlow password) {
    this.password = password;
    if (this.password != null) {
      this.password.setConfiguration("password");
    }
    return this;
  }

  // ClientCredentialsOAuthFlow
  public OAuthFlow getClientCredentials() {
    return clientCredentials;
  }

  public OAuthFlows setClientCredentials(OAuthFlow clientCredentials) {
    this.clientCredentials = clientCredentials;
    if (this.clientCredentials != null) {
      this.clientCredentials.setConfiguration("clientCredentials");
    }
    return this;
  }

  // AuthorizationCodeOAuthFlow
  public OAuthFlow getAuthorizationCode() {
    return authorizationCode;
  }

  public OAuthFlows setAuthorizationCode(OAuthFlow authorizationCode) {
    this.authorizationCode = authorizationCode;
    if (this.authorizationCode != null) {
      this.authorizationCode.setConfiguration("authorizationCode");
    }
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
  public OAuthFlows copy(OAIContext context, boolean followRefs) {
    OAuthFlows copy = new OAuthFlows();

    copy.setImplicit(copyField(getImplicit(), context, followRefs));
    copy.setPassword(copyField(getPassword(), context, followRefs));
    copy.setClientCredentials(copyField(getClientCredentials(), context, followRefs));
    copy.setAuthorizationCode(copyField(getAuthorizationCode(), context, followRefs));
    copy.setExtensions(copyMap(getExtensions()));

    return copy;
  }
}
