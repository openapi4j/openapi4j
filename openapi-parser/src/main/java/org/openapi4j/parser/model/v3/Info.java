package org.openapi4j.parser.model.v3;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;

import org.openapi4j.core.model.OAIContext;
import org.openapi4j.parser.model.AbsOpenApiSchema;

import java.util.HashMap;
import java.util.Map;

public class Info extends AbsOpenApiSchema<Info> {
  private Contact contact;
  private String description;
  private Map<String, Object> extensions;
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
  public Info copy(OAIContext context, boolean followRefs) {
    Info copy = new Info();

    copy.setTitle(getTitle());
    copy.setDescription(getDescription());
    copy.setTermsOfService(getTermsOfService());
    copy.setContact(copyField(getContact(), context, followRefs));
    copy.setLicense(copyField(getLicense(), context, followRefs));
    copy.setVersion(getVersion());
    copy.setExtensions(copyMap(getExtensions()));

    return copy;
  }
}
