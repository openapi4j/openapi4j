package org.openapi4j.operation.validator.model;

import org.openapi4j.operation.validator.model.impl.Body;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

/**
 * Representation of a HTTP request
 */
public interface Request {
  /**
   * @return The request path without the query string.
   */
  String getPath();

  /**
   * @return The HTTP request method.
   */
  Method getMethod();

  /**
   * @return The request body.
   */
  Body getBody();

  /**
   * @return The query string.
   */
  String getQuery();

  /**
   * Get the cookies on this request.
   *
   * @return The map of <code>key-&gt;value</code> for the cookies associated with this request.
   */
  Map<String, String> getCookies();

  /**
   * Get the headers on this request.
   * <p>
   * Result will include key and all values (in the case of multiple headers with the same key)
   *
   * @return The map of <code>key-&gt;values</code> for the headers associated with this request.
   */
  Map<String, Collection<String>> getHeaders();

  /**
   * Get the collection of header values for the header param with the given name.
   *
   * @param name The (case insensitive) name of the parameter to retrieve
   * @return The header values for that param.
   */
  Collection<String> getHeaderValues(String name);

  /**
   * Get the first of header value for the header param with the given name (if any exist).
   *
   * @param name The (case insensitive) name of the parameter to retrieve
   * @return The first header value for that param (if it exists)
   */
  default Optional<String> getHeaderValue(final String name) {
    return getHeaderValues(name).stream().findFirst();
  }

  /**
   * Get the content-type header of this request, if it has been set.
   *
   * @return The content-type header, or empty if it has not been set.
   */
  default Optional<String> getContentType() {
    return getHeaderValue("Content-Type");
  }

  /**
   * HTTP request methods
   */
  enum Method {
    GET,
    POST,
    PUT,
    PATCH,
    DELETE,
    HEAD,
    OPTIONS,
    TRACE;

    // Cache to iterate only once for request adapters new instances.
    private static final Map<String, Method> BY_LABEL = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

    static {
      for (Method e: values()) {
        BY_LABEL.put(e.name(), e);
      }
    }

    public static Method getMethod(String value) {
      return BY_LABEL.get(value);
    }
  }
}
