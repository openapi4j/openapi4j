package org.openapi4j.operation.validator.util;

import com.fasterxml.jackson.databind.JsonNode;

import org.openapi4j.operation.validator.model.impl.Regexes;
import org.openapi4j.parser.model.v3.Schema;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;

class FormUrlConverter {
  private static final FormUrlConverter INSTANCE = new FormUrlConverter();

  private FormUrlConverter() {
  }

  public static FormUrlConverter instance() {
    return INSTANCE;
  }

  JsonNode formUrlEncodedToNode(final Schema schema, final InputStream body, String encoding) {
    return formUrlEncodedToNode(schema, BodyConverter.streamToString(body, Charset.forName(encoding)), encoding);
  }

  JsonNode formUrlEncodedToNode(final Schema schema, final String body, final  String encoding) {
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
    Matcher matcher = Regexes.QUERY_STRING.matcher(decodedBody);
    while (matcher.find()) {
      Object value = params.get(matcher.group(1));
      if (value == null) {
        params.put(matcher.group(1), matcher.group(2));
      } else {
        if (value instanceof Collection) {
          //noinspection unchecked
          ((Collection) value).add(matcher.group(2));
        } else {
          Collection<Object> values = new ArrayList<>();
          values.add(value);
          values.add(matcher.group(2));
          params.put(matcher.group(1), values);
        }
      }
    }

    return BodyConverter.mapToNode(schema, params);
  }
}
