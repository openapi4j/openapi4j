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
    Request rq = new RestAssuredRequest(restAssuredRequest);
    Assert.assertEquals(QUERY_PARAM_NAME + "=" + QUERY_PARAM_VALUE, rq.getQuery());
    Assert.assertEquals(PATH, rq.getPath());

    Assert.assertNotNull(rq.getCookies());
    Assert.assertTrue(rq.getCookies().containsKey(COOKIE_NAME));
    Assert.assertEquals(COOKIE_VALUE, rq.getCookies().get(COOKIE_NAME));

    Assert.assertEquals("atype", rq.getContentType());
    Assert.assertNotNull(rq.getHeaders());
    Assert.assertTrue(rq.getHeaders().containsKey(H_NAME));
    Assert.assertEquals(H_VALUE, rq.getHeaders().get(H_NAME).iterator().next());
    Assert.assertEquals(JsonNodeFactory.instance.textNode("a body"), rq.getBody().getContentAsNode(null, null, null));
  }

}
