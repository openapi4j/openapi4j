package org.openapi4j.core.util;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class StringUtilTest {
  private static final String STR = "a , b, c ,,d, ";
  private static final String DELIMITERS = ",";

  @Test
  public void rawValues() {
    List<String> result = StringUtil.tokenize(STR, DELIMITERS, false, false);
    assertEquals(6, result.size());
    assertEquals("a ", result.get(0));
    assertEquals(" b", result.get(1));
    assertEquals(" c ", result.get(2));
    assertEquals("", result.get(3));
    assertEquals("d", result.get(4));
    assertEquals(" ", result.get(5));
  }

  @Test
  public void trimmedValues() {
    List<String> result = StringUtil.tokenize(STR, DELIMITERS, true, false);
    assertEquals(6, result.size());
    assertEquals("a", result.get(0));
    assertEquals("b", result.get(1));
    assertEquals("c", result.get(2));
    assertEquals("", result.get(3));
    assertEquals("d", result.get(4));
    assertEquals("", result.get(5));
  }

  @Test
  public void ignoredEmptyValues() {
    List<String> result = StringUtil.tokenize(STR, DELIMITERS, false, true);
    assertEquals(5, result.size());
    assertEquals("a ", result.get(0));
    assertEquals(" b", result.get(1));
    assertEquals(" c ", result.get(2));
    assertEquals("d", result.get(3));
    assertEquals(" ", result.get(4));
  }

  @Test
  public void trimmedAndIgnoredEmptyValues() {
    List<String> result = StringUtil.tokenize(STR, DELIMITERS, true, true);
    assertEquals(4, result.size());
    assertEquals("a", result.get(0));
    assertEquals("b", result.get(1));
    assertEquals("c", result.get(2));
    assertEquals("d", result.get(3));
  }
}
