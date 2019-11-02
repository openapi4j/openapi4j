package org.openapi4j.parser.model.v3;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.databind.JsonNode;

import org.openapi4j.core.model.OAI;
import org.openapi4j.core.model.OAIContext;
import org.openapi4j.core.model.reference.Reference;
import org.openapi4j.core.model.v3.OAI3;
import org.openapi4j.parser.model.AbsOpenApiSchema;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OpenApi3 extends AbsOpenApiSchema<OAI3, OpenApi3> implements OAI<OAI3> {
  private String openapi;
  private Info info;
  private List<Server> servers;
  private List<Tag> tags;
  @JsonInclude
  // paths is required but can be empty (https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.1.md?utf8=%E2%9C%93#security-filtering)
  private Map<String, Path> paths;
  private Components components;
  private ExternalDocs externalDocs;
  private SecurityRequirement security;
  @JsonUnwrapped
  private Extensions extensions;
  @JsonIgnore
  private OAIContext<OAI3> context;

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
    if (servers == null) {
      servers = new ArrayList<>();
    }
    servers.add(server);
    return this;
  }

  public OpenApi3 removeServer(int index) {
    if (servers != null) {
      servers.remove(index);
    }
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
    return has(paths, name);
  }

  public Path getPath(String name) {
    return get(paths, name);
  }

  public OpenApi3 setPath(String name, Path path) {
    if (paths == null) {
      paths = new HashMap<>();
    }
    paths.put(name, path);
    return this;
  }

  public OpenApi3 removePath(String name) {
    remove(paths, name);
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
  public SecurityRequirement getSecurity() {
    return security;
  }

  public OpenApi3 setSecurity(SecurityRequirement security) {
    this.security = security;
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

  public Tag getTag(int index) {
    if (tags == null) {
      return null;
    }
    return tags.get(index);
  }

  public OpenApi3 addTag(Tag tag) {
    if (tags == null) {
      tags = new ArrayList<>();
    }
    tags.add(tag);
    return this;
  }

  public OpenApi3 insertTag(int index, Tag tag) {
    if (tags == null) {
      tags = new ArrayList<>();
    }
    tags.add(index, tag);
    return this;
  }

  public OpenApi3 removeTag(int index) {
    if (tags != null) {
      tags.remove(index);
    }
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

  // Extensions
  public Extensions getExtensions() {
    return extensions;
  }

  public OpenApi3 setExtensions(Extensions extensions) {
    this.extensions = extensions;
    return this;
  }

  @Override
  public OAIContext<OAI3> getContext() {
    return context;
  }

  @Override
  public void setContext(OAIContext<OAI3> context) {
    this.context = context;
  }


  //////////////////////////////////////////////////////////////
  // UTILITY METHODS
  //////////////////////////////////////////////////////////////

  public JsonNode getReferenceContent(String refString) {
    if (context == null) {
      return null;
    }

    Reference reference = context.getReferenceRegistry().getRef(refString);
    if (reference != null) {
      return reference.getContent();
    }

    return null;
  }

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
  public OpenApi3 copy(OAIContext<OAI3> context, boolean followRefs) {
    OpenApi3 copy = new OpenApi3();

    copy.setOpenapi(openapi);
    copy.setInfo(copyField(info, context, followRefs));
    copy.setServers(copyList(servers, context, followRefs));
    copy.setTags(copyList(tags, context, followRefs));
    copy.setPaths(copyMap(paths, context, followRefs));
    copy.setComponents(copyField(components, context, followRefs));
    copy.setExternalDocs(copyField(externalDocs, context, followRefs));
    copy.setSecurity(copyField(security, context, followRefs));
    copy.setExtensions(copyField(extensions, context, followRefs));
    copy.setContext(context);

    return copy;
  }
}
