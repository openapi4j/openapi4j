package org.openapi4j.core.model.reference;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;

import org.openapi4j.core.exception.DecodeException;
import org.openapi4j.core.util.TreeUtil;

import java.net.URI;

public class Reference {
  private static final String CLASS_MISMATCH_ERR_MSG = "Unable to map reference '%s' from class '%s' with class '%s'.";
  private static final String ERR_MSG = "Unable to map reference '%s' content with class '%s'.";

  private final URI baseUri;
  private final String ref;
  private JsonNode content;
  private Object mappedContent;

  Reference(URI baseUri, String ref, JsonNode content) {
    this.baseUri = baseUri;
    this.ref = ref;
    this.content = content;
  }

  /**
   * Get the base URI for external reference document.
   */
  URI getBaseUri() {
    return baseUri;
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
   * @throws DecodeException If the given class is not matching the content to map
   *                         or if the content has already been mapped and the
   *                         class does not correspond to the previous one.
   */
  @SuppressWarnings("unchecked")
  public <T> T getMappedContent(Class<T> tClass) throws DecodeException {
    if (mappedContent == null) {
      try {
        mappedContent = TreeUtil.json.treeToValue(content, tClass);
        return (T) mappedContent;
      } catch (JsonProcessingException e) {
        throw new DecodeException(String.format(ERR_MSG, ref, tClass.getSimpleName()), e);
      }
    } else if (mappedContent.getClass().equals(tClass)) {
      return (T) mappedContent;
    }

    throw new DecodeException(
      String.format(CLASS_MISMATCH_ERR_MSG,
        ref,
        mappedContent.getClass().getName(),
        tClass.getName()));
  }
}
