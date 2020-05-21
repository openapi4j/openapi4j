package org.openapi4j.parser.model.v3;

@SuppressWarnings({"unused", "UnusedReturnValue"})
public class Tag extends AbsExtendedOpenApiSchema<Tag> {
  private String name;
  private String description;
  private ExternalDocs externalDocs;

  // Name
  public String getName() {
    return name;
  }

  public Tag setName(String name) {
    this.name = name;
    return this;
  }

  // Description
  public String getDescription() {
    return description;
  }

  public Tag setDescription(String description) {
    this.description = description;
    return this;
  }

  // ExternalDocs
  public ExternalDocs getExternalDocs() {
    return externalDocs;
  }

  public Tag setExternalDocs(ExternalDocs externalDocs) {
    this.externalDocs = externalDocs;
    return this;
  }

  @Override
  public Tag copy() {
    Tag copy = new Tag();

    copy.setName(getName());
    copy.setDescription(getDescription());
    copy.setExternalDocs(copyField(getExternalDocs()));
    copy.setExtensions(copySimpleMap(getExtensions()));

    return copy;
  }
}
