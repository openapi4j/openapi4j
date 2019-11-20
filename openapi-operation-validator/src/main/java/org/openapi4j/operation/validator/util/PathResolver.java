package org.openapi4j.operation.validator.util;

import org.openapi4j.core.exception.ResolutionException;
import org.openapi4j.parser.model.v3.Parameter;

import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PathResolver {
  private static final Pattern OAS_PATH_PARAMETERS_PATTERN = Pattern.compile("\\{[.;?*+]*([^{}.;?*+]+)[^}]*}");
  private static final Pattern ILLEGAL_PATH_MATCHER = Pattern.compile("\\{[^/]*/[^/]*}");

  private static final PathResolver INSTANCE = new PathResolver();

  private PathResolver() {
  }

  public static PathResolver instance() {
    return INSTANCE;
  }

  /**
   * This method returns a pattern only if a pattern is needed, otherwise it returns an empty optional
   *
   * @return a pattern only if a pattern is needed.
   */
  public Optional<String> solve(String oasPath, List<Parameter> pathParameters) throws ResolutionException {
    if (ILLEGAL_PATH_MATCHER.matcher(oasPath).matches())
      throw new ResolutionException("Path template not supported");

    if (pathParameters.isEmpty()) {
      return Optional.empty();
    }

    StringBuilder regex = new StringBuilder();
    int lastMatchEnd = 0;

    Matcher parametersMatcher = OAS_PATH_PARAMETERS_PATTERN.matcher(oasPath);
    while (parametersMatcher.find()) {
      // Append constant string
      addFixedFragment(regex, oasPath, lastMatchEnd, parametersMatcher.start());
      lastMatchEnd = parametersMatcher.end();

      String paramName = parametersMatcher.group(1);
      Optional<Parameter> optParameter = pathParameters.stream().filter(p -> p.getName().equals(paramName)).findFirst();
      if (!optParameter.isPresent()) {
        throw new ResolutionException("Missing parameter description for parameter name: " + paramName);
      }

      addVariableFragment(regex, paramName);
    }

    addFixedFragment(regex, oasPath, lastMatchEnd, oasPath.length());

    return Optional.of(regex.toString());
  }

  private void addVariableFragment(StringBuilder regex, String paramName) {
    String reg = "(?<" + paramName + ">[^\\/]*)";
    regex.append(reg);
  }

  private void addFixedFragment(StringBuilder regex,
                                String oasPath,
                                int beginIndex,
                                int endIndex) {

    String toQuote = oasPath.substring(beginIndex, endIndex);
    if (toQuote.length() != 0) {
      regex.append(Pattern.quote(toQuote));
    }
  }
}
