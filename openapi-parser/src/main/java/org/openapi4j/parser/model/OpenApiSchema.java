package org.openapi4j.parser.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import org.openapi4j.core.exception.EncodeException;
import org.openapi4j.core.model.OAIContext;

import java.util.EnumSet;

@JsonInclude(JsonInclude.Include.NON_NULL)
public interface OpenApiSchema<M extends OpenApiSchema> extends Cloneable {
  /**
   * Copy the model by following the references. This allows to flatten the model
   *
   * @param context    The OpenApi context. Must not be null if flag followRefs is {@code true}.
   * @param followRefs The flag to follow the references and flatten the output.
   * @return A copy of the model with flatten references if found, null otherwise
   */
  M copy(OAIContext context, boolean followRefs);

  /**
   * Serialize the model to JSON
   *
   * @param context    The OpenApi context. Must not be null if flag followRefs is {@code true}.
   * @param followRefs The flag to follow the references and flatten the output.
   * @return The node representation
   * @throws EncodeException in case of serialization error
   */
  JsonNode toNode(OAIContext context, boolean followRefs) throws EncodeException;

  /**
   * Serialize the model to JSON string
   *
   * @param context The OpenApi context. Must not be null if flag FOLLOW_REFS is set.
   * @param flags   The flags to setup the output.
   * @return The serialized model.
   * @throws EncodeException in case of serialization error
   */
  String toString(OAIContext context, EnumSet<SerializationFlag> flags) throws EncodeException;
}
