package org.openapi4j.operation.validator.util;

import com.fasterxml.jackson.databind.JsonNode;
import org.openapi4j.core.util.IOUtil;
import org.openapi4j.core.util.TreeUtil;
import org.openapi4j.parser.model.v3.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

class FormUrlConverter {
  private static final FormUrlConverter INSTANCE = new FormUrlConverter();

  private FormUrlConverter() {
  }

  public static FormUrlConverter instance() {
    return INSTANCE;
  }

  JsonNode formUrlEncodedToNode(final MediaType mediaType, final InputStream body, String encoding) throws IOException {
    return formUrlEncodedToNode(mediaType, IOUtil.toString(body, encoding), encoding);
  }

  JsonNode formUrlEncodedToNode(final MediaType mediaType, final String body, final String encoding) {
    Map<String, EncodingProperty> encodings
      = mediaType.getEncodings() != null
      ? mediaType.getEncodings()
      : new HashMap<>();

    Map<String, AbsParameter<Parameter>> specParameters = new HashMap<>();

    for (Map.Entry<String, Schema> propEntry : mediaType.getSchema().getProperties().entrySet()) {
      String propName = propEntry.getKey();

      specParameters.put(
        propName,
        fillParameter(encodings, propName, propEntry.getValue()));
    }

    Map<String, JsonNode> params = ParameterConverter.queryToNode(specParameters, body, encoding);

    return TreeUtil.json.valueToTree(params);
  }

  private AbsParameter<Parameter> fillParameter(final Map<String, EncodingProperty> encodings,
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
