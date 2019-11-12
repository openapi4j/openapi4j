package org.openapi4j.operation.validator.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUpload;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.RequestContext;
import org.openapi4j.core.util.IOUtil;
import org.openapi4j.core.util.TreeUtil;
import org.openapi4j.operation.validator.model.impl.Body;
import org.openapi4j.parser.model.v3.Schema;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import static org.openapi4j.core.model.v3.OAI3SchemaKeywords.TYPE_ARRAY;
import static org.openapi4j.core.model.v3.OAI3SchemaKeywords.TYPE_OBJECT;

class MultipartConverter {
  static JsonNode multipartToNode(final Schema schema, final InputStream body, final String rawContentType, final String encoding) throws IOException {
    RequestContext requestContext = rcInstance.apply(body, rawContentType, encoding);

    ObjectNode mappedBody = JsonNodeFactory.instance.objectNode();

    try {
      FileItemIterator iterator = new FileUpload().getItemIterator(requestContext);
      while (iterator.hasNext()) {
        FileItemStream item = iterator.next();
        String name = item.getFieldName();
        if (item.isFormField()) {
          // Add value as direct value or collection if multi is detected.
          JsonNode value = mappedBody.get(name);
          if (value == null) {
            if (item.getContentType() != null) {
              mappedBody.put(name, Body.from(item.openStream()).getContentAsJson(schema.getProperty(name), item.getContentType()));
            } else {
              mappedBody.put(name, convertType(schema.getProperty(name), item, encoding));
            }
          } else {
            if (value instanceof ArrayNode) {
              ((ArrayNode) value).add(convertType(schema.getProperty(name), item, encoding));
            } else {
              ArrayNode values = JsonNodeFactory.instance.arrayNode();
              values.add(value);
              values.add(convertType(schema.getProperty(name), item, encoding));
              mappedBody.put(name, values);
            }
          }
        } else {
          mappedBody.put(name, item.getName());
        }
      }
    } catch (FileUploadException ex) {
      throw new IOException(ex);
    }

    return mappedBody;
  }

  static JsonNode multipartToNode(final Schema schema, final String body, final String rawContentType, final String encoding) throws IOException {
    InputStream is = new ByteArrayInputStream(body.getBytes(encoding));
    return multipartToNode(schema, is, rawContentType, encoding);
  }

  private static JsonNode convertType(final Schema schema, final FileItemStream item, final String encoding) throws IOException {
    switch (schema.getSupposedType()) {
      case TYPE_OBJECT:
        String content = IOUtil.toString(item.openStream(), encoding);
        Map<String, Object> jsonContent = TreeUtil.json.readValue(content, new TypeReference<Map<String, Object>>() {});
        return TypeConverter.instance().convertObject(schema, jsonContent);
      case TYPE_ARRAY:
        return convertType(schema.getItemsSchema(), item, encoding);
      default:
        return TypeConverter.instance().convertPrimitiveType(schema, IOUtil.toString(item.openStream(), encoding));
    }
  }

  /**
   * Represents a function that creates a new instance of UploadContext object.
   */
  @FunctionalInterface
  private interface UploadContextInstance {
    RequestContext apply(
      final InputStream body,
      final String contentType,
      final String encoding);
  }

  private static final UploadContextInstance rcInstance = (body, contentType, encoding) -> new RequestContext() {
    @Override
    public String getCharacterEncoding() {
      return encoding;
    }

    @Override
    public String getContentType() {
      return contentType;
    }

    @Override
    public int getContentLength() {
      return 0;
    }

    @Override
    public InputStream getInputStream() {
      return body;
    }
  };
}
