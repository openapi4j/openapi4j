package org.openapi4j.parser.model.v3;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import org.openapi4j.core.model.OAIContext;
import org.openapi4j.parser.model.AbsOpenApiSchema;

@SuppressWarnings({"unused", "UnusedReturnValue"})
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SecurityScheme extends AbsOpenApiSchema<SecurityScheme> {
  private String type;
  private String description;
  private String name;
  private String in;
  private String scheme;
  private String bearerFormat;
  private OAuthFlows flows;
  private String openIdConnectUrl;
  @JsonUnwrapped
  private Extensions extensions;

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
  public Extensions getExtensions() {
    return extensions;
  }

  public SecurityScheme setExtensions(Extensions extensions) {
    this.extensions = extensions;
    return this;
  }

  @Override
  public SecurityScheme copy(OAIContext context, boolean followRefs) {
    SecurityScheme copy = new SecurityScheme();

    copy.setType(type);
    copy.setDescription(description);
    copy.setName(name);
    copy.setIn(in);
    copy.setScheme(scheme);
    copy.setBearerFormat(bearerFormat);
    copy.setFlows(copyField(flows, context, followRefs));
    copy.setOpenIdConnectUrl(openIdConnectUrl);
    copy.setExtensions(copyField(extensions, context, followRefs));

    return copy;
  }
}
