package org.openapi4j.operation.validator.util;

import org.openapi4j.core.exception.ResolutionException;
import org.openapi4j.parser.model.v3.Parameter;

import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PathResolver {
  private static final String PATH_PARAM_MISSING_ERR_MSG = "Missing path parameter description for : '%s'.";

  private static final Pattern OAS_PATH_PARAMETERS_PATTERN = Pattern.compile("\\{[.;?*+]*([^{}.;?*+]+)[^}]*}");

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
    if (pathParameters.isEmpty()) {
      return Optional.empty();
    }

    final StringBuilder regex = new StringBuilder();
    int lastMatchEnd = 0;

    Matcher parametersMatcher = OAS_PATH_PARAMETERS_PATTERN.matcher(oasPath);
    while (parametersMatcher.find()) {
      addConstantFragment(regex, oasPath, lastMatchEnd, parametersMatcher.start());
      lastMatchEnd = parametersMatcher.end();

      final String paramName = parametersMatcher.group(1);
      final Optional<Parameter> optParameter = pathParameters.stream().filter(p -> p.getName().equals(paramName)).findFirst();
      if (!optParameter.isPresent()) {
        throw new ResolutionException(String.format(PATH_PARAM_MISSING_ERR_MSG, paramName));
      }

      addVariableFragment(regex, paramName);
    }

    addConstantFragment(regex, oasPath, lastMatchEnd, oasPath.length());

    return Optional.of(regex.toString());
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
