package org.openapi4j.operation.validator.adapters.spring.mock.mvc;

import java.io.ByteArrayInputStream;
import java.util.function.Function;
import javax.servlet.http.HttpServletResponse;
import org.openapi4j.operation.validator.model.Response;
import org.openapi4j.operation.validator.model.impl.Body;
import org.openapi4j.operation.validator.model.impl.DefaultResponse;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.util.ClassUtils;

/**
 * Factory method to create a {@link Response} from a {@link HttpServletResponse}.
 */
public abstract class MvcResponse implements Response {

  private static final Function<HttpServletResponse, byte[]> MOCK_HTTP_SERVLET_RESPONSE_ACCESS =
    ClassUtils.isPresent("org.springframework.mock.web.MockHttpServletResponse", null) ?
    new MockHttpServletResponseAccess() : request -> null;

  /**
   * Create a response from source.
   * The body is set for {@link MockHttpServletResponse} only.
   * @param source the response with status and headers
   * @return the created response
   */
  public static Response of(HttpServletResponse source) {
    final DefaultResponse.Builder builder = new DefaultResponse.Builder(source.getStatus());
    for (String name : source.getHeaderNames()) {
      builder.header(name, source.getHeaders(name));
    }
    byte[] body = MOCK_HTTP_SERVLET_RESPONSE_ACCESS.apply(source);
    if (body != null) {
      builder.body(Body.from(new ByteArrayInputStream(body)));
    }
    return builder.build();
  }

  private MvcResponse() {
  }

}
