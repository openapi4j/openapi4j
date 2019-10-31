package org.openapi4j.parser.model.v3;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

import org.openapi4j.core.model.OAIContext;
import org.openapi4j.core.model.v3.OAI3;
import org.openapi4j.parser.model.AbsOpenApiSchema;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class License extends AbsOpenApiSchema<OAI3, License> {
  @JsonUnwrapped
  private Extensions extensions;
  private String name;
  private String url;

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

  // Extensions
  public Extensions getExtensions() {
    return extensions;
  }

  public License setExtensions(Extensions extensions) {
    this.extensions = extensions;
    return this;
  }

  @Override
  public License copy(OAIContext<OAI3> context, boolean followRefs) {
    License copy = new License();

    copy.setName(name);
    copy.setUrl(url);
    copy.setExtensions(copyField(extensions, context, followRefs));

    return copy;
  }
}
