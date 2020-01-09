package org.openapi4j.operation.validator.converter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;

import org.junit.Test;
import org.openapi4j.operation.validator.OpenApi3Util;
import org.openapi4j.operation.validator.util.parameter.ParameterConverter;
import org.openapi4j.parser.model.v3.AbsParameter;
import org.openapi4j.parser.model.v3.OpenApi3;
import org.openapi4j.parser.model.v3.Parameter;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class HeaderParamConverterTest {
  @Test
  public void headerSimpleNotExplodedPrimitive() throws Exception {
    check(
      "simpleNotExplodedPrimitive",
      Collections.singleton("5"),
      Collections.singleton("wrong"),
      ParamChecker::checkPrimitive);
  }

  @Test
  public void headerSimpleExplodedPrimitive() throws Exception {
    check(
      "simpleExplodedPrimitive",
      Collections.singleton("5"),
      Collections.singleton("wrong"),
      ParamChecker::checkPrimitive);
  }

  @Test
  public void headerSimpleNotExplodedArray() throws Exception {
    check(
      "simpleNotExplodedArray",
      Arrays.asList("3", "4", "5"),
      Collections.singleton("wrong"),
      ParamChecker::checkArray);
  }

  @Test
  public void headerSimpleExplodedArray() throws Exception {
    check(
      "simpleExplodedArray",
      Arrays.asList("3", "4", "5"),
      Collections.singleton("wrong"),
      ParamChecker::checkArray);
  }

  @Test
  public void headerSimpleNotExplodedObject() throws Exception {
    check(
      "simpleNotExplodedObject",
      Arrays.asList("boolProp", "true", "stringProp", "admin"),
      Collections.singleton("wrong"),
      ParamChecker::checkObject);
  }

  @Test
  public void headerSimpleExplodedObject() throws Exception {
    check(
      "simpleExplodedObject",
      Arrays.asList("boolProp=true", "stringProp=admin"),
      Collections.singleton("wrong"),
      ParamChecker::checkObject);
  }

  @Test
  public void headerContentObject() throws Exception {
    check(
      "content",
      Collections.singletonList("{\"boolProp\":true,\"stringProp\":\"admin\"}"),
      Collections.singleton("wrong"),
      ParamChecker::checkObject);
  }

  protected void check(String parameterName,
                       Collection<String> validValue,
                       Collection<String> invalidValue,
                       BiFunction<Map<String, JsonNode>, String, Boolean> validChecker) throws Exception {

    OpenApi3 api = OpenApi3Util.loadApi("/operation/parameter/headerParameters.yaml");

    Map<String, AbsParameter<Parameter>> parameters = new HashMap<>();
    parameters.put(parameterName, api.getComponents().getParameters().get(parameterName));

    // Valid check
    Map<String, Collection<String>> values = new HashMap<>();
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
      Map<String, JsonNode> nodes = mapToNodes(parameters, values);

      if (nodes.get(parameterName) != null) {
        assertEquals(
          JsonNodeFactory.instance.nullNode(),
          nodes.get(parameterName));
      }
    }

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

  private Map<String, JsonNode> mapToNodes(Map<String, AbsParameter<Parameter>> parameters, Map<String, Collection<String>> values) {
    return ParameterConverter.headersToNode(parameters, values);
  }
}
