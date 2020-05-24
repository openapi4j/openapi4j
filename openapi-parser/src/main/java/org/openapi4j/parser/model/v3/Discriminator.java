package org.openapi4j.parser.model.v3;

import org.openapi4j.parser.model.AbsOpenApiSchema;

import java.util.Map;

public class Discriminator extends AbsOpenApiSchema<Discriminator> {
  private Map<String, String> mapping;
  private String propertyName;

  public String getPropertyName() {
    return propertyName;
  }

  public Discriminator setPropertyName(String propertyName) {
    this.propertyName = propertyName;
    return this;
  }

  public Map<String, String> getMapping() {
    return mapping;
  }

  public Discriminator setMapping(Map<String, String> mapping) {
    this.mapping = mapping;
    return this;
  }

  @Override
  public Discriminator copy() {
    Discriminator copy = new Discriminator();

    copy.setPropertyName(getPropertyName());
    copy.setMapping(copySimpleMap(getMapping()));

    return copy;
  }
}
