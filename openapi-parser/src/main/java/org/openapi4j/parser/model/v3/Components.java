package org.openapi4j.parser.model.v3;

import org.openapi4j.core.model.OAIContext;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unused")
public class Components extends AbsExtendedOpenApiSchema<Components> {
  private Map<String, Callback> callbacks;
  private Map<String, Example> examples;
  private Map<String, Header> headers;
  private Map<String, Link> links;
  private Map<String, Parameter> parameters;
  private Map<String, RequestBody> requestBodies;
  private Map<String, Response> responses;
  private Map<String, Schema> schemas;
  private Map<String, SecurityScheme> securitySchemes;

  // Schema
  public Map<String, Schema> getSchemas() {
    return schemas;
  }

  public Components setSchemas(Map<String, Schema> schemas) {
    this.schemas = schemas;
    return this;
  }

  public boolean hasSchema(String name) {
    return mapHas(schemas, name);
  }

  public Schema getSchema(String name) {
    return mapGet(schemas, name);
  }

  public Components setSchema(String name, Schema schema) {
    if (schemas == null) {
      schemas = new HashMap<>();
    }
    schemas.put(name, schema);
    return this;
  }

  public Components removeSchema(String name) {
    mapRemove(schemas, name);
    return this;
  }

  // Response
  public Map<String, Response> getResponses() {
    return responses;
  }

  public Components setResponses(Map<String, Response> responses) {
    this.responses = responses;
    return this;
  }

  public boolean hasResponse(String name) {
    return mapHas(responses, name);
  }

  public Response getResponse(String name) {
    return mapGet(responses, name);
  }

  public Components setResponse(String name, Response response) {
    if (responses == null) {
      responses = new HashMap<>();
    }
    responses.put(name, response);
    return this;
  }

  public Components removeResponse(String name) {
    mapRemove(responses, name);
    return this;
  }

  // Parameter
  public Map<String, Parameter> getParameters() {
    return parameters;
  }

  public Components setParameters(Map<String, Parameter> parameters) {
    this.parameters = parameters;
    return this;
  }

  public boolean hasParameter(String name) {
    return mapHas(parameters, name);
  }

  public Parameter getParameter(String name) {
    return mapGet(parameters, name);
  }

  public Components setParameter(String name, Parameter parameter) {
    if (parameters == null) {
      parameters = new HashMap<>();
    }
    parameters.put(name, parameter);
    return this;
  }

  public Components removeParameter(String name) {
    mapRemove(parameters, name);
    return this;
  }

  // Example
  public Map<String, Example> getExamples() {
    return examples;
  }

  public Components setExamples(Map<String, Example> examples) {
    this.examples = examples;
    return this;
  }

  public boolean hasExample(String name) {
    return mapHas(examples, name);
  }

  public Example getExample(String name) {
    return mapGet(examples, name);
  }

  public Components setExample(String name, Example example) {
    if (examples == null) {
      examples = new HashMap<>();
    }
    examples.put(name, example);
    return this;
  }

  public Components removeExample(String name) {
    mapRemove(examples, name);
    return this;
  }

  // RequestBody
  public Map<String, RequestBody> getRequestBodies() {
    return requestBodies;
  }

  public Components setRequestBodies(Map<String, RequestBody> requestBodies) {
    this.requestBodies = requestBodies;
    return this;
  }

  public boolean hasRequestBody(String name) {
    return mapHas(requestBodies, name);
  }

  public RequestBody getRequestBody(String name) {
    return mapGet(requestBodies, name);
  }

  public Components setRequestBody(String name, RequestBody requestBody) {
    if (requestBodies == null) {
      requestBodies = new HashMap<>();
    }
    requestBodies.put(name, requestBody);
    return this;
  }

  public Components removeRequestBody(String name) {
    mapRemove(requestBodies, name);
    return this;
  }

  // Header
  public Map<String, Header> getHeaders() {
    return headers;
  }

  public Components setHeaders(Map<String, Header> headers) {
    this.headers = headers;
    return this;
  }

  public boolean hasHeader(String name) {
    return mapHas(headers, name);
  }

  public Header getHeader(String name) {
    return mapGet(headers, name);
  }

  public Components setHeader(String name, Header header) {
    if (headers == null) {
      headers = new HashMap<>();
    }
    headers.put(name, header);
    return this;
  }

  public Components removeHeader(String name) {
    mapRemove(headers, name);
    return this;
  }

  // SecurityScheme
  public Map<String, SecurityScheme> getSecuritySchemes() {
    return securitySchemes;
  }

  public Components setSecuritySchemes(Map<String, SecurityScheme> securitySchemes) {
    this.securitySchemes = securitySchemes;
    return this;
  }

  public boolean hasSecurityScheme(String name) {
    return mapHas(securitySchemes, name);
  }

  public SecurityScheme getSecurityScheme(String name) {
    return mapGet(securitySchemes, name);
  }

  public Components setSecurityScheme(String name, SecurityScheme securityScheme) {
    if (securitySchemes == null) {
      securitySchemes = new HashMap<>();
    }
    securitySchemes.put(name, securityScheme);
    return this;
  }

  public Components removeSecurityScheme(String name) {
    mapRemove(securitySchemes, name);
    return this;
  }

  // Link
  public Map<String, Link> getLinks() {
    return links;
  }

  public Components setLinks(Map<String, Link> links) {
    this.links = links;
    return this;
  }

  public boolean hasLink(String name) {
    return mapHas(links, name);
  }

  public Link getLink(String name) {
    return mapGet(links, name);
  }

  public Components setLink(String name, Link link) {
    if (links == null) {
      links = new HashMap<>();
    }
    links.put(name, link);
    return this;
  }

  public Components removeLink(String name) {
    mapRemove(links, name);
    return this;
  }

  // Callback
  public Map<String, Callback> getCallbacks() {
    return callbacks;
  }

  public Components setCallbacks(Map<String, Callback> callbacks) {
    this.callbacks = callbacks;
    return this;
  }

  public boolean hasCallback(String name) {
    return mapHas(callbacks, name);
  }

  public Callback getCallback(String name) {
    return mapGet(callbacks, name);
  }

  public Components setCallback(String name, Callback callback) {
    if (callbacks == null) {
      callbacks = new HashMap<>();
    }
    callbacks.put(name, callback);
    return this;
  }

  public Components removeCallback(String name) {
    mapRemove(callbacks, name);
    return this;
  }

  @Override
  public Components copy(OAIContext context, boolean followRefs) {
    Components copy = new Components();

    copy.setSchemas(copyMap(getSchemas(), context, followRefs));
    copy.setResponses(copyMap(getResponses(), context, followRefs));
    copy.setParameters(copyMap(getParameters(), context, followRefs));
    copy.setExamples(copyMap(getExamples(), context, followRefs));
    copy.setRequestBodies(copyMap(getRequestBodies(), context, followRefs));
    copy.setHeaders(copyMap(getHeaders(), context, followRefs));
    copy.setSecuritySchemes(copyMap(getSecuritySchemes(), context, followRefs));
    copy.setLinks(copyMap(getLinks(), context, followRefs));
    copy.setCallbacks(copyMap(getCallbacks(), context, followRefs));
    copy.setExtensions(copyMap(getExtensions()));

    return copy;
  }
}
