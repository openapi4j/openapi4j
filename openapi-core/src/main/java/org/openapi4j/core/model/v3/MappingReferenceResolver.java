package org.openapi4j.core.model.v3;

import com.fasterxml.jackson.databind.JsonNode;
import org.openapi4j.core.model.AuthOption;
import org.openapi4j.core.model.reference.AbstractReferenceResolver;
import org.openapi4j.core.model.reference.ReferenceRegistry;

import java.net.URL;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import static org.openapi4j.core.model.v3.OAI3SchemaKeywords.$REF;
import static org.openapi4j.core.model.v3.OAI3SchemaKeywords.MAPPING;

/**
 * The JSON reference resolver for discriminator mapping.
 */
class MappingReferenceResolver extends AbstractReferenceResolver {
  MappingReferenceResolver(URL baseUrl, List<AuthOption> authOptions, JsonNode apiNode, ReferenceRegistry referenceRegistry) {
    super(baseUrl, authOptions, apiNode, $REF, referenceRegistry);
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
