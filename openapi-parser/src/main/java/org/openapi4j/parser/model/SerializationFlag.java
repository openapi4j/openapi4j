package org.openapi4j.parser.model;

/**
 * Options for serialization
 */
public enum SerializationFlag {
  /**
   * Flag to follow the JSON references
   */
  FOLLOW_REFS,
  /**
   * Flag for returning a JSON node
   */
  OUT_AS_JSON,
  /**
   * Flag for returning a JSON node string representation
   */
  OUT_AS_STRING,
  /**
   * Flag for returning a YAML node
   */
  OUT_AS_YAML
}
