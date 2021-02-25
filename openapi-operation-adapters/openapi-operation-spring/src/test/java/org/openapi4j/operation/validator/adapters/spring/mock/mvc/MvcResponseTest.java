package org.openapi4j.operation.validator.adapters.spring.mock.mvc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Collections;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import org.junit.Test;
import org.mockito.Mockito;
import org.openapi4j.operation.validator.model.Response;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpServletResponse;

public class MvcResponseTest {

  private static final String JSON = "application/json";

  @Test
  public void statusIsSet() throws IOException {
    MockHttpServletResponse source = new MockHttpServletResponse();
    source.sendError(400);

    Response actual = MvcResponse.of(source);

    assertEquals(400, actual.getStatus());
  }

  @Test
  public void headersAreCopied() {
    MockHttpServletResponse source = new MockHttpServletResponse();
    source.addCookie(new Cookie("foo", "bar"));

    Response actual = MvcResponse.of(source);

    assertEquals("foo=bar", actual.getHeaderValue("Set-Cookie"));
  }

  @Test
  public void bodyIsSet() throws IOException {
    MockHttpServletResponse source = new MockHttpServletResponse();
    String json = "{\"foo\":true}";
    source.setContentType(JSON);
    source.getWriter().println(json);

    Response actual = MvcResponse.of(source);

    assertEquals(JSON, actual.getContentType());
    assertNotNull(actual.getBody());
    assertEquals(new ObjectMapper().readTree(json), actual.getBody().getContentAsNode(null, null, JSON));
  }

  @Test
  public void bodyIsNotSetForNonMockSource() {
    HttpServletResponse source = Mockito.mock(HttpServletResponse.class);
    Mockito.when(source.getStatus())
      .thenReturn(200);
    Mockito.when(source.getHeaderNames())
      .thenReturn(Collections.singleton(HttpHeaders.CONTENT_TYPE));
    Mockito.when(source.getHeaders(HttpHeaders.CONTENT_TYPE))
      .thenReturn(Collections.singletonList(JSON));

    Response actual = MvcResponse.of(source);

    assertEquals(200, actual.getStatus());
    assertEquals(JSON, actual.getContentType());
    assertNull(actual.getBody());
  }
}
