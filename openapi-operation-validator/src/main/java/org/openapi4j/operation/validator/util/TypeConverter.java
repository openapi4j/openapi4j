package org.openapi4j.operation.validator.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.openapi4j.parser.model.v3.Schema;

import java.math.BigDecimal;
import java.math.BigInteger;
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

  public JsonNode convertObject(final Schema schema,
                                final Map<String, Object> content) {

    if (schema == null || content == null) {
      return JsonNodeFactory.instance.nullNode();
    }

    Map<String, Schema> properties = schema.getProperties();
    if (properties == null || properties.isEmpty()) {
      return JsonNodeFactory.instance.nullNode();
    }

    ObjectNode convertedContent = JsonNodeFactory.instance.objectNode();

    for (Map.Entry<String, Schema> entry : properties.entrySet()) {
      String entryKey = entry.getKey();

      if (!content.containsKey(entryKey)) {
        continue;
      }

      Object value = content.get(entryKey);

      Schema propSchema = entry.getValue();
      switch (propSchema.getSupposedType()) {
        case TYPE_OBJECT:
          convertedContent.set(entryKey, convertObject(propSchema, castMap(value)));
          break;
        case TYPE_ARRAY:
          convertedContent.set(entryKey, convertArray(propSchema.getItemsSchema(), castList(value)));
          break;
        default:
          convertedContent.set(entryKey, convertPrimitive(propSchema, value));
          break;
      }
    }

    return convertedContent;
  }

  public JsonNode convertArray(final Schema schema,
                               final Collection<Object> content) {

    if (schema == null || content == null) {
      return JsonNodeFactory.instance.nullNode();
    }

    ArrayNode convertedContent = JsonNodeFactory.instance.arrayNode();

    switch (schema.getSupposedType()) {
      case TYPE_OBJECT:
        for (Object value : content) {
          convertedContent.add(convertObject(schema, castMap(value)));
        }
        break;
      case TYPE_ARRAY:
        for (Object value : content) {
          convertedContent.add(convertArray(schema.getItemsSchema(), castList(value)));
        }
        break;
      default:
        for (Object value : content) {
          convertedContent.add(convertPrimitive(schema, value));
        }
        break;
    }

    return convertedContent;
  }

  public JsonNode convertPrimitive(final Schema schema, Object value) {
    if (schema == null || value == null) {
      return JsonNodeFactory.instance.nullNode();
    }

    try {
      switch (schema.getSupposedType()) {
        case TYPE_BOOLEAN:
          return JsonNodeFactory.instance.booleanNode(parseBoolean(value.toString()));
        case TYPE_INTEGER:
          if (FORMAT_INT32.equals(schema.getFormat())) {
            return JsonNodeFactory.instance.numberNode(Integer.parseInt(value.toString()));
          } else if (FORMAT_INT64.equals(schema.getFormat())) {
            return JsonNodeFactory.instance.numberNode(Long.parseLong(value.toString()));
          } else {
            return JsonNodeFactory.instance.numberNode(new BigInteger(value.toString()));
          }
        case TYPE_NUMBER:
          if (FORMAT_FLOAT.equals(schema.getFormat())) {
            return JsonNodeFactory.instance.numberNode(Float.parseFloat(value.toString()));
          } else if (FORMAT_DOUBLE.equals(schema.getFormat())) {
            return JsonNodeFactory.instance.numberNode(Double.parseDouble(value.toString()));
          } else {
            return JsonNodeFactory.instance.numberNode(new BigDecimal(value.toString()));
          }
        case TYPE_STRING:
        default:
          return JsonNodeFactory.instance.textNode(value.toString());
      }
    } catch (IllegalArgumentException ex) {
      return JsonNodeFactory.instance.nullNode();
    }
  }

  /**
   * Parse boolean with exception if the value is not a boolean at all.
   * @param value The boolean value to parse.
   * @return If the value is not a boolean representation.
   */
  private boolean parseBoolean(String value) {
    value = value.trim().toLowerCase();

    if ("true".equals(value)) {
      return true;
    } else if ("false".equals(value)) {
      return false;
    }

    throw new IllegalArgumentException(value);
  }

  @SuppressWarnings("unchecked")
  private Map<String, Object> castMap(Object obj) {
    try {
      return (Map<String, Object>) obj;
    } catch (ClassCastException ex) {
      return null;
    }
  }

  @SuppressWarnings("unchecked")
  private Collection<Object> castList(Object obj) {
    try {
      return (Collection<Object>) obj;
    } catch (ClassCastException ex) {
      return null;
    }
  }
}
