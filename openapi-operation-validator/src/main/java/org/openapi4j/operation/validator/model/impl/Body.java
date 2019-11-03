package org.openapi4j.operation.validator.model.impl;

import com.fasterxml.jackson.databind.JsonNode;
import org.openapi4j.operation.validator.util.BodyConverter;
import org.openapi4j.operation.validator.util.ContentType;
import org.openapi4j.parser.model.v3.Schema;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import static java.util.Objects.requireNonNull;

public class Body {
  private static final String BODY_REQUIRED_ERR_MSG = "Body content is required.";

  private final Map<String, Object> bodyMap;
  private final JsonNode bodyNode;
  private final String bodyStr;
  private final InputStream bodyIs;

  private Body(Map<String, Object> body) {
    this.bodyMap = body;
    this.bodyNode = null;
    this.bodyStr = null;
    this.bodyIs = null;
  }

  private Body(JsonNode bodyNode) {
    this.bodyMap = null;
    this.bodyNode = bodyNode;
    this.bodyStr = null;
    this.bodyIs = null;
  }

  private Body(String body) {
    this.bodyMap = null;
    this.bodyNode = null;
    this.bodyStr = body;
    this.bodyIs = null;
  }

  private Body(InputStream bodyIs) {
    this.bodyMap = null;
    this.bodyNode = null;
    this.bodyStr = null;
    this.bodyIs = bodyIs;
  }

  /**
   * Check if there's content in the body
   * @return {@code true} if the body is {@code null}, {@code false} otherwise.
   */
  public boolean isNull() {
    return bodyMap == null && bodyNode == null && bodyStr == null && bodyIs == null;
  }

  /**
   * Constructs a body from the given abstract node model.
   * This is the preferred way to build a body wrapper.
   *
   * @param body the given abstract node model (JSON, XML, protobuf, form data, ...)
   * @return The constructed body.
   */
  public static Body from(JsonNode body) {
    requireNonNull(body, BODY_REQUIRED_ERR_MSG);
    return new Body(body);
  }

  /**
   * Constructs a body from the given map tree model.
   * This is the preferred way to build a body wrapper.
   *
   * @param body the given map tree model (JSON, XML, protobuf, ...)
   * @return The constructed body.
   */
  public static Body from(Map<String, Object> body) {
    requireNonNull(body, BODY_REQUIRED_ERR_MSG);
    return new Body(body);
  }

  /**
   * Constructs a body from the given string.
   * This is a convenient method to build a body wrapper.
   * Also, you should definitely at look at {@link Body#from(JsonNode)} or
   * {@link Body#from(Map<String, Object>)} for performance.
   *
   * @param body The given body string.
   * @return The constructed body.
   */
  public static Body from(String body) {
    requireNonNull(body, BODY_REQUIRED_ERR_MSG);
    return new Body(body);
  }

  /**
   * Constructs a body from the given stream.
   * This is a convenient method to build a body wrapper.
   * Also, you should definitely at look at {@link Body#from(JsonNode)} or
   * {@link Body#from(Map)} for performance.
   *
   * @param body The given body stream
   * @return The constructed body.
   */
  public static Body from(InputStream body) {
    requireNonNull(body, BODY_REQUIRED_ERR_MSG);
    return new Body(body);
  }

  public JsonNode getContentAsJson(final Schema schema,
                                   final String rawContentType) throws IOException {
    if (bodyNode != null) {
      return bodyNode;
    }
    if (bodyMap != null) {
      return BodyConverter.mapToNode(schema, bodyMap);
    }

    String contentType = ContentType.getTypeOnly(rawContentType);
    String encoding = ContentType.getCharSet(rawContentType);

    if (ContentType.isJson(contentType)) {
      return (bodyStr != null)
        ? BodyConverter.jsonToNode(bodyStr)
        : BodyConverter.jsonToNode(bodyIs);

    } else if (ContentType.isXml(contentType)) {
      return (bodyStr != null)
        ? BodyConverter.xmlToNode(schema, bodyStr)
        : BodyConverter.xmlToNode(schema, bodyIs);

    } else if (ContentType.isFormUrlEncoded(contentType)) {
      return (bodyStr != null)
        ? BodyConverter.formUrlEncodedToNode(schema, bodyStr, encoding)
        : BodyConverter.formUrlEncodedToNode(schema, bodyIs, encoding);

    } else if (ContentType.isMultipartFormData(contentType)) {
      return (bodyStr != null)
        ? BodyConverter.multipartToNode(schema, bodyStr, rawContentType, encoding)
        : BodyConverter.multipartToNode(schema, bodyIs, rawContentType, encoding);

    } else { // UNKNOWN
      return (bodyStr != null)
        ? BodyConverter.textToNode(bodyStr)
        : BodyConverter.textToNode(bodyIs);
    }
  }
}
