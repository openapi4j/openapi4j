package org.openapi4j.operation.validator.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.apache.commons.fileupload.*;
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
  private static final MultipartConverter INSTANCE = new MultipartConverter();

  private MultipartConverter() {
  }

  public static MultipartConverter instance() {
    return INSTANCE;
  }

  JsonNode multipartToNode(final Schema schema, final InputStream body, final String rawContentType, final String encoding) throws IOException {
    UploadContext requestContext = UPLOAD_CONTEXT_INSTANCE.create(body, rawContentType, encoding);

    ObjectNode mappedBody = JsonNodeFactory.instance.objectNode();

    try {
      FileItemIterator iterator = new FileUpload().getItemIterator(requestContext);
      while (iterator.hasNext()) {
        FileItemStream item = iterator.next();
        String name = item.getFieldName();
        if (item.isFormField()) {
          JsonNode value = mappedBody.get(name);
          setValue(schema, mappedBody, item, name, value, encoding);
        } else {
          mappedBody.put(name, item.getName());
        }
      }
    } catch (FileUploadException ex) {
      throw new IOException(ex);
    }

    return mappedBody;
  }

  JsonNode multipartToNode(final Schema schema, final String body, final String rawContentType, final String encoding) throws IOException {
    InputStream is = new ByteArrayInputStream(body.getBytes(encoding));
    return multipartToNode(schema, is, rawContentType, encoding);
  }

  // Add value as direct value or collection if multi is detected.
  private void setValue(Schema schema, ObjectNode mappedBody, FileItemStream item, String name, JsonNode value, String encoding) throws IOException {
    if (value == null) {
      if (item.getContentType() != null) {
        mappedBody.set(name, Body.from(item.openStream()).getContentAsNode(schema.getProperty(name), item.getContentType()));
      } else {
        mappedBody.set(name, convertType(schema.getProperty(name), item, encoding));
      }
    } else {
      if (value instanceof ArrayNode) {
        ((ArrayNode) value).add(convertType(schema.getProperty(name), item, encoding));
      } else {
        ArrayNode values = JsonNodeFactory.instance.arrayNode();
        values.add(value);
        values.add(convertType(schema.getProperty(name), item, encoding));
        mappedBody.set(name, values);
      }
    }
  }

  private JsonNode convertType(final Schema schema, final FileItemStream item, final String encoding) throws IOException {
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
    UploadContext create(
      final InputStream body,
      final String contentType,
      final String encoding);
  }

  private static final UploadContextInstance UPLOAD_CONTEXT_INSTANCE = (body, contentType, encoding) -> new UploadContext() {
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
    public long contentLength() {
      return 0;
    }

    @Override
    public InputStream getInputStream() {
      return body;
    }
  };
}
