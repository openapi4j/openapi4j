package org.openapi4j.operation.validator.util;

import org.openapi4j.core.model.OAIContext;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.EnumSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PathResolver {
  public enum Anchor {
    NONE,
    START_STRING,
    END_STRING
  }

  private static final Pattern OAS_PATH_PARAMETERS_PATTERN = Pattern.compile("\\{[.;?*+]*([^{}.;?*+]+)[^}]*}");
  private static final Pattern ABSOLUTE_URL_PATTERN = Pattern.compile("\\A[a-z0-9.+-]+://.*", Pattern.CASE_INSENSITIVE);

  private static final PathResolver INSTANCE = new PathResolver();

  private PathResolver() {
  }

  public static PathResolver instance() {
    return INSTANCE;
  }

  /**
   * This method returns a pattern only if a pattern is needed, otherwise it returns {@code null}.
   * This will not add any anchor to the regular expression.
   *
   * @param oasPath The OAS path to build.
   * @return a pattern only if a pattern is needed.
   */
  public Pattern solve(String oasPath) {
    return solve(oasPath, EnumSet.of(Anchor.NONE));
  }

  /**
   * This method returns a pattern only if a pattern is needed, otherwise it returns {@code null}.
   *
   * @param oasPath The OAS path to build.
   * @param anchors Anchor options to add to the regular expression.
   * @return a pattern only if a pattern is needed.
   */
  public Pattern solve(String oasPath, EnumSet<Anchor> anchors) {
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

      if (anchors.contains(Anchor.START_STRING)) {
        regex.insert(0, "^");
      }
      if (anchors.contains(Anchor.END_STRING)) {
        regex.append("$");
      }

      return Pattern.compile(regex.toString());
    }

    return null;
  }

  /**
   * This method returns a pattern for a non templated path.
   *
   * @param oasPath The OAS path to build.
   * @param anchors Anchor options to add to the regular expression.
   * @return a pattern only if a pattern is needed.
   */
  public Pattern solveFixedPath(String oasPath, EnumSet<Anchor> anchors) {
    final StringBuilder regex = new StringBuilder(Pattern.quote(oasPath));

    if (anchors.contains(Anchor.START_STRING)) {
      regex.insert(0, "^");
    }
    if (anchors.contains(Anchor.END_STRING)) {
      regex.append("$");
    }

    return Pattern.compile(regex.toString());
  }

  public String getResolvedPath(OAIContext context, String url) {
    // server URL may be relative to the location where the OpenAPI document is being served.
    // https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.2.md#server-object
    try {
      if (isAbsoluteUrl(url)) {
        return new URL(url).getPath();
      } else {
        // Check if there's a defined file name in URL
        URL resource = context.getBaseUri().toURL();
        // trim query & anchor
        String basePath = resource.toString().split("\\?")[0].split("#")[0];
        // handle scheme://api.com
        String host = resource.getHost();
        if (host.length() > 0 && basePath.endsWith(host)) {
          return "/";
        }

        // Get last path fragment (maybe file name)
        String lastFragment = basePath.substring(basePath.lastIndexOf('/') + 1);

        // remove filename from URL
        if (lastFragment.contains(".")) {
          basePath = basePath.substring(0, basePath.indexOf(lastFragment));
        }

        return new URL(new URL(basePath), url).getPath();
      }
    } catch (MalformedURLException e) {
      return "/";
    }
  }

  /**
   * Decides if a URL is absolute based on whether it contains a valid scheme name, as
   * defined in RFC 1738.
   */
  private boolean isAbsoluteUrl(String url) {
    return ABSOLUTE_URL_PATTERN.matcher(url).matches();
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
