package org.openapi4j.parser.validation.v3;

import java.util.regex.Pattern;

class Regexes {
  static final Pattern PATH_REGEX = Pattern.compile("/.*");
  static final Pattern EXT_REGEX = Pattern.compile("x-.+");
  static final Pattern NOEXT_REGEX = Pattern.compile("(?!x-).*");
  static final Pattern NAME_REGEX = Pattern.compile("[a-zA-Z0-9._-]+");
  static final Pattern NOEXT_NAME_REGEX = Pattern.compile("(?!x-)[a-zA-Z0-9._-]+");
  static final Pattern METHOD_REGEX = Pattern.compile("get|put|post|delete|options|head|patch|trace");
  static final Pattern PARAM_IN_REGEX = Pattern.compile("path|query|header|cookie");
  static final Pattern STYLE_REGEX = Pattern.compile("matrix|label|form|simple|spaceDelimited|pipeDelimited|deepObject");
  static final Pattern RESPONSE_REGEX = Pattern.compile("default|\\d\\d\\d");

  static final Pattern EMAIL_REGEX = Pattern.compile("(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9]" +
    "(?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:" +
    "(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])");
}
