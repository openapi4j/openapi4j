package org.openapi4j.core.model.reference;

import com.fasterxml.jackson.databind.JsonNode;

import java.net.URI;
import java.util.Collection;

public class ReferenceResolver extends AbstractReferenceResolver {
  public ReferenceResolver(URI baseUri, JsonNode apiNode, String refKeyword, ReferenceRegistry referenceRegistry) {
    super(baseUri, apiNode, refKeyword, referenceRegistry);
  }

  @Override
  protected Collection<JsonNode> getReferencePaths(JsonNode document) {
    return document.findValues(refKeyword);
  }
}
