package org.openapi4j.core.exception;

/**
 * Exception for resolution representation.
 * <p/>
 * Resolution means that an expected resource has not been found or
 * cannot be reached.
 */
public class ResolutionException extends Exception {
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
