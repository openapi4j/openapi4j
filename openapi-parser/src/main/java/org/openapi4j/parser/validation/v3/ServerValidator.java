package org.openapi4j.parser.validation.v3;

import org.openapi4j.core.validation.ValidationResults;
import org.openapi4j.core.validation.ValidationSeverity;
import org.openapi4j.parser.model.v3.OpenApi3;
import org.openapi4j.parser.model.v3.Server;
import org.openapi4j.parser.validation.Validator;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.openapi4j.parser.validation.v3.OAI3Keywords.EXTENSIONS;
import static org.openapi4j.parser.validation.v3.OAI3Keywords.URL;
import static org.openapi4j.parser.validation.v3.OAI3Keywords.VARIABLES;

class ServerValidator extends Validator3Base<OpenApi3, Server> {
  private static final String VARIABLE_NOT_DEFINED = "Undefined variable '%s' for url '%s'";
  private static final String VARIABLES_NOT_DEFINED = "Undefined variables for url '%s'";

  private static final Pattern PATTERN_VARIABLES = Pattern.compile("(\\{)(.*?)(})");

  private static final Validator<OpenApi3, Server> INSTANCE = new ServerValidator();

  private ServerValidator() {
  }

  public static Validator<OpenApi3, Server> instance() {
    return INSTANCE;
  }

  @Override
  public void validate(OpenApi3 api, Server server, ValidationResults results) {
    checkUrlWithVariables(server, results);
    validateMap(api, server.getVariables(), results, false, VARIABLES, Regexes.NAME_REGEX, ServerVariableValidator.instance());
    validateMap(api, server.getExtensions(), results, false, EXTENSIONS, Regexes.EXT_REGEX, null);
  }

  private void checkUrlWithVariables(Server server, ValidationResults results) {
    String url = server.getUrl();

    // Find variables
    Matcher matcher = PATTERN_VARIABLES.matcher(url);
    final List<String> variables = new ArrayList<>();
    while (matcher.find()) {
      variables.add(matcher.group(2));
    }

    if (variables.isEmpty()) {
      // Validate directly
      validateUrl(url, results, true, URL, ValidationSeverity.ERROR);
    } else if (server.getVariables() != null) {
      // Validate defined variables
      for (String variable : variables) {
        if (!server.getVariables().containsKey(variable)) {
          results.addError(String.format(VARIABLE_NOT_DEFINED, variable, url), URL);
        }
      }
    } else {
      results.addError(String.format(VARIABLES_NOT_DEFINED, url), URL);
    }
  }
}
