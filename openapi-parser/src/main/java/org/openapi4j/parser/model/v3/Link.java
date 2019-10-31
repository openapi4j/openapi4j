package org.openapi4j.parser.model.v3;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

import org.openapi4j.core.model.OAIContext;
import org.openapi4j.core.model.v3.OAI3;
import org.openapi4j.parser.model.AbsRefOpenApiSchema;

import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Link extends AbsRefOpenApiSchema<OAI3, Link> {
  private String operationId;
  private String operationRef;
  private Map<String, String> parameters;
  private Map<String, Header> headers;
  private String description;
  private Server server;
  @JsonUnwrapped
  private Extensions extensions;

  // OperationId
  public String getOperationId() {
    return operationId;
  }

  public Link setOperationId(String operationId) {
    this.operationId = operationId;
    return this;
  }

  // OperationRef
  public String getOperationRef() {
    return operationRef;
  }

  public Link setOperationRef(String operationRef) {
    this.operationRef = operationRef;
    return this;
  }

  // Parameter
  public Map<String, String> getParameters() {
    return parameters;
  }

  public Link setParameters(Map<String, String> parameters) {
    this.parameters = parameters;
    return this;
  }

  public boolean hasParameter(String name) {
    return has(parameters, name);
  }

  public String getParameter(String name) {
    return get(parameters, name);
  }

  public Link setParameter(String name, String parameter) {
    if (parameters == null) {
      parameters = new HashMap<>();
    }
    parameters.put(name, parameter);
    return this;
  }

  public Link removeParameter(String name) {
    remove(parameters, name);
    return this;
  }

  // Header
  public Map<String, Header> getHeaders() {
    return headers;
  }

  public Link setHeaders(Map<String, Header> headers) {
    this.headers = headers;
    return this;
  }

  public boolean hasHeader(String name) {
    return has(headers, name);
  }

  public Header getHeader(String name) {
    return get(headers, name);
  }

  public Link setHeader(String name, Header header) {
    if (headers == null) {
      headers = new HashMap<>();
    }
    headers.put(name, header);
    return this;
  }

  public Link removeHeader(String name) {
    remove(headers, name);
    return this;
  }

  // Description
  public String getDescription() {
    return description;
  }

  public Link setDescription(String description) {
    this.description = description;
    return this;
  }

  // Server
  public Server getServer() {
    return server;
  }

  public Link setServer(Server server) {
    this.server = server;
    return this;
  }

  // Extensions
  public Extensions getExtensions() {
    return extensions;
  }

  public Link setExtensions(Extensions extensions) {
    this.extensions = extensions;
    return this;
  }

  @Override
  protected Link copyReference(OAIContext<OAI3> context) {
    Link copy = new Link();
    copy.set$ref(get$ref());
    return copy;
  }

  @Override
  protected Link copyContent(OAIContext<OAI3> context, boolean followRefs) {
    Link copy = new Link();

    copy.setOperationId(operationId);
    copy.setOperationRef(operationRef);
    copy.setParameters(copyMap(parameters));
    copy.setHeaders(copyMap(headers, context, followRefs));
    copy.setDescription(description);
    copy.setServer(copyField(server, context, followRefs));
    copy.setExtensions(copyField(extensions, context, followRefs));

    return copy;
  }
}
