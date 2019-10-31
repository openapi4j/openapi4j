package org.openapi4j.operation.validator.model;

import org.openapi4j.operation.validator.model.impl.Body;

import java.util.Collection;
import java.util.Optional;

public interface Response {
  /**
   * @return The response status code
   */
  int getStatus();

  /**
   * @return The response body, if there is one.
   */
  Body getBody();

  /**
   * Get the collection of header values for the header param with the given name.
   *
   * @param name The (case insensitive) name of the parameter to retrieve
   * @return The header values for that param; or empty list. Must be {@code nonnull}.
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
    return getHeaderValue(Headers.CONTENT_TYPE);
  }
}
