package org.openapi4j.parser.model.v3;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import org.openapi4j.core.model.OAIContext;
import org.openapi4j.parser.model.AbsOpenApiSchema;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Contact extends AbsOpenApiSchema<Contact> {
  private String email;
  @JsonUnwrapped
  private Extensions extensions;
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

  // Extensions
  public Extensions getExtensions() {
    return extensions;
  }

  public Contact setExtensions(Extensions extensions) {
    this.extensions = extensions;
    return this;
  }

  @Override
  public Contact copy(OAIContext context, boolean followRefs) {
    Contact copy = new Contact();

    copy.setName(name);
    copy.setUrl(url);
    copy.setEmail(email);
    copy.setExtensions(copyField(extensions, context, followRefs));

    return copy;
  }
}
