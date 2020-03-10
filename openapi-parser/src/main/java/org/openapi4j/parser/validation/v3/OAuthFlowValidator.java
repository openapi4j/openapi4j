package org.openapi4j.parser.validation.v3;

import org.openapi4j.core.validation.ValidationResult;
import org.openapi4j.core.validation.ValidationResults;
import org.openapi4j.parser.model.v3.OAuthFlow;
import org.openapi4j.parser.model.v3.OpenApi3;
import org.openapi4j.parser.validation.ValidationContext;
import org.openapi4j.parser.validation.Validator;

import static org.openapi4j.core.validation.ValidationSeverity.ERROR;
import static org.openapi4j.parser.validation.v3.OAI3Keywords.AUTHORIZATIONCODE;
import static org.openapi4j.parser.validation.v3.OAI3Keywords.AUTHORIZATIONURL;
import static org.openapi4j.parser.validation.v3.OAI3Keywords.CLIENTCREDENTIALS;
import static org.openapi4j.parser.validation.v3.OAI3Keywords.EXTENSIONS;
import static org.openapi4j.parser.validation.v3.OAI3Keywords.IMPLICIT;
import static org.openapi4j.parser.validation.v3.OAI3Keywords.PASSWORD;
import static org.openapi4j.parser.validation.v3.OAI3Keywords.REFRESHURL;
import static org.openapi4j.parser.validation.v3.OAI3Keywords.SCOPES;
import static org.openapi4j.parser.validation.v3.OAI3Keywords.TOKENURL;

class OAuthFlowValidator extends Validator3Base<OpenApi3, OAuthFlow> {
  private static final ValidationResult AUTH_URL_NOT_ALLOWED = new ValidationResult(ERROR, 120, "'authorizationUrl' is not allowed when OAuth2 configuration is 'implicit' or 'authorizationCode'.");
  private static final ValidationResult TOKEN_URL_NOT_ALLOWED = new ValidationResult(ERROR, 121, "'tokenUrl' is not allowed when OAuth2 configuration is 'implicit'.");

  private static final Validator<OpenApi3, OAuthFlow> INSTANCE = new OAuthFlowValidator();

  private OAuthFlowValidator() {
  }

  public static Validator<OpenApi3, OAuthFlow> instance() {
    return INSTANCE;
  }

  @Override
  public void validate(ValidationContext<OpenApi3> context, OpenApi3 api, OAuthFlow oauthFlow, ValidationResults results) {
    String conf = oauthFlow.getConfiguration();

    if (IMPLICIT.equals(conf) || AUTHORIZATIONCODE.equals(conf)) {
      validateUrl(oauthFlow.getAuthorizationUrl(), results, true, false, AUTHORIZATIONURL);
    } else if (oauthFlow.getAuthorizationUrl() != null) {
      results.add(AUTHORIZATIONURL, AUTH_URL_NOT_ALLOWED);
    }

    if (PASSWORD.equals(conf) || CLIENTCREDENTIALS.equals(conf) || AUTHORIZATIONCODE.equals(conf)) {
      validateUrl(oauthFlow.getTokenUrl(), results, true, false, TOKENURL);
    } else if (oauthFlow.getTokenUrl() != null) {
      results.add(TOKENURL, TOKEN_URL_NOT_ALLOWED);
    }

    validateUrl(oauthFlow.getRefreshUrl(), results, false, false, REFRESHURL);
    validateMap(context, api, oauthFlow.getScopes(), results, true, SCOPES, Regexes.NOEXT_REGEX, null);
    validateMap(context, api, oauthFlow.getExtensions(), results, false, EXTENSIONS, Regexes.EXT_REGEX, null);
  }
}
