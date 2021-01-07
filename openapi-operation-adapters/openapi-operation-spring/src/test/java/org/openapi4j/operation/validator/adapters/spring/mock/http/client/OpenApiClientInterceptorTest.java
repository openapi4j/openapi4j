package org.openapi4j.operation.validator.adapters.spring.mock.http.client;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withCreatedEntity;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withNoContent;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import java.util.Collections;
import java.util.Map;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

public class OpenApiClientInterceptorTest {

  private static final ClassPathResource SPEC = new ClassPathResource("openapi3.yaml");

  private RestTemplate client;
  private MockRestServiceServer server;

  @Rule
  public ExpectedException exception = ExpectedException.none();

  @Before
  public void setupClientAndServer() {
    client = new RestTemplate();
    client.setInterceptors(Collections.singletonList(OpenApiClientInterceptor.openApi(SPEC)));
    server = MockRestServiceServer.bindTo(client).build();
  }

  @Test
  public void validGetPasses() {
    server.expect(method(HttpMethod.GET))
      .andExpect(requestTo("http://localhost/examples"))
      .andRespond(withSuccess("[]", MediaType.APPLICATION_JSON));

    client.getForObject("http://localhost/examples", Map.class);
  }

  @Test
  public void invalidRequestFails() {
    server.expect(method(HttpMethod.GET))
      .andExpect(requestTo("http://localhost/unknown"))
      .andRespond(withNoContent());
    exception.expect(ResourceAccessException.class);
    exception.expectMessage("invalid request");

    client.execute("http://localhost/unknown", HttpMethod.GET, null, null);
  }

  @Test
  public void invalidResponseFails() {
    server.expect(method(HttpMethod.GET))
      .andExpect(requestTo("http://localhost/examples"))
      .andRespond(withSuccess("[{}]", MediaType.APPLICATION_JSON));
    exception.expect(ResourceAccessException.class);
    exception.expectMessage("invalid response");

    client.getForObject("http://localhost/examples", Map.class);
  }

  @Test
  public void validPostPasses() {
    server.expect(method(HttpMethod.POST))
      .andExpect(requestTo("http://localhost/examples"))
      .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
      .andRespond(withCreatedEntity(null));

    client.postForEntity("http://localhost/examples", Collections.singletonMap("name", "test"), Void.class);
  }

  @Test
  public void undocumentedStatusIsIgnored() {
    server.expect(method(HttpMethod.POST))
      .andExpect(requestTo("http://localhost/examples"))
      .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
      .andRespond(withSuccess());

    client.postForEntity("http://localhost/examples", Collections.singletonMap("name", "test"), Void.class);
  }
}
