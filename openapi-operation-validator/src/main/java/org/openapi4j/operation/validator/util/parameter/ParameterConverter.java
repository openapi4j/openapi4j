package org.openapi4j.operation.validator.util.parameter;

import com.fasterxml.jackson.databind.JsonNode;
import org.openapi4j.core.exception.ResolutionException;
import org.openapi4j.parser.model.v3.Parameter;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This utility class handles the conversion for the following parameter styles and types :
 * <p>
 * * : for default style/explode combination.
 * <p>
 * -- PATH :
 * ---------------------------------------------------------------------------------------------------------------------------------------------
 * | style    | explode | URI template  | Primitive value id = 5 | Array id = [3, 4, 5]   | Object id = {"role": "admin", "firstName": "Alex"} |
 * |----------|---------|---------------|------------------------|------------------------|----------------------------------------------------|
 * | simple * | false * | /users/{id}   | /users/5               | /users/3,4,5           | /users/role,admin,firstName,Alex                   |
 * | simple   | true    | /users/{id*}  | /users/5               | /users/3,4,5           | /users/role=admin,firstName=Alex                   |
 * | label    | false   | /users/{.id}  | /users/.5              | /users/.3,4,5          | /users/.role,admin,firstName,Alex                  |
 * | label    | true    | /users/{.id*} | /users/.5              | /users/.3.4.5          | /users/.role=admin.firstName=Alex                  |
 * | matrix   | false   | /users/{;id}  | /users/;id=5           | /users/;id=3,4,5       | /users/;id=role,admin,firstName,Alex               |
 * | matrix   | true    | /users/{;id*} | /users/;id=5           | /users/;id=3;id=4;id=5 | /users/;role=admin;firstName=Alex                  |
 * ---------------------------------------------------------------------------------------------------------------------------------------------
 * <p>
 * -- QUERY :
 * -------------------------------------------------------------------------------------------------------------------------------------------------
 * | style          | explode | URI template | Primitive value id = 5 | Array id = [3, 4, 5]  | Object id = {"role": "admin", "firstName": "Alex"} |
 * |----------------|---------|--------------|------------------------|-----------------------|----------------------------------------------------|
 * | form *         | true *  | /users{?id*} | /users?id=5            | /users?id=3&id=4&id=5 | /users?role=admin&firstName=Alex                   |
 * | form           | false   | /users{?id}  | /users?id=5            | /users?id=3,4,5       | /users?id=role,admin,firstName,Alex                |
 * | spaceDelimited | true    | /users{?id*} | n/a                    | /users?id=3&id=4&id=5 | n/a                                                |
 * | spaceDelimited | false   | n/a          | n/a                    | /users?id=3%204%205   | n/a                                                |
 * | pipeDelimited  | true    | /users{?id*} | n/a                    | /users?id=3&id=4&id=5 | n/a                                                |
 * | pipeDelimited  | false   | n/a          | n/a                    | /users?id=34|5|       | n/a                                                |
 * | deepObject     | true    | n/a          | n/a                    | n/a                   | /users?id[role]=admin&id[firstName]=Alex           |
 * -------------------------------------------------------------------------------------------------------------------------------------------------
 * <p>
 * -- HEADER :
 * -----------------------------------------------------------------------------------------------------------------------------
 * | style    | explode | URI template | value = 5     | Array = [3, 4, 5]  | Object = {"role": "admin", "firstName": "Alex"}  |
 * |----------|---------|--------------|---------------|--------------------|--------------------------------------------------|
 * | simple * | false * | {id}         | X-MyHeader: 5 | X-MyHeader: 3,4,5  | X-MyHeader: role,admin,firstName,Alex            |
 * | simple   | true    | {id*}        | X-MyHeader: 5 | X-MyHeader: 3,4,5  | X-MyHeader: role=admin,firstName=Alex            |
 * -----------------------------------------------------------------------------------------------------------------------------
 * <p>
 * -- COOKIE :
 * --------------------------------------------------------------------------------------------------------------------------------------------
 * | style  | explode | URI template | Primitive value id = 5 | Array id = [3, 4, 5]     | Object id = {"role": "admin", "firstName": "Alex"} |
 * |--------|---------|--------------|------------------------|--------------------------|----------------------------------------------------|
 * | form * | true *  | Cookie: id=5 | Cookie: id=5           | n/a                      | n/a                                                |
 * | form   | false   | id={id}      | Cookie: id=5           | Cookie: id=3,4,5         | Cookie: id=role,admin,firstName,Alex               |
 * --------------------------------------------------------------------------------------------------------------------------------------------
 * <p>
 * <p>
 * <p>
 * ---------------------------------------------------------
 * RFC 3986 section 2.2 Reserved Characters (January 2005)
 * !	*	'	(	)	;	:	@	&	=	+	$	,	/	?	#	[	]
 * ---------------------------------------------------------
 * <p>
 * <p>
 * <p>
 */
public final class ParameterConverter {
  private static final String LABEL = "label";
  private static final String MATRIX = "matrix";
  private static final String SPACEDELIMITED = "spaceDelimited";
  private static final String PIPEDELIMITED = "pipeDelimited";
  private static final String DEEPOBJECT = "deepObject";

  private ParameterConverter() {}

  /**
   * Convert path parameters to nodes.
   *
   * @param specPath       The path from specification.
   * @param path           The rendered path from the request.
   * @param pathParameters The path parameters rom specification.
   * @return A map with parameters names associated with the value as node
   */
  public static Map<String, JsonNode> pathToNode(String specPath, String path, Set<Parameter> pathParameters) {
    String[] specPathFragments = specPath.trim().split("/");
    String[] pathFragments = path.trim().split("/");
    // Align the paths as the spec path could be prefixed by server definition
    if (pathFragments.length > specPathFragments.length) {
      pathFragments = Arrays.copyOfRange(pathFragments, pathFragments.length - specPathFragments.length, pathFragments.length);
    }

    Map<String, JsonNode> paramValues = new HashMap<>();

    for (Parameter param : pathParameters) {
      // Get {paramName}
      String paramName = param.getName();
      Pattern pattern = Pattern.compile("(?:\\{.*)(" + paramName + ")(?:.*})");
      for (int i = 0; i < specPathFragments.length; i++) {
        Matcher matcher = pattern.matcher(specPathFragments[i]);
        if (matcher.matches()) {
          final String style = param.getStyle();

          if (LABEL.equals(style)) {
            paramValues.put(paramName, LabelStyleConverter.instance().convert(param, pathFragments[i]));
          } else if (MATRIX.equals(style)) {
            paramValues.put(paramName, MatrixStyleConverter.instance().convert(param, pathFragments[i]));
          } else { // simple is the default
            paramValues.put(paramName, SimpleStyleConverter.instance().convert(param, pathFragments[i]));
          }
          break;
        }
      }
    }

    return paramValues;
  }

  /**
   * Convert query parameters to nodes.
   * The query string MUST BE in the appropriate form corresponding to the associated style.
   *
   * @param rawValue        The raw query string.
   * @param queryParameters The spec query parameters.
   * @return A map with parameters names associated with the value as node.
   */
  public static Map<String, JsonNode> queryToNode(String rawValue, Set<Parameter> queryParameters) throws ResolutionException {
    Map<String, JsonNode> values = new HashMap<>();

    for (Parameter param : queryParameters) {
      final String style = param.getStyle();

      if (SPACEDELIMITED.equals(style)) {
        values.put(param.getName(), SpaceDelimitedStyleConverter.instance().convert(param, rawValue));
      } else if (PIPEDELIMITED.equals(style)) {
        values.put(param.getName(), PipeDelimitedStyleConverter.instance().convert(param, rawValue));
      } else if (DEEPOBJECT.equals(style)) {
        values.put(param.getName(), DeepObjectStyleConverter.instance().convert(param, rawValue));
      } else { // form is the default
        if (param.getExplode() == null) { // explode true is default
          param.setExplode(true);
        }
        values.put(param.getName(), FormStyleConverter.instance().convert(param, rawValue));
      }
    }

    return values;
  }

  /**
   * Convert header parameters to nodes.
   *
   * @param headers          The headers.
   * @param headerParameters The spec header parameters.
   * @return A map with parameters names associated with the value as node.
   */
  public static Map<String, JsonNode> headersToNode(Map<String, Collection<String>> headers, Set<Parameter> headerParameters) {
    Map<String, JsonNode> values = new HashMap<>();

    for (Parameter param : headerParameters) {
      Collection<String> headerValues = headers.get(param.getName());
      if (headerValues == null) {
        headerValues = new ArrayList<>();
      }
      values.put(param.getName(), SimpleStyleConverter.instance().convert(param, String.join(",", headerValues)));
    }

    return values;
  }

  /**
   * Convert cookie parameters to nodes.
   *
   * @param cookies          The cookies.
   * @param cookieParameters The spec cookie parameters.
   * @return A map with parameters names associated with the value as node.
   */
  public static Map<String, JsonNode> cookiesToNode(Map<String, String> cookies, Set<Parameter> cookieParameters) {
    Map<String, JsonNode> values = new HashMap<>();

    for (Parameter param : cookieParameters) {
      if (param.getExplode() == null) { // explode true is default
        param.setExplode(true);
      }
      // We choose here the simple style because of parsed input
      // Should be form if we had a raw value
      values.put(param.getName(), SimpleStyleConverter.instance().convert(param, cookies.get(param.getName())));
    }

    return values;
  }
}
