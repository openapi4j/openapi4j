package org.openapi4j.operation.validator.model.impl;

import org.junit.Test;
import org.openapi4j.operation.validator.model.Response;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class DefaultResponseTest {
  @Test
  public void testResponseBuild() {
    DefaultResponse.Builder builder = new DefaultResponse.Builder(200);

    Map<String, Collection<String>> headers = new HashMap<>();
    headers.put("X-foo", Collections.singleton("bar"));

    Response resp = builder
      .header("Content-Type", "application/json")
      .header("X-Rate-Limit", Collections.singleton(String.valueOf(1)))
      .headers(headers)
      .build();

    assertEquals(200, resp.getStatus());

    assertEquals("application/json", resp.getHeaderValue("Content-Type"));
    assertEquals("application/json", resp.getContentType());
    assertEquals("bar", resp.getHeaderValue("x-FOO"));
    assertEquals(Collections.singleton(String.valueOf(1)), resp.getHeaderValues("x-rate-limit"));
    assertNull(resp.getHeaderValues("non_filled"));

    resp = new DefaultResponse.Builder(200).build();
    assertNull(resp.getHeaderValues("non_filled"));
    assertNull(resp.getHeaderValue("non_filled"));
  }
}
