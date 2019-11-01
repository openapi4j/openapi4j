package org.openapi4j.core.model;

import java.net.URL;
import java.util.function.Predicate;

import static java.util.Objects.requireNonNull;

/**
 * A representation of authentication value to be used for loading access restricted
 * documents.
 */
public class AuthOption {
  private static final String TYPE_ERR_MSG = "Type is required.";
  private static final String KEY_ERR_MSG = "Key is required.";
  private static final String VALUE_ERR_MSG = "Value is required.";
  private static final String URL_MATCHER_ERR_MSG = "URL matcher is required.";

  /**
   * Where to apply the key/value of authentication.
   */
  public enum Type {
    HEADER, QUERY
  }

  private final String value;
  private final Type type;
  private final String key;
  private final Predicate<URL> urlMatcher;

  public AuthOption(Type type, String key, String value) {
    this(type, key, value, url -> true);
  }

  public AuthOption(Type type, String key, String value, Predicate<URL> urlMatcher) {
    this.type = requireNonNull(type, TYPE_ERR_MSG);
    this.key = requireNonNull(key, KEY_ERR_MSG);
    this.value = requireNonNull(value, VALUE_ERR_MSG);
    this.urlMatcher = requireNonNull(urlMatcher, URL_MATCHER_ERR_MSG);
  }

  public Type getType() {
    return type;
  }

  public String getKey() {
    return key;
  }

  public String getValue() {
    return value;
  }

  public Predicate<URL> getUrlMatcher() {
    return urlMatcher;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    AuthOption that = (AuthOption) o;

    if (!type.equals(that.getType())) return false;
    if (!key.equals(that.getKey())) return false;
    if (!value.equals(that.getValue())) return false;
    return urlMatcher.equals(that.getUrlMatcher());
  }

  @Override
  public int hashCode() {
    int result = value.hashCode();
    result = 31 * result + type.hashCode();
    result = 31 * result + key.hashCode();
    result = 31 * result + urlMatcher.hashCode();
    return result;
  }
}
