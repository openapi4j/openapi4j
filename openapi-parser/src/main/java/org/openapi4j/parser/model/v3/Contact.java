package org.openapi4j.parser.model.v3;

public class Contact extends AbsExtendedOpenApiSchema<Contact> {
  private String email;
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

  @Override
  public Contact copy() {
    Contact copy = new Contact();

    copy.setName(getName());
    copy.setUrl(getUrl());
    copy.setEmail(getEmail());
    copy.setExtensions(copySimpleMap(getExtensions()));

    return copy;
  }
}
