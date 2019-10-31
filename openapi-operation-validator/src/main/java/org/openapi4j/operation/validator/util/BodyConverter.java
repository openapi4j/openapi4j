package org.openapi4j.operation.validator.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;

import org.openapi4j.core.util.Json;
import org.openapi4j.parser.model.v3.Schema;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Convert supported media types to abstract tree nodes.
 */
public final class BodyConverter {
  public static JsonNode formUrlEncodedToNode(final Schema schema, final InputStream body, String encoding) {
    return FormUrlConverter.instance().formUrlEncodedToNode(schema, streamToString(body, Charset.forName(encoding)), encoding);
  }

  public static JsonNode formUrlEncodedToNode(final Schema schema, final String body, final  String encoding) {
    return FormUrlConverter.instance().formUrlEncodedToNode(schema, body, encoding);
  }

  public static JsonNode multipartToNode(final Schema schema, InputStream body, final String rawContentType, final String encoding) throws IOException {
    return MultipartConverter.multipartToNode(schema, body, rawContentType, encoding);
  }

  public static JsonNode multipartToNode(final Schema schema, final String body, final String rawContentType, final String encoding) throws IOException {
    return MultipartConverter.multipartToNode(schema, body, rawContentType, encoding);
  }

  public static JsonNode jsonToNode(InputStream body) throws IOException {
    return Json.jsonMapper.readTree(body);
  }

  public static JsonNode jsonToNode(String body) throws IOException {
    return Json.jsonMapper.readTree(body);
  }

  public static JsonNode xmlToNode(final Schema schema, InputStream body) {
    return XmlConverter.instance().xmlToNode(schema, streamToString(body, StandardCharsets.UTF_8));
  }

  public static JsonNode xmlToNode(final Schema schema, String body) {
    return XmlConverter.instance().xmlToNode(schema, body);
  }

  public static JsonNode textToNode(InputStream body) {
    return JsonNodeFactory.instance.textNode(streamToString(body, StandardCharsets.UTF_8));
  }

  public static JsonNode textToNode(String body) {
    return JsonNodeFactory.instance.textNode(body);
  }

  public static JsonNode mapToNode(final Schema schema, final Map<String, Object> content) {
    return TypeConverter.instance().convertTypes(schema, content);
  }

  static String streamToString(InputStream inputStream, Charset charset) {
    BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, charset));
    return br.lines().collect(Collectors.joining(System.lineSeparator()));
  }
}
