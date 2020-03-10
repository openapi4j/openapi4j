package org.openapi4j.parser.validation.v3;

import org.openapi4j.core.validation.ValidationResult;
import org.openapi4j.core.validation.ValidationResults;
import org.openapi4j.parser.model.v3.OpenApi3;
import org.openapi4j.parser.model.v3.Server;
import org.openapi4j.parser.validation.ValidationContext;
import org.openapi4j.parser.validation.Validator;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.openapi4j.core.validation.ValidationSeverity.ERROR;
import static org.openapi4j.parser.validation.v3.OAI3Keywords.EXTENSIONS;
import static org.openapi4j.parser.validation.v3.OAI3Keywords.URL;
import static org.openapi4j.parser.validation.v3.OAI3Keywords.VARIABLES;

class ServerValidator extends Validator3Base<OpenApi3, Server> {
  private static final ValidationResult VARIABLE_NOT_DEFINED = new ValidationResult(ERROR, 142, "Undefined variable '%s' for url '%s'");
  private static final ValidationResult VARIABLES_NOT_DEFINED = new ValidationResult(ERROR, 143, "Undefined variables for url '%s'");

  private static final Pattern PATTERN_VARIABLES = Pattern.compile("(\\{)(.*?)(})");

  private static final Validator<OpenApi3, Server> INSTANCE = new ServerValidator();

  private ServerValidator() {
  }

  public static Validator<OpenApi3, Server> instance() {
    return INSTANCE;
  }

  @Override
  public void validate(ValidationContext<OpenApi3> context, OpenApi3 api, Server server, ValidationResults results) {
    checkUrlWithVariables(server, results);
    validateMap(context, api, server.getVariables(), results, false, VARIABLES, Regexes.NAME_REGEX, ServerVariableValidator.instance());
    validateMap(context, api, server.getExtensions(), results, false, EXTENSIONS, Regexes.EXT_REGEX, null);
  }

  private void checkUrlWithVariables(Server server, ValidationResults results) {
    String url = server.getUrl();

    validateUrl(url, results, true, true, URL);

    // Find variables
    Matcher matcher = PATTERN_VARIABLES.matcher(url);
    final List<String> variables = new ArrayList<>();
    while (matcher.find()) {
      variables.add(matcher.group(2));
    }

    if (!variables.isEmpty() && server.getVariables() == null) {
      results.add(URL, VARIABLES_NOT_DEFINED, url);
    } else if (server.getVariables() != null) {
      // Validate defined variables
      for (String variable : variables) {
        if (!server.getVariables().containsKey(variable)) {
          results.add(URL, VARIABLE_NOT_DEFINED, variable, url);
        }
      }
    }
  }
}
