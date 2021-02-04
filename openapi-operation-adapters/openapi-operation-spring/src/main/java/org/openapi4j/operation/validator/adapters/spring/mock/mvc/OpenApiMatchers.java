package org.openapi4j.operation.validator.adapters.spring.mock.mvc;

import org.openapi4j.core.validation.ValidationException;
import org.openapi4j.operation.validator.adapters.spring.mock.OpenApiCache;
import org.openapi4j.operation.validator.validation.RequestValidator;
import org.openapi4j.parser.model.v3.OpenApi3;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.setup.ConfigurableMockMvcBuilder;
import org.springframework.test.web.servlet.setup.MockMvcConfigurer;

/**
 * A MockMVC configurer based on an Open API specification.
 * It will add a request validation using a {@link RequestPostProcessor}
 * and a response validation using a {@link ResultMatcher} which is always expected.
 * Simply {@link org.springframework.test.web.servlet.setup.AbstractMockMvcBuilder#apply(MockMvcConfigurer)}
 * the instance created for the specification.
 */
public class OpenApiMatchers implements MockMvcConfigurer {

  /**
   * Creates a configurer for the API specification loaded from the resource.
   * @param spec the resource offering the specification
   */
  public static OpenApiMatchers openApi(Resource spec) {
    return openApi(OpenApiCache.INSTANCE.loadApi(spec));
  }

  /**
   * Creates a configurer for the API specification.
   * @param api the API specification
   */
  public static OpenApiMatchers openApi(OpenApi3 api) {
    return new OpenApiMatchers(api);
  }

  private final OpenApi3 api;

  private OpenApiMatchers(OpenApi3 api) {
    this.api = api;
  }

  /**
   * Adds a result matcher checking for a documented response always.
   * @param builder the builder to add the result matcher
   * @see ConfigurableMockMvcBuilder#alwaysExpect(ResultMatcher)
   * @see RequestValidator#validate(org.openapi4j.operation.validator.model.Response, org.openapi4j.operation.validator.model.Request)
   */
  @Override
  public void afterConfigurerAdded(ConfigurableMockMvcBuilder<?> builder) {
    builder.alwaysExpect(this::validateResponse);
  }

  /**
   * Returns a post processor validation the request to be documented in the API specification.
   * The processor either passes the unchanged request or raise an error.
   * @param builder the (unused) builder
   * @param context the (unused) context
   * @see RequestValidator#validate(org.openapi4j.operation.validator.model.Request)
   * @throws AssertionError if the request is not documented
   */
  @Override
  public RequestPostProcessor beforeMockMvcCreated(ConfigurableMockMvcBuilder<?> builder,
      org.springframework.web.context.WebApplicationContext context) {
    return request -> {
      validateRequest(request);
      return request;
    };
  }

  private void validateRequest(MockHttpServletRequest request) {
    try
    {
      new RequestValidator(api).validate(MvcRequest.of(request));
    }
    catch (ValidationException e)
    {
      String reason = e.results() == null ? "not documented" : "invalid";
      String message = String.format("Request '%s %s' is %s", request.getMethod(), request.getRequestURI(), reason);
      throw new AssertionError(message, e);
    }
  }

  private void validateResponse(MvcResult result)
  {
    MockHttpServletResponse response = result.getResponse();
    try {
      new RequestValidator(api).validate(MvcResponse.of(response), MvcRequest.of(result.getRequest()));
    } catch (ValidationException e)
      {
        String reason = e.results() == null ? "not documented" : "invalid";
        String message = String.format("Response %d '%s' is %s", response.getStatus(), response.getContentType(), reason);
        throw new AssertionError(message, e);
      }
  }
}
