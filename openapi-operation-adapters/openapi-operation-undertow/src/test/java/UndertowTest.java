import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.Cookie;
import io.undertow.server.handlers.CookieImpl;
import io.undertow.util.HeaderMap;
import io.undertow.util.HttpString;
import io.undertow.util.Methods;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.openapi4j.operation.validator.adapters.server.undertow.v2.UndertowRequest;
import org.openapi4j.operation.validator.model.Request;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class UndertowTest {
  private static final String PATH = "http://localhost:8080";
  private static final String H_NAME = "headerName";
  private static final String H_VALUE = "headerValue";

  private HttpServerExchange exchange;
  private Cookie cookie;

  @Before
  public void setUp() {
    exchange = Mockito.mock(HttpServerExchange.class);

    Mockito.when(exchange.getRequestPath()).thenReturn(PATH);
    Mockito.when(exchange.getQueryString()).thenReturn("id=2&name=foo");

    cookie = new CookieImpl("bis", "cuit");
    Map<String, Cookie> cookies = new HashMap<>();
    cookies.put("bis", cookie);
    Mockito.when(exchange.getRequestCookies()).thenReturn(cookies);

    HeaderMap headers = new HeaderMap();
    headers.add(HttpString.tryFromString(H_NAME), H_VALUE);
    headers.add(HttpString.tryFromString("Content-Type"), "atype");
    Mockito.when(exchange.getRequestHeaders()).thenReturn(headers);
  }

  @Test
  public void getTest() {
    Mockito.when(exchange.getRequestMethod()).thenReturn(Methods.GET);

    Request rq = UndertowRequest.of(exchange);
    checkCommons(rq);

    Assert.assertEquals(exchange.getQueryString(), rq.getQuery());
  }

  @Test
  public void postTest() throws IOException {
    Mockito.when(exchange.getRequestMethod()).thenReturn(Methods.POST);
    Mockito.when(exchange.getInputStream()).thenReturn(new ByteArrayInputStream("a body".getBytes()));

    Request rq = UndertowRequest.of(exchange);
    checkCommons(rq);

    Assert.assertNull(rq.getQuery());

    Assert.assertEquals(
      JsonNodeFactory.instance.textNode("a body"),
      rq.getBody().getContentAsJson(null, null));
  }

  private void checkCommons(Request rq) {
    Assert.assertEquals(PATH, rq.getPath());
    Assert.assertEquals("atype", rq.getContentType().orElse(null));

    Assert.assertNotNull(rq.getCookies());
    Assert.assertTrue(rq.getCookies().containsKey(cookie.getName()));
    Assert.assertEquals(cookie.getValue(), rq.getCookies().get(cookie.getName()));

    Assert.assertNotNull(rq.getHeaders());
    Assert.assertTrue(rq.getHeaders().containsKey(H_NAME));
    Assert.assertEquals(H_VALUE, rq.getHeaders().get(H_NAME).iterator().next());
  }
}
