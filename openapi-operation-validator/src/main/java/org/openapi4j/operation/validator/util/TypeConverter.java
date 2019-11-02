package org.openapi4j.operation.validator.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.openapi4j.parser.model.v3.Schema;

import java.util.Collection;
import java.util.Map;

import static org.openapi4j.core.model.v3.OAI3SchemaKeywords.*;

public final class TypeConverter {
  private static final TypeConverter INSTANCE = new TypeConverter();

  private TypeConverter() {
  }

  public static TypeConverter instance() {
    return INSTANCE;
  }

  public JsonNode convertTypes(final Schema schema,
                               final Map<String, Object> content) {

    String type = schema.getType() != null ? schema.getType() : TYPE_OBJECT;
    switch (type) {
      case TYPE_ARRAY:
        return convertArray(schema.getItemsSchema(), content);
      case TYPE_OBJECT:
      default:
        return convertObject(schema, content);
    }
  }

  @SuppressWarnings("unchecked")
  public JsonNode convertObject(final Schema schema,
                                final Map<String, Object> content) {

    Map<String, Schema> properties = schema.getProperties();
    if (properties == null) {
      return JsonNodeFactory.instance.nullNode();
    }

    ObjectNode convertedContent = JsonNodeFactory.instance.objectNode();

    for (Map.Entry<String, Schema> entry : properties.entrySet()) {
      String entryKey = entry.getKey();

      Object value = content.get(entryKey);
      if (value == null) {
        convertedContent.set(entryKey, JsonNodeFactory.instance.nullNode());
        continue;
      }

      Schema propSchema = entry.getValue();
      String type = propSchema.getType() != null ? propSchema.getType() : TYPE_OBJECT;
      switch (type) {
        case TYPE_OBJECT:
          if (value instanceof Map) {
            convertedContent.set(entryKey, convertObject(propSchema, (Map<String, Object>) value));
          }
          break;
        case TYPE_ARRAY:
          convertedContent.set(entryKey, convertArray(propSchema.getItemsSchema(), value));
          break;
        default:
          convertedContent.set(entryKey, convertPrimitiveType(propSchema, value));
          break;
      }
    }

    return convertedContent;
  }

  @SuppressWarnings("unchecked")
  public JsonNode convertArray(final Schema schema,
                               final Object content) {

    if (schema == null) {
      return JsonNodeFactory.instance.nullNode();
    }

    ArrayNode convertedContent = JsonNodeFactory.instance.arrayNode();

    String type = schema.getType() != null ? schema.getType() : TYPE_OBJECT;
    switch (type) {
      case TYPE_OBJECT:
        if (content instanceof Map) {
          convertedContent.add(convertObject(schema, (Map<String, Object>) content));
        }
        break;
      case TYPE_ARRAY:
        convertedContent.add(convertArray(schema.getItemsSchema(), content));
        break;
      default:
        if (content instanceof Collection) {
          for (Object value : (Collection) content) {
            convertedContent.add(convertPrimitiveType(schema, value));
          }
        }
        break;
    }

    return convertedContent;
  }

  public JsonNode convertPrimitiveType(final Schema schema, Object value) {
    if (value == null) {
      return JsonNodeFactory.instance.nullNode();
    }

    String type = schema.getType() != null ? schema.getType() : TYPE_OBJECT;
    switch (type) {
      case TYPE_BOOLEAN:
        return JsonNodeFactory.instance.booleanNode(Boolean.parseBoolean(value.toString()));
      case TYPE_INTEGER:
        if (FORMAT_INT32.equals(schema.getFormat())) {
          return JsonNodeFactory.instance.numberNode(Integer.parseInt(value.toString()));
        } else {
          return JsonNodeFactory.instance.numberNode(Long.parseLong(value.toString()));
        }
      case TYPE_NUMBER:
        if (FORMAT_FLOAT.equals(schema.getFormat())) {
          return JsonNodeFactory.instance.numberNode(Float.parseFloat(value.toString()));
        } else {
          return JsonNodeFactory.instance.numberNode(Double.parseDouble(value.toString()));
        }
      case TYPE_STRING:
      default:
        return JsonNodeFactory.instance.textNode(value.toString());
    }
  }
}
