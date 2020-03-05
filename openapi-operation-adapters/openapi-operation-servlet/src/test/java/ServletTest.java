import com.fasterxml.jackson.databind.node.JsonNodeFactory;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.openapi4j.operation.validator.adapters.server.servlet.ServletRequest;
import org.openapi4j.operation.validator.model.Request;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

public class ServletTest {
  private static final String URL = "http://localhost:8080/path";
  private static final String PATH = "/path";
  private static final String H_NAME = "headerName";
  private static final String H_VALUE = "headerValue";

  private HttpServletRequest servletRequest;
  private Cookie cookie;

  @Before
  public void setUp() throws IOException {
    servletRequest = Mockito.mock(HttpServletRequest.class);
    cookie = null;

    Mockito.when(servletRequest.getRequestURL()).thenReturn(new StringBuffer(URL));
    Mockito.when(servletRequest.getQueryString()).thenReturn("id=2&name=foo");

    MockServletInputStream msis = new MockServletInputStream(new ByteArrayInputStream("a body".getBytes()));
    Mockito.when(servletRequest.getInputStream()).thenReturn(msis);
  }

  private void mockCookies(boolean enable) {
    if (enable) {
      cookie = new Cookie("bis", "cuit");
      Cookie[] cookies = new Cookie[] {cookie};
      Mockito.when(servletRequest.getCookies()).thenReturn(cookies);
    } else {
      Mockito.when(servletRequest.getCookies()).thenReturn(null);
    }
  }

  private void mockHeaders(boolean enable) {
    if (enable) {
      Vector<String> headerNames = new Vector<>();
      headerNames.add(H_NAME);
      headerNames.add("Content-Type");

      Mockito.when(servletRequest.getHeaderNames()).thenReturn(headerNames.elements());
      Vector<String> headerValues = new Vector<>();
      headerValues.add(H_VALUE);
      Mockito.when(servletRequest.getHeaders(H_NAME)).thenReturn(headerValues.elements());
      headerValues = new Vector<>();
      headerValues.add("atype");
      Mockito.when(servletRequest.getHeaders("Content-Type")).thenReturn(headerValues.elements());
    } else {
      Mockito.when(servletRequest.getHeaders(H_NAME)).thenReturn(null);
      Mockito.when(servletRequest.getHeaders("Content-Type")).thenReturn(null);
    }
  }

  @Test
  public void basicTest() throws IOException {
    mockCookies(false);
    mockHeaders(false);
    Mockito.when(servletRequest.getMethod()).thenReturn("GET");

    Request rq = ServletRequest.of(servletRequest);
    checkCommons(rq, false, false);

    Assert.assertEquals(servletRequest.getQueryString(), rq.getQuery());
  }

  @Test
  public void getTest() throws IOException {
    mockCookies(true);
    mockHeaders(true);
    Mockito.when(servletRequest.getMethod()).thenReturn("GET");

    Request rq = ServletRequest.of(servletRequest);
    checkCommons(rq, true, true);

    Assert.assertEquals(servletRequest.getQueryString(), rq.getQuery());
  }

  @Test
  public void postTest() throws IOException {
    mockCookies(true);
    mockHeaders(true);
    Mockito.when(servletRequest.getMethod()).thenReturn("POST");

    Request rq = ServletRequest.of(servletRequest);
    checkCommons(rq, true, true);

    Assert.assertNull(rq.getQuery());

    Assert.assertEquals(
      JsonNodeFactory.instance.textNode("a body"),
      rq.getBody().getContentAsNode(null, null));
  }

  private void checkCommons(Request rq, boolean checkCookies, boolean checkHeaders) {
    Assert.assertEquals(PATH, rq.getPath());

    if (checkCookies) {
      Assert.assertNotNull(rq.getCookies());
      Assert.assertTrue(rq.getCookies().containsKey(cookie.getName()));
      Assert.assertEquals(cookie.getValue(), rq.getCookies().get(cookie.getName()));
    }

    if (checkHeaders) {
      Assert.assertEquals("atype", rq.getContentType());
      Assert.assertNotNull(rq.getHeaders());
      Assert.assertTrue(rq.getHeaders().containsKey(H_NAME));
      Assert.assertEquals(H_VALUE, rq.getHeaders().get(H_NAME).iterator().next());
    }
  }

  private static class MockServletInputStream extends ServletInputStream {
    private final InputStream sourceStream;

    MockServletInputStream(InputStream sourceStream) {
      this.sourceStream = sourceStream;
    }

    @Override
    public int read() throws IOException {
      return sourceStream.read();
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
      return sourceStream.read(b, off, len);
    }

    @Override
    public int read(byte[] b) throws IOException {
      return sourceStream.read(b);
    }

    public void close() throws IOException {
      sourceStream.close();
    }

    @Override
    public boolean isFinished() {
      return false;
    }

    @Override
    public boolean isReady() {
      return false;
    }

    @Override
    public void setReadListener(ReadListener readListener) {

    }
  }
}
