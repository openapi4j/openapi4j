package org.openapi4j.operation.validator.adapters.server.restassured;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import io.restassured.http.Header;
import io.restassured.http.Headers;
import io.restassured.response.Response;
import io.restassured.response.ResponseBody;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class RestAssuredResponseTest {

  @Test
  public void test()
    throws IOException {
    Response mockResponse = Mockito.mock(Response.class);
    ResponseBody<?> mockBody = Mockito.mock(ResponseBody.class);
    Mockito.when(mockBody.asByteArray()).thenReturn("hello".getBytes());

    Mockito.when(mockResponse.statusCode()).thenReturn(200);
    Mockito.when(mockResponse.body()).thenReturn(mockBody);
    Mockito.when(mockResponse.headers()).thenReturn(new Headers(
      new Header("Content-Type", "application/json"),
      new Header("Other-Header", "value"),
      new Header("Other-Header", "other value")
    ));

    RestAssuredResponse underTest = new RestAssuredResponse(mockResponse);

    Assert.assertEquals(200, underTest.getStatus());
    Assert.assertEquals(JsonNodeFactory.instance.textNode("hello"), underTest.getBody().getContentAsNode(null, null, null));

    Map<String, Collection<String>> expectedHeaders = new HashMap<>();
    expectedHeaders.put("Other-Header", Arrays.asList("value", "other value"));
    expectedHeaders.put("Content-Type", Collections.singletonList("application/json"));
    Assert.assertEquals(expectedHeaders, underTest.getHeaders());
    Assert.assertEquals("application/json", underTest.getContentType());
    Assert.assertEquals("hello", underTest.toString());
  }
}
