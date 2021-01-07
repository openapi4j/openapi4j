package org.openapi4j.operation.validator.adapters.spring.mock.mvc;

import java.io.IOException;
import java.util.Collections;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import org.openapi4j.operation.validator.model.Request;
import org.openapi4j.operation.validator.model.impl.Body;
import org.openapi4j.operation.validator.model.impl.DefaultRequest;

/**
 * Factory methods to create a {@link Request} from a {@link HttpServletRequest}.
 */
public abstract class MvcRequest implements Request {

  /**
   * Create a request from source.
   * The body is not extracted for GET methods. Ensure to pass a content buffering request to allow multiple reads.
   * @param source the request with method, uri, headers and body
   * @return the created request
   * @see org.springframework.web.util.ContentCachingRequestWrapper
   */
  public static Request of(final HttpServletRequest source) {
    final DefaultRequest.Builder builder = new DefaultRequest.Builder(
      source.getRequestURL().toString(),
      Request.Method.getMethod(source.getMethod()));
    if (Request.Method.GET.name().equalsIgnoreCase(source.getMethod())) {
      builder.query(source.getQueryString());
    } else {
      try {
        builder.body(Body.from(source.getInputStream()));
      } catch (IOException e) {
        // ignore body
      }
    }
    if (source.getCookies() != null) {
      for (Cookie cookie : source.getCookies()) {
        builder.cookie(cookie.getName(), cookie.getValue());
      }
    }
    for (String name : Collections.list(source.getHeaderNames())) {
      builder.header(name, Collections.list(source.getHeaders(name)));
    }
    return builder.build();
  }

  private MvcRequest() {
  }
}
