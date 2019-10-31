package org.openapi4j.parser.model;

import org.openapi4j.core.model.OAI;
import org.openapi4j.core.model.OAIContext;
import org.openapi4j.core.model.reference.Reference;
import org.openapi4j.core.util.Json;

public abstract class AbsRefOpenApiSchema<O extends OAI, M extends OpenApiSchema<O, M>> extends AbsOpenApiSchema<O, M> {
  private String $ref;

  // $ref
  public String get$ref() {
    return $ref;
  }

  public boolean is$ref() {
    return $ref != null;
  }

  public void set$ref(String $ref) {
    this.$ref = $ref;
  }

  @SuppressWarnings("unchecked")
  public M copy(OAIContext<O> context, boolean followRefs) {
    if (is$ref()) {
      if (followRefs) {
        Reference reference = context.getReferenceRegistry().getRef(get$ref());
        if (reference != null) {
          M copy = (M) Json.jsonMapper.convertValue(reference.getContent(), getClass());
          return copy.copy(context, true);
        }
      } else {
        return copyReference(context);
      }
    }

    return copyContent(context, followRefs);
  }

  protected abstract M copyReference(OAIContext<O> context);

  protected abstract M copyContent(OAIContext<O> context, boolean followRefs);
}
