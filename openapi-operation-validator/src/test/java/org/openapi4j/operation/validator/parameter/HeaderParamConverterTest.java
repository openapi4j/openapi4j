package org.openapi4j.operation.validator.parameter;

import com.fasterxml.jackson.databind.JsonNode;

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

import static org.openapi4j.operation.validator.parameter.ParamChecker.checkArray;
import static org.openapi4j.operation.validator.parameter.ParamChecker.checkObject;
import static org.openapi4j.operation.validator.parameter.ParamChecker.checkPrimitive;

public class HeaderParamConverterTest {
  @Test
  public void headerSimpleNotExplodedPrimitive() throws Exception {
    Map<String, Collection<String>> headers = new HashMap<>();
    headers.put("simpleNotExplodedPrimitive", Collections.singleton("5"));
    Map<String, JsonNode> nodes = headerToNode("simpleNotExplodedPrimitive", headers);
    checkPrimitive(nodes, "simpleNotExplodedPrimitive");
  }

  @Test
  public void headerSimpleExplodedPrimitive() throws Exception {
    Map<String, Collection<String>> headers = new HashMap<>();
    headers.put("simpleExplodedPrimitive", Collections.singleton("5"));
    Map<String, JsonNode> nodes = headerToNode("simpleExplodedPrimitive", headers);
    checkPrimitive(nodes, "simpleExplodedPrimitive");
  }

  @Test
  public void headerSimpleNotExplodedArray() throws Exception {
    Map<String, Collection<String>> headers = new HashMap<>();
    headers.put("simpleNotExplodedArray", Arrays.asList("3", "4", "5"));
    Map<String, JsonNode> nodes = headerToNode("simpleNotExplodedArray", headers);
    checkArray(nodes, "simpleNotExplodedArray");
  }

  @Test
  public void headerSimpleExplodedArray() throws Exception {
    Map<String, Collection<String>> headers = new HashMap<>();
    headers.put("simpleExplodedArray", Arrays.asList("3", "4", "5"));
    Map<String, JsonNode> nodes = headerToNode("simpleExplodedArray", headers);
    checkArray(nodes, "simpleExplodedArray");
  }

  @Test
  public void headerSimpleNotExplodedObject() throws Exception {
    Map<String, Collection<String>> headers = new HashMap<>();
    headers.put("simpleNotExplodedObject", Arrays.asList("boolProp", "true", "stringProp", "admin"));
    Map<String, JsonNode> nodes = headerToNode("simpleNotExplodedObject", headers);
    checkObject(nodes, "simpleNotExplodedObject");
  }

  @Test
  public void headerSimpleExplodedObject() throws Exception {
    Map<String, Collection<String>> headers = new HashMap<>();
    headers.put("simpleExplodedObject", Arrays.asList("boolProp=true", "stringProp=admin"));
    Map<String, JsonNode> nodes = headerToNode("simpleExplodedObject", headers);
    checkObject(nodes, "simpleExplodedObject");
  }

  @Test
  public void headerContentObject() throws Exception {
    Map<String, Collection<String>> headers = new HashMap<>();
    headers.put("content", Arrays.asList("{\"boolProp\":true,\"stringProp\":\"admin\"}"));
    Map<String, JsonNode> nodes = headerToNode("content", headers);
    checkObject(nodes, "content");
  }

  private Map<String, JsonNode> headerToNode(String parameterName, Map<String, Collection<String>> headers) throws Exception {
    OpenApi3 api = OpenApi3Util.loadApi("/operation/parameter/headerParameters.yaml");

    Map<String, AbsParameter<Parameter>> parameters = new HashMap<>();
    parameters.put(parameterName, api.getComponents().getParameters().get(parameterName));

    return ParameterConverter.headersToNode(
      parameters,
      headers);
  }
}
