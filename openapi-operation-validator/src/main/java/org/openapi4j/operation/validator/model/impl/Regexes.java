package org.openapi4j.operation.validator.model.impl;

import java.util.regex.Pattern;

public class Regexes {
  private Regexes() {}

  // allows p1= a b &p2=1 &p3=
  public static final Pattern QUERY_STRING = Pattern.compile("([^&=]+?)\\s*=([^&=]*)");
  public static final Pattern CHARSET = Pattern.compile("(?:charset=)(.*)");
}
