package org.openapi4j.operation.validator.adapters.server.restassured;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import io.restassured.authentication.NoAuthScheme;
import io.restassured.config.RestAssuredConfig;
import io.restassured.internal.RequestSpecificationImpl;
import io.restassured.specification.FilterableRequestSpecification;
import org.junit.Assert;
import org.junit.Test;
import org.openapi4j.operation.validator.model.Request;

import java.io.IOException;

import static java.util.Collections.emptyList;

public class RestAssuredRequestTest {

  private static final String URL = "http://localhost:8080/path";
  private static final String PATH = "/path";
  private static final String H_NAME = "headerName";
  private static final String H_VALUE = "headerValue";
  private static final String COOKIE_NAME = "cookieName";
  private static final String COOKIE_VALUE = "cookieValue";
  private static final String QUERY_PARAM_VALUE = "paramValue";
  private static final String QUERY_PARAM_NAME = "paramName";

  private FilterableRequestSpecification restAssuredRequest = new RequestSpecificationImpl("http://localhost", 8080,
    "", new NoAuthScheme(), emptyList(), null, false, RestAssuredConfig.config(), null, null).path(PATH);

  {
    restAssuredRequest.body("a body").cookie(COOKIE_NAME, COOKIE_VALUE)
      .header("Content-Type", "atype")
      .header(H_NAME, H_VALUE)
      .queryParam(QUERY_PARAM_NAME, QUERY_PARAM_VALUE);
  }

  @Test
  public void test()
    throws IOException {
    Request underTest = new RestAssuredRequest(restAssuredRequest);
    System.out.println(restAssuredRequest.getURI());
    Assert.assertEquals(QUERY_PARAM_NAME + "=" + QUERY_PARAM_VALUE, underTest.getQuery());
    Assert.assertEquals(PATH, underTest.getPath());
    Assert.assertEquals("http://localhost:8080/path?paramName=paramValue", underTest.getURL());

    Assert.assertNotNull(underTest.getCookies());
    Assert.assertTrue(underTest.getCookies().containsKey(COOKIE_NAME));
    Assert.assertEquals(COOKIE_VALUE, underTest.getCookies().get(COOKIE_NAME));

    Assert.assertEquals("atype", underTest.getContentType());
    Assert.assertNotNull(underTest.getHeaders());
    Assert.assertTrue(underTest.getHeaders().containsKey(H_NAME));
    Assert.assertEquals(H_VALUE, underTest.getHeaders().get(H_NAME).iterator().next());
    Assert.assertEquals(JsonNodeFactory.instance.textNode("a body"), underTest.getBody().getContentAsNode(null, null, null));
  }

}
