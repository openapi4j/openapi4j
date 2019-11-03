package org.openapi4j.core.model;

public interface OAI {
  /**
   * Get the Open API context.
   * @return The Open API context.
   */
  OAIContext getContext();

  /**
   * Set the Open API context.
   * @param context The Open API context.
   */
  void setContext(OAIContext context);
}
