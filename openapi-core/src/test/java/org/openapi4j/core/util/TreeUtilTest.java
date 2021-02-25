package org.openapi4j.core.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.MissingNode;
import org.json.JSONException;
import org.junit.Test;
import org.openapi4j.core.exception.DecodeException;
import org.openapi4j.core.exception.EncodeException;
import org.openapi4j.core.model.AuthOption;
import org.skyscreamer.jsonassert.JSONAssert;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

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
  public void simpleLoad() throws DecodeException {
    assertNotNull(TreeUtil.load(getClass().getResource("/parsing/discriminator.yaml")));
    assertNotNull(TreeUtil.load(getClass().getResource("/parsing/discriminator.json")));
  }

  @Test
  public void remoteLoad() throws DecodeException, MalformedURLException {
    // Test authent even if it's useless, it will be ignored by bit.ly
    AuthOption option1 = new AuthOption(AuthOption.Type.QUERY, "a", "value");
    AuthOption option2 = new AuthOption(AuthOption.Type.HEADER, "another", "value");

    // Test redirection with bit.ly
    // https://raw.githubusercontent.com/openapi4j/openapi4j/master/openapi-core/src/test/resources/parsing/discriminator.yaml
    assertNotNull(TreeUtil.load(
      new URL("http://bit.ly/348ujup?foo=bar"),
      Arrays.asList(option1, option2)));
  }

  @Test(expected = DecodeException.class)
  public void simpleLoadInvalid() throws DecodeException, MalformedURLException {
    TreeUtil.load(new URL("file://parsing/invalid.yaml"));
  }

  @Test
  public void classLoad() throws DecodeException {
    assertNotNull(TreeUtil.load(getClass().getResource("/parsing/discriminator.yaml"), Object.class));
    assertNotNull(TreeUtil.load(getClass().getResource("/parsing/discriminator.json"), Object.class));
  }

  @Test(expected = DecodeException.class)
  public void classLoadInvalid() throws DecodeException, MalformedURLException {
    TreeUtil.load(new URL("file://parsing/invalid.yaml"), Object.class);
  }

  @Test
  public void emptyDocumentValid() throws DecodeException {
    assertTrue(TreeUtil.load(getClass().getResource("/parsing/empty.yaml")) instanceof MissingNode);
  }
}
