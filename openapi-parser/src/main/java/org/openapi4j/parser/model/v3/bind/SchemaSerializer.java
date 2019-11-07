package org.openapi4j.parser.model.v3.bind;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import org.openapi4j.parser.model.v3.Schema;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Map;

public class SchemaSerializer extends StdSerializer<Schema> {
  protected SchemaSerializer() {
    this(Schema.class);
  }

  protected SchemaSerializer(Class<Schema> t) {
    super(t);
  }

  @Override
  public void serialize(Schema schema, JsonGenerator gen, SerializerProvider provider) throws IOException {
    gen.writeStartObject();

    if (schema.isRef()) {
      addStringField(gen, "$ref", schema.getRef());
      addStringField(gen, "description", schema.getDescription());
    } else {
      addStringField(gen, "title", schema.getTitle());
      addNumberField(gen, "multipleOf", schema.getMultipleOf());
      addNumberField(gen, "maximum", schema.getMaximum());
      addBooleanField(gen, "exclusiveMaximum", schema.getExclusiveMaximum());
      addNumberField(gen, "minimum", schema.getMinimum());
      addBooleanField(gen, "exclusiveMinimum", schema.getExclusiveMinimum());
      addNumberField(gen, "maxLength", schema.getMaxLength());
      addNumberField(gen, "minLength", schema.getMinLength());
      addStringField(gen, "pattern", schema.getPattern());
      addNumberField(gen, "maxItems", schema.getMaxItems());
      addNumberField(gen, "minItems", schema.getMinItems());
      addBooleanField(gen, "uniqueItems", schema.getUniqueItems());
      addNumberField(gen, "maxProperties", schema.getMaxProperties());
      addNumberField(gen, "minProperties", schema.getMinProperties());
      addStringCollection(gen, "required", schema.getRequiredFields());
      addStringCollection(gen, "enum", schema.getEnums());
      addStringField(gen, "type", schema.getType());
      addSchemaCollection(gen, "allOf", schema.getAllOfSchemas());
      addSchemaCollection(gen, "oneOf", schema.getOneOfSchemas());
      addSchemaCollection(gen, "anyOf", schema.getAnyOfSchemas());
      addObjectField(gen, "not", schema.getNotSchema());
      addObjectField(gen, "items", schema.getItemsSchema());
      addMapField(gen, "properties", schema.getProperties());

      if (schema.getAdditionalProperties() != null) {
        gen.writeObjectField("additionalProperties", schema.getAdditionalProperties());
      } else if (schema.getAdditionalPropertiesAllowed() != null) {
        gen.writeBooleanField("additionalProperties", schema.getAdditionalPropertiesAllowed());
      }
      addStringField(gen, "description", schema.getDescription());
      addStringField(gen, "format", schema.getFormat());
      addObjectField(gen, "default", schema.getDefault());
      addBooleanField(gen, "nullable", schema.getNullable());
      addObjectField(gen, "discriminator", schema.getDiscriminator());
      addBooleanField(gen, "readOnly", schema.getReadOnly());
      addBooleanField(gen, "writeOnly", schema.getWriteOnly());
      addObjectField(gen, "xml", schema.getXml());
      addObjectField(gen, "externalDocs", schema.getExternalDocs());
      addObjectField(gen, "example", schema.getExample());
      addBooleanField(gen, "deprecated", schema.getDeprecated());

      if (schema.getExtensions() != null) {
        for (Map.Entry<String, Object> entry : schema.getExtensions().entrySet()) {
          gen.writeObjectField(entry.getKey(), entry.getValue());
        }
      }
    }

    gen.writeEndObject();
  }

  private void addBooleanField(JsonGenerator gen, String fieldName, Boolean value) throws IOException {
    if (value != null) {
      gen.writeBooleanField(fieldName, value);
    }
  }

  private void addObjectField(JsonGenerator gen, String fieldName, Object value) throws IOException {
    if (value != null) {
      gen.writeObjectField(fieldName, value);
    }
  }

  private void addStringField(JsonGenerator gen, String fieldName, String value) throws IOException {
    if (value != null) {
      gen.writeStringField(fieldName, value);
    }
  }

  private void addMapField(JsonGenerator gen, String fieldName, Map<String, Schema> value) throws IOException {
    if (value != null) {
      gen.writeObjectFieldStart(fieldName);
      for (Map.Entry<String, Schema> entry : value.entrySet()) {
        gen.writeObjectField(entry.getKey(), entry.getValue());
      }
      gen.writeEndObject();
    }
  }

  private void addNumberField(JsonGenerator gen, String fieldName, Number value) throws IOException {
    if (value != null) {
      if (value instanceof Integer) {
        gen.writeNumberField(fieldName, (Integer) value);
      } else if (value instanceof Long) {
        gen.writeNumberField(fieldName, (Long) value);
      } else if (value instanceof Float) {
        gen.writeNumberField(fieldName, (Float) value);
      } else if (value instanceof Double) {
        gen.writeNumberField(fieldName, (Double) value);
      } else if (value instanceof BigDecimal) {
        gen.writeNumberField(fieldName, (BigDecimal) value);
      }
    }
  }

  private void addStringCollection(JsonGenerator gen, String fieldName, Collection<String> collection) throws IOException {
    if (collection != null) {
      gen.writeArrayFieldStart(fieldName);
      for (String value : collection) {
        gen.writeString(value);
      }
      gen.writeEndArray();
    }
  }

  private void addSchemaCollection(JsonGenerator gen, String fieldName, Collection<Schema> collection) throws IOException {
    if (collection != null) {
      gen.writeArrayFieldStart(fieldName);
      for (Schema value : collection) {
        gen.writeObject(value);
      }
      gen.writeEndArray();
    }
  }
}
