package org.openapi4j.core.model.reference;

import java.net.URI;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * The reference registry cache
 */
public class ReferenceRegistry {
  private final Map<String, Reference> references = new HashMap<>();

  void addRef(URI baseUri, String ref) {
    references.put(ref, new Reference(baseUri, ref, null));
  }

  public Reference getRef(String ref) {
    if (ref == null) return null;

    return references.get(ref);
  }

  public void mergeRefs(ReferenceRegistry registry) {
    this.references.putAll(registry.references);
  }

  Collection<Reference> getReferences() {
    return references.values();
  }
}
