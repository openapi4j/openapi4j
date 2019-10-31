package org.openapi4j.parser.model.v3;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

import org.openapi4j.core.model.OAIContext;
import org.openapi4j.core.model.v3.OAI3;
import org.openapi4j.parser.model.AbsOpenApiSchema;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Operation extends AbsOpenApiSchema<OAI3, Operation> {
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
  @JsonUnwrapped
  private Extensions extensions;

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
    if (tags == null) {
      tags = new ArrayList<>();
    }
    tags.add(tag);
    return this;
  }

  public Operation removeTag(int index) {
    if (tags != null) {
      tags.remove(index);
    }
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

  public Parameter getParameter(int index) {
    if (parameters == null) {
      return null;
    }
    return parameters.get(index);
  }

  public Operation addParameter(Parameter parameter) {
    if (parameters == null) {
      parameters = new ArrayList<>();
    }
    parameters.add(parameter);
    return this;
  }

  public Operation insertParameter(int index, Parameter parameter) {
    if (parameters == null) {
      parameters = new ArrayList<>();
    }
    parameters.add(index, parameter);
    return this;
  }

  public Operation removeParameter(int index) {
    if (parameters != null) {
      parameters.remove(index);
    }
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

  public boolean Response(String name) {
    return has(responses, name);
  }

  public Response getResponse(String name) {
    return get(responses, name);
  }

  public Operation setResponse(String name, Response response) {
    if (responses == null) {
      responses = new HashMap<>();
    }
    responses.put(name, response);
    return this;
  }

  public Operation removeResponse(String name) {
    remove(responses, name);
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
    return has(callbacks, name);
  }

  public Callback getCallback(String name) {
    return get(callbacks, name);
  }

  public Operation setCallback(String name, Callback callback) {
    if (callbacks == null) {
      callbacks = new HashMap<>();
    }
    callbacks.put(name, callback);
    return this;
  }

  public Operation removeCallback(String name) {
    remove(callbacks, name);
    return this;
  }

  // Deprecated
  public Boolean getDeprecated() {
    return deprecated;
  }

  public boolean isDeprecated() {
    return deprecated != null ? deprecated : false;
  }

  public Operation setDeprecated(Boolean deprecated) {
    this.deprecated = deprecated;
    return this;
  }

  // SecurityRequirement
  public Collection<SecurityRequirement> getSecurityRequirements() {
    return securityRequirements;
  }

  public Operation setSecurityRequirements(List<SecurityRequirement> securityRequirements) {
    this.securityRequirements = securityRequirements;
    return this;
  }

  public boolean hasSecurityRequirements() {
    return securityRequirements != null;
  }

  public SecurityRequirement getSecurityRequirement(int index) {
    if (securityRequirements == null) {
      return null;
    }
    return securityRequirements.get(index);
  }

  public Operation addSecurityRequirement(SecurityRequirement securityRequirement) {
    if (securityRequirements == null) {
      securityRequirements = new ArrayList<>();
    }
    securityRequirements.add(securityRequirement);
    return this;
  }

  public Operation insertSecurityRequirement(int index, SecurityRequirement securityRequirement) {
    if (securityRequirements == null) {
      securityRequirements = new ArrayList<>();
    }
    securityRequirements.add(index, securityRequirement);
    return this;
  }

  public Operation removeSecurityRequirement(int index) {
    if (securityRequirements != null) {
      securityRequirements.remove(index);
    }
    return this;
  }

  // Server
  public Collection<Server> getServers() {
    return servers;
  }

  public Operation setServers(List<Server> servers) {
    this.servers = servers;
    return this;
  }

  public boolean hasServers() {
    return servers != null;
  }

  public Server getServer(int index) {
    if (servers == null) {
      return null;
    }
    return servers.get(index);
  }

  public Operation addServer(Server server) {
    if (servers == null) {
      servers = new ArrayList<>();
    }
    servers.add(server);
    return this;
  }

  public Operation insertServer(int index, Server server) {
    if (servers == null) {
      servers = new ArrayList<>();
    }
    servers.add(index, server);
    return this;
  }

  public Operation removeServer(int index) {
    if (servers != null) {
      servers.remove(index);
    }
    return this;
  }

  // Extensions
  public Extensions getExtensions() {
    return extensions;
  }

  public Operation setExtensions(Extensions extensions) {
    this.extensions = extensions;
    return this;
  }

  @Override
  public Operation copy(OAIContext<OAI3> context, boolean followRefs) {
    Operation copy = new Operation();

    copy.setTags(copyList(tags));
    copy.setDescription(description);
    copy.setExternalDocs(copyField(externalDocs, context, followRefs));
    copy.setOperationId(operationId);
    copy.setParameters(copyList(parameters, context, followRefs));
    copy.setRequestBody(copyField(requestBody, context, followRefs));
    copy.setResponses(copyMap(responses, context, followRefs));
    copy.setCallbacks(copyMap(callbacks, context, followRefs));
    copy.setDeprecated(deprecated);
    copy.setSecurityRequirements(copyList(securityRequirements, context, followRefs));
    copy.setServers(copyList(servers, context, followRefs));
    copy.setExtensions(copyField(extensions, context, followRefs));

    return copy;
  }
}
