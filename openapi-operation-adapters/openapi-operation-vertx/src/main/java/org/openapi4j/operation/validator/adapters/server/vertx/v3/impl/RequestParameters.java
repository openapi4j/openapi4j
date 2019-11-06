package org.openapi4j.operation.validator.adapters.server.vertx.v3.impl;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.Map;

public class RequestParameters {
  private final Map<String, JsonNode> pathParameters;
  private final Map<String, JsonNode> queryParameters;
  private final Map<String, JsonNode> headerParameters;
  private final Map<String, JsonNode> cookieParameters;

  public RequestParameters(Map<String, JsonNode> pathParameters,
                           Map<String, JsonNode> queryParameters,
                           Map<String, JsonNode> headerParameters,
                           Map<String, JsonNode> cookieParameters) {

    this.pathParameters = pathParameters;
    this.queryParameters = queryParameters;
    this.headerParameters = headerParameters;
    this.cookieParameters = cookieParameters;
  }

  public Map<String, JsonNode> getPathParameters() {
    return pathParameters;
  }

  public JsonNode getPathParameter(String name) {
    if (pathParameters == null) return null;

    return pathParameters.get(name);
  }

  public Map<String, JsonNode> getQueryParameters() {
    return queryParameters;
  }

  public JsonNode getQueryParameter(String name) {
    if (queryParameters == null) return null;

    return queryParameters.get(name);
  }

  public Map<String, JsonNode> getHeaderParameters() {
    return headerParameters;
  }

  public JsonNode getHeaderParameter(String name) {
    if (headerParameters == null) return null;

    return headerParameters.get(name);
  }

  public Map<String, JsonNode> getCookieParameters() {
    return cookieParameters;
  }

  public JsonNode getCookieParameter(String name) {
    if (cookieParameters == null) return null;

    return cookieParameters.get(name);
  }
}
