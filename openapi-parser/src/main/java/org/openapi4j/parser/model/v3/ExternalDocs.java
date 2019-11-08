package org.openapi4j.parser.model.v3;

import org.openapi4j.core.model.OAIContext;

public class ExternalDocs extends AbsExtendedOpenApiSchema<ExternalDocs> {
  private String description;
  private String url;

  // Description
  public String getDescription() {
    return description;
  }

  public ExternalDocs setDescription(String description) {
    this.description = description;
    return this;
  }

  // Url
  public String getUrl() {
    return url;
  }

  public ExternalDocs setUrl(String url) {
    this.url = url;
    return this;
  }

  @Override
  public ExternalDocs copy(OAIContext context, boolean followRefs) {
    ExternalDocs copy = new ExternalDocs();

    copy.setDescription(getDescription());
    copy.setUrl(getUrl());
    copy.setExtensions(copyMap(getExtensions()));

    return copy;
  }
}
