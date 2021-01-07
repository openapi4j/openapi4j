package org.openapi4j.operation.validator.adapters.spring.mock.mvc;

import java.util.function.Function;
import javax.servlet.http.HttpServletResponse;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * This class exists to avoid a {@link NoClassDefFoundError} without {@code spring-test} dependency.
 */
class MockHttpServletResponseAccess implements Function<HttpServletResponse, byte[]> {

  @Override
  public byte[] apply(HttpServletResponse source) {
    if (source instanceof MockHttpServletResponse) {
      return ((MockHttpServletResponse) source).getContentAsByteArray();
    }
    return null;
  }
}
