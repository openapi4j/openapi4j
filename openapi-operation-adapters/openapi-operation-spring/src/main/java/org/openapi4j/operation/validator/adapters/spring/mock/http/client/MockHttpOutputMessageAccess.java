package org.openapi4j.operation.validator.adapters.spring.mock.http.client;

import java.util.function.Function;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.mock.http.MockHttpOutputMessage;

/**
 * This class exists to avoid a {@link NoClassDefFoundError} without {@code spring-test} dependency.
 */
class MockHttpOutputMessageAccess implements Function<ClientHttpRequest, byte[]> {

  @Override
  public byte[] apply(ClientHttpRequest source) {
    if (source instanceof MockHttpOutputMessage) {
      return ((MockHttpOutputMessage) source).getBodyAsBytes();
    }
    return null;
  }
}
