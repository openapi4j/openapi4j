package org.openapi4j.operation.validator.converter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;

import org.junit.Test;
import org.openapi4j.operation.validator.OpenApi3Util;
import org.openapi4j.operation.validator.util.parameter.ParameterConverter;
import org.openapi4j.parser.model.v3.AbsParameter;
import org.openapi4j.parser.model.v3.OpenApi3;
import org.openapi4j.parser.model.v3.Parameter;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class CookieParamConverterTest {
  @Test
  public void cookieFormNotExplodedPrimitive() throws Exception {
    check("formNotExplodedPrimitive", "5", "wrong", ParamChecker::checkPrimitive);
  }

  @Test
  public void cookieFormExplodedPrimitive() throws Exception {
    check("formExplodedPrimitive", "5", "wrong", ParamChecker::checkPrimitive);
  }

  @Test
  public void cookieFormNotExplodedArray() throws Exception {
    check("formNotExplodedArray", "3,4,5", "wrong", ParamChecker::checkArray);
  }

  @Test
  public void cookieFormNotExplodedObject() throws Exception {
    check("formNotExplodedObject", "boolProp,true,stringProp,admin", "wrong", ParamChecker::checkObject);
  }

  @Test
  public void cookieContentObject() throws Exception {
    check("content", "{\"boolProp\":true,\"stringProp\":\"admin\"}", "wrong", ParamChecker::checkObject);
  }

  private void check(String parameterName,
                     String validValue,
                     String invalidValue,
                     BiFunction<Map<String, JsonNode>, String, Boolean> validChecker) throws Exception {

    OpenApi3 api = OpenApi3Util.loadApi("/operation/parameter/cookieParameters.yaml");

    Map<String, AbsParameter<Parameter>> parameters = new HashMap<>();
    parameters.put(parameterName, api.getComponents().getParameters().get(parameterName));

    // Valid check
    Map<String, String> values = new HashMap<>();
    values.put(parameterName, validValue);
    boolean isArray = validChecker.apply(mapToNodes(parameters, values), parameterName);

    // Invalid checks

    // wrong value
    values.put(parameterName, invalidValue);
    if (isArray) {
      assertEquals(
        JsonNodeFactory.instance.arrayNode().add(JsonNodeFactory.instance.nullNode()),
        mapToNodes(parameters, values).get(parameterName));
    } else {
      assertEquals(
        JsonNodeFactory.instance.nullNode(),
        mapToNodes(parameters, values).get(parameterName));
    }

    // null value
    values.put(parameterName, null);
    assertEquals(
      JsonNodeFactory.instance.nullNode(),
      mapToNodes(parameters, values).get(parameterName));

    // empty map
    values.clear();
    assertEquals(
      JsonNodeFactory.instance.nullNode(),
      mapToNodes(parameters, values).get(parameterName));

    // null map
    assertNull(mapToNodes(parameters, null).get(parameterName));
  }

  private Map<String, JsonNode> mapToNodes(Map<String, AbsParameter<Parameter>> parameters, Map<String, String> values) {
    return ParameterConverter.cookiesToNode(parameters, values);
  }
}
