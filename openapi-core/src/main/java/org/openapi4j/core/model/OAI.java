package org.openapi4j.core.model;

public interface OAI<O extends OAI> {
  OAIContext<O> getContext();

  void setContext(OAIContext<O> context);
}
