package org.openapi4j.operation.validator.adapters.spring.mock.http.client;

import java.io.IOException;
import org.openapi4j.core.validation.ValidationException;
import org.openapi4j.operation.validator.adapters.spring.mock.OpenApiCache;
import org.openapi4j.operation.validator.model.Request;
import org.openapi4j.operation.validator.validation.RequestValidator;
import org.openapi4j.parser.model.v3.OpenApi3;
import org.springframework.core.NestedIOException;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

/**
 * A client interceptor based on an Open API specification.
 * It will validate each request before execution and each response after receiving it.
 * Simply add the interceptor to the {@link org.springframework.http.client.support.InterceptingHttpAccessor#getInterceptors()}
 * of your {@code RestTemplate}.
 */
public class OpenApiClientInterceptor implements ClientHttpRequestInterceptor {

  /**
   * Creates an interceptor for the API specification loaded from the resource.
   *
   * @param spec the resource offering the specification
   */
  public static ClientHttpRequestInterceptor openApi(Resource spec) {
    return openApi(OpenApiCache.INSTANCE.loadApi(spec));
  }

  /**
   * Creates an interceptor for the API specification.
   *
   * @param api the API specification
   */
  public static ClientHttpRequestInterceptor openApi(OpenApi3 api) {
    return new OpenApiClientInterceptor(api);
  }

  private final RequestValidator validator;

  private OpenApiClientInterceptor(OpenApi3 api) {
    validator = new RequestValidator(api);
  }

  /**
   * Executes the request if validation passes and validates the response afterwards.
   * If any of the validation fails, a {@link NestedIOException} is thrown.
   * @param clientRequest the request to process
   * @param body the body to send
   * @param execution the execution to use
   * @return the response created by the execution
   * @throws IOException if a validation or the execution fails
   */
  @Override
  public ClientHttpResponse intercept(HttpRequest clientRequest, byte[] body, ClientHttpRequestExecution execution) throws IOException {
    Request request = ClientRequest.of(clientRequest, body);
    try {
      validator.validate(request);
    } catch (ValidationException e) {
      throw new NestedIOException("Execution stopped due to invalid request", e);
    }
    ClientHttpResponse response = execution.execute(clientRequest, body);
    try {
      validator.validate(ClientResponse.of(response), request);
    } catch (ValidationException e) {
      throw new NestedIOException("Execution stopped due to invalid response", e);
    }
    return response;
  }
}
