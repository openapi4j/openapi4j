package org.perf.check.schema;

import com.fasterxml.jackson.databind.JsonNode;

import org.openapi4j.core.exception.ResolutionException;
import org.openapi4j.core.validation.ValidationResults;
import org.openapi4j.schema.validator.v3.SchemaValidator;

class OpenApi4j implements JsonValidator {
  private final SchemaValidator schemaValidator;

  OpenApi4j(JsonNode schema) throws ResolutionException {
    schemaValidator = new SchemaValidator("schemas", schema);
  }

  @Override
  public String validate(JsonNode data) {
    ValidationResults results = new ValidationResults();
    schemaValidator.validate(data, results);
    if (!results.isValid()) {
      return results.toString();
    }

    return null;
  }

  @Override
  public String getVersion() {
    return ValidationResults.class.getPackage().getImplementationVersion();
  }
}
