package org.openapi4j.operation.validator.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;

import org.openapi4j.core.util.IOUtil;
import org.openapi4j.core.util.TreeUtil;
import org.openapi4j.parser.model.v3.Schema;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * Convert supported media types to abstract tree nodes.
 */
public final class BodyConverter {
  private BodyConverter() {}

  public static JsonNode formUrlEncodedToNode(final Schema schema, final InputStream body, String encoding) throws IOException {
    return FormUrlConverter.instance().formUrlEncodedToNode(schema, IOUtil.toString(body, encoding), encoding);
  }

  public static JsonNode formUrlEncodedToNode(final Schema schema, final String body, final String encoding) {
    return FormUrlConverter.instance().formUrlEncodedToNode(schema, body, encoding);
  }

  public static JsonNode multipartToNode(final Schema schema, InputStream body, final String rawContentType, final String encoding) throws IOException {
    return MultipartConverter.multipartToNode(schema, body, rawContentType, encoding);
  }

  public static JsonNode multipartToNode(final Schema schema, final String body, final String rawContentType, final String encoding) throws IOException {
    return MultipartConverter.multipartToNode(schema, body, rawContentType, encoding);
  }

  public static JsonNode jsonToNode(InputStream body) throws IOException {
    return TreeUtil.json.readTree(body);
  }

  public static JsonNode jsonToNode(String body) throws IOException {
    return TreeUtil.json.readTree(body);
  }

  public static JsonNode xmlToNode(final Schema schema, InputStream body) throws IOException {
    return XmlConverter.instance().xmlToNode(schema, IOUtil.toString(body, StandardCharsets.UTF_8.name()));
  }

  public static JsonNode xmlToNode(final Schema schema, String body) {
    return XmlConverter.instance().xmlToNode(schema, body);
  }

  public static JsonNode textToNode(InputStream body) throws IOException {
    return JsonNodeFactory.instance.textNode(IOUtil.toString(body, StandardCharsets.UTF_8.name()));
  }

  public static JsonNode textToNode(String body) {
    return JsonNodeFactory.instance.textNode(body);
  }

  public static JsonNode mapToNode(final Schema schema, final Map<String, Object> content) {
    return TypeConverter.instance().convertTypes(schema, content);
  }
}
