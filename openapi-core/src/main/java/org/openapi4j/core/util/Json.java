package org.openapi4j.core.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import org.openapi4j.core.exception.DecodeException;
import org.openapi4j.core.exception.EncodeException;

import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import static java.util.Objects.requireNonNull;

public final class Json {
  public static final ObjectMapper jsonMapper = new ObjectMapper();
  public static final ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());

  private Json() {
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
      return jsonMapper.writeValueAsString(obj);
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
      return jsonMapper.valueToTree(obj);
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
      return yamlMapper.writeValueAsString(obj);
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
      return yamlMapper.valueToTree(obj);
    } catch (Exception e) {
      throw new EncodeException("Failed to encode as YAML: " + e.getMessage());
    }
  }

  public static <T> T load(URL url, Class<T> clazz) throws DecodeException {
    requireNonNull(url, "URL is required");

    try {
      InputStream in = url.openStream();
      String json = IOUtil.toString(in, StandardCharsets.UTF_8).trim();

      if (json.startsWith("{") || json.startsWith("[")) {
        return jsonMapper.readValue(json, clazz);
      } else {
        return yamlMapper.readValue(json, clazz);
      }
    } catch (Exception e) {
      throw new DecodeException("Failed to decode : " + e.getMessage());
    }
  }

  public static JsonNode load(URL url) throws DecodeException {
    requireNonNull(url, "URL is required");

    try {
      InputStream in = url.openStream();
      String json = IOUtil.toString(in, StandardCharsets.UTF_8).trim();

      if (json.startsWith("{") || json.startsWith("[")) {
        return jsonMapper.readTree(json);
      } else {
        return yamlMapper.readTree(json);
      }
    } catch (Exception e) {
      throw new DecodeException("Failed to decode : " + e.getMessage());
    }
  }
}
