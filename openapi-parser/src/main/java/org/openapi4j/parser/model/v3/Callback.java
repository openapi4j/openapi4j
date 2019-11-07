package org.openapi4j.parser.model.v3;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

import org.openapi4j.core.model.OAIContext;
import org.openapi4j.parser.model.AbsRefOpenApiSchema;

import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Callback extends AbsRefOpenApiSchema<Callback> {
  private Map<String, Path> callbackPaths;
  @JsonUnwrapped
  private Extensions extensions;

  // CallbackPath
  @JsonAnyGetter
  public Map<String, Path> getCallbackPaths() {
    return callbackPaths;
  }

  public Callback setCallbackPaths(Map<String, Path> callbackPaths) {
    this.callbackPaths = callbackPaths;
    return this;
  }

  public boolean hasCallbackPath(String expression) {
    return mapHas(callbackPaths, expression);
  }

  public Path getCallbackPath(String expression) {
    return mapGet(callbackPaths, expression);
  }

  @JsonAnySetter
  public Callback setCallbackPath(String expression, Path callbackPath) {
    if (callbackPaths == null) {
      callbackPaths = new HashMap<>();
    }
    callbackPaths.put(expression, callbackPath);
    return this;
  }

  public Callback removeCallbackPath(String expression) {
    mapRemove(callbackPaths, expression);
    return this;
  }

  // Extensions
  public Extensions getExtensions() {
    return extensions;
  }

  public Callback setExtensions(Extensions extensions) {
    this.extensions = extensions;
    return this;
  }


  @Override
  protected Callback copyReference(OAIContext context) {
    Callback copy = new Callback();
    copy.setRef(getRef());
    return copy;
  }

  @Override
  protected Callback copyContent(OAIContext context, boolean followRefs) {
    Callback copy = new Callback();
    copy.setCallbackPaths(copyMap(callbackPaths, context, followRefs));
    copy.setExtensions(copyField(extensions, context, followRefs));

    return copy;
  }
}
