package org.openapi4j.operation.validator.adapters.spring.mock;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.openapi4j.core.exception.ResolutionException;
import org.openapi4j.core.validation.ValidationException;
import org.openapi4j.parser.OpenApi3Parser;
import org.openapi4j.parser.model.v3.OpenApi3;
import org.springframework.core.io.Resource;

/**
 * A simple cache to avoid multiple loading and parsing of Open API specs.
 */
public class OpenApiCache {

  public static final OpenApiCache INSTANCE = new OpenApiCache();

  private final ConcurrentMap<Resource, OpenApi3> cache = new ConcurrentHashMap<>();

  OpenApiCache() {
  }

  /**
   * Loads and parses an Open API spec.
   * @param spec the resource to load the spec from
   * @return The parsed spec without servers set
   * @throws IllegalArgumentException if any problem reading or parsing the spec occurs
   */
  public OpenApi3 loadApi(Resource spec) {
    return cache.computeIfAbsent(spec, this::loadApiWithoutServers);
  }

  private OpenApi3 loadApiWithoutServers(Resource spec) {
    try {
      OpenApi3 result = new OpenApi3Parser().parse(spec.getURL(), false);
      result.setServers(null);
      return result;
    } catch (IOException | ResolutionException | ValidationException e) {
      throw new IllegalArgumentException("Cannot load OpenAPI Spec " + spec, e);
    }
  }
}
