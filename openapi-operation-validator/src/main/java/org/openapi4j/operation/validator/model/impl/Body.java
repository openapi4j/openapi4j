package org.openapi4j.operation.validator.model.impl;

import com.fasterxml.jackson.databind.JsonNode;

import org.openapi4j.core.model.OAIContext;
import org.openapi4j.core.util.TreeUtil;
import org.openapi4j.operation.validator.util.convert.ContentConverter;
import org.openapi4j.parser.model.v3.MediaType;

import java.io.IOException;
import java.io.InputStream;

import static java.util.Objects.requireNonNull;

public class Body {
  private static final String BODY_REQUIRED_ERR_MSG = "Body content is required.";

  private final Object bodyObject;
  private final JsonNode bodyNode;
  private final String bodyStr;
  private final InputStream bodyIs;

  private Body(Object body) {
    this.bodyObject = body;
    this.bodyNode = null;
    this.bodyStr = null;
    this.bodyIs = null;
  }

  private Body(JsonNode bodyNode) {
    this.bodyObject = null;
    this.bodyNode = bodyNode;
    this.bodyStr = null;
    this.bodyIs = null;
  }

  private Body(String body) {
    this.bodyObject = null;
    this.bodyNode = null;
    this.bodyStr = body;
    this.bodyIs = null;
  }

  private Body(InputStream bodyIs) {
    this.bodyObject = null;
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
   * Constructs a body from the given Java Object.
   * This is the preferred way to build a body wrapper.
   *
   * @param body the given model as a Java Object (JSON, XML, ...)
   * @return The constructed body.
   */
  public static Body from(Object body) {
    requireNonNull(body, BODY_REQUIRED_ERR_MSG);
    return new Body(body);
  }

  /**
   * Constructs a body from the given string.
   * This is a convenient method to build a body wrapper.
   * Also, you should definitely at look at {@link Body#from(JsonNode)} or
   * {@link Body#from(Object)} for performance.
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
   * {@link Body#from(Object)} for performance.
   *
   * @param body The given body stream
   * @return The constructed body.
   */
  public static Body from(InputStream body) {
    requireNonNull(body, BODY_REQUIRED_ERR_MSG);
    return new Body(body);
  }

  public JsonNode getContentAsNode(final OAIContext context,
                                   final MediaType mediaType,
                                   final String rawContentType) throws IOException {
    if (bodyNode != null) {
      return bodyNode;
    } else if (bodyObject != null) {
      return TreeUtil.json.convertValue(bodyObject, JsonNode.class);
    } else {
      return ContentConverter.convert(context, mediaType, rawContentType, bodyIs, bodyStr);
    }
  }
}
