package org.openapi4j.parser.model.v3;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import org.openapi4j.core.model.OAIContext;
import org.openapi4j.parser.model.AbsOpenApiSchema;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"unused", "UnusedReturnValue"})
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Path extends AbsOpenApiSchema<Path> {
  private String description;
  @JsonUnwrapped
  private Extensions extensions;
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
    return has(operations, id);
  }

  public Operation getOperation(String id) {
    return get(operations, id);
  }

  public Path setOperation(String id, Operation operation) {
    if (operations == null) {
      operations = new HashMap<>();
    }
    operations.put(id, operation);
    return this;
  }

  public Path removeOperation(String id) {
    remove(operations, id);
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

  public Server getServer(int index) {
    if (servers == null) {
      return null;
    }
    return servers.get(index);
  }

  public Path addServer(Server server) {
    if (servers == null) {
      servers = new ArrayList<>();
    }
    servers.add(server);
    return this;
  }

  public Path insertServer(int index, Server server) {
    if (servers == null) {
      servers = new ArrayList<>();
    }
    servers.add(index, server);
    return this;
  }

  public Path removeServer(int index) {
    if (servers != null) {
      servers.remove(index);
    }
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

  public Parameter getParameter(int index) {
    if (parameters == null) {
      return null;
    }
    return parameters.get(index);
  }

  public Path addParameter(Parameter parameter) {
    if (parameters == null) {
      parameters = new ArrayList<>();
    }
    parameters.add(parameter);
    return this;
  }

  public Path insertParameter(int index, Parameter parameter) {
    if (parameters == null) {
      parameters = new ArrayList<>();
    }
    parameters.add(index, parameter);
    return this;
  }

  public Path removeParameter(int index) {
    if (parameters != null) {
      parameters.remove(index);
    }
    return this;
  }

  // Extensions
  public Extensions getExtensions() {
    return extensions;
  }

  public Path setExtensions(Extensions extensions) {
    this.extensions = extensions;
    return this;
  }

  @Override
  public Path copy(OAIContext context, boolean followRefs) {
    Path copy = new Path();

    copy.setSummary(summary);
    copy.setDescription(description);
    copy.setOperations(copyMap(operations, context, followRefs));
    copy.setServers(copyList(servers, context, followRefs));
    copy.setParameters(copyList(parameters, context, followRefs));
    copy.setExtensions(copyField(extensions, context, followRefs));

    return copy;
  }
}
