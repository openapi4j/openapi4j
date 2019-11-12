package org.openapi4j.parser.model.v3.bind;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import org.openapi4j.parser.model.v3.Schema;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Map;

import static org.openapi4j.core.model.v3.OAI3SchemaKeywords.$REF;
import static org.openapi4j.core.model.v3.OAI3SchemaKeywords.ADDITIONALPROPERTIES;
import static org.openapi4j.core.model.v3.OAI3SchemaKeywords.ALLOF;
import static org.openapi4j.core.model.v3.OAI3SchemaKeywords.ANYOF;
import static org.openapi4j.core.model.v3.OAI3SchemaKeywords.DEFAULT;
import static org.openapi4j.core.model.v3.OAI3SchemaKeywords.DISCRIMINATOR;
import static org.openapi4j.core.model.v3.OAI3SchemaKeywords.ENUM;
import static org.openapi4j.core.model.v3.OAI3SchemaKeywords.EXCLUSIVEMAXIMUM;
import static org.openapi4j.core.model.v3.OAI3SchemaKeywords.EXCLUSIVEMINIMUM;
import static org.openapi4j.core.model.v3.OAI3SchemaKeywords.FORMAT;
import static org.openapi4j.core.model.v3.OAI3SchemaKeywords.ITEMS;
import static org.openapi4j.core.model.v3.OAI3SchemaKeywords.MAXIMUM;
import static org.openapi4j.core.model.v3.OAI3SchemaKeywords.MAXITEMS;
import static org.openapi4j.core.model.v3.OAI3SchemaKeywords.MAXLENGTH;
import static org.openapi4j.core.model.v3.OAI3SchemaKeywords.MAXPROPERTIES;
import static org.openapi4j.core.model.v3.OAI3SchemaKeywords.MINIMUM;
import static org.openapi4j.core.model.v3.OAI3SchemaKeywords.MINITEMS;
import static org.openapi4j.core.model.v3.OAI3SchemaKeywords.MINLENGTH;
import static org.openapi4j.core.model.v3.OAI3SchemaKeywords.MINPROPERTIES;
import static org.openapi4j.core.model.v3.OAI3SchemaKeywords.MULTIPLEOF;
import static org.openapi4j.core.model.v3.OAI3SchemaKeywords.NOT;
import static org.openapi4j.core.model.v3.OAI3SchemaKeywords.NULLABLE;
import static org.openapi4j.core.model.v3.OAI3SchemaKeywords.ONEOF;
import static org.openapi4j.core.model.v3.OAI3SchemaKeywords.PATTERN;
import static org.openapi4j.core.model.v3.OAI3SchemaKeywords.PROPERTIES;
import static org.openapi4j.core.model.v3.OAI3SchemaKeywords.REQUIRED;
import static org.openapi4j.core.model.v3.OAI3SchemaKeywords.TYPE;
import static org.openapi4j.core.model.v3.OAI3SchemaKeywords.UNIQUEITEMS;

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
      addStringField(gen, $REF, schema.getRef());
      addStringField(gen, "description", schema.getDescription());
    } else {
      addStringField(gen, "title", schema.getTitle());
      addNumberField(gen, MULTIPLEOF, schema.getMultipleOf());
      addNumberField(gen, MAXIMUM, schema.getMaximum());
      addBooleanField(gen, EXCLUSIVEMAXIMUM, schema.getExclusiveMaximum());
      addNumberField(gen, MINIMUM, schema.getMinimum());
      addBooleanField(gen, EXCLUSIVEMINIMUM, schema.getExclusiveMinimum());
      addNumberField(gen, MAXLENGTH, schema.getMaxLength());
      addNumberField(gen, MINLENGTH, schema.getMinLength());
      addStringField(gen, PATTERN, schema.getPattern());
      addNumberField(gen, MAXITEMS, schema.getMaxItems());
      addNumberField(gen, MINITEMS, schema.getMinItems());
      addBooleanField(gen, UNIQUEITEMS, schema.getUniqueItems());
      addNumberField(gen, MAXPROPERTIES, schema.getMaxProperties());
      addNumberField(gen, MINPROPERTIES, schema.getMinProperties());
      addStringCollection(gen, REQUIRED, schema.getRequiredFields());
      addStringCollection(gen, ENUM, schema.getEnums());
      addStringField(gen, TYPE, schema.getType());
      addSchemaCollection(gen, ALLOF, schema.getAllOfSchemas());
      addSchemaCollection(gen, ONEOF, schema.getOneOfSchemas());
      addSchemaCollection(gen, ANYOF, schema.getAnyOfSchemas());
      addObjectField(gen, NOT, schema.getNotSchema());
      addObjectField(gen, ITEMS, schema.getItemsSchema());
      addMapField(gen, PROPERTIES, schema.getProperties());

      if (schema.getAdditionalProperties() != null) {
        gen.writeObjectField(ADDITIONALPROPERTIES, schema.getAdditionalProperties());
      } else if (schema.getAdditionalPropertiesAllowed() != null) {
        gen.writeBooleanField(ADDITIONALPROPERTIES, schema.getAdditionalPropertiesAllowed());
      }
      addStringField(gen, "description", schema.getDescription());
      addStringField(gen, FORMAT, schema.getFormat());
      addObjectField(gen, DEFAULT, schema.getDefault());
      addBooleanField(gen, NULLABLE, schema.getNullable());
      addObjectField(gen, DISCRIMINATOR, schema.getDiscriminator());
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
