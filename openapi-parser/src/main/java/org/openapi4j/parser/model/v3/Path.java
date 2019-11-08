package org.openapi4j.parser.model.v3;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnore;

import org.openapi4j.core.model.OAIContext;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"unused", "UnusedReturnValue"})
public class Path extends AbsExtendedOpenApiSchema<Path> {
  private String description;
  @JsonAlias({"get", "put", "post", "delete", "options", "head", "patch", "trace"})
  @JsonIgnore
  private Map<String, Operation> operations = new HashMap<>();
  private List<Parameter> parameters;
  private List<Server> servers;
  private String summary;

  public Operation getGet() {
    return operations.get("get");
  }

  public Path setGet(Operation get) {
    operations.put("get", get);
    return this;
  }

  public Operation getPut() {
    return operations.get("put");
  }

  public Path setPut(Operation put) {
    operations.put("put", put);
    return this;
  }

  public Operation getPost() {
    return operations.get("post");
  }

  public Path setPost(Operation post) {
    operations.put("post", post);
    return this;
  }

  public Operation getDelete() {
    return operations.get("delete");
  }

  public Path setDelete(Operation delete) {
    operations.put("delete", delete);
    return this;
  }

  public Operation getOptions() {
    return operations.get("options");
  }

  public Path setOptions(Operation options) {
    operations.put("options", options);
    return this;
  }

  public Operation getHead() {
    return operations.get("head");
  }

  public Path setHead(Operation head) {
    operations.put("head", head);
    return this;
  }

  public Operation getPatch() {
    return operations.get("patch");
  }

  public Path setPatch(Operation patch) {
    operations.put("patch", patch);
    return this;
  }

  public Operation getTrace() {
    return operations.get("trace");
  }

  public Path setTrace(Operation trace) {
    operations.put("trace", trace);
    return this;
  }

  // Summary
  public String getSummary() {
    return summary;
  }

  public Path setSummary(String summary) {
    this.summary = summary;
    return this;
  }

  // Description
  public String getDescription() {
    return description;
  }

  public Path setDescription(String description) {
    this.description = description;
    return this;
  }

  // Operation
  public Map<String, Operation> getOperations() {
    return operations;
  }

  public Path setOperations(Map<String, Operation> operations) {
    this.operations = operations;
    return this;
  }

  public boolean hasOperation(String id) {
    return mapHas(operations, id);
  }

  public Operation getOperation(String id) {
    return mapGet(operations, id);
  }

  public Path setOperation(String id, Operation operation) {
    if (operations == null) {
      operations = new HashMap<>();
    }
    operations.put(id, operation);
    return this;
  }

  public Path removeOperation(String id) {
    mapRemove(operations, id);
    return this;
  }

  // Server
  public List<Server> getServers() {
    return servers;
  }

  public Path setServers(List<Server> servers) {
    this.servers = servers;
    return this;
  }

  public boolean hasServers() {
    return servers != null;
  }

  public Path addServer(Server server) {
    servers = listAdd(servers, server);
    return this;
  }

  public Path insertServer(int index, Server server) {
    servers = listAdd(servers, index, server);
    return this;
  }

  public Path removeServer(Server value) {
    listRemove(servers, value);
    return this;
  }

  // Parameter
  public List<Parameter> getParameters() {
    return parameters;
  }

  public Path setParameters(List<Parameter> parameters) {
    this.parameters = parameters;
    return this;
  }

  public boolean hasParameters() {
    return parameters != null;
  }

  public Path addParameter(Parameter parameter) {
    parameters = listAdd(parameters, parameter);
    return this;
  }

  public Path insertParameter(int index, Parameter parameter) {
    parameters = listAdd(parameters, index, parameter);
    return this;
  }

  public Path removeParameter(Parameter value) {
    listRemove(parameters, value);
    return this;
  }

  @Override
  public Path copy(OAIContext context, boolean followRefs) {
    Path copy = new Path();

    copy.setSummary(getSummary());
    copy.setDescription(getDescription());
    copy.setOperations(copyMap(getOperations(), context, followRefs));
    copy.setServers(copyList(getServers(), context, followRefs));
    copy.setParameters(copyList(getParameters(), context, followRefs));
    copy.setExtensions(copyMap(getExtensions()));

    return copy;
  }
}
