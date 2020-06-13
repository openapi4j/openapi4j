package org.openapi4j.operation.validator.util.convert;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.type.MapType;
import org.apache.commons.fileupload.*;
import org.openapi4j.core.model.OAIContext;
import org.openapi4j.core.util.IOUtil;
import org.openapi4j.core.util.TreeUtil;
import org.openapi4j.parser.model.v3.EncodingProperty;
import org.openapi4j.parser.model.v3.MediaType;
import org.openapi4j.parser.model.v3.Schema;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import static org.openapi4j.core.model.v3.OAI3SchemaKeywords.*;

class MultipartConverter {
  private static final MultipartConverter INSTANCE = new MultipartConverter();

  private static final MapType MAP_TYPE = TreeUtil.json.getTypeFactory().constructMapType(
    HashMap.class,
    TreeUtil.json.getTypeFactory().constructType(String.class),
    TreeUtil.json.getTypeFactory().constructType(Object.class));

  private MultipartConverter() {
  }

  public static MultipartConverter instance() {
    return INSTANCE;
  }

  JsonNode convert(final OAIContext context, final MediaType mediaType, final String body, final String rawContentType, final String encoding) throws IOException {
    InputStream is = new ByteArrayInputStream(body.getBytes(encoding));
    return convert(context, mediaType, is, rawContentType, encoding);
  }

  JsonNode convert(final OAIContext context, final MediaType mediaType, final InputStream body, final String rawContentType, final String encoding) throws IOException {
    UploadContext requestContext = UPLOAD_CONTEXT_INSTANCE.create(body, rawContentType, encoding);

    ObjectNode result = JsonNodeFactory.instance.objectNode();

    try {
      FileItemIterator iterator = new FileUpload().getItemIterator(requestContext);
      while (iterator.hasNext()) {
        FileItemStream item = iterator.next();
        String name = item.getFieldName();

        if (item.isFormField()) {
          JsonNode convertedValue = mapValue(context, result, mediaType, item, name, encoding);
          if (convertedValue != null) {
            addValue(result, name, convertedValue);
          }
        } else { // Add file name only
          addValue(result, name, JsonNodeFactory.instance.textNode(item.getName()));
        }
      }
    } catch (FileUploadException ex) {
      throw new IOException(ex);
    }

    return result;
  }

  private JsonNode mapValue(OAIContext context, ObjectNode result, MediaType mediaType, FileItemStream item, String name, String encoding) throws IOException {
    Schema propSchema = mediaType.getSchema().getProperty(name);
    String itemContentType = item.getContentType();

    if (itemContentType != null) {
      final int checkResult = checkContentType(context, propSchema, mediaType.getEncoding(name), item);
      if (checkResult == -1) {
        // content type mismatch
        String content = IOUtil.toString(item.openStream(), encoding);
        return JsonNodeFactory.instance.textNode(content);
      } else if (checkResult == 0) {
        // Process with the given content type
        String content = IOUtil.toString(item.openStream(), encoding);
        try {
          return ContentConverter.convert(context, new MediaType().setSchema(propSchema), itemContentType, null, content);
        } catch (IOException ex) {
          // content type mismatch
          return JsonNodeFactory.instance.textNode(content);
        }
      }
    }

    // Process as JSON
    return convertToJsonNode(context, result, name, propSchema, item, encoding);
  }

  /**
   * Check content type.
   *
   * @return -1: in case of mismatch<br/>
   * 0: if content should be processed with the given content type<br/>
   * 1: if the content should be processed as JSON.<br/>
   */
  private int checkContentType(OAIContext context, Schema propSchema, EncodingProperty encProperty, FileItemStream item) {
    String itemContentType = item.getContentType();
    String specContentType = (encProperty != null && encProperty.getContentType() != null) ? encProperty.getContentType() : null;

    // Check given content type against spec content type
    if (specContentType != null && !itemContentType.equals(specContentType)) {
      return -1;
    }

    // Cheking by default value
    Schema flatSchema = propSchema.getFlatSchema(context);
    switch (flatSchema.getSupposedType(context)) {
      case TYPE_OBJECT:
        // for object - application/json
        return itemContentType.equals("application/json") ? 1 : 0;
      case TYPE_ARRAY:
        // for array - defined based on the inner type
        return checkContentType(context, flatSchema.getItemsSchema(), encProperty, item);
      case TYPE_STRING:
        // for string with format being binary - application/octet-stream
        if (FORMAT_BINARY.equals(flatSchema.getFormat())) {
          return itemContentType.equals("application/octet-stream") ? 1 : 0;
        }
      default:
        // for other primitive types - text/plain
        return itemContentType.equals("text/plain") ? 1 : 0;
    }
  }

  private JsonNode convertToJsonNode(final OAIContext context,
                                     final ObjectNode result,
                                     final String name,
                                     final Schema schema,
                                     final FileItemStream item,
                                     final String encoding) throws IOException {

    switch (schema.getSupposedType(context)) {
      case TYPE_OBJECT:
        Map<String, Object> jsonContent = TreeUtil.json.readValue(item.openStream(), MAP_TYPE);
        return TypeConverter.instance().convertObject(context, schema, jsonContent);
      case TYPE_ARRAY:
        // Special case for arrays
        // They can be referenced multiple times in different ways
        JsonNode convertedValue = convertToJsonNode(context, result, name, schema.getItemsSchema(), item, encoding);
        JsonNode previousValue = result.get(name);
        if ((previousValue instanceof ArrayNode)) {
          ((ArrayNode) previousValue).add(convertedValue);
        } else {
          result.set(name, JsonNodeFactory.instance.arrayNode().add(convertedValue));
        }
        return null;
      default:
        return TypeConverter.instance().convertPrimitive(context, schema, IOUtil.toString(item.openStream(), encoding));
    }
  }

  private void addValue(ObjectNode result, String name, JsonNode value) {
    // Check if value is already referenced
    // If so, add new value to an array
    JsonNode previousValue = result.get(name);
    if (previousValue != null) {
      if (previousValue instanceof ArrayNode) {
        ((ArrayNode) previousValue).add(value);
      } else {
        ArrayNode values = JsonNodeFactory.instance.arrayNode();
        values.add(previousValue);
        values.add(value);

        result.set(name, values);
      }
    } else {
      result.set(name, value);
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
