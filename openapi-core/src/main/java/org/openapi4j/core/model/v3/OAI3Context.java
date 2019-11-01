package org.openapi4j.core.model.v3;

import com.fasterxml.jackson.databind.JsonNode;

import org.openapi4j.core.exception.ResolutionException;
import org.openapi4j.core.model.AuthOption;
import org.openapi4j.core.model.OAIContext;
import org.openapi4j.core.model.reference.ReferenceRegistry;
import org.openapi4j.core.model.reference.ReferenceResolver;

import java.net.URI;
import java.util.List;

import static org.openapi4j.core.model.v3.OAI3SchemaKeywords.$REF;

/**
 * Open API v3 context.
 * <p/>
 * This class handles references and base URI.
 */
public class OAI3Context implements OAIContext<OAI3> {
  private static final String OPERATION_REF = "operationRef";

  private final ReferenceRegistry referenceRegistry = new ReferenceRegistry();
  private final URI baseUri;
  private final List<AuthOption> authOptions;

  public OAI3Context(URI baseUri) throws ResolutionException {
    this(baseUri, null, null);
  }

  public OAI3Context(URI baseUri, List<AuthOption> authOptions) throws ResolutionException {
    this(baseUri, authOptions, null);
  }

  public OAI3Context(URI baseUri, JsonNode apiNode) throws ResolutionException {
    this(baseUri, null, apiNode);
  }

  public OAI3Context(URI baseUri, List<AuthOption> authOptions, JsonNode apiNode) throws ResolutionException {
    this.baseUri = baseUri;
    this.authOptions = authOptions;
    resolveReferences(apiNode);
  }

  @Override
  public ReferenceRegistry getReferenceRegistry() {
    return referenceRegistry;
  }

  @Override
  public URI getBaseUri() {
    return baseUri;
  }

  private void resolveReferences(JsonNode apiNode) throws ResolutionException {
    ReferenceResolver resolver = new ReferenceResolver(baseUri, authOptions, apiNode, $REF, referenceRegistry);
    resolver.resolve();

    ReferenceRegistry mappingRefsRegistry = new ReferenceRegistry();
    MappingReferenceResolver mappingResolver = new MappingReferenceResolver(baseUri, authOptions, apiNode, $REF, mappingRefsRegistry);
    mappingResolver.resolve();
    referenceRegistry.mergeRefs(mappingRefsRegistry);

    ReferenceRegistry operationRefsRegistry = new ReferenceRegistry();
    ReferenceResolver operationResolver = new ReferenceResolver(baseUri, authOptions, apiNode, OPERATION_REF, operationRefsRegistry);
    operationResolver.resolve();
    referenceRegistry.mergeRefs(operationRefsRegistry);
  }
}
