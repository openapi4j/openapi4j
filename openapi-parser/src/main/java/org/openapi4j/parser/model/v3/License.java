package org.openapi4j.parser.model.v3;

import org.openapi4j.core.model.OAIContext;

public class License extends AbsExtendedOpenApiSchema<License> {
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

  @Override
  public License copy(OAIContext context, boolean followRefs) {
    License copy = new License();

    copy.setName(getName());
    copy.setUrl(getUrl());
    copy.setExtensions(copyMap(getExtensions()));

    return copy;
  }
}
