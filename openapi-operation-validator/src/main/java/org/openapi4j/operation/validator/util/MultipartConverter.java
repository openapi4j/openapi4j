package org.openapi4j.operation.validator.util;

import com.fasterxml.jackson.databind.JsonNode;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUpload;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.RequestContext;
import org.apache.commons.fileupload.UploadContext;
import org.apache.commons.fileupload.util.Streams;
import org.openapi4j.parser.model.v3.Schema;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

class MultipartConverter {
  static JsonNode multipartToNode(final Schema schema, final InputStream body, final String rawContentType, final String encoding) throws IOException {
    RequestContext requestContext = rcInstance.apply(body, rawContentType, encoding);

    Map<String, Object> fields = new HashMap<>();

    try {
      FileItemIterator iterator = new FileUpload().getItemIterator(requestContext);
      while (iterator.hasNext()) {
        FileItemStream item = iterator.next();
        String name = item.getFieldName();
        if (item.isFormField()) {
          // Add value as direct value or collection if multi is detected.
          Object value = fields.get(name);
          if (value == null) {
            // TODO check content-type to get correct conversion of sub-element
            //fields.put(name, Body.from(item.openStream()).getContentAsJson(schema, item.getContentType()));
            fields.put(name, Streams.asString(item.openStream(), encoding));
          } else {
            if (value instanceof Collection) {
              //noinspection unchecked
              ((Collection) value).add(Streams.asString(item.openStream(), encoding));
            } else {
              Collection<Object> values = new ArrayList<>();
              values.add(value);
              values.add(Streams.asString(item.openStream(), encoding));
              fields.put(name, values);
            }
          }
        } else {
          fields.put(name, item.getName());
        }
      }
    } catch (FileUploadException ex) {
      throw new IOException(ex);
    }

    return BodyConverter.mapToNode(schema, fields);
  }

  static JsonNode multipartToNode(final Schema schema, final String body, final String rawContentType, final String encoding) throws IOException {
    InputStream is = new ByteArrayInputStream(body.getBytes(encoding));
    return multipartToNode(schema, is, rawContentType, encoding);
  }

  /**
   * Represents a function that creates a new instance of UploadContext object.
   */
  @FunctionalInterface
  private interface UploadContextInstance {
    UploadContext apply(
      final InputStream body,
      final String contentType,
      final String encoding);
  }

  private static final UploadContextInstance rcInstance = (body, contentType, encoding) -> new UploadContext() {
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
      return 0; // deprecated in 1.3 for getContentLength
    }

    @Override
    public InputStream getInputStream() {
      return body;
    }
  };
}
