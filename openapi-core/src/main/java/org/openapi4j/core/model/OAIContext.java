package org.openapi4j.core.model;

import org.openapi4j.core.model.reference.ReferenceRegistry;

import java.net.URI;

public interface OAIContext<O extends OAI> {
  /**
   * Get the reference registry.
   *
   * @return The reference registry.
   */
  ReferenceRegistry getReferenceRegistry();

  /**
   * The base URI of the context.
   * @return The base URI of the context.
   */
  URI getBaseUri();
}
