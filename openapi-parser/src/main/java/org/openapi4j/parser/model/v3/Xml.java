package org.openapi4j.parser.model.v3;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

import org.openapi4j.core.model.OAIContext;
import org.openapi4j.parser.model.AbsOpenApiSchema;

@SuppressWarnings("unused")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Xml extends AbsOpenApiSchema<Xml> {
  private String name;
  private String namespace;
  private String prefix;
  private Boolean attribute;
  private Boolean wrapped;
  @JsonUnwrapped
  private Extensions extensions;

  // Name
  public String getName() {
    return name;
  }

  public Xml setName(String name) {
    this.name = name;
    return this;
  }

  // Namespace
  public String getNamespace() {
    return namespace;
  }

  public Xml setNamespace(String namespace) {
    this.namespace = namespace;
    return this;
  }

  // Prefix
  public String getPrefix() {
    return prefix;
  }

  public Xml setPrefix(String prefix) {
    this.prefix = prefix;
    return this;
  }

  // Attribute
  public Boolean getAttribute() {
    return attribute;
  }

  public boolean isAttribute() {
    return attribute != null ? attribute : false;
  }

  public Xml setAttribute(Boolean attribute) {
    this.attribute = attribute;
    return this;
  }

  // Wrapped
  public Boolean getWrapped() {
    return wrapped;
  }

  public boolean isWrapped() {
    return wrapped != null ? wrapped : false;
  }

  public Xml setWrapped(Boolean wrapped) {
    this.wrapped = wrapped;
    return this;
  }

  // Extensions
  public Extensions getExtensions() {
    return extensions;
  }

  public Xml setExtensions(Extensions extensions) {
    this.extensions = extensions;
    return this;
  }

  @Override
  public Xml copy(OAIContext context, boolean followRefs) {
    Xml copy = new Xml();

    copy.setName(name);
    copy.setNamespace(namespace);
    copy.setPrefix(prefix);
    copy.setAttribute(attribute);
    copy.setWrapped(wrapped);
    copy.setExtensions(copyField(extensions, context, followRefs));

    return copy;
  }
}
