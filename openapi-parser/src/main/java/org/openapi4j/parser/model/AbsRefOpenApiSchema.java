package org.openapi4j.parser.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;

import org.openapi4j.core.exception.EncodeException;
import org.openapi4j.core.model.OAIContext;
import org.openapi4j.core.model.reference.Reference;
import org.openapi4j.core.util.TreeUtil;

import static org.openapi4j.core.model.reference.Reference.ABS_REF_FIELD;
import static org.openapi4j.core.util.TreeUtil.JSON_ENCODE_ERR_MSG;

/**
 * Base class for Open API schema which can be represented as reference.
 */
public abstract class AbsRefOpenApiSchema<M extends OpenApiSchema<M>> extends AbsOpenApiSchema<M> {
  private static final ObjectMapper INTERNAL_MAPPER;

  // Add rule to avoid export of hidden fields
  static {
    INTERNAL_MAPPER = new ObjectMapper();
    INTERNAL_MAPPER.setAnnotationIntrospector(new JacksonAnnotationIntrospector() {
      @Override
      public boolean hasIgnoreMarker(final AnnotatedMember m) {
        return ABS_REF_FIELD.equals(m.getName()) || super.hasIgnoreMarker(m);
      }
    });
  }

  @JsonProperty("$ref")
  private String ref;
  @JsonProperty(value = ABS_REF_FIELD)
  private String canonicalRef;

  // $ref
  public String getRef() {
    return ref;
  }

  public boolean isRef() {
    return ref != null;
  }

  public void setRef(String ref) {
    this.ref = ref;
  }

  public void setCanonicalRef(String canonicalRef) {
    this.canonicalRef = canonicalRef;
  }

  public String getCanonicalRef() {
    return canonicalRef;
  }

  public Reference getReference(OAIContext context) {
    return context.getReferenceRegistry().getRef(canonicalRef != null ? canonicalRef : ref);
  }

  @Override
  public JsonNode toNode(OAIContext context, boolean followRefs) throws EncodeException {
    OpenApiSchema<M> model
      = followRefs
      ? copy(context, true)
      : this;

    try {
      return INTERNAL_MAPPER.valueToTree(model);
    } catch (Exception e) {
      throw new EncodeException(String.format(JSON_ENCODE_ERR_MSG, e.getMessage()));
    }
  }

  @SuppressWarnings("unchecked")
  public M copy(OAIContext context, boolean followRefs) {
    if (isRef()) {
      if (followRefs) {
        Reference reference = getReference(context);
        if (reference != null) {
          M copy = (M) TreeUtil.json.convertValue(reference.getContent(), getClass());
          return copy.copy(context, true);
        }
      } else {
        return copyReference(context);
      }
    }

    return copyContent(context, followRefs);
  }

  /**
   * Copy the reference object.
   *
   * @param context The current context.
   * @return The copied reference object.
   */
  protected abstract M copyReference(OAIContext context);

  /**
   * Copy the flat content of the current schema.
   *
   * @param context    The current context.
   * @param followRefs {@code true} for following references.
   * @return The copied model.
   */
  protected abstract M copyContent(OAIContext context, boolean followRefs);
}
