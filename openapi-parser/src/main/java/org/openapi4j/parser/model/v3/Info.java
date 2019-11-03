package org.openapi4j.parser.model.v3;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import org.openapi4j.core.model.OAIContext;
import org.openapi4j.parser.model.AbsOpenApiSchema;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Info extends AbsOpenApiSchema<Info> {
  private Contact contact;
  private String description;
  @JsonUnwrapped
  private Extensions extensions;
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

  // Extensions
  public Extensions getExtensions() {
    return extensions;
  }

  public Info setExtensions(Extensions extensions) {
    this.extensions = extensions;
    return this;
  }

  @Override
  public Info copy(OAIContext context, boolean followRefs) {
    Info copy = new Info();

    copy.setTitle(title);
    copy.setDescription(description);
    copy.setTermsOfService(termsOfService);
    copy.setContact(copyField(contact, context, followRefs));
    copy.setLicense(copyField(license, context, followRefs));
    copy.setVersion(version);
    copy.setExtensions(copyField(extensions, context, followRefs));

    return copy;
  }
}
