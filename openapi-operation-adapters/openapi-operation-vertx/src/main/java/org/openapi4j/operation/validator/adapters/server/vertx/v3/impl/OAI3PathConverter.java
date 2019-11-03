package org.openapi4j.operation.validator.adapters.server.vertx.v3.impl;

import org.openapi4j.core.exception.ResolutionException;
import org.openapi4j.parser.model.v3.Parameter;
import org.openapi4j.parser.model.v3.Schema;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class OAI3PathConverter {
  private static final Pattern OAS_PATH_PARAMETERS_PATTERN = Pattern.compile("\\{{1}[.;?*+]*([^\\{\\}.;?*+]+)[^\\}]*\\}{1}");
  private static final Pattern ILLEGAL_PATH_MATCHER = Pattern.compile("\\{[^\\/]*\\/[^\\/]*\\}");

  private static final OAI3PathConverter INSTANCE = new OAI3PathConverter();

  private OAI3PathConverter() {
  }

  static OAI3PathConverter instance() {
    return INSTANCE;
  }

  /**
   * This method returns a pattern only if a pattern is needed, otherwise it returns an empty optional
   *
   * @return
   */
  Optional<Pattern> solve(String oasPath, List<Parameter> pathParameters) throws ResolutionException {
    if (ILLEGAL_PATH_MATCHER.matcher(oasPath).matches())
      throw new ResolutionException("Path template not supported");

    if (pathParameters.isEmpty()) {
      return Optional.empty();
    }

    // If there's a parameter with label style, the dot should be escaped to avoid conflicts
    boolean isDotReserved = hasParameterWithLabelStyle(pathParameters);

    StringBuilder regex = new StringBuilder();
    Map<String, String> mappedGroups = new HashMap<>();
    int lastMatchEnd = 0;
    MutableInt groupIndex = new MutableInt(0);

    Matcher parametersMatcher = OAS_PATH_PARAMETERS_PATTERN.matcher(oasPath);
    while (parametersMatcher.find()) {
      // Append constant string
      String toQuote = oasPath.substring(lastMatchEnd, parametersMatcher.start());
      if (toQuote.length() != 0) {
        regex.append(Pattern.quote(toQuote));
      }
      lastMatchEnd = parametersMatcher.end();

      String paramName = parametersMatcher.group(1);
      Optional<Parameter> optParameter = pathParameters.stream().filter(p -> p.getName().equals(paramName)).findFirst();
      if (!optParameter.isPresent()) {
        throw new ResolutionException("Missing parameter description for parameter name: " + paramName);
      }

      Parameter parameter = optParameter.get();
      String style = solveParamStyle(parameter);
      boolean explode = parameter.isExplode();
      boolean isObject = isObjectSchema(parameter.getSchema()) || isAllOfSchema(parameter.getSchema());
      boolean isArray = isArraySchema(parameter.getSchema());

      String groupName = "p" + groupIndex.increment();

      if (style.equals("simple")) {
        addSimpleParameter(paramName, regex, mappedGroups, groupName, isDotReserved);
      } else if (style.equals("label")) {
        addLabelParameter(paramName, parameter, regex, mappedGroups, groupName, groupIndex, explode, isObject);
      } else if (style.equals("matrix")) {
        addMatrixParameter(paramName, parameter, regex, mappedGroups, groupName, isDotReserved, groupIndex, explode, isObject, isArray);
      }
    }

    if (regex.length() == 0) {
      return Optional.empty();
    } else {
      boolean endSlash = oasPath.charAt(oasPath.length() - 1) == '/';

      String toAppendQuoted = oasPath.substring(lastMatchEnd, (endSlash) ? oasPath.length() - 1 : oasPath.length());
      if (toAppendQuoted.length() != 0)
        regex.append(Pattern.quote(toAppendQuoted));
      if (endSlash)
        regex.append("\\/");

      return Optional.of(Pattern.compile(regex.toString()));
    }
  }

  private void addSimpleParameter(String paramName,
                                  StringBuilder regex,
                                  Map<String, String> mappedGroups,
                                  String groupName,
                                  boolean isDotReserved) {

    String not = "[^" + escapeCharacters("!*'();@&+$/?#[]" + (isDotReserved ? "." : null)) + "]*";
    String reg = "(?<" + groupName + ">" + not + ")*";

    regex.append(reg);
    mappedGroups.put(groupName, paramName);
  }

  private void addLabelParameter(String paramName,
                                 Parameter parameter,
                                 StringBuilder regex,
                                 Map<String, String> mappedGroups,
                                 String groupName,
                                 MutableInt groupIndex,
                                 boolean explode,
                                 boolean isObject) throws ResolutionException {
    if (isObject && explode) {
      Map<String, Schema> properties = solveObjectSchema(parameter.getSchema());
      for (Map.Entry<String, Schema> entry : properties.entrySet()) {
        String not = "[^" + escapeCharacters("!*'();@&+$/?#[].=") + "]*";
        String param = "\\.?" + Pattern.quote(entry.getKey()) + "=";
        String group = "(?<" + groupName + ">" + not + ")";
        String reg = "(?>" + param + group + ")?";

        regex.append(reg);
        mappedGroups.put(groupName, entry.getKey());

        groupName = "p" + groupIndex.increment();
      }
    } else {
      String not = "[^" + escapeCharacters("!*'();@&=+$,/?#[]") + "]*";
      String reg = "\\.?(?<" + groupName + ">" + not + ")?";

      regex.append(reg);
      mappedGroups.put(groupName, paramName);
    }
  }

  private void addMatrixParameter(String paramName,
                                  Parameter parameter,
                                  StringBuilder regex,
                                  Map<String, String> mappedGroups,
                                  String groupName,
                                  boolean isDotReserved,
                                  MutableInt groupIndex,
                                  boolean explode,
                                  boolean isObject,
                                  boolean isArray) throws ResolutionException {
    if (isObject && explode) {
      Map<String, Schema> properties = solveObjectSchema(parameter.getSchema());
      for (Map.Entry<String, Schema> entry : properties.entrySet()) {
        String not = "[^" + escapeCharacters("!*'();@&=+$,/?#[]" + (isDotReserved ? "." : null)) + "]*";
        String param = "\\;" + Pattern.quote(entry.getKey()) + "=";
        String group = "(?<" + groupName + ">" + not + ")";
        String reg = "(?>" + param + group + ")?";

        regex.append(reg);
        mappedGroups.put(groupName, entry.getKey());

        groupName = "p" + groupIndex.increment();
      }
    } else if (isArray && explode) {
      String not = "[^" + escapeCharacters("!*'();@&=+$,/?#[]" + (isDotReserved ? "." : null)) + "]*";
      String param = ";" + Pattern.quote(paramName) + "=";
      String group = "(?" + param + not + ")+";
      String reg = "(?<" + groupName + ">" + group + ")";

      regex.append(reg);
      mappedGroups.put(groupName, paramName);
    } else {
      String not = "[^" + escapeCharacters("!*'();@&=+$/?#[]" + (isDotReserved ? "." : null)) + "]*";
      String param = ";" + Pattern.quote(paramName) + "=";
      String group = "(?<" + groupName + ">" + not + ")?";
      String reg = param + group;

      regex.append(reg);
      mappedGroups.put(groupName, paramName);
    }
  }

  private String solveParamStyle(Parameter parameter) {
    return (parameter.getStyle() != null) ? parameter.getStyle() : "simple";
  }

  private boolean hasParameterWithLabelStyle(List<Parameter> pathParameters) {
    return pathParameters.stream().map(this::solveParamStyle).anyMatch(s -> s.equals("label"));
  }

  private boolean isObjectSchema(Schema schema) {
    return schema != null && ("object".equals(schema.getType()) || schema.getProperties() != null);
  }

  private boolean isArraySchema(Schema schema) {
    return schema != null && ("array".equals(schema.getType()) || schema.getItemsSchema() != null);
  }

  private boolean isAllOfSchema(Schema schema) {
    if (schema == null) return false;
    return (schema.getAllOfSchemas() != null && !schema.getAllOfSchemas().isEmpty());
  }

  private Map<String, Schema> solveObjectSchema(Schema schema) throws ResolutionException {
    if (isAllOfSchema(schema)) {
      return solveAllOfSchemas(schema.getAllOfSchemas());
    } else {
      return schema.getProperties();
    }
  }

  private Map<String, Schema> solveAllOfSchemas(List<Schema> allOfSchemas) throws ResolutionException {
    Map<String, Schema> properties = new HashMap<>();
    for (Schema schema : allOfSchemas) {
      if (!isObjectSchema(schema))
        throw new ResolutionException("allOf only allows inner object types");
      for (Map.Entry<String, Schema> entry : schema.getProperties().entrySet()) {
        properties.put(entry.getKey(), entry.getValue());
      }
    }
    return properties;
  }

  private String escapeCharacters(String characters) {
    StringBuilder escapedChars = new StringBuilder();
    for (int i = 0; i < characters.length(); ++i) {
      escapedChars.append("\\").append(characters.charAt(i));
    }
    return escapedChars.toString();
  }

  private static class MutableInt {
    private int value;

    MutableInt(int value) {
      this.value = value;
    }

    int increment() {
      value++;
      return value;
    }
  }
}
