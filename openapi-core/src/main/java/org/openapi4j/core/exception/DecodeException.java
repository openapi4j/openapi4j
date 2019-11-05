package org.openapi4j.core.exception;

/**
 * Exception for decoding/de-serialization representation.
 */
public class DecodeException extends Exception {
  public DecodeException(String message) {
    super(message);
  }

  public DecodeException(String message, Throwable cause) {
    super(message, cause);
  }
}
