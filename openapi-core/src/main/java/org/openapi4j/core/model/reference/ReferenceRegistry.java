package org.openapi4j.core.model.reference;

import java.net.URI;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * The reference registry cache.
 */
public class ReferenceRegistry {
  private final URI baseUri;
  private final Map<String, Reference> references = new HashMap<>();
  private static final String HASH = "#";

  public ReferenceRegistry(URI baseUri) {
    this.baseUri = baseUri;
  }

  /**
   * Add/replace a reference to the registry.
   *
   * @param uri      The base uri.
   * @param refValue The reference expression.
   * @return The reference created or replaced.
   */
  public Reference addRef(URI uri, String refValue) {
    String canonicalRefValue = buildCanonicalRef(uri, refValue);

    Reference reference = new Reference(uri, canonicalRefValue, refValue, null);
    references.put(canonicalRefValue, reference);

    return reference;
  }

  /**
   * Get the reference from the given reference expression.
   * The expression can be absolute or relative to the base context URI.
   *
   * @param refValue The given reference expression.
   * @return The reference found, {@code null} otherwise.
   */
  public Reference getRef(String refValue) {
    if (URI.create(ReferenceUri.encodeBraces(refValue)).isAbsolute()) {
      return references.get(refValue);
    }

    // Try to resolve the relative path from base URI
    return getRef(baseUri, refValue);
  }

  /**
   * Get the reference from the given reference expression with uri as base.
   *
   * @param uri      The given base URI, can be null to fallback to {@link #getRef(String)}.
   * @param refValue The given reference expression.
   * @return The reference found, {@code null} otherwise.
   */
  public Reference getRef(URI uri, String refValue) {
    if (uri == null) {
      return getRef(refValue);
    }

    String canonicalRefValue = ReferenceUri.resolveAsString(uri, refValue);
    return references.get(canonicalRefValue);
  }

  public void mergeRefs(ReferenceRegistry registry) {
    this.references.putAll(registry.references);
  }

  Collection<Reference> getReferences() {
    return references.values();
  }

  private String buildCanonicalRef(URI uri, String refValue) {
    final int indexHash = refValue.indexOf(HASH);

    return indexHash != -1
      ? ReferenceUri.resolveAsString(uri, refValue.substring(indexHash))
      : ReferenceUri.resolveAsString(uri, refValue);
  }
}
