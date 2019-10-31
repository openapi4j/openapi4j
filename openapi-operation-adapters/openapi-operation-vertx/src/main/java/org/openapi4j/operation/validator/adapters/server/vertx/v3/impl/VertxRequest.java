package org.openapi4j.operation.validator.adapters.server.vertx.v3.impl;

import org.openapi4j.operation.validator.model.Request;
import org.openapi4j.operation.validator.model.impl.Body;
import org.openapi4j.operation.validator.model.impl.DefaultRequest;

import java.util.Map;

import io.vertx.core.http.Cookie;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.ext.web.RoutingContext;

import static java.util.Objects.requireNonNull;

public abstract class VertxRequest implements Request {
  private static final String ERR_MSG = "A RoutingContext is required";

  private VertxRequest() {
  }

  /**
   * Creates a wrapped request from Vert.x routing context.
   *
   * @param rc The given routing context.
   * @return The wrapped request to work this.
   */
  public static Request of(final RoutingContext rc) {
    requireNonNull(rc, ERR_MSG);

    HttpServerRequest serverRq = rc.request();

    // Method & path
    final DefaultRequest.Builder builder = new DefaultRequest.Builder(
      Request.Method.getMethod(serverRq.rawMethod()),
      serverRq.path());

    // Query string or body
    if (HttpMethod.GET.equals(serverRq.method())) {
      builder.query(serverRq.query());
    } else {
      builder.body(Body.from(rc.getBodyAsString()));
    }

    // Cookies
    if (serverRq.cookieCount() != 0) {
      for (Map.Entry<String, Cookie> entry : serverRq.cookieMap().entrySet()) {
        builder.cookie(entry.getKey(), entry.getValue().getValue());
      }
    }

    // Headers
    if (serverRq.headers() != null) {
      for (String headerName : serverRq.headers().names()) {
        builder.header(headerName, serverRq.headers().getAll(headerName));
      }
    }

    return builder.build();
  }
}
