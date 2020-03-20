package org.openapi4j.operation.validator.util.convert;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.json.JSONObject;
import org.json.XML;
import org.json.XMLParserConfiguration;
import org.openapi4j.core.util.TreeUtil;
import org.openapi4j.parser.model.v3.Schema;
import org.openapi4j.parser.model.v3.Xml;

import java.io.IOException;
import java.util.Map;
import java.util.regex.Pattern;

import static org.openapi4j.core.model.v3.OAI3SchemaKeywords.TYPE_ARRAY;
import static org.openapi4j.core.model.v3.OAI3SchemaKeywords.TYPE_OBJECT;

class XmlConverter {
  private static final XmlConverter INSTANCE = new XmlConverter();

  // Pattern to remove all namespace information
  private static final Pattern nsPattern
    = Pattern.compile("<(/)*" // begin XML chapter & slash ?
    + "\\w+:" // namespace prefix
    + "(\\w+)" // XML chapter
    + "((?:\\s+\\w+=\".+?\")*)" // XML attributes before namespace reference
    + "(?:\\s+\\w+:\\w+=\".+?\")?" // namespace reference
    + "((?:\\s+\\w+=\".+?\")*)" // XML attributes after namespace reference
    + "(\\s*/?)>"); // trailing slash ? & end XML chapter

  private static final String nsReplace = "<$1$2$3$4$5>";

  private XmlConverter() {
  }

  static XmlConverter instance() {
    return INSTANCE;
  }

  JsonNode convert(final Schema schema, String body) {
    return convert(
      schema,
      XML.toJSONObject(nsPattern.matcher(body).replaceAll(nsReplace), XMLParserConfiguration.KEEP_STRINGS));
  }

  private JsonNode convert(final Schema schema, final JSONObject xml) {
    if (xml.isEmpty()) {
      return JsonNodeFactory.instance.nullNode();
    }

    JsonNode content;
    try {
      content = TreeUtil.json.readTree(xml.toString());
    } catch (IOException e) {
      return JsonNodeFactory.instance.nullNode();
    }

    // Specific case of xml2json mapping : Unwrap first key to match JSON content
    if (TYPE_OBJECT.equals(schema.getSupposedType())) {
      content = content.fields().next().getValue();
    }

    return processNode(schema, content);
  }

  private JsonNode processNode(final Schema schema, final JsonNode node) {
    JsonNode content = unwrap(schema, node, null);
    if (content == null) {
      return null;
    }

    if (TYPE_ARRAY.equals(schema.getSupposedType())) {
      return parseArray(schema, content);
    } else if (TYPE_OBJECT.equals(schema.getSupposedType())) {
      return parseObject(schema, content);
    } else {
      return TypeConverter.instance().convertPrimitive(schema, content.asText());
    }
  }

  private JsonNode parseArray(final Schema schema, final JsonNode node) {
    if (!node.isArray()) {
      return JsonNodeFactory.instance.nullNode();
    }

    ArrayNode resultNode = JsonNodeFactory.instance.arrayNode();
    for (JsonNode arrayItem : node) {
      resultNode.add(processNode(schema.getItemsSchema(), arrayItem));
    }

    return resultNode;
  }

  private JsonNode parseObject(final Schema schema, final JsonNode node) {
    if (!node.isObject()) {
      return JsonNodeFactory.instance.nullNode();
    }

    ObjectNode resultNode = JsonNodeFactory.instance.objectNode();

    for (Map.Entry<String, Schema> entry : schema.getProperties().entrySet()) {
      String entryKey = entry.getKey();
      Schema propSchema = entry.getValue();

      JsonNode value = processNode(propSchema, unwrap(schema, node, entryKey));

      if (value != null) {
        resultNode.set(entryKey, value);
      }
    }

    return resultNode;
  }

  private JsonNode unwrap(final Schema schema, final JsonNode content, final String defaultKey) {
    Xml xmlConf = schema.getXml();

    if (TYPE_ARRAY.equals(schema.getSupposedType())) {
      // is array wrapped ?
      if (xmlConf != null && xmlConf.isWrapped()) {
        if (xmlConf.getName() != null) {
          return getRenamedNode(xmlConf, content.get(xmlConf.getName()), xmlConf.getName());
        }

        // fallback for array as root element
        if (content.size() == 1) {
          return content.fields().next().getValue();
        } else {
          return JsonNodeFactory.instance.nullNode();
        }
      }

      // is unwrapped array has a renamed node ?
      xmlConf = schema.getItemsSchema().getXml();
      if (xmlConf != null) {
        return getRenamedNode(xmlConf, content, xmlConf.getName());
      }
    } else if (TYPE_OBJECT.equals(schema.getSupposedType())) {
      return getRenamedNode(xmlConf, content, defaultKey);
    }

    return content;
  }

  private JsonNode getRenamedNode(final Xml xmlConf, final JsonNode content, final String defaultKey) {
    if (xmlConf != null && xmlConf.getName() != null) {
      return content.get(xmlConf.getName());
    } else if (defaultKey != null) {
      return content.get(defaultKey);
    }

    return content;
  }
}
