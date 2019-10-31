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

      if ("title".equals(name)) {
        schema.setTitle(jp.getText());
      } else if (MULTIPLEOF.equals(name)) {
        schema.setMultipleOf(jp.getIntValue());
      } else if (MAXIMUM.equals(name)) {
        schema.setMaximum(jp.getNumberValue());
      } else if (EXCLUSIVEMAXIMUM.equals(name)) {
        schema.setExclusiveMaximum(jp.getBooleanValue());
      } else if (MINIMUM.equals(name)) {
        schema.setMinimum(jp.getNumberValue());
      } else if (EXCLUSIVEMINIMUM.equals(name)) {
        schema.setExclusiveMinimum(jp.getBooleanValue());
      } else if (MAXLENGTH.equals(name)) {
        schema.setMaxLength(jp.getIntValue());
      } else if (MINLENGTH.equals(name)) {
        schema.setMinLength(jp.getIntValue());
      } else if (PATTERN.equals(name)) {
        schema.setPattern(jp.getText());
      } else if (MAXITEMS.equals(name)) {
        schema.setMaxItems(jp.getIntValue());
      } else if (MINITEMS.equals(name)) {
        schema.setMinItems(jp.getIntValue());
      } else if (UNIQUEITEMS.equals(name)) {
        schema.setUniqueItems(jp.getBooleanValue());
      } else if (MAXPROPERTIES.equals(name)) {
        schema.setMaxProperties(jp.getIntValue());
      } else if (MINPROPERTIES.equals(name)) {
        schema.setMinProperties(jp.getIntValue());
      } else if (REQUIRED.equals(name)) {
        schema.setRequiredFields(jp.readValueAs(new TypeReference<List<String>>() {
        }));
      } else if (ENUM.equals(name)) {
        schema.setEnums(jp.readValueAs(new TypeReference<List<String>>() {
        }));
      } else if (TYPE.equals(name)) {
        schema.setType(jp.getText());
      } else if (ALLOF.equals(name)) {
        schema.setAllOfSchemas(jp.readValueAs(new TypeReference<List<Schema>>() {
        }));
      } else if (ONEOF.equals(name)) {
        schema.setOneOfSchemas(jp.readValueAs(new TypeReference<List<Schema>>() {
        }));
      } else if (ANYOF.equals(name)) {
        schema.setAnyOfSchemas(jp.readValueAs(new TypeReference<List<Schema>>() {
        }));
      } else if (NOT.equals(name)) {
        schema.setNotSchema(jp.readValueAs(new TypeReference<Schema>() {
        }));
      } else if (ITEMS.equals(name)) {
        schema.setItemsSchema(jp.readValueAs(new TypeReference<Schema>() {
        }));
      } else if (PROPERTIES.equals(name)) {
        schema.setProperties(jp.readValueAs(new TypeReference<Map<String, Schema>>() {
        }));
      } else if (ADDITIONALPROPERTIES.equals(name)) {
        if (token.isBoolean()) {
          schema.setAdditionalPropertiesAllowed(jp.getBooleanValue());
        } else if (token.isStructStart()) {
          schema.setAdditionalProperties(jp.readValueAs(new TypeReference<Schema>() {
          }));
        } else {
          throw new IOException("Unexpected value type");
        }
      } else if ("description".equals(name)) {
        schema.setDescription(jp.getText());
      } else if (FORMAT.equals(name)) {
        schema.setFormat(jp.getText());
      } else if (DEFAULT.equals(name)) {
        schema.setDefault(jp.readValueAs(new TypeReference<Object>() {
        }));
      } else if (NULLABLE.equals(name)) {
        schema.setNullable(jp.getBooleanValue());
      } else if (DISCRIMINATOR.equals(name)) {
        schema.setDiscriminator(jp.readValueAs(new TypeReference<Discriminator>() {
        }));
      } else if ("readOnly".equals(name)) {
        schema.setReadOnly(jp.getBooleanValue());
      } else if ("writeOnly".equals(name)) {
        schema.setWriteOnly(jp.getBooleanValue());
      } else if ("xml".equals(name)) {
        schema.setXml(jp.readValueAs(new TypeReference<Xml>() {
        }));
      } else if ("externalDocs".equals(name)) {
        schema.setExternalDocs(jp.readValueAs(new TypeReference<ExternalDocs>() {
        }));
      } else if ("example".equals(name)) {
        schema.setExample(jp.readValueAs(new TypeReference<Object>() {
        }));
      } else if ("deprecated".equals(name)) {
        schema.setDeprecated(jp.getBooleanValue());
      } else if ($REF.equals(name)) {
        schema.set$ref(jp.getValueAsString());
      }
    }

    return schema;
  }
}
