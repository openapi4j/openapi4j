package org.openapi4j.operation.validator.util;

import org.openapi4j.core.validation.ValidationException;
import org.openapi4j.operation.validator.model.Request;
import org.openapi4j.parser.model.v3.Path;
import org.openapi4j.parser.model.v3.Server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class PathUtil {
  private PathUtil() {}

  public static List<Pattern> buildServerUrlPatterns(List<Server> servers) {
    if (servers == null || servers.size() == 0) {
      return null;
    }

    List<Pattern> patterns = new ArrayList<>();

    for (Server server : servers) {
      Pattern pattern = PathResolver.instance().solve(server.getUrl());
      if (pattern != null) {
        patterns.add(pattern);
      } else {
        // add full fixed URL
        patterns.add(Pattern.compile(Pattern.quote(server.getUrl())));
      }
    }

    return patterns.size() == 0 ? null : patterns;
  }

  public static Map<Pattern, Path> buildPathPatterns(Map<String, Path> paths) {
    Map<Pattern, Path> patterns = new HashMap<>();

    if (paths != null) {
      for (Map.Entry<String, Path> pathEntry : paths.entrySet()) {
        Pattern pattern = PathResolver.instance().solve(pathEntry.getKey());
        if (pattern != null) {
          patterns.put(pattern, pathEntry.getValue());
        }
      }
    }

    return patterns;
  }

  public static Path findPath(List<Pattern> serverUrlPatterns, Map<Pattern, Path> pathPatterns, Request request) throws ValidationException {
    String pathValue;

    // Match server URL pattern
    if (serverUrlPatterns != null) {
      String value = null;
      for (Pattern pattern : serverUrlPatterns) {
        Matcher matcher = pattern.matcher(request.getURL());
        if (matcher.find()) {
          String urlPath = findPathFromUrl(matcher.group(0));
          value = request.getPath().substring(urlPath.length());
          break;
        }
      }

      if (value == null) {
        throw new ValidationException(String.format("Server URL not found from URL: %s", request.getURL()));
      }

      pathValue = value;
    } else {
      pathValue = request.getPath();
    }

    // Match path pattern
    for (Map.Entry<Pattern, Path> pathPatternEntry : pathPatterns.entrySet()) {
      Matcher matcher = pathPatternEntry.getKey().matcher(pathValue);
      if (matcher.matches()) {
        return pathPatternEntry.getValue();
      }
    }

    throw new ValidationException(String.format("Operation path not found from URL: %s", pathValue));
  }

  public static String findPathFromUrl(String url) {
    int schemeEndIndex = url.indexOf("//");
    schemeEndIndex = schemeEndIndex == -1 ? 0 : schemeEndIndex + "//".length() + 1;

    int slashIndex = url.indexOf("/", schemeEndIndex);

    return url.substring(slashIndex);
  }
}
