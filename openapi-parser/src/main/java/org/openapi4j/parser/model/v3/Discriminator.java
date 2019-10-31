package org.openapi4j.parser.model.v3;

import org.openapi4j.core.model.OAIContext;
import org.openapi4j.core.model.v3.OAI3;
import org.openapi4j.parser.model.AbsOpenApiSchema;

import java.util.Map;

public class Discriminator extends AbsOpenApiSchema<OAI3, Discriminator> {
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
  public Discriminator copy(OAIContext<OAI3> context, boolean followRefs) {
    Discriminator copy = new Discriminator();

    copy.setPropertyName(propertyName);
    copy.setMapping(copyMap(mapping));

    return copy;
  }
}
