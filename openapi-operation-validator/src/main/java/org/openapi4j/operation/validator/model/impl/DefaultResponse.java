package org.openapi4j.operation.validator.model.impl;

import org.openapi4j.core.util.MultiStringMap;
import org.openapi4j.operation.validator.model.Response;

import java.util.Collection;
import java.util.Map;

import static java.util.Objects.requireNonNull;

@SuppressWarnings("unused")
public class DefaultResponse implements Response {
  private final int status;
  private final Map<String, Collection<String>> headers;
  private final Body body;

  private DefaultResponse(final int status,
                          final Map<String, Collection<String>> headers,
                          final Body body) {
    this.status = status;
    this.headers = requireNonNull(headers);
    this.body = body;
  }

  @Override
  public int getStatus() {
    return status;
  }

  @Override
  public Body getBody() {
    return body;
  }

  @Override
  public Map<String, Collection<String>> getHeaders() {
    return headers;
  }

  @Override
  public Collection<String> getHeaderValues(final String name) {
    if (headers == null) {
      return null;
    }

    return headers.get(name);
  }

  /**
   * A builder for constructing new {@link DefaultResponse} instances.
   */
  public static final class Builder {
    private final int status;
    private final MultiStringMap<String> headers;
    private Body body;

    /**
     * Creates a {@link DefaultResponse.Builder} with the given HTTP status code.
     *
     * @param status the responses HTTP status code
     */
    public Builder(final int status) {
      this.status = status;
      headers = new MultiStringMap<>(false);
    }

    /**
     * Adds headers to this builder.
     *
     * @param headers The headers where the key is the header name,
     * @return This builder
     */
    public Builder headers(final Map<String, Collection<String>> headers) {
      headers.forEach(this.headers::putAll);
      return this;
    }

    /**
     * Adds a header to this builder.
     *
     * @param name   The header name
     * @param values The values for this header
     * @return This builder
     */
    public Builder header(final String name, final Collection<String> values) {
      headers.putAll(name, values);
      return this;
    }

    /**
     * Adds a header to this builder or value if already exists.
     *
     * @param name  The header name
     * @param value A single value for this header
     * @return This builder
     */
    public Builder header(final String name, final String value) {
      headers.put(name, value);
      return this;
    }

    /**
     * Adds a body to this builder.
     *
     * @param body the response body
     * @return this builder
     */
    public Builder body(final Body body) {
      this.body = body;
      return this;
    }

    /**
     * Builds a {@link DefaultResponse} out of this builder.
     *
     * @return the build {@link DefaultResponse}
     */
    public DefaultResponse build() {
      return new DefaultResponse(status, headers.asUnmodifiableMap(), body);
    }
  }
}
