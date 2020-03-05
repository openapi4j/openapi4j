import com.fasterxml.jackson.databind.node.JsonNodeFactory;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.openapi4j.operation.validator.adapters.server.vertx.v3.impl.VertxRequest;
import org.openapi4j.operation.validator.model.Request;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import io.vertx.core.MultiMap;
import io.vertx.core.http.Cookie;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.impl.CookieImpl;
import io.vertx.ext.web.RoutingContext;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class VertxTest {
  private static final String URL = "http://localhost:8080/path";
  private static final String PATH = "/path";
  private static final String H_NAME = "headerName";
  private static final String H_VALUE = "headerValue";

  private RoutingContext routingContext;
  private HttpServerRequest serverRq;
  private Cookie cookie;

  @Before
  public void setUp() {
    routingContext = Mockito.mock(RoutingContext.class);
    serverRq = Mockito.mock(HttpServerRequest.class);

    Mockito.when(routingContext.request()).thenReturn(serverRq);
    Mockito.when(serverRq.absoluteURI()).thenReturn(URL);
    Mockito.when(serverRq.query()).thenReturn("id=2&name=foo");

    cookie = new CookieImpl("bis", "cuit");
    Map<String, Cookie> cookies = new HashMap<>();
    cookies.put("bis", cookie);
    Mockito.when(serverRq.cookieCount()).thenReturn(1);
    Mockito.when(serverRq.cookieMap()).thenReturn(cookies);

    MultiMap headers = MultiMap.caseInsensitiveMultiMap();
    headers.add(H_NAME, H_VALUE);
    headers.add("Content-Type", "atype");
    Mockito.when(serverRq.headers()).thenReturn(headers);
  }

  @Test
  public void getTest() {
    Mockito.when(serverRq.rawMethod()).thenReturn("GET");
    Mockito.when(serverRq.method()).thenReturn(HttpMethod.GET);

    Request rq = VertxRequest.of(routingContext);
    checkCommons(rq);

    assertEquals(serverRq.query(), rq.getQuery());
  }

  @Test
  public void postTest() throws IOException {
    Mockito.when(serverRq.rawMethod()).thenReturn("POST");
    Mockito.when(serverRq.method()).thenReturn(HttpMethod.POST);
    Mockito.when(routingContext.getBodyAsString()).thenReturn("a body");

    Request rq = VertxRequest.of(routingContext);
    checkCommons(rq);

    assertNull(rq.getQuery());

    assertEquals(
      JsonNodeFactory.instance.textNode("a body"),
      rq.getBody().getContentAsNode(null, null));
  }

  private void checkCommons(Request rq) {
    assertEquals(PATH, rq.getPath());
    assertEquals("atype", rq.getContentType());

    assertNotNull(rq.getCookies());
    assertTrue(rq.getCookies().containsKey(cookie.getName()));
    assertEquals(cookie.getValue(), rq.getCookies().get(cookie.getName()));

    assertNotNull(rq.getHeaders());
    assertTrue(rq.getHeaders().containsKey(H_NAME));
    assertEquals(H_VALUE, rq.getHeaders().get(H_NAME).iterator().next());
  }
}
