package org.openapi4j.operation.validator.util;

import org.openapi4j.core.model.OAIContext;
import org.openapi4j.parser.model.v3.Server;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.openapi4j.operation.validator.util.PathResolver.Options.END_STRING;
import static org.openapi4j.operation.validator.util.PathResolver.Options.RETURN_FIXED_PATTERN;
import static org.openapi4j.operation.validator.util.PathResolver.Options.START_STRING;

public class PathResolver {
  public enum Options {
    NONE,
    START_STRING,
    END_STRING,
    RETURN_FIXED_PATTERN
  }

  private static final Pattern OAS_PATH_PARAMETERS_PATTERN = Pattern.compile("\\{[.;?*+]*([^{}.;?*+]+)[^}]*}");
  private static final Pattern ABSOLUTE_URL_PATTERN = Pattern.compile("\\A[a-z0-9.+-]+://.*", Pattern.CASE_INSENSITIVE);
  private static final String START_STRING_ANCHOR = "^";
  private static final String END_STRING_ANCHOR = "$";
  private static final String START_PARAM_NAMED_GROUP = "(?<";
  private static final String END_PARAM_NAMED_GROUP = ">[^\\/]*)";

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
   * @param templatePath The template path to build the regular expression from.
   * @return Pattern only if a pattern is needed.
   */
  public Pattern solve(String templatePath) {
    return solve(templatePath, EnumSet.of(Options.NONE));
  }

  /**
   * This method returns a pattern for the given path.
   * You need to give {@code RETURN_FIXED_PATTERN} in case of no parameter has been found on path.
   *
   * @param templatePath The template path to build the regular expression from.
   * @param options      Options for the regular expression build.
   * @return Pattern.
   */
  public Pattern solve(String templatePath, Set<Options> options) {
    final StringBuilder regex = new StringBuilder();
    int lastMatchEnd = 0;
    boolean foundParameter = false;

    Matcher parametersMatcher = OAS_PATH_PARAMETERS_PATTERN.matcher(templatePath);
    while (parametersMatcher.find()) {
      addConstantFragment(regex, templatePath, lastMatchEnd, parametersMatcher.start());
      lastMatchEnd = parametersMatcher.end();

      final String paramName = parametersMatcher.group(1);
      addVariableFragment(regex, paramName);
      foundParameter = true;
    }

    if (foundParameter) {
      addConstantFragment(regex, templatePath, lastMatchEnd, templatePath.length());
      setupAnchors(regex, options);

      return Pattern.compile(regex.toString());

    } else if (options.contains(Options.RETURN_FIXED_PATTERN)) {
      regex.append(Pattern.quote(templatePath));
      setupAnchors(regex, options);

      return Pattern.compile(regex.toString());
    }

    return null;
  }

  /**
   * Resolves the given URL path with the context of the Document if URL is relative.
   *
   * @param context The context of the Document
   * @param url     The URL to resolve.
   * @return The resolved URL.
   */
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

  public List<Pattern> buildPathPatterns(OAIContext context, List<Server> servers, String templatePath) {
    List<Pattern> patterns = new ArrayList<>();

    if (servers == null) {
      patterns.add(buildPathPattern("", templatePath));
    } else {
      for (Server server : servers) {
        Pattern pattern = buildPathPattern(getResolvedPath(context, server.getUrl()), templatePath);
        patterns.add(pattern);
      }
    }

    return patterns;
  }

  public Pattern findPathPattern(Collection<Pattern> pathPatterns, String requestPath) {
    if (requestPath == null || requestPath.isEmpty()) {
      requestPath = "/";
    }

    // Match path pattern
    for (Pattern pathPattern : pathPatterns) {
      Matcher matcher = pathPattern.matcher(requestPath);
      if (matcher.matches()) {
        return pathPattern;
      }
    }

    return null;
  }

  private Pattern buildPathPattern(String basePath, String templatePath) {
    return solve(
      basePath + templatePath,
      EnumSet.of(START_STRING, END_STRING, RETURN_FIXED_PATTERN));
  }

  /**
   * Append anchors, if any, to the regular expression.
   *
   * @param regex   The given regular expression.
   * @param options The anchors to append.
   */
  private void setupAnchors(StringBuilder regex, Set<Options> options) {
    if (options.contains(Options.START_STRING)) {
      regex.insert(0, START_STRING_ANCHOR);
    }
    if (options.contains(Options.END_STRING)) {
      regex.append(END_STRING_ANCHOR);
    }
  }

  /**
   * Decides if a URL is absolute based on whether it contains a valid scheme name, as
   * defined in RFC 1738.
   */
  private boolean isAbsoluteUrl(String url) {
    return ABSOLUTE_URL_PATTERN.matcher(url).matches();
  }

  /**
   * Append named group to the regular expression.
   *
   * @param regex     The given regular expression.
   * @param paramName The parameter named.
   */
  private void addVariableFragment(StringBuilder regex, String paramName) {
    regex
      .append(START_PARAM_NAMED_GROUP)
      .append(paramName)
      .append(END_PARAM_NAMED_GROUP);
  }

  /**
   * Append constant section to the regular expression if found in {@code oasPath}.
   *
   * @param regex      The given regular expression.
   * @param oasPath    The OAS path to apply.
   * @param beginIndex Begin index.
   * @param endIndex   End index.
   */
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
