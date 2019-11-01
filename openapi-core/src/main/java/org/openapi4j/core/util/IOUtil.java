package org.openapi4j.core.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.nio.charset.Charset;

/**
 * Utility class related to IO.
 */
public class IOUtil {
  private static final int EOF = -1;
  private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;

  private IOUtil() {
  }

  public static String toString(final InputStream input, final Charset encoding) throws IOException {
    final StringWriter output = new StringWriter();
    final InputStreamReader in = new InputStreamReader(input, encoding);

    int n;
    char[] buffer = new char[DEFAULT_BUFFER_SIZE];
    while (EOF != (n = in.read(buffer))) {
      output.write(buffer, 0, n);
    }

    return output.toString();
  }
}
