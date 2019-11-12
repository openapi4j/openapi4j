package org.openapi4j.parser.model;

/**
 * Options for serialization
 */
public enum SerializationFlag {
  /**
   * Flag to follow the JSON references.
   */
  FOLLOW_REFS,
  /**
   * Flag for returning a JSON node string.
   */
  OUT_AS_JSON,
  /**
   * Flag for returning a YAML node string.
   */
  OUT_AS_YAML
}
