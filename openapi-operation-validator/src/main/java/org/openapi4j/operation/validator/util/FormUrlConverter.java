package org.openapi4j.operation.validator.util;

import com.fasterxml.jackson.databind.JsonNode;
import org.openapi4j.core.util.IOUtil;
import org.openapi4j.core.util.MultiStringMap;
import org.openapi4j.core.util.StringUtil;
import org.openapi4j.core.util.TreeUtil;
import org.openapi4j.operation.validator.util.parameter.DeepObjectStyleConverter;
import org.openapi4j.operation.validator.util.parameter.FormStyleConverter;
import org.openapi4j.operation.validator.util.parameter.PipeDelimitedStyleConverter;
import org.openapi4j.operation.validator.util.parameter.SpaceDelimitedStyleConverter;
import org.openapi4j.parser.model.v3.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

class FormUrlConverter {
  private static final FormUrlConverter INSTANCE = new FormUrlConverter();

  private static final String SPACE_DELIMITED = "spaceDelimited";
  private static final String PIPE_DELIMITED = "pipeDelimited";
  private static final String DEEP_OBJECT = "deepObject";

  public static FormUrlConverter instance() {
    return INSTANCE;
  }

  private FormUrlConverter() {
  }

  private Map<MediaType, Map<String, AbsParameter<Parameter>>> mediaTypesCache = new HashMap<>();

  JsonNode convert(final MediaType mediaType, final InputStream body, String encoding) throws IOException {
    return convert(mediaType, IOUtil.toString(body, encoding), encoding);
  }

  JsonNode convert(final MediaType mediaType, final String body, final String encoding) {
    Map<String, JsonNode> params = convert(getParameters(mediaType), body, true, encoding);
    return TreeUtil.json.valueToTree(params);
  }

  Map<String, JsonNode> convert(final Map<String, AbsParameter<Parameter>> specParameters,
                                final String body,
                                final boolean caseSensitive,
                                final String encoding) {

    final Map<String, JsonNode> mappedValues = new HashMap<>();

    if (body == null) {
      return mappedValues;
    }

    MultiStringMap<String> values = getParameterValues(body, caseSensitive, encoding);

    for (Map.Entry<String, AbsParameter<Parameter>> paramEntry : specParameters.entrySet()) {
      final String paramName = paramEntry.getKey();
      final AbsParameter<Parameter> param = paramEntry.getValue();
      final JsonNode convertedValue;

      if (param.getSchema() != null) {
        final String style = param.getStyle();

        if (SPACE_DELIMITED.equals(style)) {
          convertedValue = SpaceDelimitedStyleConverter.instance().convert(param, paramName, values.get(paramName));
        } else if (PIPE_DELIMITED.equals(style)) {
          convertedValue = PipeDelimitedStyleConverter.instance().convert(param, paramName, values.get(paramName));
        } else if (DEEP_OBJECT.equals(style)) {
          convertedValue = DeepObjectStyleConverter.instance().convert(param, paramName, body);
        } else { // form is the default
          if (param.getExplode() == null) { // explode true is default
            param.setExplode(true);
          }
          convertedValue = FormStyleConverter.instance().convert(param, paramName, values);
        }
      } else {
        convertedValue = getValueFromContentType(param.getContentMediaTypes(), body); // ?????
      }

      if (convertedValue != null) {
        mappedValues.put(paramName, convertedValue);
      }
    }

    // TODO remove added values & add remaining ones

    return mappedValues;
  }

  private MultiStringMap<String> getParameterValues(String value, boolean caseSensitive, String encoding) {
    List<String> pairs = StringUtil.tokenize(value, "&", true, true);
    MultiStringMap<String> result = new MultiStringMap<>(caseSensitive);

    for (String pair : pairs) {
      int idx = pair.indexOf('=');
      if (idx == -1) {
        result.put(decode(pair, encoding), null);
      } else {
        result.put(
          decode(pair.substring(0, idx), encoding),
          decode(pair.substring(idx + 1), encoding));
      }
    }

    return result;
  }

  private String decode(String value, String encoding) {
    try {
      return URLDecoder.decode(value, encoding);
    } catch (UnsupportedEncodingException e) {
      try {
        return URLDecoder.decode(value, StandardCharsets.UTF_8.name());
      } catch (UnsupportedEncodingException ignored) {
        return value; // Will never happen - value is coming from JDK
      }
    }
  }

  private static JsonNode getValueFromContentType(final Map<String, MediaType> mediaTypes,
                                                  final String value) {

    if (mediaTypes != null && value != null) {
      Optional<Map.Entry<String, MediaType>> entry = mediaTypes.entrySet().stream().findFirst();

      if (entry.isPresent()) {
        Map.Entry<String, MediaType> mediaType = entry.get();

        try {
          return ContentConverter.convert(mediaType.getValue(), mediaType.getKey(), null, value);
        } catch (IOException e) {
          return null;
        }
      }
    }

    return null;
  }

  private Map<String, AbsParameter<Parameter>> getParameters(final MediaType mediaType) {
    // check cache
    Map<String, AbsParameter<Parameter>> specParameters = mediaTypesCache.get(mediaType);
    if (specParameters != null) {
      return specParameters;
    }

    // Cache missed
    Map<String, EncodingProperty> encodings
      = mediaType.getEncodings() != null
      ? mediaType.getEncodings()
      : new HashMap<>();

    specParameters = new HashMap<>();

    for (Map.Entry<String, Schema> propEntry : mediaType.getSchema().getProperties().entrySet()) {
      String propName = propEntry.getKey();

      specParameters.put(
        propName,
        createParameter(encodings, propName, propEntry.getValue()));
    }

    // Add media type to cache
    mediaTypesCache.put(mediaType, specParameters);

    return specParameters;
  }

  private AbsParameter<Parameter> createParameter(final Map<String, EncodingProperty> encodings,
                                                  final String propName,
                                                  final Schema schema) {

    EncodingProperty encodingProperty = encodings.get(propName);

    Parameter param = new Parameter().setName(propName);
    param.setSchema(schema);

    if (encodingProperty != null) {
      param
        .setStyle(encodingProperty.getStyle())
        .setExplode(encodingProperty.getExplode());

      if (encodingProperty.getContentType() != null) {
        param.setContentMediaType(encodingProperty.getContentType(), new MediaType().setSchema(schema));
        param.setSchema(null); // reset schema
      }
    }

    return param;
  }
}
