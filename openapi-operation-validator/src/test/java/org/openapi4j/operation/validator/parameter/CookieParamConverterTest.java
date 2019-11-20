package org.openapi4j.operation.validator.parameter;

import com.fasterxml.jackson.databind.JsonNode;

import org.junit.Test;
import org.openapi4j.operation.validator.OpenApi3Util;
import org.openapi4j.operation.validator.util.parameter.ParameterConverter;
import org.openapi4j.parser.model.v3.AbsParameter;
import org.openapi4j.parser.model.v3.OpenApi3;
import org.openapi4j.parser.model.v3.Parameter;

import java.util.HashMap;
import java.util.Map;

import static org.openapi4j.operation.validator.parameter.ParamChecker.checkArray;
import static org.openapi4j.operation.validator.parameter.ParamChecker.checkObject;
import static org.openapi4j.operation.validator.parameter.ParamChecker.checkPrimitive;

public class CookieParamConverterTest {
  @Test
  public void cookieFormNotExplodedPrimitive() throws Exception {
    Map<String, String> cookies = new HashMap<>();
    cookies.put("formNotExplodedPrimitive", "5");
    Map<String, JsonNode> nodes = cookieToNode("formNotExplodedPrimitive", cookies);
    checkPrimitive(nodes, "formNotExplodedPrimitive");
  }

  @Test
  public void cookieFormExplodedPrimitive() throws Exception {
    Map<String, String> cookies = new HashMap<>();
    cookies.put("formExplodedPrimitive", "5");
    Map<String, JsonNode> nodes = cookieToNode("formExplodedPrimitive", cookies);
    checkPrimitive(nodes, "formExplodedPrimitive");
  }

  @Test
  public void cookieFormNotExplodedArray() throws Exception {
    Map<String, String> cookies = new HashMap<>();
    cookies.put("formNotExplodedArray", "3,4,5");
    Map<String, JsonNode> nodes = cookieToNode("formNotExplodedArray", cookies);
    checkArray(nodes, "formNotExplodedArray");
  }

  @Test
  public void cookieFormNotExplodedObject() throws Exception {
    Map<String, String> cookies = new HashMap<>();
    cookies.put("formNotExplodedObject", "boolProp,true,stringProp,admin");
    Map<String, JsonNode> nodes = cookieToNode("formNotExplodedObject", cookies);
    checkObject(nodes, "formNotExplodedObject");
  }

  @Test
  public void cookieContentObject() throws Exception {
    Map<String, String> cookies = new HashMap<>();
    cookies.put("content", "{\"boolProp\":true,\"stringProp\":\"admin\"}");
    Map<String, JsonNode> nodes = cookieToNode("content", cookies);
    checkObject(nodes, "content");
  }

  private Map<String, JsonNode> cookieToNode(String parameterName, Map<String, String> cookies) throws Exception {
    OpenApi3 api = OpenApi3Util.loadApi("/operation/parameter/cookieParameters.yaml");

    Map<String, AbsParameter<Parameter>> parameters = new HashMap<>();
    parameters.put(parameterName, api.getComponents().getParameters().get(parameterName));

    return ParameterConverter.cookiesToNode(
      parameters,
      cookies);
  }
}
