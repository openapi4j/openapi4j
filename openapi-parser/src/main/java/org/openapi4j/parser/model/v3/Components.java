package org.openapi4j.parser.model.v3;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import org.openapi4j.core.model.OAIContext;
import org.openapi4j.parser.model.AbsOpenApiSchema;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unused")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Components extends AbsOpenApiSchema<Components> {
  private Map<String, Callback> callbacks;
  private Map<String, Example> examples;
  @JsonUnwrapped
  private Extensions extensions;
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
    return has(schemas, name);
  }

  public Schema getSchema(String name) {
    return get(schemas, name);
  }

  public Components setSchema(String name, Schema schema) {
    if (schemas == null) {
      schemas = new HashMap<>();
    }
    schemas.put(name, schema);
    return this;
  }

  public Components removeSchema(String name) {
    remove(schemas, name);
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
    return has(responses, name);
  }

  public Response getResponse(String name) {
    return get(responses, name);
  }

  public Components setResponse(String name, Response response) {
    if (responses == null) {
      responses = new HashMap<>();
    }
    responses.put(name, response);
    return this;
  }

  public Components removeResponse(String name) {
    remove(responses, name);
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
    return has(parameters, name);
  }

  public Parameter getParameter(String name) {
    return get(parameters, name);
  }

  public Components setParameter(String name, Parameter parameter) {
    if (parameters == null) {
      parameters = new HashMap<>();
    }
    parameters.put(name, parameter);
    return this;
  }

  public Components removeParameter(String name) {
    remove(parameters, name);
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
    return has(examples, name);
  }

  public Example getExample(String name) {
    return get(examples, name);
  }

  public Components setExample(String name, Example example) {
    if (examples == null) {
      examples = new HashMap<>();
    }
    examples.put(name, example);
    return this;
  }

  public Components removeExample(String name) {
    remove(examples, name);
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
    return has(requestBodies, name);
  }

  public RequestBody getRequestBody(String name) {
    return get(requestBodies, name);
  }

  public Components setRequestBody(String name, RequestBody requestBody) {
    if (requestBodies == null) {
      requestBodies = new HashMap<>();
    }
    requestBodies.put(name, requestBody);
    return this;
  }

  public Components removeRequestBody(String name) {
    remove(requestBodies, name);
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
    return has(headers, name);
  }

  public Header getHeader(String name) {
    return get(headers, name);
  }

  public Components setHeader(String name, Header header) {
    if (headers == null) {
      headers = new HashMap<>();
    }
    headers.put(name, header);
    return this;
  }

  public Components removeHeader(String name) {
    remove(headers, name);
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
    return has(securitySchemes, name);
  }

  public SecurityScheme getSecurityScheme(String name) {
    return get(securitySchemes, name);
  }

  public Components setSecurityScheme(String name, SecurityScheme securityScheme) {
    if (securitySchemes == null) {
      securitySchemes = new HashMap<>();
    }
    securitySchemes.put(name, securityScheme);
    return this;
  }

  public Components removeSecurityScheme(String name) {
    remove(securitySchemes, name);
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
    return has(links, name);
  }

  public Link getLink(String name) {
    return get(links, name);
  }

  public Components setLink(String name, Link link) {
    if (links == null) {
      links = new HashMap<>();
    }
    links.put(name, link);
    return this;
  }

  public Components removeLink(String name) {
    remove(links, name);
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
    return has(callbacks, name);
  }

  public Callback getCallback(String name) {
    return get(callbacks, name);
  }

  public Components setCallback(String name, Callback callback) {
    if (callbacks == null) {
      callbacks = new HashMap<>();
    }
    callbacks.put(name, callback);
    return this;
  }

  public Components removeCallback(String name) {
    remove(callbacks, name);
    return this;
  }

  // Extensions
  public Extensions getExtensions() {
    return extensions;
  }

  public Components setExtensions(Extensions extensions) {
    this.extensions = extensions;
    return this;
  }

  @Override
  public Components copy(OAIContext context, boolean followRefs) {
    Components copy = new Components();

    copy.setSchemas(copyMap(schemas, context, followRefs));
    copy.setResponses(copyMap(responses, context, followRefs));
    copy.setParameters(copyMap(parameters, context, followRefs));
    copy.setExamples(copyMap(examples, context, followRefs));
    copy.setRequestBodies(copyMap(requestBodies, context, followRefs));
    copy.setHeaders(copyMap(headers, context, followRefs));
    copy.setSecuritySchemes(copyMap(securitySchemes, context, followRefs));
    copy.setLinks(copyMap(links, context, followRefs));
    copy.setCallbacks(copyMap(callbacks, context, followRefs));
    copy.setExtensions(copyField(extensions, context, followRefs));

    return copy;
  }
}
