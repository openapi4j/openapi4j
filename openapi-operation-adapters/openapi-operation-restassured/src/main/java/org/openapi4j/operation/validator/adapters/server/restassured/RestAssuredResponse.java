package org.openapi4j.operation.validator.adapters.server.restassured;

import io.restassured.http.Header;
import io.restassured.response.Response;
import org.openapi4j.operation.validator.model.impl.Body;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static java.util.stream.Collectors.toList;

public class RestAssuredResponse implements org.openapi4j.operation.validator.model.Response {

  private final Response response;

  private final String rawResponseBody;

  RestAssuredResponse(Response response) {
    this.response = response;
    rawResponseBody = new String(response.body().asByteArray());
  }

  @Override
  public int getStatus() {
    return response.statusCode();
  }

  @Override
  public Body getBody() {
    return Body.from(rawResponseBody);
  }

  @Override
  public Map<String, Collection<String>> getHeaders() {
    Map<String, Collection<String>> rval = new HashMap<>();
    response.headers().asList().forEach(
      header -> rval.computeIfAbsent(header.getName(), __ -> new ArrayList<>(1)).add(header.getValue()));
    return rval;
  }

  @Override
  public Collection<String> getHeaderValues(String s) {
    return response.headers().asList().stream()
      .filter(h -> s.equals(h.getName()))
      .map(Header::getValue)
      .collect(toList());
  }

  @Override
  public String toString() {
    return rawResponseBody;
  }
}
