package org.openapi4j.core.model;

import org.openapi4j.core.model.reference.ReferenceRegistry;

import java.net.URI;

public interface OAIContext<O extends OAI> {
  ReferenceRegistry getReferenceRegistry();

  URI getBaseUri();
}
