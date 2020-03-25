package org.openapi4j.core.util;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class MultiStringMapTest {
  @Test
  public void caseSensitive() {
    MultiStringMap<String> map = new MultiStringMap<>(true);
    assertTrue(map.isCaseSensitive());

    check(map, true);
  }

  @Test
  public void caseInsensitive() {
    MultiStringMap<String> map = new MultiStringMap<>(false);
    assertFalse(map.isCaseSensitive());

    check(map, false);
  }

  private void check(MultiStringMap<String> map, boolean caseSensitive) {
    map.put("key", "value");

    assertTrue(map.containsKey("key"));
    if (caseSensitive) {
      assertFalse(map.containsKey("kEy"));
    } else {
      assertTrue(map.containsKey("kEy"));
    }


    MultiStringMap<String> otherMap = new MultiStringMap<>(true);
    otherMap.put("key", "value2");
    otherMap.put("key", "value3");
    map.putAll(otherMap);
    assertEquals(3, map.get("key").size());

    List<String> list = new ArrayList<>();
    list.add("value4");
    list.add("value5");
    map.putAll("key", list);
    assertEquals(5, map.get("key").size());

    assertEquals(1, map.entrySet().size());
    assertEquals(1, map.keySet().size());
    assertEquals(1, map.values().size());
    assertEquals(5, map.size());
    assertTrue(map.remove("key", "value5"));
    assertEquals(4, map.get("key").size());
    assertFalse(map.remove("key", "unknown value"));
    assertFalse(map.remove("unknown key", "value"));

    Map<String, Collection<String>> unmodifiableMap = map.asUnmodifiableMap();
    assertEquals(4, unmodifiableMap.get("key").size());
    try {
      unmodifiableMap.put("key", unmodifiableMap.get("key"));
      fail("Shouldn't be able to modify the map");
    } catch (UnsupportedOperationException ignored) {
    }

    assertEquals(4, map.remove("key").size());
    assertTrue(map.isEmpty());
    map.put("key", "value");
    map.clear();
    assertTrue(map.isEmpty());
  }
}
