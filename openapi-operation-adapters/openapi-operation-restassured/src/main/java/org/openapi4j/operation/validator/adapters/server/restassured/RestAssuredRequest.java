package org.openapi4j.operation.validator.adapters.server.restassured;

import io.restassured.specification.FilterableRequestSpecification;
import org.openapi4j.operation.validator.model.Request;
import org.openapi4j.operation.validator.model.impl.Body;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static java.util.stream.Collectors.joining;

public class RestAssuredRequest
  implements Request {
  private final FilterableRequestSpecification requestSpec;

  public RestAssuredRequest(FilterableRequestSpecification requestSpec) {
    this.requestSpec = requestSpec;
  }

  @Override
  public String getURL() {
    return requestSpec.getURI();
  }

  @Override
  public String getPath() {
    return requestSpec.getUserDefinedPath();
  }

  @Override
  public Method getMethod() {
    return Method.getMethod(requestSpec.getMethod());
  }

  @Override
  public Body getBody() {
    return Body.from(requestSpec.getBody().toString());
  }

  @Override
  public String getQuery() {
    return requestSpec.getQueryParams().entrySet().stream()
      .map(entry -> URLEncoder.encode(entry.getKey()) + "=" + URLEncoder.encode(entry.getValue()))
      .collect(joining("&"));
  }

  @Override
  public Map<String, String> getCookies() {
    Map<String, String> rval = new HashMap<>();
    requestSpec.getCookies().asList().forEach(cookie -> rval.put(cookie.getName(), cookie.getValue()));
    return rval;
  }

  @Override
  public Map<String, Collection<String>> getHeaders() {
    Map<String, Collection<String>> rval = new HashMap<>(requestSpec.getHeaders().size());
    requestSpec.getHeaders().forEach(
      header -> rval.computeIfAbsent(header.getName(), __ -> new ArrayList<>(1)).add(header.getValue()));
    return rval;
  }

  @Override
  public Collection<String> getHeaderValues(String s) {
    return requestSpec.getHeaders().getValues(s);
  }
}
