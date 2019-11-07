package org.openapi4j.parser.model.v3;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;

import org.openapi4j.core.model.OAIContext;
import org.openapi4j.parser.model.AbsOpenApiSchema;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unused")
public class Xml extends AbsOpenApiSchema<Xml> {
  private String name;
  private String namespace;
  private String prefix;
  private Boolean attribute;
  private Boolean wrapped;
  private Map<String, Object> extensions;

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
