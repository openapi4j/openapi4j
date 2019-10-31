package org.openapi4j.parser.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import org.openapi4j.core.exception.EncodeException;
import org.openapi4j.core.model.OAI;
import org.openapi4j.core.model.OAIContext;

import java.util.EnumSet;

@JsonInclude(JsonInclude.Include.NON_NULL)
public interface OpenApiSchema<O extends OAI, M extends OpenApiSchema> extends Cloneable {
  /**
   * Copy the model by following the references. This allows to flatten the model
   *
   * @param context The OpenApi context to follow references
   * @return A copy of the model with flatten references if found, null otherwise
   */
  M copy(OAIContext<O> context, boolean followRefs);

  /**
   * Serialize the model to JSON
   *
   * @param context Must not be null if flag FOLLOW_REFS is set
   * @param flags   The flags to setup the output
   * @param <T>     JsonNode or String depending of flags. Default is JsonNode
   * @return The serialized model
   * @throws EncodeException in case of serialization error
   */

  <T> T toJson(OAIContext<O> context, EnumSet<SerializationFlag> flags) throws EncodeException;

  /**
   * Serialize the model to JSON string
   *
   * @return The serialized model
   * @throws EncodeException in case of serialization error
   */
  String toJson() throws EncodeException;
}
