package org.openapi4j.parser.model.v3;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.openapi4j.core.model.OAIContext;
import org.openapi4j.parser.model.AbsOpenApiSchema;

import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Extensions extends AbsOpenApiSchema<Extensions> {
  private Map<String, Object> extMap = new HashMap<>();

  @JsonAnyGetter
  public Map<String, Object> getExtensions() {
    return extMap;
  }

  @JsonAnySetter
  public Extensions setExtensions(Map<String, Object> extensions) {
    this.extMap = extensions;
    return this;
  }

  public boolean hasExtension(String name) {
    return has(extMap, name);
  }

  public Object getExtension(String name) {
    return get(extMap, name);
  }

  public Extensions setExtension(String name, Object extension) {
    if (extMap == null) {
      extMap = new HashMap<>();
    }
    extMap.put(name, extension);
    return this;
  }

  public Extensions removeExtension(String name) {
    remove(extMap, name);
    return this;
  }

  @Override
  public Extensions copy(OAIContext context, boolean followRefs) {
    Extensions copy = new Extensions();

    copy.setExtensions(copyMap(extMap));

    return copy;
  }
}
