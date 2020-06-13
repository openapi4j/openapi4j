package org.openapi4j.operation.validator.convert;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.Test;
import org.openapi4j.core.model.OAIContext;
import org.openapi4j.operation.validator.OpenApi3Util;
import org.openapi4j.operation.validator.model.Request;
import org.openapi4j.operation.validator.model.impl.DefaultRequest;
import org.openapi4j.operation.validator.util.convert.ParameterConverter;
import org.openapi4j.operation.validator.validation.OperationValidator;
import org.openapi4j.parser.OpenApi3Parser;
import org.openapi4j.parser.model.v3.*;
import org.openapi4j.schema.validator.ValidationData;

import java.io.File;
import java.nio.file.Files;
import java.util.Collections;
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
      "content=%7B%22boolProp%22%3Atrue%2C%22stringProp%22%3A%22admin%22%7D",
      "content=%7B%22boolProp%22%3A%22wrong%22%7D",
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
    validChecker.accept(mapToNodes(api.getContext(), parameters, validValue), parameterName);
    // Invalid check
    invalidChecker.accept(mapToNodes(api.getContext(), parameters, invalidValue), parameterName);

    // null
    assertNull(mapToNodes(api.getContext(), parameters, null).get(parameterName));
  }

  private Map<String, JsonNode> mapToNodes(OAIContext context,
                                           Map<String, AbsParameter<Parameter>> parameters,
                                           String values) {
    return ParameterConverter.queryToNode(context, parameters, values, "UTF-8");
  }
}
