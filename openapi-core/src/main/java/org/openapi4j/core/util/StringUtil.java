package org.openapi4j.core.util;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public final class StringUtil {
  private StringUtil() {}

  /**
   * Tokenize the given {@code String} into a {@code String} list via a
   * {@link StringTokenizer}.
   * <p>The given {@code delimiters} string can consist of any number of
   * delimiter characters. Each of those characters can be used to separate
   * tokens. A delimiter is always a single character.
   *
   * @param value             The {@code String} to tokenize (potentially {@code null} or empty)
   * @param delimiters        The delimiter characters, assembled as a {@code String}
   *                          (each of the characters is individually considered as a delimiter).
   * @param trimTokens        Trim the tokens via {@link String#trim()}.
   * @param ignoreEmptyTokens Ignore empty tokens.
   * @return List of the tokens
   */
  public static List<String> tokenize(String value,
                                      String delimiters,
                                      boolean trimTokens,
                                      boolean ignoreEmptyTokens) {

    List<String> tokens = new ArrayList<>();

    if (value == null) {
      return tokens;
    }

    String[] st = value.split(delimiters);
    for (String token : st) {
      if (trimTokens) {
        token = token.trim();
      }

      if (!ignoreEmptyTokens || token.length() > 0) {
        tokens.add(token);
      }
    }

    return tokens;
  }
}
