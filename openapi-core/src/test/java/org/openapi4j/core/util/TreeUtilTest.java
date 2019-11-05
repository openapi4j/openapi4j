package org.openapi4j.core.util;

import com.fasterxml.jackson.databind.JsonNode;

import org.json.JSONException;
import org.junit.Test;
import org.openapi4j.core.exception.EncodeException;
import org.skyscreamer.jsonassert.JSONAssert;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class TreeUtilTest {
  @Test(expected = EncodeException.class)
  public void toJson() throws EncodeException, JSONException {
    Map<String, Object> map = new HashMap<>();
    map.put("akey", "aValue");
    String str = TreeUtil.toJson(map);
    JSONAssert.assertEquals("{\"akey\": \"aValue\"}", str, true);
    map.put(null, "aValue");
    TreeUtil.toJson(map);
  }

  @Test(expected = EncodeException.class)
  public void toJsonNode() throws EncodeException, JSONException {
    Map<String, Object> map = new HashMap<>();
    map.put("akey", "aValue");
    JsonNode jsonNode = TreeUtil.toJsonNode(map);
    JSONAssert.assertEquals("{\"akey\": \"aValue\"}", jsonNode.toString(), true);
    map.put(null, "aValue");
    TreeUtil.toJsonNode(map);
  }

  @Test(expected = EncodeException.class)
  public void toYaml() throws EncodeException {
    Map<String, Object> map = new HashMap<>();
    map.put("akey", "aValue");
    assertEquals("---\nakey: \"aValue\"\n", TreeUtil.toYaml(map));
    map.put(null, "aValue");
    TreeUtil.toYaml(map);
  }

  @Test
  public void load() {
  }

  @Test
  public void testLoad() {
  }

  @Test
  public void testLoad1() {
  }

  @Test
  public void testLoad2() {
  }
}
