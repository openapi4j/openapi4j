package org.perf.check.schema;

import com.fasterxml.jackson.databind.JsonNode;

public interface JsonValidator {
  String validate(JsonNode data);

  String getVersion();
}
