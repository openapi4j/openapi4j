package org.openapi4j.parser.model.v3;

public class License extends AbsExtendedOpenApiSchema<License> {
  private String name;
  private String url;
  // 3.1
  private String identifier;

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

  public String getIdentifier() {
    return identifier;
  }

  public License setIdentifier(String identifier) {
    this.identifier = identifier;
    return this;
  }

  @Override
  public License copy() {
    License copy = new License();

    copy.setName(getName());
    copy.setUrl(getUrl());
    copy.setExtensions(copySimpleMap(getExtensions()));
    copy.setIdentifier(getIdentifier());

    return copy;
  }
}
