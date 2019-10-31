package org.openapi4j.core.exception;

public class DecodeException extends Exception {
  public DecodeException() {
  }

  public DecodeException(String message) {
    super(message);
  }

  public DecodeException(String message, Throwable cause) {
    super(message, cause);
  }
}
