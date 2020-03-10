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
import java.util.function.BiConsumer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class CookieParamConverterTest {
  @Test
  public void cookieFormNotExplodedPrimitive() throws Exception {
    check("formNotExplodedPrimitive", "5", "wrong", ParamChecker::checkPrimitive, ParamChecker::checkWrongPrimitive);
  }

  @Test
  public void cookieFormExplodedPrimitive() throws Exception {
    check("formExplodedPrimitive", "5", "wrong", ParamChecker::checkPrimitive, ParamChecker::checkWrongPrimitive);
  }

  @Test
  public void cookieFormNotExplodedArray() throws Exception {
    check("formNotExplodedArray", "3,4,5", "wrong", ParamChecker::checkArray, ParamChecker::checkWrongArray);
  }

  @Test
  public void cookieFormNotExplodedObject() throws Exception {
    check("formNotExplodedObject", "boolProp,true,stringProp,admin", "boolProp,wrong", ParamChecker::checkObject, ParamChecker::checkWrongObject);
  }

  @Test
  public void cookieContentObject() throws Exception {
    check("content", "{\"boolProp\":true,\"stringProp\":\"admin\"}", "{\"boolProp\":\"wrong\"}", ParamChecker::checkObject, ParamChecker::checkWrongObject);
  }

  private void check(String parameterName,
                     String validValue,
                     String invalidValue,
                     BiConsumer<Map<String, JsonNode>, String> validChecker,
                     BiConsumer<Map<String, JsonNode>, String> invalidChecker) throws Exception {

    OpenApi3 api = OpenApi3Util.loadApi("/operation/parameter/cookieParameters.yaml");

    Map<String, AbsParameter<Parameter>> parameters = new HashMap<>();
    parameters.put(parameterName, api.getComponents().getParameters().get(parameterName));

    // Valid check
    Map<String, String> values = new HashMap<>();
    values.put(parameterName, validValue);
    validChecker.accept(mapToNodes(parameters, values), parameterName);
    // Invalid check
    values.put(parameterName, invalidValue);
    invalidChecker.accept(mapToNodes(parameters, values), parameterName);

    // null value
    values.put(parameterName, null);
    assertEquals(JsonNodeFactory.instance.nullNode(), mapToNodes(parameters, values).get(parameterName));

    // unlinked param/value
    // empty map
    values.clear();
    assertNull(mapToNodes(parameters, values).get(parameterName));
    // null map
    assertNull(mapToNodes(parameters, null).get(parameterName));
  }

  private Map<String, JsonNode> mapToNodes(Map<String, AbsParameter<Parameter>> parameters, Map<String, String> values) {
    return ParameterConverter.cookiesToNode(parameters, values);
  }
}
