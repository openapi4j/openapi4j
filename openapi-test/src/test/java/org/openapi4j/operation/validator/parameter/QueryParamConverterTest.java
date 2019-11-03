package org.openapi4j.operation.validator.parameter;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.Test;
import org.openapi4j.operation.validator.OpenApi3Util;
import org.openapi4j.operation.validator.util.parameter.ParameterConverter;
import org.openapi4j.parser.model.v3.OpenApi3;
import org.openapi4j.parser.model.v3.Parameter;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.openapi4j.operation.validator.parameter.ParamChecker.*;

public class QueryParamConverterTest {
  // --------------- FORM --------------------
  // -----------------------------------------
  @Test
  public void queryFormNotExplodedPrimitive() throws Exception {
    Map<String, JsonNode> nodes = queryToNode("formNotExplodedPrimitive", "formNotExplodedPrimitive=5");
    checkPrimitive(nodes, "formNotExplodedPrimitive");
  }

  @Test
  public void queryFormExplodedPrimitive() throws Exception {
    Map<String, JsonNode> nodes = queryToNode("formExplodedPrimitive", "formExplodedPrimitive=5");
    checkPrimitive(nodes, "formExplodedPrimitive");
  }

  @Test
  public void queryFormNotExplodedArray() throws Exception {
    Map<String, JsonNode> nodes = queryToNode("formNotExplodedArray", "formNotExplodedArray=3,4,5");
    checkArray(nodes, "formNotExplodedArray");
  }

  @Test
  public void queryFormExplodedArray() throws Exception {
    Map<String, JsonNode> nodes = queryToNode("formExplodedArray", "formExplodedArray=3&formExplodedArray=4&formExplodedArray=5");
    checkArray(nodes, "formExplodedArray");
  }

  @Test
  public void queryFormNotExplodedObject() throws Exception {
    Map<String, JsonNode> nodes = queryToNode("formNotExplodedObject", "formNotExplodedObject=boolProp,true,stringProp,admin");
    checkObject(nodes, "formNotExplodedObject");
  }

  @Test
  public void queryFormExplodedObject() throws Exception {
    Map<String, JsonNode> nodes = queryToNode("formExplodedObject", "boolProp=true&stringProp=admin");
    checkObject(nodes, "formExplodedObject");
  }

  // --------------- SPACE DELIMITED -------------------
  // ---------------------------------------------------
  @Test
  public void querySpaceNotExplodedArray() throws Exception {
    Map<String, JsonNode> nodes = queryToNode("spaceNotExplodedArray", "spaceNotExplodedArray=3%204%205");
    checkArray(nodes, "spaceNotExplodedArray");
  }

  @Test
  public void querySpaceExplodedArray() throws Exception {
    Map<String, JsonNode> nodes = queryToNode("spaceExplodedArray", "spaceExplodedArray=3&spaceExplodedArray=4&spaceExplodedArray=5");
    checkArray(nodes, "spaceExplodedArray");
  }

  // --------------- PIPE DELIMITED -------------------
  // --------------------------------------------------
  @Test
  public void queryPipeNotExplodedArray() throws Exception {
    Map<String, JsonNode> nodes = queryToNode("pipeNotExplodedArray", "pipeNotExplodedArray=3|4|5");
    checkArray(nodes, "pipeNotExplodedArray");
  }

  @Test
  public void queryPipeExplodedArray() throws Exception {
    Map<String, JsonNode> nodes = queryToNode("pipeExplodedArray", "pipeExplodedArray=3&pipeExplodedArray=4&pipeExplodedArray=5");
    checkArray(nodes, "pipeExplodedArray");
  }

  // --------------- DEEP OBJECT -------------------
  // -----------------------------------------------
  @Test
  public void queryDeepExplodedObject() throws Exception {
    Map<String, JsonNode> nodes = queryToNode("deepExplodedObject", "deepExplodedObject[boolProp]=true&deepExplodedObject[stringProp]=admin");
    checkObject(nodes, "deepExplodedObject");
  }

  private Map<String, JsonNode> queryToNode(String parameterName, String value) throws Exception {
    OpenApi3 api = OpenApi3Util.loadApi("/operation/parameter/queryParameters.yaml");

    Set<Parameter> parameters = new HashSet<>();
    parameters.add(api.getComponents().getParameters().get(parameterName));

    return ParameterConverter.queryToNode(
      value,
      parameters);
  }
}
