package org.perf.check.schema;

import com.fasterxml.jackson.databind.JsonNode;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.ValidationMessage;

import java.util.Set;

class Networknt implements JsonValidator {
  private final JsonSchema jsonSchema;

  Networknt(JsonNode schema) {
    JsonSchemaFactory factory = JsonSchemaFactory.getInstance();
    jsonSchema = factory.getSchema(schema);
  }

  @Override
  public String validate(final JsonNode data) {
    Set<ValidationMessage> results = jsonSchema.validate(data);
    if (results.size() > 0) {
      return results.toString();
    }

    return null;
  }

  @Override
  public String getVersion() {
    return JsonSchemaFactory.class.getPackage().getImplementationVersion();
  }
}
