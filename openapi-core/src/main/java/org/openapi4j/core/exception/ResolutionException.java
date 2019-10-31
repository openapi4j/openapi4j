package org.openapi4j.core.exception;

public class ResolutionException extends RuntimeException {
  public ResolutionException() {
    super();
  }

  public ResolutionException(String message, Throwable cause) {
    super(message, cause);
  }

  public ResolutionException(String message) {
    super(message);
  }

  public ResolutionException(Throwable cause) {
    super(cause);
  }
}
