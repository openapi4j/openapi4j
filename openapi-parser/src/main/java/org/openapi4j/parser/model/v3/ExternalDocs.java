package org.openapi4j.parser.model.v3;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import org.openapi4j.core.model.OAIContext;
import org.openapi4j.parser.model.AbsOpenApiSchema;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExternalDocs extends AbsOpenApiSchema<ExternalDocs> {
  private String description;
  @JsonUnwrapped
  private Extensions extensions;
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

  // Extensions
  public Extensions getExtensions() {
    return extensions;
  }

  public ExternalDocs setExtensions(Extensions extensions) {
    this.extensions = extensions;
    return this;
  }

  @Override
  public ExternalDocs copy(OAIContext context, boolean followRefs) {
    ExternalDocs copy = new ExternalDocs();

    copy.setDescription(description);
    copy.setUrl(url);
    copy.setExtensions(copyField(extensions, context, followRefs));

    return copy;
  }
}
