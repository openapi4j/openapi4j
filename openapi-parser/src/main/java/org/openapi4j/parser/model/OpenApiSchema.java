package org.openapi4j.parser.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import org.openapi4j.core.exception.EncodeException;

import java.util.EnumSet;

@JsonInclude(JsonInclude.Include.NON_NULL)
public interface OpenApiSchema<M> extends Cloneable {
  /**
   * Copy the model by following the references. This allows to flatten the model
   *
   * @return A copy of the model.
   */
  M copy();

  /**
   * Serialize the model to JSON
   *
   * @return The node representation
   * @throws EncodeException in case of serialization error
   */
  JsonNode toNode() throws EncodeException;

  /**
   * Serialize the model to JSON string
   *
   * @param flags   The flags to setup the output.
   * @return The serialized model.
   * @throws EncodeException in case of serialization error
   */
  String toString(EnumSet<SerializationFlag> flags) throws EncodeException;
}
