package org.openapi4j.operation.validator.convert;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openapi4j.core.util.TreeUtil;
import org.openapi4j.operation.validator.OpenApi3Util;
import org.openapi4j.operation.validator.util.ContentType;
import org.openapi4j.operation.validator.util.convert.ContentConverter;
import org.openapi4j.parser.model.v3.EncodingProperty;
import org.openapi4j.parser.model.v3.MediaType;
import org.openapi4j.parser.model.v3.OpenApi3;
import org.skyscreamer.jsonassert.JSONAssert;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

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
    check("/operation/contentType/formurl.json");
  }

  @Test
  public void testMultipart() throws Exception {
    check("/operation/contentType/multipart.json");
  }

  @Test
  public void testJson() throws Exception {
    check("/operation/contentType/json.json");
  }

  @Test
  public void testXml() throws Exception {
    check("/operation/contentType/xml.json");
  }

  @Test
  public void testDirect() {
    assertFalse(ContentType.isMultipartFormData("foo"));
    assertEquals(StandardCharsets.UTF_8.name(), ContentType.getCharSet(null));
  }

  private void check(String testPath) throws Exception {
    ArrayNode testCases = (ArrayNode) TreeUtil.json.readTree(ContentConverterTest.class.getResource(testPath));

    for (int index = 0; index < testCases.size(); index++) {
      JsonNode testCase = testCases.get(index);
      JsonNode schemaModelName = testCase.get("schemaModel");
      JsonNode contentType = testCase.get("contentType");
      JsonNode encodings = testCase.get("encodings");
      JsonNode inputData = testCase.get("input");
      JsonNode expectedData = testCase.get("expected");

      MediaType mediaType = new MediaType()
        .setSchema(api.getComponents().getSchemas().get(schemaModelName.textValue()))
        .setEncodings(TreeUtil.json.convertValue(encodings, new TypeReference<Map<String, EncodingProperty>>() {}));

      check(
        mediaType,
        contentType.textValue(),
        inputData.textValue(),
        expectedData.toString(),
        testCase.get("description").textValue());
    }
  }

  private void check(MediaType mediaType, String contentType, String input, String expected, String description) throws Exception {
    // With string
    JsonNode actual = ContentConverter.convert(mediaType, contentType, null, input);
    JSONAssert.assertEquals(
      String.format("JSON matching test failed on test '%s'", description),
      expected,
      actual.toString(),
      true);

    // With input stream
    actual = ContentConverter.convert(mediaType, contentType, new ByteArrayInputStream(input != null ? input.getBytes() : "".getBytes()), null);
    JSONAssert.assertEquals(
      String.format("JSON matching test failed on test '%s'", description),
      expected,
      actual.toString(),
      true);
  }
}
