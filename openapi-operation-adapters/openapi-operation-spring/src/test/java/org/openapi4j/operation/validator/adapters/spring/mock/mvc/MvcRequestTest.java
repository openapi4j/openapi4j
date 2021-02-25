package org.openapi4j.operation.validator.adapters.spring.mock.mvc;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Collections;
import javax.servlet.http.Cookie;
import org.junit.Test;
import org.openapi4j.operation.validator.model.Request;
import org.springframework.mock.web.MockHttpServletRequest;

public class MvcRequestTest {

  private static final String JSON = "application/json";

  @Test
  public void pathAndUrlAreSet() {
    MockHttpServletRequest source = new MockHttpServletRequest("GET", "/tests");

    Request actual = MvcRequest.of(source);
    assertEquals("/tests", actual.getPath());
    assertEquals("http://localhost/tests", actual.getURL());
  }

  @Test
  public void headersAndCookiesAreCopied() {
    MockHttpServletRequest source = new MockHttpServletRequest("GET", "/");
    source.addHeader("Authorization", "Basic dGVzdDpzZWNyZXQK");
    source.setCookies(new Cookie("foo", "bar"));

    Request actual = MvcRequest.of(source);

    assertEquals("Basic dGVzdDpzZWNyZXQK", actual.getHeaderValue("Authorization"));
    assertEquals(Collections.singletonMap("foo", "bar"), actual.getCookies());
  }

  @Test
  public void getRequestHasQueryButNoBody() {
    MockHttpServletRequest source = new MockHttpServletRequest("GET", "/");
    source.setContent("ignored".getBytes(UTF_8));
    source.setQueryString("foo=bar");

    Request actual = MvcRequest.of(source);

    assertEquals(Request.Method.GET, actual.getMethod());
    assertEquals("foo=bar", actual.getQuery());
    assertNull(actual.getContentType());
    assertNull(actual.getBody());
  }

  @Test
  public void postRequestHasBodyButNoQuery() throws IOException {
    MockHttpServletRequest source = new MockHttpServletRequest("POST", "/");
    String json = "{\"foo\":true}";
    source.setContent(json.getBytes(UTF_8));
    source.setContentType(JSON);
    source.setQueryString("unknown=ignored");

    Request actual = MvcRequest.of(source);

    assertEquals(Request.Method.POST, actual.getMethod());
    assertEquals(JSON, actual.getContentType());
    assertNotNull(actual.getBody());
    assertEquals(new ObjectMapper().readTree(json), actual.getBody().getContentAsNode(null, null, JSON));
    assertNull(actual.getQuery());
  }
}
