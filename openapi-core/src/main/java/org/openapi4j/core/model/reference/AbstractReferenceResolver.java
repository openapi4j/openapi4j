package org.openapi4j.core.model.reference;

import com.fasterxml.jackson.databind.JsonNode;

import org.openapi4j.core.exception.ResolutionException;
import org.openapi4j.core.model.AuthOption;
import org.openapi4j.core.util.TreeUtil;

import java.net.URI;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Base class for JSON reference resolution implementation.
 * See <a href="https://tools.ietf.org/html/draft-pbryan-zyp-json-ref-03">JSON reference specification</a>.
 */
public abstract class AbstractReferenceResolver {
  private static final String LOAD_DOC_ERR_MSG = "Failed to load document from '%s'";
  private static final String CYCLING_REF_ERR_MSG = "Cycling reference with the following chain :\n%s";
  private static final String MISSING_REF_ERR_MSG = "Reference '%s' is unreachable in '%s.";
  private static final String HASH = "#";
  private static final String SLASH = "/";

  private final URI baseUri;
  private final List<AuthOption> authOptions;
  private final JsonNode baseDocument;
  private final Map<URI, JsonNode> documentRegistry = new HashMap<>();
  private final ReferenceRegistry referenceRegistry;
  final String refKeyword;

  protected AbstractReferenceResolver(URI baseUri, List<AuthOption> authOptions, JsonNode baseDocument, String refKeyword, ReferenceRegistry referenceRegistry) {
    this.baseUri = baseUri;
    this.authOptions = authOptions;
    this.baseDocument = baseDocument;
    this.refKeyword = refKeyword;
    this.referenceRegistry = referenceRegistry;
  }

  public void resolve() throws ResolutionException {
    // Register base resolution document
    JsonNode document
      = baseDocument != null
      ? registerDocument(baseUri, baseDocument)
      : registerDocument(baseUri);

    // Find all external documents from references
    findReferences(baseUri, document);

    // Resolves all references
    resolveReferences();
  }

  protected abstract Collection<JsonNode> getReferencePaths(JsonNode document);

  private void findReferences(URI uri, JsonNode document) throws ResolutionException {
    Collection<JsonNode> referencePaths = getReferencePaths(document);

    for (JsonNode refNode : referencePaths) {
      String refValue = refNode.textValue();
      if (refValue != null) {
        if (!refValue.startsWith(HASH)) {
          URI subUri = getReferenceUri(uri, refValue);
          referenceRegistry.addRef(subUri, refValue);

          if (!documentRegistry.containsKey(subUri)) {
            JsonNode subDocument = registerDocument(subUri);
            findReferences(subUri, subDocument);
          }
        } else {
          referenceRegistry.addRef(uri, refValue);
        }
      }
    }
  }

  private JsonNode registerDocument(URI uri) throws ResolutionException {
    if (documentRegistry.containsKey(uri)) {
      return documentRegistry.get(uri);
    }

    try {
      JsonNode document = TreeUtil.load(uri.toURL(), authOptions);
      documentRegistry.put(uri, document);
      return document;
    } catch (Exception e) {
      throw new ResolutionException(String.format(LOAD_DOC_ERR_MSG, uri), e);
    }
  }

  private JsonNode registerDocument(URI uri, JsonNode node) {
    if (documentRegistry.containsKey(uri)) {
      return documentRegistry.get(uri);
    }

    documentRegistry.put(uri, node);
    return node;
  }

  private void resolveReferences() throws ResolutionException {
    for (Reference ref : referenceRegistry.getReferences()) {
      resolveReference(ref, new HashSet<>());
    }
  }

  private void resolveReference(Reference ref, Set<Reference> visitedRefs) throws ResolutionException {
    // Check visited references
    if (!visitedRefs.add(ref)) {
      StringBuilder stringBuilder = new StringBuilder();
      for (Reference visitedRef : visitedRefs) {
        stringBuilder.append(visitedRef.getRef()).append("\n");
      }
      throw new ResolutionException(String.format(CYCLING_REF_ERR_MSG, stringBuilder.toString()));
    }

    String jsonPointer = getJsonPointer(ref.getRef());
    if (!jsonPointer.startsWith(SLASH)) {
      // Silence this since subclasses can setup other values
      // or referencing self
      return;
    }

    JsonNode document = documentRegistry.get(ref.getBaseUri());

    JsonNode valueNode = document.at(jsonPointer);
    if (valueNode.isMissingNode()) {
      throw new ResolutionException(String.format(MISSING_REF_ERR_MSG, ref.getRef(), ref.getBaseUri()));
    }

    JsonNode subRefNode = valueNode.get(refKeyword);
    if (subRefNode != null) {
      resolveReference(referenceRegistry.getRef(subRefNode.textValue()), visitedRefs);
    }

    ref.setContent(valueNode);
  }

  private URI getReferenceUri(URI uri, String ref) {
    if (ref.contains(HASH)) {
      return uri.resolve(ref.substring(0, ref.indexOf(HASH)));
    }

    return uri;
  }

  private String getJsonPointer(String ref) {
    if (ref.contains(HASH)) {
      return ref.substring(ref.indexOf(HASH) + 1);
    }

    return ref;
  }
}
