package org.openapi4j.parser.model.v3;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.openapi4j.core.model.OAI;
import org.openapi4j.core.model.OAIContext;

import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
public class OpenApi3 extends AbsExtendedOpenApiSchema<OpenApi3> implements OAI {
  private String openapi;
  private Info info;
  private List<Server> servers;
  private List<Tag> tags;
  // paths is required but can be empty (https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.1.md?utf8=%E2%9C%93#security-filtering)
  @JsonInclude
  private Map<String, Path> paths;
  private Components components;
  private ExternalDocs externalDocs;
  @JsonProperty("security")
  private List<SecurityRequirement> securityRequirements;
  @JsonIgnore
  private OAIContext context;

  // OpenApi
  public String getOpenapi() {
    return openapi;
  }

  public OpenApi3 setOpenapi(String openapi) {
    this.openapi = openapi;
    return this;
  }

  // Info
  public Info getInfo() {
    return info;
  }

  public OpenApi3 setInfo(Info info) {
    this.info = info;
    return this;
  }

  // Server
  public List<Server> getServers() {
    return servers;
  }

  public OpenApi3 setServers(List<Server> servers) {
    this.servers = servers;
    return this;
  }

  public boolean hasServers() {
    return servers != null;
  }

  public OpenApi3 addServer(Server server) {
    servers = listAdd(servers, server);
    return this;
  }

  public OpenApi3 insertServer(int index, Server server) {
    servers = listAdd(servers, index, server);
    return this;
  }

  public OpenApi3 removeServer(Server server) {
    listRemove(servers, server);
    return this;
  }

  // Path
  public Map<String, Path> getPaths() {
    return paths;
  }

  public OpenApi3 setPaths(Map<String, Path> paths) {
    this.paths = paths;
    return this;
  }

  public boolean hasPath(String name) {
    return mapHas(paths, name);
  }

  public Path getPath(String name) {
    return mapGet(paths, name);
  }

  public OpenApi3 setPath(String name, Path path) {
    paths = mapPut(paths, name, path);
    return this;
  }

  public OpenApi3 removePath(String name) {
    mapRemove(paths, name);
    return this;
  }

  // Components
  public Components getComponents() {
    return components;
  }

  public OpenApi3 setComponents(Components components) {
    this.components = components;
    return this;
  }

  // SecurityRequirement
  public List<SecurityRequirement> getSecurityRequirements() {
    return securityRequirements;
  }

  public OpenApi3 setSecurityRequirements(List<SecurityRequirement> securityRequirements) {
    this.securityRequirements = securityRequirements;
    return this;
  }

  public boolean hasSecurityRequirements() {
    return securityRequirements != null;
  }

  public OpenApi3 addSecurityRequirement(SecurityRequirement securityRequirement) {
    securityRequirements = listAdd(securityRequirements, securityRequirement);
    return this;
  }

  public OpenApi3 insertSecurityRequirement(int index, SecurityRequirement securityRequirement) {
    securityRequirements = listAdd(securityRequirements, index, securityRequirement);
    return this;
  }

  public OpenApi3 removeSecurityRequirement(SecurityRequirement securityRequirement) {
    listRemove(securityRequirements, securityRequirement);
    return this;
  }

  // Tag
  public List<Tag> getTags() {
    return tags;
  }

  public OpenApi3 setTags(List<Tag> tags) {
    this.tags = tags;
    return this;
  }

  public boolean hasTags() {
    return tags != null;
  }

  public OpenApi3 addTag(Tag tag) {
    tags = listAdd(tags, tag);
    return this;
  }

  public OpenApi3 insertTag(int index, Tag tag) {
    tags = listAdd(tags, index, tag);
    return this;
  }

  public OpenApi3 removeTag(Tag tag) {
    listRemove(tags, tag);
    return this;
  }

  // ExternalDocs
  public ExternalDocs getExternalDocs() {
    return externalDocs;
  }

  public OpenApi3 setExternalDocs(ExternalDocs externalDocs) {
    this.externalDocs = externalDocs;
    return this;
  }

  @Override
  public OAIContext getContext() {
    return context;
  }

  @Override
  public void setContext(OAIContext context) {
    this.context = context;
  }


  //////////////////////////////////////////////////////////////
  // UTILITY METHODS
  //////////////////////////////////////////////////////////////

  /**
   * @param operationId the operationId (case sensitive)
   * @return Get the corresponding operation with the given ID
   */
  public Operation getOperationById(String operationId) {
    if (paths == null) return null;

    for (Path path : paths.values()) {
      if (path.getOperations() == null) {
        continue;
      }

      for (Operation operation : path.getOperations().values()) {
        if (operationId.equals(operation.getOperationId())) {
          return operation;
        }
      }
    }

    return null;
  }

  public String getPathFrom(Path specPath) {
    if (paths == null) return null;

    for (Map.Entry<String, Path> path : paths.entrySet()) {
      if (path.getValue().equals(specPath)) {
        return path.getKey();
      }
    }

    return null;
  }

  /**
   * @param operationId the operationId (case sensitive)
   * @return Get the corresponding operation with the given ID
   */
  public Path getPathItemByOperationId(String operationId) {
    if (paths == null) return null;

    for (Path path : paths.values()) {
      if (path.getOperations() == null) {
        continue;
      }

      for (Operation operation : path.getOperations().values()) {
        if (operationId.equals(operation.getOperationId())) {
          return path;
        }
      }
    }

    return null;
  }

  @Override
  public OpenApi3 copy() {
    OpenApi3 copy = new OpenApi3();

    copy.setOpenapi(getOpenapi());
    copy.setInfo(copyField(getInfo()));
    copy.setServers(copyList(getServers()));
    copy.setTags(copyList(getTags()));
    copy.setPaths(copyMap(getPaths()));
    copy.setComponents(copyField(getComponents()));
    copy.setExternalDocs(copyField(getExternalDocs()));
    copy.setSecurityRequirements(copyList(getSecurityRequirements()));
    copy.setExtensions(copySimpleMap(getExtensions()));
    copy.setContext(context);

    return copy;
  }
}
