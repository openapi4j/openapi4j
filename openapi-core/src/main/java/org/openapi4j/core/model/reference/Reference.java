package org.openapi4j.core.model.reference;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import org.openapi4j.core.exception.DecodeException;
import org.openapi4j.core.util.TreeUtil;

import java.net.URL;

/**
 * The reference model.
 */
public class Reference {
  public static final String ABS_REF_FIELD = "abs$ref";

  private static final String ERR_MSG = "Unable to map reference '%s' content with class '%s'.";

  // The URL from where the reference expression applies
  private final URL baseUrl;
  // The reference canonical expression
  private final String canonicalRef;
  // The reference expression
  private final String ref;
  // The raw content of the targeted reference expression
  private JsonNode content;
  // The mapped content of the targeted reference expression
  private Object mappedContent;

  Reference(URL baseUrl, String canonicalRef, String ref) {
    this.baseUrl = baseUrl;
    this.canonicalRef = canonicalRef;
    this.ref = ref;
    this.content = null;
  }

  /**
   * Get the base URL for external reference document.
   */
  URL getBaseUrl() {
    return baseUrl;
  }

  /**
   * Get the canonical reference string
   */
  public String getCanonicalRef() {
    return canonicalRef;
  }

  /**
   * Get the reference string
   */
  public String getRef() {
    return ref;
  }

  /**
   * Get the reference content
   */
  public JsonNode getContent() {
    return content;
  }

  void setContent(JsonNode content) {
    this.content = content;
  }

  /**
   * Get the mapped content from the given the class to enable conversion.
   *
   * @param tClass The given the class to enable conversion.
   * @return The POJO from the raw content.
   * @throws DecodeException If the given class is not matching the content to map.
   */
  @SuppressWarnings("unchecked")
  public <T> T getMappedContent(Class<T> tClass) throws DecodeException {
    if (mappedContent == null || !mappedContent.getClass().equals(tClass)) {
      try {
        mappedContent = TreeUtil.json.treeToValue(content, tClass);
        return (T) mappedContent;
      } catch (JsonProcessingException | RuntimeException e) {
        throw new DecodeException(String.format(ERR_MSG, ref, tClass.getSimpleName()), e);
      }
    } else {
      return (T) mappedContent;
    }
  }
}
