package org.openapi4j.operation.validator.model.impl;

import org.openapi4j.core.util.MultiStringMap;
import org.openapi4j.operation.validator.model.Request;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static java.util.Objects.requireNonNull;

public class DefaultRequest implements Request {
  private final String url;
  private final Method method;
  private final String path;
  private final Map<String, String> cookies;
  private final Map<String, Collection<String>> headers;
  private final String query;
  private final Body body;

  private DefaultRequest(final String url,
                         final Method method,
                         final String path,
                         final Map<String, String> cookies,
                         final Map<String, Collection<String>> headers,
                         final String query,
                         final Body body) {

    this.url = requireNonNull(url, "A URL is required");
    this.method = requireNonNull(method, "A method is required");
    this.path = requireNonNull(path, "A request path is required");
    this.cookies = requireNonNull(cookies);
    this.headers = requireNonNull(headers);
    this.query = query;
    this.body = body;
  }

  @Override
  public String getURL() {
    return url;
  }

  @Override
  public String getPath() {
    return path;
  }

  @Override
  public Method getMethod() {
    return method;
  }

  @Override
  public Body getBody() {
    return body;
  }

  @Override
  public String getQuery() {
    return query;
  }

  @Override
  public Map<String, String> getCookies() {
    return cookies;
  }

  @Override
  public Map<String, Collection<String>> getHeaders() {
    return headers;
  }

  @Override
  public Collection<String> getHeaderValues(final String name) {
    return headers.get(name);
  }

  /**
   * A builder for {@link DefaultRequest} construction.
   */
  public static final class Builder {
    private final String url;
    private final Method method;
    private final String path;
    private final Map<String, String> cookies;
    private final MultiStringMap<String> headers;
    private String query;
    private Body body;

    /**
     * Creates a {@link DefaultRequest.Builder} with the given HTTP {@link Request.Method} and path.
     * Headers are always treated as case insensitive.
     *
     * @param url The HTTP URL request
     * @param method The HTTP method
     * @param path   The requests path
     */
    public Builder(final String url, final Method method, final String path) {
      this.url = requireNonNull(url, "A URL is required");
      this.method = requireNonNull(method, "A method is required");
      this.path = requireNonNull(path, "A path is required");

      this.cookies = new HashMap<>();
      this.headers = new MultiStringMap<>(false);
    }

    /**
     * Adds cookies to this builder.
     *
     * @param cookies The cookies where the key is the cookie name,
     * @return This builder
     */
    public Builder cookies(final Map<String, String> cookies) {
      this.cookies.putAll(cookies);
      return this;
    }

    /**
     * Adds a cookie to this builder.
     *
     * @param name  The cookie name
     * @param value A single value for this cookie
     * @return This builder
     */
    public Builder cookie(final String name, final String value) {
      cookies.put(name, value);
      return this;
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
     * Adds a header to this builder or value if already exists.
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
     * Adds a query parameter to this builder.
     *
     * @param query The query string
     * @return This builder
     */
    public Builder query(final String query) {
      this.query = query;
      return this;
    }

    /**
     * Adds the request body to this builder.
     *
     * @param body The request body
     * @return This builder
     */
    public Builder body(final Body body) {
      this.body = body;
      return this;
    }

    /**
     * Builds a {@link DefaultRequest}.
     *
     * @return The built {@link DefaultRequest}
     */
    public DefaultRequest build() {
      return new DefaultRequest(
        url,
        method,
        path,
        Collections.unmodifiableMap(cookies),
        headers.asUnmodifiableMap(),
        query,
        body);
    }
  }
}
