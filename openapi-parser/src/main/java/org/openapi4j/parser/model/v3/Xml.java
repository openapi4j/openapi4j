package org.openapi4j.parser.model.v3;

import org.openapi4j.core.model.OAIContext;

@SuppressWarnings({"unused", "WeakerAccess", "UnusedReturnValue"})
public class Xml extends AbsExtendedOpenApiSchema<Xml> {
  private String name;
  private String namespace;
  private String prefix;
  private Boolean attribute;
  private Boolean wrapped;

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
    return Boolean.TRUE.equals(attribute);
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
    return Boolean.TRUE.equals(wrapped);
  }

  public Xml setWrapped(Boolean wrapped) {
    this.wrapped = wrapped;
    return this;
  }

  @Override
  public Xml copy(OAIContext context, boolean followRefs) {
    Xml copy = new Xml();

    copy.setName(getName());
    copy.setNamespace(getNamespace());
    copy.setPrefix(getPrefix());
    copy.setAttribute(getAttribute());
    copy.setWrapped(getWrapped());
    copy.setExtensions(copyMap(getExtensions()));

    return copy;
  }
}
