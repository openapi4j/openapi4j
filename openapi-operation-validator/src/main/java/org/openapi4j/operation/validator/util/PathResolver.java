package org.openapi4j.operation.validator.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PathResolver {
  private static final Pattern OAS_PATH_PARAMETERS_PATTERN = Pattern.compile("\\{[.;?*+]*([^{}.;?*+]+)[^}]*}");

  private static final PathResolver INSTANCE = new PathResolver();

  private PathResolver() {
  }

  public static PathResolver instance() {
    return INSTANCE;
  }

  /**
   * This method returns a pattern only if a pattern is needed, otherwise it returns {@code null}.
   * This will not add begin or end of string anchors to the regular expression.
   *
   * @param oasPath The OAS path to build.
   * @return a pattern only if a pattern is needed.
   */
  public Pattern solve(String oasPath) {
    return solve(oasPath, false);
  }

  /**
   * This method returns a pattern only if a pattern is needed, otherwise it returns {@code null}.
   *
   * @param oasPath      The OAS path to build.
   * @param addEndString Add end of string anchor to the regular expression.
   * @return a pattern only if a pattern is needed.
   */
  public Pattern solve(String oasPath, boolean addEndString) {
    final StringBuilder regex = new StringBuilder();
    int lastMatchEnd = 0;
    boolean foundParameter = false;

    Matcher parametersMatcher = OAS_PATH_PARAMETERS_PATTERN.matcher(oasPath);
    while (parametersMatcher.find()) {
      addConstantFragment(regex, oasPath, lastMatchEnd, parametersMatcher.start());
      lastMatchEnd = parametersMatcher.end();

      final String paramName = parametersMatcher.group(1);
      addVariableFragment(regex, paramName);
      foundParameter = true;
    }

    if (foundParameter) {
      addConstantFragment(regex, oasPath, lastMatchEnd, oasPath.length());

      if (addEndString) {
        regex.append("$");
      }

      return Pattern.compile(regex.toString());
    }

    return null;
  }

  private void addVariableFragment(StringBuilder regex, String paramName) {
    String reg = "(?<" + paramName + ">[^\\/]*)";
    regex.append(reg);
  }

  private void addConstantFragment(StringBuilder regex,
                                   String oasPath,
                                   int beginIndex,
                                   int endIndex) {

    String toQuote = oasPath.substring(beginIndex, endIndex);
    if (toQuote.length() != 0) {
      regex.append(Pattern.quote(toQuote));
    }
  }
}
