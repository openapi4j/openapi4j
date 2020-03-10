package org.openapi4j.operation.validator.converter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

abstract class ParamChecker {
  static void checkPrimitive(Map<String, JsonNode> nodes, String propName) {
    assertEquals(1, nodes.size());
    assertEquals(5, nodes.get(propName).intValue());
  }

  static void checkWrongPrimitive(Map<String, JsonNode> nodes, String propName) {
    assertEquals(1, nodes.size());
    assertEquals(JsonNodeFactory.instance.textNode("wrong"), nodes.get(propName));
  }

  static void checkArray(Map<String, JsonNode> nodes, String propName) {
    assertEquals(1, nodes.size());
    assertEquals(3, nodes.get(propName).size());
    assertEquals(3, nodes.get(propName).get(0).intValue());
    assertEquals(4, nodes.get(propName).get(1).intValue());
    assertEquals(5, nodes.get(propName).get(2).intValue());
  }

  static void checkWrongArray(Map<String, JsonNode> nodes, String propName) {
    assertEquals(1, nodes.size());
    assertEquals(1, nodes.get(propName).size());
    assertEquals(JsonNodeFactory.instance.arrayNode().add(JsonNodeFactory.instance.textNode("wrong")), nodes.get(propName));
  }

  static void checkObject(Map<String, JsonNode> nodes, String propName) {
    assertEquals(1, nodes.size());
    assertTrue(nodes.get(propName).get("stringProp").isTextual());
    assertEquals("admin", nodes.get(propName).get("stringProp").textValue());
    assertTrue(nodes.get(propName).get("boolProp").isBoolean());
    assertTrue(nodes.get(propName).get("boolProp").booleanValue());
  }

  static void checkWrongObject(Map<String, JsonNode> nodes, String propName) {
    assertEquals(1, nodes.size());
    assertEquals(JsonNodeFactory.instance.objectNode().put("boolProp", "wrong"), nodes.get(propName));
  }
}
