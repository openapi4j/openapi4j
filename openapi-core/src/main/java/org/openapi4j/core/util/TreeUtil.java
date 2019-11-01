package org.openapi4j.core.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import org.openapi4j.core.exception.DecodeException;
import org.openapi4j.core.exception.EncodeException;
import org.openapi4j.core.exception.ResolutionException;
import org.openapi4j.core.model.AuthOption;

import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static java.util.Objects.requireNonNull;

/**
 * Utility class to manipulate tree structures.
 */
public final class TreeUtil {
  /**
   * The global JSON mapper.
   */
  public static final ObjectMapper json = new ObjectMapper();
  /**
   * The global YAML mapper.
   */
  public static final ObjectMapper yaml = new ObjectMapper(new YAMLFactory());

  private TreeUtil() {
  }

  /**
   * Encode a POJO to JSON using the underlying Jackson mapper.
   *
   * @param obj a POJO
   * @return a String containing the JSON representation of the given POJO.
   * @throws EncodeException if a property cannot be encoded.
   */
  public static String toJson(Object obj) throws EncodeException {
    try {
      return json.writeValueAsString(obj);
    } catch (Exception e) {
      throw new EncodeException("Failed to encode as JSON: " + e.getMessage());
    }
  }

  /**
   * Encode a POJO to JSON using the underlying Jackson mapper.
   *
   * @param obj a POJO
   * @return a JsonNode containing the JSON representation of the given POJO.
   * @throws EncodeException if a property cannot be encoded.
   */
  public static JsonNode toJsonNode(Object obj) throws EncodeException {
    try {
      return json.valueToTree(obj);
    } catch (Exception e) {
      throw new EncodeException("Failed to encode as JSON: " + e.getMessage());
    }
  }

  /**
   * Encode a POJO to YAML using the underlying Jackson mapper.
   *
   * @param obj a POJO
   * @return a String containing the YAML representation of the given POJO.
   * @throws EncodeException if a property cannot be encoded.
   */
  public static String toYaml(Object obj) throws EncodeException {
    try {
      return yaml.writeValueAsString(obj);
    } catch (Exception e) {
      throw new EncodeException("Failed to encode as YAML: " + e.getMessage());
    }
  }

  /**
   * Encode a POJO to YAML using the underlying Jackson mapper.
   *
   * @param obj a POJO
   * @return a JsonNode containing the YAML representation of the given POJO.
   * @throws EncodeException if a property cannot be encoded.
   */
  public static JsonNode toYamlNode(Object obj) throws EncodeException {
    try {
      return yaml.valueToTree(obj);
    } catch (Exception e) {
      throw new EncodeException("Failed to encode as YAML: " + e.getMessage());
    }
  }

  /**
   * Load a document and attempts to convert it to the desired given class.
   *
   * @param url   The url of the resource to load.
   * @param clazz The given class definition.
   * @return The content mapped.
   * @throws DecodeException
   */
  public static <T> T load(final URL url, Class<T> clazz) throws DecodeException {
    return load(url, null, clazz);
  }

  /**
   * Load a document and attempts to convert it to the desired given class.
   *
   * @param url         The url of the resource to load.
   * @param authOptions The authentication values.
   * @param clazz       The given class definition.
   * @return The content mapped.
   * @throws DecodeException
   */
  public static <T> T load(final URL url, final List<AuthOption> authOptions, Class<T> clazz) throws DecodeException {
    requireNonNull(url, "URL is required");

    try {
      InputStream in = UrlContentRetriever.get(url, authOptions);
      String json = IOUtil.toString(in, StandardCharsets.UTF_8).trim();

      if (json.startsWith("{") || json.startsWith("[")) {
        return TreeUtil.json.readValue(json, clazz);
      } else {
        return yaml.readValue(json, clazz);
      }
    } catch (Exception e) {
      throw new DecodeException("Failed to decode : " + e.getMessage());
    }
  }

  /**
   * Load a document and convert it to a tree node.
   *
   * @param url The url of the resource to load.
   * @return The content mapped.
   * @throws DecodeException
   */
  public static JsonNode load(final URL url) throws DecodeException {
    return load(url, (List<AuthOption>) null);
  }

  /**
   * Load a document and convert it to a tree node.
   *
   * @param url         The url of the resource to load.
   * @param authOptions The authentication values.
   * @return The content mapped.
   * @throws DecodeException
   */
  public static JsonNode load(final URL url, final List<AuthOption> authOptions) throws DecodeException {
    requireNonNull(url, "URL is required");

    try {
      InputStream in = UrlContentRetriever.get(url, authOptions);
      String json = IOUtil.toString(in, StandardCharsets.UTF_8).trim();

      if (json.startsWith("{") || json.startsWith("[")) {
        return TreeUtil.json.readTree(json);
      } else {
        return yaml.readTree(json);
      }
    } catch (Exception e) {
      throw new DecodeException("Failed to decode : " + e.getMessage());
    }
  }
}
