package org.openapi4j.operation.validator.adapters.spring.mock.http.client;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.junit.Test;
import org.openapi4j.operation.validator.model.Response;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.http.client.MockClientHttpResponse;

public class ClientResponseTest {

  private static final String JSON = "application/json";

  @Test
  public void statusIsSet() throws IOException {
    MockClientHttpResponse source = new MockClientHttpResponse(new byte[0], HttpStatus.BAD_REQUEST);

    Response actual = ClientResponse.of(source);

    assertEquals(400, actual.getStatus());
  }

  @Test
  public void headersAreCopied() throws IOException {
    MockClientHttpResponse source = new MockClientHttpResponse(new byte[0], HttpStatus.OK);
    source.getHeaders().set(HttpHeaders.SET_COOKIE, "foo=bar");

    Response actual = ClientResponse.of(source);

    assertEquals("foo=bar", actual.getHeaderValue(HttpHeaders.SET_COOKIE));
  }

  @Test
  public void bodyIsSet() throws IOException {
    String json = "{\"foo\":true}";
    MockClientHttpResponse source = new MockClientHttpResponse(json.getBytes(UTF_8), HttpStatus.OK);
    source.getHeaders().setContentType(MediaType.APPLICATION_JSON);

    Response actual = ClientResponse.of(source);

    assertEquals(JSON, actual.getContentType());
    assertNotNull(actual.getBody());
    assertEquals(new ObjectMapper().readTree(json), actual.getBody().getContentAsNode(null, null, JSON));
  }
}
