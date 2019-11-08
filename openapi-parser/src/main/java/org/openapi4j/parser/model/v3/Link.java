package org.openapi4j.parser.model.v3;

import org.openapi4j.core.model.OAIContext;

import java.util.HashMap;
import java.util.Map;

public class Link extends AbsExtendedRefOpenApiSchema<Link> {
  private String operationId;
  private String operationRef;
  private Map<String, String> parameters;
  private Map<String, Header> headers;
  private String description;
  private Server server;

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
    return mapHas(parameters, name);
  }

  public String getParameter(String name) {
    return mapGet(parameters, name);
  }

  public Link setParameter(String name, String parameter) {
    if (parameters == null) {
      parameters = new HashMap<>();
    }
    parameters.put(name, parameter);
    return this;
  }

  public Link removeParameter(String name) {
    mapRemove(parameters, name);
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
    return mapHas(headers, name);
  }

  public Header getHeader(String name) {
    return mapGet(headers, name);
  }

  public Link setHeader(String name, Header header) {
    if (headers == null) {
      headers = new HashMap<>();
    }
    headers.put(name, header);
    return this;
  }

  public Link removeHeader(String name) {
    mapRemove(headers, name);
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

  @Override
  protected Link copyReference(OAIContext context) {
    Link copy = new Link();
    copy.setRef(getRef());
    return copy;
  }

  @Override
  protected Link copyContent(OAIContext context, boolean followRefs) {
    Link copy = new Link();

    copy.setOperationId(getOperationId());
    copy.setOperationRef(getOperationRef());
    copy.setParameters(copyMap(getParameters()));
    copy.setHeaders(copyMap(getHeaders(), context, followRefs));
    copy.setDescription(getDescription());
    copy.setServer(copyField(getServer(), context, followRefs));
    copy.setExtensions(copyMap(getExtensions()));

    return copy;
  }
}
