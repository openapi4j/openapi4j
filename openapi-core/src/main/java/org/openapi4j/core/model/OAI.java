package org.openapi4j.core.model;

public interface OAI<O extends OAI> {
  /**
   * Get the Open API context.
   * @return The Open API context.
   */
  OAIContext<O> getContext();

  /**
   * Set the Open API context.
   * @param context The Open API context.
   */
  void setContext(OAIContext<O> context);
}
