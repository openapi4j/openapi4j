package org.openapi4j.core.model.reference;

import java.net.URI;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * The reference registry cache
 */
public class ReferenceRegistry {
  private URI baseUri;
  private final Map<String, Reference> references = new HashMap<>();

  public ReferenceRegistry(URI baseUri) {
    this.baseUri = baseUri;
  }

  public void addRef(URI baseUri, String canonicalRefValue, String refValue) {
    references.put(canonicalRefValue, new Reference(baseUri, canonicalRefValue, refValue, null));
  }

  /**
   * Get the reference from the given reference expression.
   * The expression
   * @param ref
   * @return
   */
  public Reference getRef(String ref) {
    URI uri = URI.create(ref);

    if (uri.isAbsolute()) {
      return references.get(ref);
    }

    // Try to resolve the relative path from base URI
    return references.get(baseUri.resolve(ref).toString());
  }

  public Reference getRef(URI baseUri, String ref) {
    if (baseUri == null) {
      return getRef(ref);
    }

    for (Reference reference : references.values()) {
      if (reference.getBaseUri().equals(baseUri) && reference.getRef().equals(ref)) {
        return reference;
      }
    }

    return null;
  }

  public void mergeRefs(ReferenceRegistry registry) {
    this.references.putAll(registry.references);
  }

  Collection<Reference> getReferences() {
    return references.values();
  }
}
