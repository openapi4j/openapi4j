package org.openapi4j.parser.model.v3;

public class OAuthFlows extends AbsExtendedOpenApiSchema<OAuthFlows> {
  private OAuthFlow implicit;
  private OAuthFlow password;
  private OAuthFlow clientCredentials;
  private OAuthFlow authorizationCode;

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

  @Override
  public OAuthFlows copy() {
    OAuthFlows copy = new OAuthFlows();

    copy.setImplicit(copyField(getImplicit()));
    copy.setPassword(copyField(getPassword()));
    copy.setClientCredentials(copyField(getClientCredentials()));
    copy.setAuthorizationCode(copyField(getAuthorizationCode()));
    copy.setExtensions(copySimpleMap(getExtensions()));

    return copy;
  }
}
