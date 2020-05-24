package org.openapi4j.parser.model.v3;

public class Info extends AbsExtendedOpenApiSchema<Info> {
  private Contact contact;
  private String description;
  private License license;
  private String termsOfService;
  private String title;
  private String version;

  // Title
  public String getTitle() {
    return title;
  }

  public Info setTitle(String title) {
    this.title = title;
    return this;
  }

  // Description
  public String getDescription() {
    return description;
  }

  public Info setDescription(String description) {
    this.description = description;
    return this;
  }

  // TermsOfService
  public String getTermsOfService() {
    return termsOfService;
  }

  public Info setTermsOfService(String termsOfService) {
    this.termsOfService = termsOfService;
    return this;
  }

  // Contact
  public Contact getContact() {
    return contact;
  }

  public Info setContact(Contact contact) {
    this.contact = contact;
    return this;
  }

  // License
  public License getLicense() {
    return license;
  }

  public Info setLicense(License license) {
    this.license = license;
    return this;
  }

  // Version
  public String getVersion() {
    return version;
  }

  public Info setVersion(String version) {
    this.version = version;
    return this;
  }

  @Override
  public Info copy() {
    Info copy = new Info();

    copy.setTitle(getTitle());
    copy.setDescription(getDescription());
    copy.setTermsOfService(getTermsOfService());
    copy.setContact(copyField(getContact()));
    copy.setLicense(copyField(getLicense()));
    copy.setVersion(getVersion());
    copy.setExtensions(copySimpleMap(getExtensions()));

    return copy;
  }
}
