package org.openapi4j.operation.validator.model.impl;

import org.junit.Test;
import org.openapi4j.operation.validator.model.Request;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class DefaultRequestTest {
  @Test
  public void testRequestBuild() {
    DefaultRequest.Builder builder = new DefaultRequest.Builder("/", Request.Method.POST);

    Map<String, String> cookies = new HashMap<>();
    cookies.put("cookie2", "2");

    Map<String, Collection<String>> headers = new HashMap<>();
    headers.put("X-foo", Collections.singleton("bar"));

    Request rq = builder
      .cookie("cookie1", "1")
      .cookies(cookies)
      .header("Content-Type", "application/json")
      .header("X-Rate-Limit", Collections.singleton(String.valueOf(1)))
      .headers(headers)
      .build();

    assertEquals(Request.Method.POST, rq.getMethod());

    assertEquals("1", rq.getCookies().get("cookie1"));
    assertEquals("2", rq.getCookies().get("cookie2"));

    assertEquals("application/json", rq.getHeaderValue("Content-Type"));
    assertEquals("application/json", rq.getContentType());
    assertEquals("bar", rq.getHeaderValue("x-FOO"));
    assertEquals(Collections.singleton(String.valueOf(1)), rq.getHeaderValues("x-rate-limit"));
    assertNull(rq.getHeaderValues("non_filled"));

    rq = new DefaultRequest.Builder("/", Request.Method.POST).build();
    assertNull(rq.getHeaderValues("non_filled"));
    assertNull(rq.getHeaderValue("non_filled"));
  }
}
