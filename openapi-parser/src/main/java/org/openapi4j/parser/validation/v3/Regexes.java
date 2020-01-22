package org.openapi4j.parser.validation.v3;

import java.util.regex.Pattern;

class Regexes {
  private Regexes() {}

  static final Pattern PATH_REGEX = Pattern.compile("/.*");
  static final Pattern EXT_REGEX = Pattern.compile("x-.+");
  static final Pattern NOEXT_REGEX = Pattern.compile("(?!x-).*");
  static final Pattern NAME_REGEX = Pattern.compile("[a-zA-Z0-9._-]+");
  static final Pattern NOEXT_NAME_REGEX = Pattern.compile("(?!x-)[a-zA-Z0-9._-]+");
  static final Pattern METHOD_REGEX = Pattern.compile("get|put|post|delete|options|head|patch|trace");
  static final Pattern PARAM_IN_REGEX = Pattern.compile("path|query|header|cookie");
  static final Pattern STYLE_REGEX = Pattern.compile("matrix|label|form|simple|spaceDelimited|pipeDelimited|deepObject");
  static final Pattern RESPONSE_REGEX = Pattern.compile("default|(\\d[0-9X]{2})");
}
