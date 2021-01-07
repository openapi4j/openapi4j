package org.openapi4j.operation.validator.adapters.spring.mock.mvc;

import static org.openapi4j.operation.validator.adapters.spring.mock.mvc.OpenApiMatchers.openApi;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.openapi4j.core.validation.ValidationException;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@SuppressWarnings("unused")
public class OpenApiMatchersTest {

  private static final ClassPathResource SPEC = new ClassPathResource("openapi3.yaml");
  private static final String EXAMPLE_CONTENT = "{\"name\":\"test\"}";

  private MockMvc mvc;

  @Rule
  public ExpectedException exception = ExpectedException.none();

  @Before
  public void createMockMvc() {
    mvc = MockMvcBuilders.standaloneSetup(new TestController())
      .apply(openApi(SPEC))
      .build();
  }

  @Test
  public void validGetRequest() throws Exception {
    mvc.perform(get("/examples"))
      .andExpect(status().isOk());
  }

  @Test
  public void unknownGetRequest() throws Exception {
    exception.expect(AssertionError.class);
    exception.expectMessage("not documented");
    mvc.perform(get("/unknown"));
  }

  @Test
  public void validPostRequest() throws Exception {
    mvc.perform(post("/examples")
      .contentType(MediaType.APPLICATION_JSON)
      .content(EXAMPLE_CONTENT))
      .andExpect(status().isCreated());
  }

  @Test
  public void unknownRequestContentType() throws Exception {
    exception.expect(AssertionError.class);
    exception.expectMessage("invalid");
    mvc.perform(post("/examples")
      .contentType(MediaType.APPLICATION_XML)
      .content("<example/>"));
  }

  @Test
  public void invalidRequestBody() throws Exception {
    exception.expect(AssertionError.class);
    exception.expectMessage("invalid");
    mvc.perform(post("/examples")
      .contentType(MediaType.APPLICATION_JSON)
      .content("{}"));
  }

  @Test
  public void invalidResponseStatus() throws Exception {
    mvc.perform(post("/examples")
      .contentType(MediaType.APPLICATION_JSON)
      .content(EXAMPLE_CONTENT))
      .andExpect(status().isCreated());
    exception.expect(ValidationException.class);
    exception.expectMessage("Invalid response.");
    mvc.perform(post("/examples")
      .contentType(MediaType.APPLICATION_JSON)
      .content(EXAMPLE_CONTENT));
  }

  @RestController
  @RequestMapping(path = "examples", produces = MediaType.APPLICATION_JSON_VALUE)
  private static class TestController {

    private final Map<String, Example> examples = new ConcurrentHashMap<>();

    @GetMapping
    Collection<Example> listExamples() {
      return examples.values();
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    void createExample(@RequestBody OpenApiMatchersTest.Example example) {
      if (examples.containsKey(example.getName())) {
        throw new IllegalArgumentException("Duplicate example " + example.getName());
      }
      examples.put(example.getName(), example);
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<?> handleException(Exception error) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(Collections.singletonMap("error", error.getMessage()));
    }
  }

  private static class Example {
    private String name;

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }
  }
}
