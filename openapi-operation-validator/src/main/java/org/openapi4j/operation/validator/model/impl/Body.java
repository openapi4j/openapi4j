package org.openapi4j.operation.validator.model.impl;

import com.fasterxml.jackson.databind.JsonNode;

import org.openapi4j.core.util.TreeUtil;
import org.openapi4j.operation.validator.util.convert.ContentConverter;
import org.openapi4j.parser.model.v3.MediaType;

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
   * Constructs a body from the given abstract node model.
   * This is the preferred way to build a body wrapper.
   *
   * @param body the given abstract node model (JSON, XML, form data, ...)
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
   * @param body the given map tree model (JSON, XML, ...)
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
   * {@link Body#from(Map)} for performance.
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

  public JsonNode getContentAsNode(final MediaType mediaType,
                                   final String rawContentType) throws IOException {
    if (bodyNode != null) {
      return bodyNode;
    } else if (bodyMap != null) {
      return TreeUtil.json.convertValue(bodyMap, JsonNode.class);
    } else {
      return ContentConverter.convert(mediaType, rawContentType, bodyIs, bodyStr);
    }
  }
}
