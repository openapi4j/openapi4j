package org.openapi4j.parser.model.v3;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

import org.openapi4j.core.model.OAIContext;
import org.openapi4j.core.model.v3.OAI3;
import org.openapi4j.parser.model.AbsRefOpenApiSchema;

import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Callback extends AbsRefOpenApiSchema<OAI3, Callback> {
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
    return has(callbackPaths, expression);
  }

  public Path getCallbackPath(String expression) {
    return get(callbackPaths, expression);
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
    remove(callbackPaths, expression);
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
  protected Callback copyReference(OAIContext<OAI3> context) {
    Callback copy = new Callback();
    copy.set$ref(get$ref());
    return copy;
  }

  @Override
  protected Callback copyContent(OAIContext<OAI3> context, boolean followRefs) {
    Callback copy = new Callback();
    copy.setCallbackPaths(copyMap(callbackPaths, context, followRefs));
    copy.setExtensions(copyField(extensions, context, followRefs));

    return copy;
  }
}
