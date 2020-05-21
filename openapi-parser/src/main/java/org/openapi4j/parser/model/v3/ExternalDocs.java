package org.openapi4j.parser.model.v3;

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
  public ExternalDocs copy() {
    ExternalDocs copy = new ExternalDocs();

    copy.setDescription(getDescription());
    copy.setUrl(getUrl());
    copy.setExtensions(copySimpleMap(getExtensions()));

    return copy;
  }
}
