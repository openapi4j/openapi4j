package org.openapi4j.operation.validator.model.impl;

import org.openapi4j.operation.validator.util.ContentType;

import java.util.Objects;
import java.util.regex.Pattern;

public class MediaTypeContainer {
  private static final String TEXT_TYPE = "text";
  private static final Pattern PLACEHOLDER_TYPE = Pattern.compile("^.+/\\*");

  private final String contentType;
  private final String charset;
  private final boolean hasPlaceholder;

  public static MediaTypeContainer create(String rawContentType) {
    String contentType = ContentType.getTypeOnly(rawContentType);
    if (contentType == null) return null;

    String charset = ContentType.getCharSetOrNull(rawContentType);

    return new MediaTypeContainer(contentType, charset);
  }

  private MediaTypeContainer(String contentType, String charset) {
    this.contentType = contentType;
    this.charset = charset;
    hasPlaceholder = PLACEHOLDER_TYPE.matcher(contentType).matches();
  }

  public boolean match(MediaTypeContainer mediaTypeContainer) {
    if (this == mediaTypeContainer) return true;

    if (contentType.equalsIgnoreCase(mediaTypeContainer.contentType)) {
      // Text specific case
      // we must ensure that the charset is matching too
      if (contentType.startsWith(TEXT_TYPE)) {
        return charset == null || charset.equalsIgnoreCase(mediaTypeContainer.charset);
      } else {
        return true;
      }
    }

    // Wildcard subtypes
    if (hasPlaceholder) {
      String definitionType = contentType.substring(0, contentType.indexOf('/'));
      String valueType = mediaTypeContainer.contentType.substring(0, mediaTypeContainer.contentType.indexOf('/'));

      return definitionType.equalsIgnoreCase(valueType);
    }

    return false;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    MediaTypeContainer that = (MediaTypeContainer) o;

    if (!contentType.equalsIgnoreCase(that.contentType)) return false;
    return Objects.equals(charset, that.charset);
  }

  @Override
  public int hashCode() {
    int result = contentType.hashCode();
    result = 31 * result + (charset != null ? charset.hashCode() : 0);
    return result;
  }

  @Override
  public String toString() {
    String str = "Content-Type: " + contentType;

    return charset != null
      ? str + "; charset=" + charset
      : str;
  }
}
