package org.openapi4j.operation.validator.adapters.spring.mock.http.client;

import java.io.IOException;
import org.openapi4j.operation.validator.model.Response;
import org.openapi4j.operation.validator.model.impl.Body;
import org.openapi4j.operation.validator.model.impl.DefaultResponse;
import org.springframework.http.client.ClientHttpResponse;

/**
 * Factory method to create a {@link Response} from a {@link ClientHttpResponse}.
 */
public abstract class ClientResponse implements Response {

  /**
   * Create a response from source.
   * Ensure to pass a content buffering response to allow multiple reads of the body.
   * @param source the response with status, headers and optional body
   * @return the created response
   * @see org.springframework.http.client.BufferingClientHttpRequestFactory
   */
  public static Response of(ClientHttpResponse source) throws IOException {
    final DefaultResponse.Builder builder = new DefaultResponse.Builder(source.getRawStatusCode());
    builder.body(Body.from(source.getBody()));
    source.getHeaders().forEach(builder::header);
    return builder.build();
  }

  private ClientResponse() {
  }
}
