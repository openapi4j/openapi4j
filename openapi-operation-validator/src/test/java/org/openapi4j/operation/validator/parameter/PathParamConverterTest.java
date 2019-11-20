package org.openapi4j.operation.validator.parameter;

import com.fasterxml.jackson.databind.JsonNode;

import org.junit.Test;
import org.openapi4j.operation.validator.OpenApi3Util;
import org.openapi4j.operation.validator.util.PathResolver;
import org.openapi4j.operation.validator.util.parameter.ParameterConverter;
import org.openapi4j.parser.model.v3.AbsParameter;
import org.openapi4j.parser.model.v3.OpenApi3;
import org.openapi4j.parser.model.v3.Parameter;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class PathParamConverterTest {
  @Test
  public void pathSimpleNotExplodedPrimitive() throws Exception {
    Map<String, JsonNode> nodes = pathToNode("simpleNotExplodedPrimitive", "5");
    checkPrimitive(nodes, "simpleNotExplodedPrimitive");
  }

  @Test
  public void pathSimpleExplodedPrimitive() throws Exception {
    Map<String, JsonNode> nodes = pathToNode("simpleExplodedPrimitive", "5");
    checkPrimitive(nodes, "simpleExplodedPrimitive");
  }

  @Test
  public void pathSimpleNotExplodedArray() throws Exception {
    Map<String, JsonNode> nodes = pathToNode("simpleNotExplodedArray", "3,4,5");
    checkArray(nodes, "simpleNotExplodedArray");
  }

  @Test
  public void pathSimpleExplodedArray() throws Exception {
    Map<String, JsonNode> nodes = pathToNode("simpleExplodedArray", "3,4,5");
    checkArray(nodes, "simpleExplodedArray");
  }

  @Test
  public void pathSimpleNotExplodedObject() throws Exception {
    Map<String, JsonNode> nodes = pathToNode("simpleNotExplodedObject", "boolProp,true,stringProp,admin");
    checkObject(nodes, "simpleNotExplodedObject");
  }

  @Test
  public void pathSimpleExplodedObject() throws Exception {
    Map<String, JsonNode> nodes = pathToNode("simpleExplodedObject", "boolProp=true,stringProp=admin");
    checkObject(nodes, "simpleExplodedObject");
  }

  // --------------- LABEL -------------------
  // -----------------------------------------
  @Test
  public void pathLabelNotExplodedPrimitive() throws Exception {
    Map<String, JsonNode> nodes = pathToNode("labelNotExplodedPrimitive", ".5");
    checkPrimitive(nodes, "labelNotExplodedPrimitive");
  }

  @Test
  public void pathLabelExplodedPrimitive() throws Exception {
    Map<String, JsonNode> nodes = pathToNode("labelExplodedPrimitive", ".5");
    checkPrimitive(nodes, "labelExplodedPrimitive");
  }

  @Test
  public void pathLabelNotExplodedArray() throws Exception {
    Map<String, JsonNode> nodes = pathToNode("labelNotExplodedArray", ".3,4,5");
    checkArray(nodes, "labelNotExplodedArray");
  }

  @Test
  public void pathLabelExplodedArray() throws Exception {
    Map<String, JsonNode> nodes = pathToNode("labelExplodedArray", ".3.4.5");
    checkArray(nodes, "labelExplodedArray");
  }

  @Test
  public void pathLabelNotExplodedObject() throws Exception {
    Map<String, JsonNode> nodes = pathToNode("labelNotExplodedObject", ".boolProp,true,stringProp,admin");
    checkObject(nodes, "labelNotExplodedObject");
  }

  @Test
  public void pathLabelExplodedObject() throws Exception {
    Map<String, JsonNode> nodes = pathToNode("labelExplodedObject", ".boolProp=true.stringProp=admin");
    checkObject(nodes, "labelExplodedObject");
  }

  // --------------- MATRIX -------------------
  // -----------------------------------------
  @Test
  public void pathMatrixNotExplodedPrimitive() throws Exception {
    Map<String, JsonNode> nodes = pathToNode("matrixNotExplodedPrimitive", ";matrixNotExplodedPrimitive=5");
    checkPrimitive(nodes, "matrixNotExplodedPrimitive");
  }

  @Test
  public void pathMatrixExplodedPrimitive() throws Exception {
    Map<String, JsonNode> nodes = pathToNode("matrixExplodedPrimitive", ";matrixExplodedPrimitive=5");
    checkPrimitive(nodes, "matrixExplodedPrimitive");
  }

  @Test
  public void pathMatrixNotExplodedArray() throws Exception {
    Map<String, JsonNode> nodes = pathToNode("matrixNotExplodedArray", ";matrixNotExplodedArray=3,4,5");
    checkArray(nodes, "matrixNotExplodedArray");
  }

  @Test
  public void pathMatrixExplodedArray() throws Exception {
    Map<String, JsonNode> nodes = pathToNode("matrixExplodedArray", ";matrixExplodedArray=3;matrixExplodedArray=4;matrixExplodedArray=5");
    checkArray(nodes, "matrixExplodedArray");
  }

  @Test
  public void pathMatrixNotExplodedObject() throws Exception {
    Map<String, JsonNode> nodes = pathToNode("matrixNotExplodedObject", ";matrixNotExplodedObject=boolProp,true,stringProp,admin");
    checkObject(nodes, "matrixNotExplodedObject");
  }

  @Test
  public void pathMatrixExplodedObject() throws Exception {
    Map<String, JsonNode> nodes = pathToNode("matrixExplodedObject", ";boolProp=true;stringProp=admin");
    checkObject(nodes, "matrixExplodedObject");
  }

  @Test
  public void pathContentObject() throws Exception {
    Map<String, JsonNode> nodes = pathToNode("content", "{\"boolProp\":true,\"stringProp\":\"admin\"}");
    checkObject(nodes, "content");
  }

  private void checkPrimitive(Map<String, JsonNode> nodes, String propName) {
    assertEquals(1, nodes.size());
    assertEquals(5, nodes.get(propName).intValue());
  }

  private void checkArray(Map<String, JsonNode> nodes, String propName) {
    assertEquals(1, nodes.size());
    assertEquals(3, nodes.get(propName).size());
    assertEquals(3, nodes.get(propName).get(0).intValue());
    assertEquals(4, nodes.get(propName).get(1).intValue());
    assertEquals(5, nodes.get(propName).get(2).intValue());
  }

  private void checkObject(Map<String, JsonNode> nodes, String propName) {
    assertEquals(1, nodes.size());
    assertTrue(nodes.get(propName).get("stringProp").isTextual());
    assertEquals("admin", nodes.get(propName).get("stringProp").textValue());
    assertTrue(nodes.get(propName).get("boolProp").isBoolean());
    assertTrue(nodes.get(propName).get("boolProp").booleanValue());
  }

  private Map<String, JsonNode> pathToNode(String parameterName, String value) throws Exception {
    OpenApi3 api = OpenApi3Util.loadApi("/operation/parameter/pathParameters.yaml");

    Map<String, AbsParameter<Parameter>> parameters = new HashMap<>();
    parameters.put(parameterName, api.getComponents().getParameters().get(parameterName));

    final List<Parameter> values = Collections.singletonList(api.getComponents().getParameters().get(parameterName));
    String oasPath = PathResolver.instance().solve("/" + parameterName + "/{" + parameterName + "}/foo", values).get();

    Pattern pattern = Pattern.compile(oasPath);

    return ParameterConverter.pathToNode(
      parameters,
      pattern,
      "/" + parameterName + "/" + value + "/foo");
  }
}
