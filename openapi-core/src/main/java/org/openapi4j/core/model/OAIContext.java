package org.openapi4j.core.model;

import com.fasterxml.jackson.databind.JsonNode;

import org.openapi4j.core.model.reference.ReferenceRegistry;

import java.net.URI;

public interface OAIContext {
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

  /**
   * Get the base document of API description.
   */
  JsonNode getBaseDocument();
}
