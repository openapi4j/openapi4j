package org.openapi4j.operation.validator.util;

import com.fasterxml.jackson.databind.JsonNode;

import org.json.JSONObject;
import org.json.XML;
import org.openapi4j.parser.model.v3.Schema;

import java.util.Map;
import java.util.regex.Pattern;

class XmlConverter {
  private static final XmlConverter INSTANCE = new XmlConverter();

  // Pattern to remove all namespace information
  private static final Pattern nsPattern = Pattern.compile(
    "<(\\/)*" + // begin XML chapter & slash ?
      "\\w+:" + // namespace prefix
      "(\\w+)" + // XML chapter
      "((?:\\s+\\w+=\".+?\")*)" + // XML attributes before namespace reference
      "(?:\\s+\\w+:\\w+=\".+?\")?" + // namespace reference
      "((?:\\s+\\w+=\".+?\")*)" + // XML attributes after namespace reference
      "(\\s*\\/?)>"); // trailing slash ? & end XML chapter

  private static final String nsReplace = "<$1$2$3$4$5>";

  private XmlConverter() {
  }

  static XmlConverter instance() {
    return INSTANCE;
  }

  JsonNode xmlToNode(final Schema schema, String body) {
    return convert(schema, XML.toJSONObject(removeNamespaces(body)));
  }

  // TODO handle wrapped and name
  @SuppressWarnings("unchecked")
  private JsonNode convert(final Schema schema, JSONObject xml) {
    Map<String, Object> xmlMapped = xml.toMap();

    // unwrap
    if (schema.getType() == null || "object".equals(schema.getType())) {
      for (Map.Entry<String, Object> entry : xmlMapped.entrySet()) {
        return TypeConverter.instance().convertTypes(schema, (Map<String, Object>) entry.getValue());
      }
    }

    return TypeConverter.instance().convertTypes(schema, xmlMapped);
  }

  public static String removeNamespaces(String xmlData) {
    return nsPattern.matcher(xmlData).replaceAll(nsReplace);
  }
}
