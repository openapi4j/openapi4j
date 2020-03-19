package org.openapi4j.operation.validator.convert;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openapi4j.core.util.TreeUtil;
import org.openapi4j.operation.validator.OpenApi3Util;
import org.openapi4j.operation.validator.util.ContentType;
import org.openapi4j.operation.validator.util.convert.ContentConverter;
import org.openapi4j.parser.model.v3.MediaType;
import org.openapi4j.parser.model.v3.OpenApi3;
import org.skyscreamer.jsonassert.JSONAssert;

import java.nio.charset.StandardCharsets;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class ContentConverterTest {
  private static OpenApi3 api;

  @BeforeClass
  public static void setup() throws Exception {
    api = OpenApi3Util.loadApi("/operation/contentType/contentType.yaml");
  }

  @Test
  public void testFormUrlEncoded() throws Exception {
    // will fallback to UTF-8
    check("application/x-www-form-urlencoded; charset=WRONG-ENCODING", "/operation/contentType/formurl.json");
  }

  @Test
  public void testMultipart() throws Exception {
    check("multipart/form-data;boundary=\"1234\"", "/operation/contentType/multipart.json");
  }

  @Test
  public void testJson() throws Exception {
    check("application/json", "/operation/contentType/json.json");
  }

  @Test
  public void testXml() throws Exception {
    check("application/xml", "/operation/contentType/xml.json");
  }

  @Test
  public void testDirect() {
    assertFalse(ContentType.isMultipartFormData("foo"));
    assertEquals(StandardCharsets.UTF_8.name(), ContentType.getCharSet(null));
  }

  private void check(String contentType, String testPath) throws Exception {
    ArrayNode testCases = (ArrayNode) TreeUtil.json.readTree(ContentConverterTest.class.getResource(testPath));

    for (int index = 0; index < testCases.size(); index++) {
      JsonNode testCase = testCases.get(index);
      JsonNode schemaModelName = testCase.get("schemaModel");
      JsonNode inputData = testCase.get("input");
      JsonNode expectedData = testCase.get("expected");

      MediaType mediaType = new MediaType().setSchema(api.getComponents().getSchemas().get(schemaModelName.textValue()));

      JsonNode actual = ContentConverter.convert(mediaType, contentType, null, inputData.textValue());

      JSONAssert.assertEquals(
        String.format("JSON matching test failed on test '%s'", testCase.get("description")),
        expectedData.toString(),
        actual.toString(),
        true);
    }
  }
}
