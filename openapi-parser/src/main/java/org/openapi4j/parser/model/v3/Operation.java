package org.openapi4j.parser.model.v3;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
public class Operation extends AbsExtendedOpenApiSchema<Operation> {
  private List<String> tags;
  private String summary;
  private String description;
  private ExternalDocs externalDocs;
  private String operationId;
  private List<Parameter> parameters;
  private RequestBody requestBody;
  private Map<String, Response> responses;
  private Map<String, Callback> callbacks;
  private Boolean deprecated;
  @JsonProperty("security")
  private List<SecurityRequirement> securityRequirements;
  private List<Server> servers;

  // Tag
  public List<String> getTags() {
    return tags;
  }

  public Operation setTags(List<String> tags) {
    this.tags = tags;
    return this;
  }

  public boolean hasTags() {
    return tags != null;
  }

  public Operation addTag(String tag) {
    tags = listAdd(tags, tag);
    return this;
  }

  public Operation insertTag(int index, String tag) {
    tags = listAdd(tags, index, tag);
    return this;
  }

  public Operation removeTag(String tag) {
    listRemove(tags, tag);
    return this;
  }

  // Summary
  public String getSummary() {
    return summary;
  }

  public Operation setSummary(String summary) {
    this.summary = summary;
    return this;
  }

  // Description
  public String getDescription() {
    return description;
  }

  public Operation setDescription(String description) {
    this.description = description;
    return this;
  }

  // ExternalDocs
  public ExternalDocs getExternalDocs() {
    return externalDocs;
  }

  public Operation setExternalDocs(ExternalDocs externalDocs) {
    this.externalDocs = externalDocs;
    return this;
  }

  // OperationId
  public String getOperationId() {
    return operationId;
  }

  public Operation setOperationId(String operationId) {
    this.operationId = operationId;
    return this;
  }

  // Parameter
  public List<Parameter> getParameters() {
    return parameters;
  }

  public Operation setParameters(List<Parameter> parameters) {
    this.parameters = parameters;
    return this;
  }

  public boolean hasParameters() {
    return parameters != null;
  }

  public Operation addParameter(Parameter parameter) {
    parameters = listAdd(parameters, parameter);
    return this;
  }

  public Operation insertParameter(int index, Parameter parameter) {
    parameters = listAdd(parameters, index, parameter);
    return this;
  }

  public Operation removeParameter(Parameter parameter) {
    listRemove(parameters, parameter);
    return this;
  }

  public List<Parameter> getParametersIn(String in) {
    List<Parameter> inParameters = new ArrayList<>();

    if (parameters != null) {
      for (Parameter param : parameters) {
        if (in.equals(param.getIn())) {
          inParameters.add(param);
        }
      }
    }

    return inParameters;
  }

  // RequestBody
  public RequestBody getRequestBody() {
    return requestBody;
  }

  public Operation setRequestBody(RequestBody requestBody) {
    this.requestBody = requestBody;
    return this;
  }

  // Response
  public Map<String, Response> getResponses() {
    return responses;
  }

  public Operation setResponses(Map<String, Response> responses) {
    this.responses = responses;
    return this;
  }

  public boolean hasResponse(String name) {
    return mapHas(responses, name);
  }

  public Response getResponse(String name) {
    return mapGet(responses, name);
  }

  public Operation setResponse(String name, Response response) {
    if (responses == null) {
      responses = new HashMap<>();
    }
    responses.put(name, response);
    return this;
  }

  public Operation removeResponse(String name) {
    mapRemove(responses, name);
    return this;
  }

  // Callback
  public Map<String, Callback> getCallbacks() {
    return callbacks;
  }

  public Operation setCallbacks(Map<String, Callback> callbacks) {
    this.callbacks = callbacks;
    return this;
  }

  public boolean hasCallback(String name) {
    return mapHas(callbacks, name);
  }

  public Callback getCallback(String name) {
    return mapGet(callbacks, name);
  }

  public Operation setCallback(String name, Callback callback) {
    if (callbacks == null) {
      callbacks = new HashMap<>();
    }
    callbacks.put(name, callback);
    return this;
  }

  public Operation removeCallback(String name) {
    mapRemove(callbacks, name);
    return this;
  }

  // Deprecated
  public Boolean getDeprecated() {
    return deprecated;
  }

  public boolean isDeprecated() {
    return Boolean.TRUE.equals(deprecated);
  }

  public Operation setDeprecated(Boolean deprecated) {
    this.deprecated = deprecated;
    return this;
  }

  // SecurityRequirement
  public List<SecurityRequirement> getSecurityRequirements() {
    return securityRequirements;
  }

  public Operation setSecurityRequirements(List<SecurityRequirement> securityRequirements) {
    this.securityRequirements = securityRequirements;
    return this;
  }

  public boolean hasSecurityRequirements() {
    return securityRequirements != null;
  }

  public Operation addSecurityRequirement(SecurityRequirement securityRequirement) {
    securityRequirements = listAdd(securityRequirements, securityRequirement);
    return this;
  }

  public Operation insertSecurityRequirement(int index, SecurityRequirement securityRequirement) {
    securityRequirements = listAdd(securityRequirements, index, securityRequirement);
    return this;
  }

  public Operation removeSecurityRequirement(SecurityRequirement securityRequirement) {
    listRemove(securityRequirements, securityRequirement);
    return this;
  }

  // Server
  public List<Server> getServers() {
    return servers;
  }

  public Operation setServers(List<Server> servers) {
    this.servers = servers;
    return this;
  }

  public boolean hasServers() {
    return servers != null;
  }

  public Operation addServer(Server server) {
    servers = listAdd(servers, server);
    return this;
  }

  public Operation insertServer(int index, Server server) {
    servers = listAdd(servers, index, server);
    return this;
  }

  public Operation removeServer(Server server) {
    listRemove(servers, server);
    return this;
  }

  @Override
  public Operation copy() {
    Operation copy = new Operation();

    copy.setSummary(getSummary());
    copy.setTags(copySimpleList(getTags()));
    copy.setDescription(getDescription());
    copy.setExternalDocs(copyField(getExternalDocs()));
    copy.setOperationId(getOperationId());
    copy.setParameters(copyList(getParameters()));
    copy.setRequestBody(copyField(getRequestBody()));
    copy.setResponses(copyMap(getResponses()));
    copy.setCallbacks(copyMap(getCallbacks()));
    copy.setDeprecated(getDeprecated());
    copy.setSecurityRequirements(copyList(getSecurityRequirements()));
    copy.setServers(copyList(getServers()));
    copy.setExtensions(copySimpleMap(getExtensions()));

    return copy;
  }
}
