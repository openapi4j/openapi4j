package org.openapi4j.parser.validation.v3;

import org.openapi4j.core.validation.ValidationResult;
import org.openapi4j.core.validation.ValidationResults;
import org.openapi4j.parser.model.v3.OpenApi3;
import org.openapi4j.parser.model.v3.Server;
import org.openapi4j.parser.model.v3.ServerVariable;
import org.openapi4j.parser.validation.ValidationContext;
import org.openapi4j.parser.validation.Validator;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.openapi4j.core.validation.ValidationSeverity.ERROR;
import static org.openapi4j.parser.validation.v3.OAI3Keywords.*;

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
    checkUrlWithVariables(api, server, results);
    validateMap(context, api, server.getVariables(), results, false, CRUMB_VARIABLES, Regexes.NAME_REGEX, ServerVariableValidator.instance());
    validateMap(context, api, server.getExtensions(), results, false, CRUMB_EXTENSIONS, Regexes.EXT_REGEX, null);
  }

  private void checkUrlWithVariables(OpenApi3 api, Server server, ValidationResults results) {
    final String url = server.getUrl();

    boolean hasVariableIssues = false;
    if (url != null) {
      // Find variables
      Matcher matcher = PATTERN_VARIABLES.matcher(url);
      final List<String> variables = new ArrayList<>();
      while (matcher.find()) {
        variables.add(matcher.group(2));
      }

      if (!variables.isEmpty() && server.getVariables() == null) {
        results.add(CRUMB_URL, VARIABLES_NOT_DEFINED, url);
        hasVariableIssues = true;
      } else if (server.getVariables() != null) {
        // Validate defined variables
        for (String variable : variables) {
          if (!server.getVariables().containsKey(variable)) {
            results.add(CRUMB_URL, VARIABLE_NOT_DEFINED, variable, url);
            hasVariableIssues = true;
          }
        }
      }
    }

    if (!hasVariableIssues) {
      checkServerUrl(api, server, results);
    }
  }

  private void checkServerUrl(final OpenApi3 api,
                              final Server server,
                              final ValidationResults results) {

    String url = server.getUrl();

    validateString(url, results, true, null, CRUMB_URL);
    if (url == null) {
      return;
    }

    // setup default variables before validating URL
    if (server.getVariables() != null) {
      for (Map.Entry<String, ServerVariable> entry : server.getVariables().entrySet()) {
        url = url.replace("{" + entry.getKey() + "}", entry.getValue().getDefault());
      }
    }

    if (isAbsoluteUrl(url)) {
      // Server URL is absolute, check it
      try {
        new URL(url);
      } catch (MalformedURLException ignored) {
        results.add(CRUMB_URL, INVALID_URL, url);
      }
    } else {
      // Server URL is relative, check with context base URL
      final URL contextBaseUrl = api.getContext().getBaseUrl();

      try {
        new URL(contextBaseUrl, url);
      } catch (MalformedURLException ignored) {
        results.add(CRUMB_URL, INVALID_URL, contextBaseUrl.toString() + url);
      }
    }
  }
}
