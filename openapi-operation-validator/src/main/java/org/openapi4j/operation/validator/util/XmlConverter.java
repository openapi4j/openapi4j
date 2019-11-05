package org.openapi4j.operation.validator.util;

import com.fasterxml.jackson.databind.JsonNode;

import org.json.JSONObject;
import org.json.XML;
import org.openapi4j.parser.model.v3.Schema;

import java.util.Map;
import java.util.regex.Pattern;

import static org.openapi4j.core.model.v3.OAI3SchemaKeywords.TYPE_ARRAY;
import static org.openapi4j.core.model.v3.OAI3SchemaKeywords.TYPE_OBJECT;

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
    return convert(
      schema,
      XML.toJSONObject(nsPattern.matcher(body).replaceAll(nsReplace)));
  }

  // TODO handle name
  private JsonNode convert(final Schema schema, final JSONObject xml) {
    Map<String, Object> xmlMapped = xml.toMap();

    // object
    if (schema.getType() == null || TYPE_OBJECT.equals(schema.getType())) {
      return getSubContent(schema, xmlMapped);
    }

    // wrapped array
    if (TYPE_ARRAY.equals(schema.getType()) && schema.getXml().isWrapped()) {
      return getSubContent(schema, xmlMapped);
    }

    return TypeConverter.instance().convertTypes(schema, xmlMapped);
  }

  @SuppressWarnings("unchecked")
  private static JsonNode getSubContent(final Schema schema, final Map<String, Object> xmlMapped) {
    // Get first child of XML chapter, to match JSON content
    Map.Entry<String, Object> entry = xmlMapped.entrySet().iterator().next();
    return TypeConverter.instance().convertTypes(schema, (Map<String, Object>) entry.getValue());
  }
}
