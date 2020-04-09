package org.openapi4j.core.model.reference;

import java.net.URI;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * The reference registry cache.
 */
public class ReferenceRegistry {
  private final URL baseUrl;
  private final Map<String, Reference> references = new HashMap<>();
  private static final String HASH = "#";

  public ReferenceRegistry(URL baseUrl) {
    this.baseUrl = baseUrl;
  }

  /**
   * Add/replace a reference to the registry.
   *
   * @param url      The base URL.
   * @param refValue The reference expression.
   * @return The reference created or replaced.
   */
  public Reference addRef(URL url, String refValue) {
    String canonicalRefValue = buildCanonicalRef(url, refValue);

    Reference reference = new Reference(url, canonicalRefValue, refValue);
    references.put(canonicalRefValue, reference);

    return reference;
  }

  /**
   * Get the reference from the given reference expression.
   * The expression can be absolute or relative to the base context URL.
   *
   * @param refValue The given reference expression.
   * @return The reference found, {@code null} otherwise.
   */
  public Reference getRef(String refValue) {
    if (URI.create(encodeBraces(refValue)).isAbsolute()) {
      return references.get(refValue);
    }

    // Try to resolve the relative path from base URL
    return getRef(baseUrl, refValue);
  }

  /**
   * Get the reference from the given reference expression with URL as base.
   *
   * @param url      The given base URL, can be null to fallback to {@link #getRef(String)}.
   * @param refValue The given reference expression.
   * @return The reference found, {@code null} otherwise.
   */
  public Reference getRef(URL url, String refValue) {
    if (url == null) {
      return getRef(refValue);
    }

    String canonicalRefValue = ReferenceUrl.resolveAsString(url, refValue);
    return references.get(canonicalRefValue);
  }

  public void mergeRefs(ReferenceRegistry registry) {
    this.references.putAll(registry.references);
  }

  Collection<Reference> getReferences() {
    return references.values();
  }

  private String buildCanonicalRef(URL url, String refValue) {
    final int indexHash = refValue.indexOf(HASH);

    return indexHash != -1
      ? ReferenceUrl.resolveAsString(url, refValue.substring(indexHash))
      : ReferenceUrl.resolveAsString(url, refValue);
  }

  private static String encodeBraces(String value) {
    return value
      .replace("{", "%7B")
      .replace("}", "%7D");
  }
}
