package org.openapi4j.core.model.reference;

import java.net.MalformedURLException;
import java.net.URL;

class ReferenceUrl {
  private ReferenceUrl() {}

  static URL resolve(URL url, String refValue) {
    if (refValue == null) {
      return url;
    }

    try {
      return new URL(url, refValue);
    } catch (MalformedURLException e) {
      return url;
    }
  }

  static String resolveAsString(URL url, String refValue) {
    return resolve(url, refValue).toString();
  }

}
