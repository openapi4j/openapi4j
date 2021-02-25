package org.openapi4j.operation.validator.adapters.spring.mock.http.client;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import org.junit.Test;
import org.mockito.Mockito;
import org.openapi4j.operation.validator.model.Request;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.mock.http.client.MockClientHttpRequest;

public class ClientRequestTest {

  private static final String JSON = "application/json";

  @Test
  public void pathAndUrlAreSet() throws IOException {
    MockClientHttpRequest source = new MockClientHttpRequest(HttpMethod.GET, URI.create("http://localhost/tests"));

    Request actual = ClientRequest.of(source);
    assertEquals("/tests", actual.getPath());
    assertEquals("http://localhost/tests", actual.getURL());
  }

  @Test
  public void headersAndCookiesAreCopied() throws IOException {
    MockClientHttpRequest source = new MockClientHttpRequest(HttpMethod.GET, URI.create("http://localhost/"));
    source.getHeaders().set(HttpHeaders.AUTHORIZATION, "Basic dGVzdDpzZWNyZXQK");
    source.getHeaders().set(HttpHeaders.COOKIE, "foo=bar");

    Request actual = ClientRequest.of(source);

    assertEquals("Basic dGVzdDpzZWNyZXQK", actual.getHeaderValue(HttpHeaders.AUTHORIZATION));
    assertEquals(Collections.singletonMap("foo", "bar"), actual.getCookies());
  }

  @Test
  public void getRequestHasQueryButNoBody() throws IOException {
    MockClientHttpRequest source = new MockClientHttpRequest(HttpMethod.GET, URI.create("http://localhost/?foo=bar"));
    source.getBody().write("ignored".getBytes(UTF_8));

    Request actual = ClientRequest.of(source);

    assertEquals(Request.Method.GET, actual.getMethod());
    assertEquals("foo=bar", actual.getQuery());
    assertNull(actual.getContentType());
    assertNull(actual.getBody());
  }

  @Test
  public void postRequestHasBodyButNoQuery() throws IOException {
    MockClientHttpRequest source = new MockClientHttpRequest(HttpMethod.POST, URI.create("http://localhost/?unknown=ignored"));
    String json = "{\"foo\":true}";
    source.getHeaders().setContentType(MediaType.APPLICATION_JSON);
    source.getBody().write(json.getBytes(UTF_8));

    Request actual = ClientRequest.of(source);

    assertEquals(Request.Method.POST, actual.getMethod());
    assertEquals(JSON, actual.getContentType());
    assertNotNull(actual.getBody());
    assertEquals(new ObjectMapper().readTree(json), actual.getBody().getContentAsNode(null, null, JSON));
    assertNull(actual.getQuery());
  }

  @Test
  public void bodyIsNotSetForNonMockSource() throws IOException {
    ClientHttpRequest source = Mockito.mock(ClientHttpRequest.class);
    Mockito.when(source.getMethodValue())
      .thenReturn(HttpMethod.POST.name());
    Mockito.when(source.getURI())
      .thenReturn(URI.create("http://localhost/"));
    Mockito.when(source.getHeaders())
      .thenReturn(new HttpHeaders());

    Request actual = ClientRequest.of(source);

    assertEquals(Request.Method.POST, actual.getMethod());
    assertNull(actual.getBody());
  }
}
