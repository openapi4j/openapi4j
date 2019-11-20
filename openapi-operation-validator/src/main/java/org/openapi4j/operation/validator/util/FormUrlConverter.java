package org.openapi4j.operation.validator.util;

import com.fasterxml.jackson.databind.JsonNode;

import org.openapi4j.core.util.IOUtil;
import org.openapi4j.parser.model.v3.Schema;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class FormUrlConverter {
  // allows p1= a b &p2=1 &p3=
  private static final Pattern QUERY_STRING_PATTERN = Pattern.compile("([^&=]+?)\\s*=([^&=]*)");

  private static final FormUrlConverter INSTANCE = new FormUrlConverter();

  private FormUrlConverter() {
  }

  public static FormUrlConverter instance() {
    return INSTANCE;
  }

  JsonNode formUrlEncodedToNode(final Schema schema, final InputStream body, String encoding) throws IOException {
    return formUrlEncodedToNode(schema, IOUtil.toString(body, encoding), encoding);
  }

  @SuppressWarnings("unchecked")
  JsonNode formUrlEncodedToNode(final Schema schema, final String body, final String encoding) {
    String decodedBody;
    try {
      decodedBody = URLDecoder.decode(body, encoding);
    } catch (UnsupportedEncodingException e) {
      try {
        decodedBody = URLDecoder.decode(body, StandardCharsets.UTF_8.name());
      } catch (UnsupportedEncodingException ignored) {
        decodedBody = body; // Will never happen - value is coming from JDK
      }
    }

    Map<String, Object> params = new HashMap<>();
    // Encoded form data is exactly the same as query string composition
    Matcher matcher = QUERY_STRING_PATTERN.matcher(decodedBody);
    while (matcher.find()) {
      Object value = params.get(matcher.group(1));
      if (value == null) {
        params.put(matcher.group(1), matcher.group(2));
      } else {
        if (value instanceof List) {
          ((List<Object>) value).add(matcher.group(2));
        } else {
          List<Object> values = new ArrayList<>();
          values.add(value);
          values.add(matcher.group(2));
          params.put(matcher.group(1), values);
        }
      }
    }

    return ContentConverter.mapToNode(schema, params);
  }
}
