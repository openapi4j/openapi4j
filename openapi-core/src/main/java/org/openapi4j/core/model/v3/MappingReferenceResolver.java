package org.openapi4j.core.model.v3;

import com.fasterxml.jackson.databind.JsonNode;

import org.openapi4j.core.model.reference.AbstractReferenceResolver;
import org.openapi4j.core.model.reference.ReferenceRegistry;

import java.net.URI;
import java.util.Collection;
import java.util.HashSet;

import static org.openapi4j.core.model.v3.OAI3SchemaKeywords.MAPPING;

class MappingReferenceResolver extends AbstractReferenceResolver {
  MappingReferenceResolver(URI baseUri, JsonNode apiNode, String refKeyword, ReferenceRegistry referenceRegistry) {
    super(baseUri, apiNode, refKeyword, referenceRegistry);
  }

  @Override
  protected Collection<JsonNode> getReferencePaths(JsonNode document) {
    Collection<JsonNode> referenceNodes = document.findValues(MAPPING);

    Collection<JsonNode> referencePaths = new HashSet<>();

    for (JsonNode refNode : referenceNodes) {
      for (JsonNode mappingNode : refNode) {
        referencePaths.add(mappingNode);
      }
    }

    return referencePaths;
  }
}
