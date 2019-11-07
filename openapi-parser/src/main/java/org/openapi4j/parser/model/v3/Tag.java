package org.openapi4j.parser.model.v3;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;

import org.openapi4j.core.model.OAIContext;
import org.openapi4j.parser.model.AbsOpenApiSchema;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings({"unused", "UnusedReturnValue"})
public class Tag extends AbsOpenApiSchema<Tag> {
  private String name;
  private String description;
  private ExternalDocs externalDocs;
  private Map<String, Object> extensions;

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
  public Tag copy(OAIContext context, boolean followRefs) {
    Tag copy = new Tag();

    copy.setName(getName());
    copy.setDescription(getDescription());
    copy.setExternalDocs(copyField(getExternalDocs(), context, followRefs));
    copy.setExtensions(copyMap(getExtensions()));

    return copy;
  }
}
