package org.openapi4j.operation.validator.model.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class RequestParametersTest {
  @Test
  public void nullCheck() {
    RequestParameters params = new RequestParameters(null, null, null, null);

    assertNull(params.getCookieParameters());
    assertNull(params.getHeaderParameters());
    assertNull(params.getPathParameters());
    assertNull(params.getQueryParameters());

    assertNull(params.getCookieParameter("foo"));
    assertNull(params.getHeaderParameter("foo"));
    assertNull(params.getPathParameter("foo"));
    assertNull(params.getQueryParameter("foo"));
  }

  @Test
  public void pathCheck() {
    Map<String, JsonNode> values = new HashMap<>();
    values.put("key", JsonNodeFactory.instance.textNode("value"));
    RequestParameters params = new RequestParameters(values, null, null, null);

    assertNotNull(params.getPathParameters());

    assertEquals(
      JsonNodeFactory.instance.textNode("value"),
      params.getPathParameter("key"));
  }

  @Test
  public void queryCheck() {
    Map<String, JsonNode> values = new HashMap<>();
    values.put("key", JsonNodeFactory.instance.textNode("value"));
    RequestParameters params = new RequestParameters(null, values, null, null);

    assertNotNull(params.getQueryParameters());

    assertEquals(
      JsonNodeFactory.instance.textNode("value"),
      params.getQueryParameter("key"));
  }

  @Test
  public void headerCheck() {
    Map<String, JsonNode> values = new HashMap<>();
    values.put("key", JsonNodeFactory.instance.textNode("value"));
    RequestParameters params = new RequestParameters(null, null, values, null);

    assertNotNull(params.getHeaderParameters());

    assertEquals(
      JsonNodeFactory.instance.textNode("value"),
      params.getHeaderParameter("key"));
  }

  @Test
  public void cookieCheck() {
    Map<String, JsonNode> values = new HashMap<>();
    values.put("key", JsonNodeFactory.instance.textNode("value"));
    RequestParameters params = new RequestParameters(null, null, null, values);

    assertNotNull(params.getCookieParameters());

    assertEquals(
      JsonNodeFactory.instance.textNode("value"),
      params.getCookieParameter("key"));
  }
}
