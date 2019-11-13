package org.perf.check.schema;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;

import static org.perf.check.BuildConfig.JSONTOOLS_VERSION;

public class JsonTools implements JsonValidator {
  private final JsonSchema jsonSchema;

  JsonTools(JsonNode schema) throws ProcessingException {
    JsonSchemaFactory factory = JsonSchemaFactory.byDefault();
    jsonSchema = factory.getJsonSchema(schema);
  }

  @Override
  public String validate(final JsonNode data) {
    try {
      jsonSchema.validate(data);
    } catch (ProcessingException e) {
      return e.getMessage();
    }

    return null;
  }

  @Override
  public String getVersion() {
    return JSONTOOLS_VERSION;
  }
}
