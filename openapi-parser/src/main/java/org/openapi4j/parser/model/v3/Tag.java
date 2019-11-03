package org.openapi4j.parser.model.v3;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import org.openapi4j.core.model.OAIContext;
import org.openapi4j.parser.model.AbsOpenApiSchema;

@SuppressWarnings({"unused", "UnusedReturnValue"})
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Tag extends AbsOpenApiSchema<Tag> {
  private String name;
  private String description;
  private ExternalDocs externalDocs;
  @JsonUnwrapped
  private Extensions extensions;

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

  // Extensions
  public Extensions getExtensions() {
    return extensions;
  }

  public Tag setExtensions(Extensions extensions) {
    this.extensions = extensions;
    return this;
  }

  @Override
  public Tag copy(OAIContext context, boolean followRefs) {
    Tag copy = new Tag();

    copy.setName(name);
    copy.setDescription(description);
    copy.setExternalDocs(copyField(externalDocs, context, followRefs));
    copy.setExtensions(copyField(extensions, context, followRefs));

    return copy;
  }
}
