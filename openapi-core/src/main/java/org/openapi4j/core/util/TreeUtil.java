package org.openapi4j.core.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import org.openapi4j.core.exception.DecodeException;
import org.openapi4j.core.exception.EncodeException;
import org.openapi4j.core.model.AuthOption;

import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Objects.requireNonNull;

/**
 * Utility class to manipulate tree structures.
 */
public final class TreeUtil {
  private static final String URL_REQUIRED_ERR_MSG = "URL is required.";
  public static final String ENCODE_ERR_MSG = "Failed to encode: %s";
  private static final String DECODE_ERR_MSG = "Failed to decode: %s";

  private static final Pattern FIRST_CHAR_PATTERN = Pattern.compile("(?:\\s*)(.)");

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
      throw new EncodeException(String.format(ENCODE_ERR_MSG, e.getMessage()));
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
      throw new EncodeException(String.format(ENCODE_ERR_MSG, e.getMessage()));
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
      throw new EncodeException(String.format(ENCODE_ERR_MSG, e.getMessage()));
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
    requireNonNull(url, URL_REQUIRED_ERR_MSG);

    try {
      InputStream in = UrlContentRetriever.instance().get(url, authOptions);
      String content = IOUtil.toString(in, StandardCharsets.UTF_8.name());
      String firstChar = getFirstVisibleCharacter(content);

      if ("{".equals(firstChar) || "[".equals(firstChar)) {
        return TreeUtil.json.readValue(content, clazz);
      } else {
        return TreeUtil.yaml.readValue(content, clazz);
      }
    } catch (Exception e) {
      throw new DecodeException(String.format(DECODE_ERR_MSG, e.getMessage()));
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
    requireNonNull(url, URL_REQUIRED_ERR_MSG);

    try {
      InputStream in = UrlContentRetriever.instance().get(url, authOptions);
      String content = IOUtil.toString(in, StandardCharsets.UTF_8.name());
      String firstChar = getFirstVisibleCharacter(content);

      if ("{".equals(firstChar) || "[".equals(firstChar)) {
        return TreeUtil.json.readTree(content);
      } else {
        return TreeUtil.yaml.readTree(content);
      }
    } catch (Exception e) {
      throw new DecodeException(String.format(DECODE_ERR_MSG, e.getMessage()));
    }
  }

  private static String getFirstVisibleCharacter(String content) {
    Matcher matcher = FIRST_CHAR_PATTERN.matcher(content);
    if (matcher.find()) {
      return matcher.group(1);
    }
    return null;
  }
}
