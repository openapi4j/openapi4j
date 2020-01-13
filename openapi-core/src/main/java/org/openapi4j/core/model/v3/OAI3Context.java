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
public class OAI3Context implements OAIContext {
  private static final String OPERATION_REF = "operationRef";

  private final ReferenceRegistry referenceRegistry;
  private final URI baseUri;
  private final List<AuthOption> authOptions;
  private final JsonNode baseDocument;

  /**
   * Creates a context from the given uri.
   *
   * @param baseUri The given uri.
   * @throws ResolutionException
   */
  public OAI3Context(URI baseUri) throws ResolutionException {
    this(baseUri, null, null);
  }

  /**
   * Creates a context from the given uri.
   *
   * @param baseUri     The given uri.
   * @param authOptions The authentication values.
   * @throws ResolutionException
   */
  public OAI3Context(URI baseUri, List<AuthOption> authOptions) throws ResolutionException {
    this(baseUri, authOptions, null);
  }

  /**
   * Creates a context from the given uri.
   *
   * @param baseUri The given uri.
   * @param baseDocument The tree node representing the Open API schema.
   * @throws ResolutionException
   */
  public OAI3Context(URI baseUri, JsonNode baseDocument) throws ResolutionException {
    this(baseUri, null, baseDocument);
  }

  /**
   * Creates a context from the given uri.
   *
   * @param baseUri     The given uri.
   * @param authOptions The authentication values.
   * @param baseDocument     The tree node representing the Open API schema.
   * @throws ResolutionException
   */
  public OAI3Context(URI baseUri, List<AuthOption> authOptions, JsonNode baseDocument) throws ResolutionException {
    this.baseUri = baseUri;
    referenceRegistry = new ReferenceRegistry(baseUri);
    this.authOptions = authOptions;
    this.baseDocument = resolveReferences(baseDocument);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ReferenceRegistry getReferenceRegistry() {
    return referenceRegistry;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public JsonNode getBaseDocument() {
    return baseDocument;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public URI getBaseUri() {
    return baseUri;
  }

  private JsonNode resolveReferences(JsonNode baseDocument) throws ResolutionException {
    // Standard JSON references
    ReferenceResolver resolver = new ReferenceResolver(baseUri, authOptions, baseDocument, $REF, referenceRegistry);
    resolver.resolve();

    // Mapping JSON references
    ReferenceRegistry mappingRefsRegistry = new ReferenceRegistry(baseUri);
    MappingReferenceResolver mappingResolver = new MappingReferenceResolver(baseUri, authOptions, baseDocument, $REF, mappingRefsRegistry);
    mappingResolver.resolve();
    referenceRegistry.mergeRefs(mappingRefsRegistry);

    // Links JSON references
    ReferenceRegistry operationRefsRegistry = new ReferenceRegistry(baseUri);
    ReferenceResolver operationResolver = new ReferenceResolver(baseUri, authOptions, baseDocument, OPERATION_REF, operationRefsRegistry);
    operationResolver.resolve();
    referenceRegistry.mergeRefs(operationRefsRegistry);

    return resolver.getBaseDocument();
  }
}
