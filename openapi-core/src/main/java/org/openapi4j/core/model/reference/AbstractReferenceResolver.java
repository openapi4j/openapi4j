package org.openapi4j.core.model.reference;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

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
    List<JsonNode> refParents = document.findParents(refKeyword);

    for (JsonNode refNode : referencePaths) {
      String refValue = refNode.textValue();
      if (refValue == null) {
        continue;
      }

      if (refValue.startsWith(HASH)) {
        // internal content of current resource (i.e. #/pointer)
        String canonicalRefValue = uri.resolve(refValue).toString();
        addRef(uri, refParents, canonicalRefValue, refValue);
      } else {
        final URI subUri;
        final String canonicalRefValue;

        if (!refValue.contains(HASH)) {
          // direct content from external resource (i.e. external.yaml)
          subUri = uri.resolve(refValue);
          canonicalRefValue = subUri.toString();
        } else {
          // or relative content from external resource (i.e. external.yaml#/pointer or /base/external.yaml#/pointer)
          subUri = uri.resolve(refValue.substring(0, refValue.indexOf(HASH)));
          canonicalRefValue = uri.resolve(refValue).toString();
        }

        addRef(subUri, refParents, canonicalRefValue, refValue);

        if (!documentRegistry.containsKey(subUri)) {
          JsonNode subDocument = registerDocument(subUri);
          findReferences(subUri, subDocument);
        }
      }
    }
  }

  private void addRef(URI uri, List<JsonNode> refParents, String canonicalRefValue, String refValue) {
    //canonicalRefValue = quietlyDecode(canonicalRefValue);

    for (JsonNode refParent : refParents) {
      if (refValue.equals(refParent.get(refKeyword).textValue())) {
        ((ObjectNode) refParent).set("canonical$ref", TreeUtil.json.getNodeFactory().textNode(canonicalRefValue));
        break;
      }
    }
    referenceRegistry.addRef(uri, canonicalRefValue, refValue);
  }

  private JsonNode registerDocument(URI uri) throws ResolutionException {
    try {
      JsonNode document = TreeUtil.load(uri.toURL(), authOptions);
      documentRegistry.put(uri, document);
      return document;
    } catch (Exception e) {
      throw new ResolutionException(String.format(LOAD_DOC_ERR_MSG, uri), e);
    }
  }

  private JsonNode registerDocument(URI uri, JsonNode node) {
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
        stringBuilder.append(visitedRef.getCanonicalRef()).append(String.format("%n"));
      }
      throw new ResolutionException(String.format(CYCLING_REF_ERR_MSG, stringBuilder.toString()));
    }

    JsonNode document = documentRegistry.get(ref.getBaseUri());
    String jsonPointer = getJsonPointer(ref.getRef());

    final JsonNode valueNode;
    if (jsonPointer.equals("/")) {
      valueNode = document;
    } else {
      valueNode = document.at(jsonPointer);
      if (valueNode.isMissingNode()) {
        throw new ResolutionException(String.format(MISSING_REF_ERR_MSG, ref.getRef(), ref.getBaseUri()));
      }
    }

    JsonNode subRefNode = valueNode.get(refKeyword);
    if (subRefNode != null) {
      resolveReference(referenceRegistry.getRef(ref.getBaseUri(), subRefNode.textValue()), visitedRefs);
    }

    ref.setContent(valueNode);
  }

  private String getJsonPointer(String ref) {
    final int index = ref.indexOf(HASH);
    return (index == -1) ? "/" : ref.substring(index + 1);
  }

  /*private String quietlyEncode(String value) {
    try {
      return URLEncoder.encode(value, "UTF-8");
    } catch (UnsupportedEncodingException ignored) {
      // will never happen
      return value;
    }
  }

  private String quietlyDecode(String refValue) {
    try {
      return URLDecoder.decode(refValue, "UTF-8");
    } catch (UnsupportedEncodingException ignored) {
      // will never happen
      return refValue;
    }
  }*/
}
