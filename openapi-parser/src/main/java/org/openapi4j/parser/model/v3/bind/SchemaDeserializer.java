package org.openapi4j.parser.model.v3.bind;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import org.openapi4j.parser.model.v3.Discriminator;
import org.openapi4j.parser.model.v3.ExternalDocs;
import org.openapi4j.parser.model.v3.Schema;
import org.openapi4j.parser.model.v3.Xml;

import java.io.IOException;
import java.util.List;
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

public class SchemaDeserializer extends StdDeserializer<Schema> {
  protected SchemaDeserializer() {
    this(Schema.class);
  }

  protected SchemaDeserializer(Class<Schema> vc) {
    super(vc);
  }

  @Override
  public Schema deserialize(JsonParser jp, DeserializationContext context) throws IOException {
    Schema schema = new Schema();

    while (jp.nextToken() != JsonToken.END_OBJECT) {
      String name = jp.getCurrentName();
      JsonToken token = jp.nextToken();

      switch (name) {
        case "title":
          schema.setTitle(jp.getText());
          break;
        case MULTIPLEOF:
          schema.setMultipleOf(jp.getIntValue());
          break;
        case MAXIMUM:
          schema.setMaximum(jp.getNumberValue());
          break;
        case EXCLUSIVEMAXIMUM:
          schema.setExclusiveMaximum(jp.getBooleanValue());
          break;
        case MINIMUM:
          schema.setMinimum(jp.getNumberValue());
          break;
        case EXCLUSIVEMINIMUM:
          schema.setExclusiveMinimum(jp.getBooleanValue());
          break;
        case MAXLENGTH:
          schema.setMaxLength(jp.getIntValue());
          break;
        case MINLENGTH:
          schema.setMinLength(jp.getIntValue());
          break;
        case PATTERN:
          schema.setPattern(jp.getText());
          break;
        case MAXITEMS:
          schema.setMaxItems(jp.getIntValue());
          break;
        case MINITEMS:
          schema.setMinItems(jp.getIntValue());
          break;
        case UNIQUEITEMS:
          schema.setUniqueItems(jp.getBooleanValue());
          break;
        case MAXPROPERTIES:
          schema.setMaxProperties(jp.getIntValue());
          break;
        case MINPROPERTIES:
          schema.setMinProperties(jp.getIntValue());
          break;
        case REQUIRED:
          schema.setRequiredFields(jp.readValueAs(new TypeReference<List<String>>() {
          }));
          break;
        case ENUM:
          schema.setEnums(jp.readValueAs(new TypeReference<List<String>>() {
          }));
          break;
        case TYPE:
          schema.setType(jp.getText());
          break;
        case ALLOF:
          schema.setAllOfSchemas(jp.readValueAs(new TypeReference<List<Schema>>() {
          }));
          break;
        case ONEOF:
          schema.setOneOfSchemas(jp.readValueAs(new TypeReference<List<Schema>>() {
          }));
          break;
        case ANYOF:
          schema.setAnyOfSchemas(jp.readValueAs(new TypeReference<List<Schema>>() {
          }));
          break;
        case NOT:
          schema.setNotSchema(jp.readValueAs(new TypeReference<Schema>() {
          }));
          break;
        case ITEMS:
          schema.setItemsSchema(jp.readValueAs(new TypeReference<Schema>() {
          }));
          break;
        case PROPERTIES:
          schema.setProperties(jp.readValueAs(new TypeReference<Map<String, Schema>>() {
          }));
          break;
        case ADDITIONALPROPERTIES:
          if (token.isBoolean()) {
            schema.setAdditionalPropertiesAllowed(jp.getBooleanValue());
          } else if (token.isStructStart()) {
            schema.setAdditionalProperties(jp.readValueAs(new TypeReference<Schema>() {
            }));
          } else {
            throw new IOException("Unexpected value type");
          }
          break;
        case "description":
          schema.setDescription(jp.getText());
          break;
        case FORMAT:
          schema.setFormat(jp.getText());
          break;
        case DEFAULT:
          schema.setDefault(jp.readValueAs(new TypeReference<Object>() {
          }));
          break;
        case NULLABLE:
          schema.setNullable(jp.getBooleanValue());
          break;
        case DISCRIMINATOR:
          schema.setDiscriminator(jp.readValueAs(new TypeReference<Discriminator>() {
          }));
          break;
        case "readOnly":
          schema.setReadOnly(jp.getBooleanValue());
          break;
        case "writeOnly":
          schema.setWriteOnly(jp.getBooleanValue());
          break;
        case "xml":
          schema.setXml(jp.readValueAs(new TypeReference<Xml>() {
          }));
          break;
        case "externalDocs":
          schema.setExternalDocs(jp.readValueAs(new TypeReference<ExternalDocs>() {
          }));
          break;
        case "example":
          schema.setExample(jp.readValueAs(new TypeReference<Object>() {
          }));
          break;
        case "deprecated":
          schema.setDeprecated(jp.getBooleanValue());
          break;
        case $REF:
          schema.setRef(jp.getValueAsString());
          break;
        default:
          break;
      }
    }

    return schema;
  }
}
