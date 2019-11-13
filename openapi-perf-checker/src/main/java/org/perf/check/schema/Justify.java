package org.perf.check.schema;

import com.fasterxml.jackson.databind.JsonNode;

import org.leadpony.justify.api.JsonSchema;
import org.leadpony.justify.api.JsonValidationService;
import org.leadpony.justify.api.ProblemHandler;
import org.openapi4j.core.exception.EncodeException;
import org.openapi4j.core.util.TreeUtil;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import javax.json.stream.JsonParser;

import static org.perf.check.BuildConfig.JUSTIFY_VERSION;

class Justify implements JsonValidator {
  private final JsonValidationService service;
  private final JsonSchema schema;
  private final ProblemHandler handler;
  private final AtomicReference<String> result;
  private final Map<JsonNode, ByteArrayInputStream> cachedData;

  Justify(JsonNode schema) throws Exception {
    service = JsonValidationService.newInstance();

    this.schema = service.readSchema(new ByteArrayInputStream(TreeUtil.toJson(schema).getBytes()));
    result = new AtomicReference<>();
    handler = service.createProblemPrinter(result::set);
    cachedData = new HashMap<>();
  }

  @Override
  public String validate(JsonNode data) {
    ByteArrayInputStream bais;

    try {
      if (cachedData.containsKey(data)) {
        bais = cachedData.get(data);
        bais.reset();
      } else {
        bais = new ByteArrayInputStream(TreeUtil.toJson(data).getBytes());
        cachedData.put(data, bais);
      }

      JsonParser parser = service.createParser(bais, schema, handler);
      while (parser.hasNext()) {
        parser.next();
      }
    } catch (EncodeException e) {
      return e.getMessage();
    }

    return result.get();
  }

  @Override
  public String getVersion() {
    return JUSTIFY_VERSION;
  }
}
