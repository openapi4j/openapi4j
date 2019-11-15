package org.openapi4j.operation.validator.util.parameter;

import com.fasterxml.jackson.databind.JsonNode;

import org.openapi4j.core.exception.ResolutionException;
import org.openapi4j.parser.model.OpenApiSchema;
import org.openapi4j.parser.model.v3.AbsParameter;
import org.openapi4j.parser.model.v3.Parameter;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
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
  private static final String SPACE_DELIMITED = "spaceDelimited";
  private static final String PIPE_DELIMITED = "pipeDelimited";
  private static final String DEEP_OBJECT = "deepObject";

  private ParameterConverter() {
  }

  /**
   * Convert path parameters to nodes.
   *
   * @param specParameters The path parameters from specification.
   * @param path           The rendered path from the request.
   * @return A map with parameters names associated with the value as node.
   */
  public static Map<String, JsonNode> pathToNode(final Map<String, AbsParameter<Parameter>> specParameters,
                                                 final Pattern pattern,
                                                 final String path) {

    final Map<String, JsonNode> mappedValues = new HashMap<>();

    if (pattern == null) {
      return mappedValues;
    }

    final Matcher matcher = pattern.matcher(path);
    if (!matcher.matches()) {
      return mappedValues;
    }

    for (Map.Entry<String, AbsParameter<Parameter>> paramEntry : specParameters.entrySet()) {
      final String paramName = paramEntry.getKey();
      final AbsParameter<Parameter> param = paramEntry.getValue();
      final String style = param.getStyle();

      JsonNode convertedValue;
        if (LABEL.equals(style)) {
          convertedValue = LabelStyleConverter.instance().convert(param, paramName, matcher.group(paramName));
        } else if (MATRIX.equals(style)) {
          convertedValue = MatrixStyleConverter.instance().convert(param, paramName, matcher.group(paramName));
        } else { // simple is the default
          convertedValue = SimpleStyleConverter.instance().convert(param, paramName, matcher.group(paramName));
        }

      if (convertedValue != null) {
        mappedValues.put(paramName, convertedValue);
      }
    }

    return mappedValues;
  }

  /**
   * Convert query parameters to nodes.
   * The query string MUST BE in the appropriate form corresponding to the associated style.
   *
   * @param specParameters The spec query parameters.
   * @param rawValue       The raw query string.
   * @return A map with parameters names associated with the value as node.
   */
  public static Map<String, JsonNode> queryToNode(final Map<String, AbsParameter<Parameter>> specParameters,
                                                  final String rawValue) throws ResolutionException {

    final Map<String, JsonNode> mappedValues = new HashMap<>();

    for (Map.Entry<String, AbsParameter<Parameter>> paramEntry : specParameters.entrySet()) {
      final String paramName = paramEntry.getKey();
      final AbsParameter<Parameter> param = paramEntry.getValue();
      final String style = param.getStyle();

      JsonNode convertedValue;
      if (SPACE_DELIMITED.equals(style)) {
        convertedValue = SpaceDelimitedStyleConverter.instance().convert(param, paramName, rawValue);
      } else if (PIPE_DELIMITED.equals(style)) {
        convertedValue = PipeDelimitedStyleConverter.instance().convert(param, paramName, rawValue);
      } else if (DEEP_OBJECT.equals(style)) {
        convertedValue = DeepObjectStyleConverter.instance().convert(param, paramName, rawValue);
      } else { // form is the default
        if (param.getExplode() == null) { // explode true is default
          param.setExplode(true);
        }
        convertedValue = FormStyleConverter.instance().convert(param, paramName, rawValue);
      }

      if (convertedValue != null) {
        mappedValues.put(paramName, convertedValue);
      }
    }

    return mappedValues;
  }

  /**
   * Convert header parameters to nodes.
   *
   * @param headers        The headers.
   * @param specParameters The spec header parameters.
   * @return A map with parameters names associated with the value as node.
   */
  public static <M extends OpenApiSchema<M>> Map<String, JsonNode> headersToNode(final Map<String, AbsParameter<M>> specParameters,
                                                                                 final Map<String, Collection<String>> headers) {

    Map<String, JsonNode> mappedValues = new HashMap<>();

    for (Map.Entry<String, AbsParameter<M>> paramEntry : specParameters.entrySet()) {
      String paramName = paramEntry.getKey();

      Collection<String> headerValues = headers.get(paramName);
      if (headerValues != null) {
        mappedValues.put(paramName, SimpleStyleConverter.instance().convert(paramEntry.getValue(), paramName, String.join(",", headerValues)));
      }
    }

    return mappedValues;
  }

  /**
   * Convert cookie parameters to nodes.
   *
   * @param cookies        The cookies.
   * @param specParameters The spec cookie parameters.
   * @return A map with parameters names associated with the value as node.
   */
  public static Map<String, JsonNode> cookiesToNode(final Map<String, AbsParameter<Parameter>> specParameters,
                                                    final Map<String, String> cookies) {

    Map<String, JsonNode> mappedValues = new HashMap<>();

    for (Map.Entry<String, AbsParameter<Parameter>> paramEntry : specParameters.entrySet()) {
      final String paramName = paramEntry.getKey();
      final AbsParameter<Parameter> param = paramEntry.getValue();

      if (param.getExplode() == null) { // explode true is default
        param.setExplode(true);
      }

      String value = cookies.get(paramName);
      if (value != null) {
        // We use simple style. Cookies are already mapped with their keys.
        mappedValues.put(paramName, SimpleStyleConverter.instance().convert(param, paramName, value));
      }
    }

    return mappedValues;
  }
}
