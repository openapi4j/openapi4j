package org.openapi4j.core.model.reference;

import java.net.URI;

class ReferenceUri {
  private ReferenceUri() {}

  static URI resolve(URI uri, String refValue) {
    if (refValue == null) {
      return uri;
    }

    return uri.resolve(encodeBraces(refValue));
  }

  static String resolveAsString(URI uri, String refValue) {
    URI resolvedUri = resolve(uri, refValue);
    return decodeBraces(resolvedUri.toString());
  }

  static String encodeBraces(String value) {
    return value
      .replace("{", "%7B")
      .replace("}", "%7D");
  }

  static String decodeBraces(String value) {
    return value
      .replace("%7B", "{")
      .replace("%7D", "}");
  }
}
