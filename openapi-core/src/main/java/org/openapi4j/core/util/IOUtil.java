package org.openapi4j.core.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Utility class related to IO.
 */
public final class IOUtil {
  private static final int EOF = -1;
  private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;

  private IOUtil() {
  }

  public static String toString(final InputStream input, final String charset) throws IOException {
    ByteArrayOutputStream result = new ByteArrayOutputStream();
    byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
    int length;

    while ((length = input.read(buffer)) != EOF) {
      result.write(buffer, 0, length);
    }

    return result.toString(charset);
  }
}
