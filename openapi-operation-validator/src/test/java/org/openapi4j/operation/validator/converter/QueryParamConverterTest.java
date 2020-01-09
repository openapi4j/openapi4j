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

public class QueryParamConverterTest {
  // --------------- FORM --------------------
  // -----------------------------------------
  @Test
  public void queryFormNotExplodedPrimitive() throws Exception {
    check(
      "formNotExplodedPrimitive",
      "formNotExplodedPrimitive=5",
      "formNotExplodedPrimitive=wrong",
      ParamChecker::checkPrimitive);
  }

  @Test
  public void queryFormExplodedPrimitive() throws Exception {
    check(
      "formExplodedPrimitive",
      "formExplodedPrimitive=5",
      "formExplodedPrimitive=wrong",
      ParamChecker::checkPrimitive);
  }

  @Test
  public void queryFormNotExplodedArray() throws Exception {
    check(
      "formNotExplodedArray",
      "formNotExplodedArray=3,4,5",
      "formNotExplodedArray=wrong",
      ParamChecker::checkArray);
  }

  @Test
  public void queryFormExplodedArray() throws Exception {
    check(
      "formExplodedArray",
      "formExplodedArray=3&formExplodedArray=4&formExplodedArray=5",
      "formExplodedArray=wrong",
      ParamChecker::checkArray);
  }

  @Test
  public void queryFormNotExplodedObject() throws Exception {
    check(
      "formNotExplodedObject",
      "formNotExplodedObject=boolProp,true,stringProp,admin",
      "formNotExplodedObject=wrong",
      ParamChecker::checkObject);
  }

  @Test
  public void queryFormExplodedObject() throws Exception {
    check(
      "formExplodedObject",
      "boolProp=true&stringProp=admin",
      "wrong",
      ParamChecker::checkObject);
  }

  // --------------- SPACE DELIMITED -------------------
  // ---------------------------------------------------
  @Test
  public void querySpaceNotExplodedArray() throws Exception {
    check(
      "spaceNotExplodedArray",
      "spaceNotExplodedArray=3%204%205",
      "spaceNotExplodedArray=wrong",
      ParamChecker::checkArray);
  }

  @Test
  public void querySpaceExplodedArray() throws Exception {
    check(
      "spaceExplodedArray",
      "spaceExplodedArray=3&spaceExplodedArray=4&spaceExplodedArray=5",
      "spaceExplodedArray=wrong",
      ParamChecker::checkArray);
  }

  // --------------- PIPE DELIMITED -------------------
  // --------------------------------------------------
  @Test
  public void queryPipeNotExplodedArray() throws Exception {
    check(
      "pipeNotExplodedArray",
      "pipeNotExplodedArray=3|4|5",
      "pipeNotExplodedArray=wrong",
      ParamChecker::checkArray);
  }

  @Test
  public void queryPipeExplodedArray() throws Exception {
    check(
      "pipeExplodedArray",
      "pipeExplodedArray=3&pipeExplodedArray=4&pipeExplodedArray=5",
      "pipeExplodedArray=wrong",
      ParamChecker::checkArray);
  }

  // --------------- DEEP OBJECT -------------------
  // -----------------------------------------------
  @Test
  public void queryDeepExplodedObject() throws Exception {
    check(
      "deepExplodedObject",
      "deepExplodedObject[boolProp]=true&deepExplodedObject[stringProp]=admin",
      "wrong",
      ParamChecker::checkObject);
  }

  @Test
  public void queryContentObject() throws Exception {
    check(
      "content",
      "{\"boolProp\":true,\"stringProp\":\"admin\"}",
      "wrong",
      ParamChecker::checkObject);
  }

  protected void check(String parameterName,
                       String validValue,
                       String invalidValue,
                       BiFunction<Map<String, JsonNode>, String, Boolean> validChecker) throws Exception {

    OpenApi3 api = OpenApi3Util.loadApi("/operation/parameter/queryParameters.yaml");

    Map<String, AbsParameter<Parameter>> parameters = new HashMap<>();
    parameters.put(parameterName, api.getComponents().getParameters().get(parameterName));

    // Valid check
    boolean isArray = validChecker.apply(mapToNodes(parameters, validValue), parameterName);

    // Invalid checks

    // wrong value
    if (isArray) {
      assertEquals(
        JsonNodeFactory.instance.arrayNode().add((JsonNode) null),
        mapToNodes(parameters, invalidValue).get(parameterName));
    } else {
      Map<String, JsonNode> nodes = mapToNodes(parameters, invalidValue);

      if (nodes.get(parameterName) != null) {
        assertEquals(
          JsonNodeFactory.instance.nullNode(),
          nodes.get(parameterName));
      }
    }

    // null
    assertNull(mapToNodes(parameters, null).get(parameterName));
  }

  private Map<String, JsonNode> mapToNodes(Map<String, AbsParameter<Parameter>> parameters, String values) {
    return ParameterConverter.queryToNode(parameters, values);
  }
}
