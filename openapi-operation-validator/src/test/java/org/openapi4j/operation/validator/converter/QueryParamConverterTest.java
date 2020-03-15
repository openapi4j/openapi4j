package org.openapi4j.operation.validator.converter;

import com.fasterxml.jackson.databind.JsonNode;

import org.junit.Test;
import org.openapi4j.operation.validator.OpenApi3Util;
import org.openapi4j.operation.validator.util.parameter.ParameterConverter;
import org.openapi4j.parser.model.v3.AbsParameter;
import org.openapi4j.parser.model.v3.OpenApi3;
import org.openapi4j.parser.model.v3.Parameter;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

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
      ParamChecker::checkPrimitive,
      ParamChecker::checkWrongPrimitive);
  }

  @Test
  public void queryFormExplodedPrimitive() throws Exception {
    check(
      "formExplodedPrimitive",
      "formExplodedPrimitive=5",
      "formExplodedPrimitive=wrong",
      ParamChecker::checkPrimitive,
      ParamChecker::checkWrongPrimitive);
  }

  @Test
  public void queryFormNotExplodedArray() throws Exception {
    check(
      "formNotExplodedArray",
      "formNotExplodedArray=3,4,5",
      "formNotExplodedArray=wrong",
      ParamChecker::checkArray,
      ParamChecker::checkWrongArray);
  }

  @Test
  public void queryFormExplodedArray() throws Exception {
    check(
      "formExplodedArray",
      "formExplodedArray=3&formExplodedArray=4&formExplodedArray=5",
      "formExplodedArray=wrong",
      ParamChecker::checkArray,
      ParamChecker::checkWrongArray);
  }

  @Test
  public void queryFormNotExplodedObject() throws Exception {
    check(
      "formNotExplodedObject",
      "formNotExplodedObject=boolProp,true,stringProp,admin",
      "formNotExplodedObject=boolProp,wrong",
      ParamChecker::checkObject,
      ParamChecker::checkWrongObject);
  }

  @Test
  public void queryFormExplodedObject() throws Exception {
    check(
      "formExplodedObject",
      "boolProp=true&stringProp=admin",
      "boolProp=wrong",
      ParamChecker::checkObject,
      ParamChecker::checkWrongObject);
  }

  // --------------- SPACE DELIMITED -------------------
  // ---------------------------------------------------
  @Test
  public void querySpaceNotExplodedArray() throws Exception {
    check(
      "spaceNotExplodedArray",
      "spaceNotExplodedArray=3%204%205",
      "spaceNotExplodedArray=wrong",
      ParamChecker::checkArray,
      ParamChecker::checkWrongArray);
  }

  @Test
  public void querySpaceExplodedArray() throws Exception {
    check(
      "spaceExplodedArray",
      "spaceExplodedArray=3&spaceExplodedArray=4&spaceExplodedArray=5",
      "spaceExplodedArray=wrong",
      ParamChecker::checkArray,
      ParamChecker::checkWrongArray);
  }

  // --------------- PIPE DELIMITED -------------------
  // --------------------------------------------------
  @Test
  public void queryPipeNotExplodedArray() throws Exception {
    check(
      "pipeNotExplodedArray",
      "pipeNotExplodedArray=3|4|5",
      "pipeNotExplodedArray=wrong",
      ParamChecker::checkArray,
      ParamChecker::checkWrongArray);
  }

  @Test
  public void queryPipeExplodedArray() throws Exception {
    check(
      "pipeExplodedArray",
      "pipeExplodedArray=3&pipeExplodedArray=4&pipeExplodedArray=5",
      "pipeExplodedArray=wrong",
      ParamChecker::checkArray,
      ParamChecker::checkWrongArray);
  }

  // --------------- DEEP OBJECT -------------------
  // -----------------------------------------------
  @Test
  public void queryDeepExplodedObject() throws Exception {
    check(
      "deepExplodedObject",
      "deepExplodedObject[boolProp]=true&deepExplodedObject[stringProp]=admin",
      "deepExplodedObject[boolProp]=wrong",
      ParamChecker::checkObject,
      ParamChecker::checkWrongObject);
  }

  @Test
  public void queryContentObject() throws Exception {
    check(
      "content",
      "{\"boolProp\":true,\"stringProp\":\"admin\"}",
      "{\"boolProp\":\"wrong\"}",
      ParamChecker::checkObject,
      ParamChecker::checkWrongObject);
  }

  protected void check(String parameterName,
                       String validValue,
                       String invalidValue,
                       BiConsumer<Map<String, JsonNode>, String> validChecker,
                       BiConsumer<Map<String, JsonNode>, String> invalidChecker) throws Exception {

    OpenApi3 api = OpenApi3Util.loadApi("/operation/parameter/queryParameters.yaml");

    Map<String, AbsParameter<Parameter>> parameters = new HashMap<>();
    parameters.put(parameterName, api.getComponents().getParameters().get(parameterName));

    // Valid check
    validChecker.accept(mapToNodes(parameters, validValue), parameterName);
    // Invalid check
    invalidChecker.accept(mapToNodes(parameters, invalidValue), parameterName);

    // null
    assertNull(mapToNodes(parameters, null).get(parameterName));
  }

  private Map<String, JsonNode> mapToNodes(Map<String, AbsParameter<Parameter>> parameters, String values) {
    return ParameterConverter.formDataToNode(parameters, values);
  }
}
