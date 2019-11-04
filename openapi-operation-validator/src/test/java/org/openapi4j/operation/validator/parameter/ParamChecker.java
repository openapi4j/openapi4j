package org.openapi4j.operation.validator.parameter;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

class ParamChecker {
  static void checkPrimitive(Map<String, JsonNode> nodes, String propName) {
    assertEquals(1, nodes.size());
    assertEquals(5, nodes.get(propName).intValue());
  }

  static void checkArray(Map<String, JsonNode> nodes, String propName) {
    assertEquals(1, nodes.size());
    assertEquals(3, nodes.get(propName).size());
    assertEquals(3, nodes.get(propName).get(0).intValue());
    assertEquals(4, nodes.get(propName).get(1).intValue());
    assertEquals(5, nodes.get(propName).get(2).intValue());
  }

  static void checkObject(Map<String, JsonNode> nodes, String propName) {
    assertEquals(1, nodes.size());
    assertTrue(nodes.get(propName).get("stringProp").isTextual());
    assertEquals("admin", nodes.get(propName).get("stringProp").textValue());
    assertTrue(nodes.get(propName).get("boolProp").isBoolean());
    assertTrue(nodes.get(propName).get("boolProp").booleanValue());
  }
}
