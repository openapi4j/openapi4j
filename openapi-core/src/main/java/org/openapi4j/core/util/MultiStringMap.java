package org.openapi4j.core.util;

import java.util.*;

/**
 * Utility class to wrap a multivalued key.
 * The key type is always {@link java.lang.String}.
 *
 * @param <V> The type of the values.
 */
@SuppressWarnings("unused")
public class MultiStringMap<V> {
  private final boolean caseSensitive;
  private final Map<String, Collection<V>> map;

  public MultiStringMap(boolean caseSensitive) {
    this.caseSensitive = caseSensitive;
    map = caseSensitive ? new HashMap<>() : new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
  }

  /**
   * @return {@code true} if the map is case sensitive, {@code false} otherwise.
   */
  public boolean isCaseSensitive() {
    return caseSensitive;
  }

  /**
   * Add the given value with the given key in this map.
   */
  public void put(String key, V value) {
    map.computeIfAbsent(key, k -> new HashSet<>()).add(value);
  }

  /**
   * Copies all of the mappings from the given map to this map.
   */
  public void putAll(MultiStringMap<V> mappings) {
    for (Map.Entry<String, Collection<V>> entry : mappings.entrySet()) {
      putAll(entry.getKey(), entry.getValue());
    }
  }

  /**
   * Put all of the mappings from the given map to this map.
   */
  public void putAll(String key, Collection<V> values) {
    map.computeIfAbsent(key, k -> new HashSet<>()).addAll(values);
  }

  /**
   * Returns the Collection of values to which the specified key is mapped,
   * or null if this map contains no mapping for the key.
   */
  public Collection<V> get(String key) {
    return map.get(key);
  }

  /**
   * Returns a Set view of the keys contained in this map.
   */
  public Set<String> keySet() {
    return map.keySet();
  }

  /**
   * Returns a Set view of the mappings contained in this map.
   */
  public Set<Map.Entry<String, Collection<V>>> entrySet() {
    return map.entrySet();
  }

  /**
   * Returns a Collection view of Collection of the values present in
   * this multimap.
   */
  public Collection<Collection<V>> values() {
    return map.values();
  }

  /**
   * Returns {@code true} if this map contains a mapping for the given key.
   */
  public boolean containsKey(String key) {
    return map.containsKey(key);
  }

  /**
   * Removes the mapping for the specified key from this multimap if present
   * and returns the Collection of previous values associated with key, or
   * null if there was no mapping for key.
   */
  public Collection<V> remove(String key) {
    return map.remove(key);
  }

  /**
   * Returns the number of key-value mappings in this multimap.
   */
  public int size() {
    int size = 0;
    for (Collection<V> value : map.values()) {
      size += value.size();
    }
    return size;
  }

  /**
   * Returns true if this multimap contains no key-value mappings.
   */
  public boolean isEmpty() {
    return map.isEmpty();
  }

  /**
   * Removes all of the mappings from this map.
   */
  public void clear() {
    map.clear();
  }

  /**
   * Removes the entry for the specified key only if it is currently
   * mapped to the specified value and return true if removed
   */
  public boolean remove(String key, V value) {
    Collection<V> values = map.get(key);

    if (values != null) {
      return values.remove(value);
    }

    return false;
  }

  /**
   * Get the underlying map as an unmodifiable map.
   *
   * @return The unmodifiable map.
   */
  public Map<String, Collection<V>> asUnmodifiableMap() {
    return Collections.unmodifiableMap(map);
  }
}
