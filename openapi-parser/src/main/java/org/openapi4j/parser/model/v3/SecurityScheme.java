package org.openapi4j.parser.model.v3;

@SuppressWarnings({"unused", "UnusedReturnValue"})
public class SecurityScheme extends AbsExtendedRefOpenApiSchema<SecurityScheme> {
  private String type;
  private String description;
  private String name;
  private String in;
  private String scheme;
  private String bearerFormat;
  private OAuthFlows flows;
  private String openIdConnectUrl;

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

  @Override
  public SecurityScheme copy() {
    SecurityScheme copy = new SecurityScheme();

    if (isRef()) {
      copy.setRef(getRef());
      copy.setCanonicalRef(getCanonicalRef());
    } else {
      copy.setType(getType());
      copy.setDescription(getDescription());
      copy.setName(getName());
      copy.setIn(getIn());
      copy.setScheme(getScheme());
      copy.setBearerFormat(getBearerFormat());
      copy.setFlows(copyField(getFlows()));
      copy.setOpenIdConnectUrl(getOpenIdConnectUrl());
      copy.setExtensions(copySimpleMap(getExtensions()));
    }

    return copy;
  }
}
