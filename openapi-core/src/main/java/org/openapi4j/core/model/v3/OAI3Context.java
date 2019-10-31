package org.openapi4j.core.model.v3;

import com.fasterxml.jackson.databind.JsonNode;

import org.openapi4j.core.exception.ResolutionException;
import org.openapi4j.core.model.OAIContext;
import org.openapi4j.core.model.reference.ReferenceRegistry;
import org.openapi4j.core.model.reference.ReferenceResolver;

import java.net.URI;

import static org.openapi4j.core.model.v3.OAI3SchemaKeywords.$REF;

/**
 * Open API v3 context.
 * <p/>
 * This class handles references and base URI.
 */
public class OAI3Context implements OAIContext<OAI3> {
  static final String OPERATIONREF = "operationRef";

  private final ReferenceRegistry referenceRegistry = new ReferenceRegistry();
  private final URI baseUri;

  public OAI3Context(URI baseUri) throws ResolutionException {
    this(baseUri, null);
  }

  public OAI3Context(URI baseUri, JsonNode apiNode) throws ResolutionException {
    this.baseUri = baseUri;

    ReferenceResolver resolver = new ReferenceResolver(baseUri, apiNode, $REF, referenceRegistry);
    resolver.resolve();

    ReferenceRegistry mappingRefsRegistry = new ReferenceRegistry();
    MappingReferenceResolver mappingResolver = new MappingReferenceResolver(baseUri, apiNode, $REF, mappingRefsRegistry);
    mappingResolver.resolve();
    referenceRegistry.mergeRefs(mappingRefsRegistry);

    ReferenceRegistry operationRefsRegistry = new ReferenceRegistry();
    ReferenceResolver operationResolver = new ReferenceResolver(baseUri, apiNode, OPERATIONREF, operationRefsRegistry);
    operationResolver.resolve();
    referenceRegistry.mergeRefs(operationRefsRegistry);
  }

  @Override
  public ReferenceRegistry getReferenceRegistry() {
    return referenceRegistry;
  }

  @Override
  public URI getBaseUri() {
    return baseUri;
  }
}
