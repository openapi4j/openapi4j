package org.openapi4j.operation.validator.adapters.spring.mock.http.client;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.StringTokenizer;
import java.util.function.Function;
import org.openapi4j.operation.validator.model.Request;
import org.openapi4j.operation.validator.model.impl.Body;
import org.openapi4j.operation.validator.model.impl.DefaultRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

/**
 * Factory methods to create a {@link Request} from a {@link HttpRequest} (especially a {@link ClientHttpRequest}).
 */
public abstract class ClientRequest implements Request {

  private static final Function<ClientHttpRequest, byte[]> MOCK_HTTP_OUTPUT_MESSAGE_ACCESS =
    ClassUtils.isPresent("org.springframework.mock.http.MockHttpOutputMessage", null) ?
    new MockHttpOutputMessageAccess() : request -> null;

  /**
   * Create a request from source using the body if given.
   * @param source the request with method, uri and headers
   * @param body optional request body
   * @return the created request
   */
  public static Request of(HttpRequest source, @Nullable byte[] body) {
    final DefaultRequest.Builder builder = new DefaultRequest.Builder(
      source.getURI().toString(),
      Request.Method.getMethod(source.getMethodValue()));
    if (Request.Method.GET.name().equalsIgnoreCase(source.getMethodValue())) {
      builder.query(source.getURI().getQuery());
    } else if (body != null) {
      builder.body(Body.from(new ByteArrayInputStream(body)));
    }
    List<String> cookies = source.getHeaders().getValuesAsList(HttpHeaders.COOKIE);
    if (!CollectionUtils.isEmpty(cookies)) {
      String all = String.join("; ", cookies);
      for (String cookie : StringUtils.delimitedListToStringArray(all, "; ")) {
        StringTokenizer tokens = new StringTokenizer(cookie, "=");
        String name = tokens.nextToken();
        String value = tokens.hasMoreTokens() ? tokens.nextToken() : "";
        builder.cookie(name, value);
      }
    }
    source.getHeaders().forEach(builder::header);
    return builder.build();
  }

  /**
   * Create a request from source.
   * If the source is a {@link org.springframework.mock.http.MockHttpOutputMessage}
   * or the body is a {@link ByteArrayOutputStream} the body is extracted as {@code byte[]}.
   * @param source the request with method, uri and headers and possibly body
   * @return the created request
   */
  public static Request of(ClientHttpRequest source) throws IOException {
    byte[] body = MOCK_HTTP_OUTPUT_MESSAGE_ACCESS.apply(source);
    if (body == null && source.getBody() instanceof ByteArrayOutputStream) {
      body = ((ByteArrayOutputStream) source.getBody()).toByteArray();
    }
    return of(source, body);
  }

  private ClientRequest() {
  }

}
