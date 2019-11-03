package org.openapi4j.operation.validator.adapters.server.undertow.v2;

import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.Cookie;
import io.undertow.util.HeaderValues;
import io.undertow.util.Methods;
import org.openapi4j.operation.validator.model.Request;
import org.openapi4j.operation.validator.model.impl.Body;
import org.openapi4j.operation.validator.model.impl.DefaultRequest;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Map;

import static java.util.Objects.requireNonNull;

public abstract class UndertowRequest implements Request {
  private static final String ERR_MSG = "A HttpServerExchange is required";

  private UndertowRequest() {
  }

  /**
   * Creates a wrapped request from Undertow.
   *
   * @param hse  The given server exchange.
   * @param body The body to consume.
   * @return The wrapped request to work this.
   */
  public static Request of(final HttpServerExchange hse, InputStream body) {
    requireNonNull(hse, ERR_MSG);

    // Method & path
    final DefaultRequest.Builder builder = new DefaultRequest.Builder(
      Request.Method.getMethod(hse.getRequestMethod().toString()),
      hse.getRequestPath());

    // Query string or body
    if (Methods.GET.equals(hse.getRequestMethod())) {
      builder.query(hse.getQueryString());
    } else {
      builder.body(Body.from(body));
    }

    // Cookies
    if (hse.getRequestCookies() != null) {
      for (Map.Entry<String, Cookie> entry : hse.getRequestCookies().entrySet()) {
        builder.cookie(entry.getKey(), entry.getValue().getValue());
      }
    }

    // Headers
    if (hse.getRequestHeaders() != null) {
      for (HeaderValues header : hse.getRequestHeaders()) {
        builder.header(header.getHeaderName().toString(), new ArrayList<>(header));
      }
    }

    return builder.build();
  }

  /**
   * Creates a wrapped request from Undertow.
   *
   * @param hse The given server exchange.
   * @return The wrapped request to work this.
   */
  public static Request of(final HttpServerExchange hse) {
    return of(hse, hse.getInputStream());
  }
}
