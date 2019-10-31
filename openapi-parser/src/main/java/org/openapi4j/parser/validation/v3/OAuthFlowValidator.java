package org.openapi4j.parser.validation.v3;

import org.openapi4j.core.validation.ValidationResults;
import org.openapi4j.core.validation.ValidationSeverity;
import org.openapi4j.parser.model.v3.OAuthFlow;
import org.openapi4j.parser.model.v3.OpenApi3;
import org.openapi4j.parser.validation.Validator;

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
  private static final String AUTH_URL_NOT_ALLOWED = "'authorizationUrl' is not allowed when OAuth2 configuration is 'implicit' or 'authorizationCode'";
  private static final String TOKEN_URL_NOT_ALLOWED = "'tokenUrl' is not allowed when OAuth2 configuration is 'implicit' or 'authorizationCode'";

  private static final Validator<OpenApi3, OAuthFlow> INSTANCE = new OAuthFlowValidator();

  private OAuthFlowValidator() {
  }

  public static Validator<OpenApi3, OAuthFlow> instance() {
    return INSTANCE;
  }

  @Override
  public void validate(OpenApi3 api, OAuthFlow oauthFlow, ValidationResults results) {
    String conf = oauthFlow.getConfiguration();

    if (IMPLICIT.equals(conf) || AUTHORIZATIONCODE.equals(conf)) {
      validateUrl(oauthFlow.getAuthorizationUrl(), results, true, AUTHORIZATIONURL, ValidationSeverity.ERROR);
    } else if (oauthFlow.getAuthorizationUrl() != null) {
      results.addError(AUTH_URL_NOT_ALLOWED, AUTHORIZATIONURL);
    }

    if (PASSWORD.equals(conf) || CLIENTCREDENTIALS.equals(conf) || AUTHORIZATIONCODE.equals(conf)) {
      validateUrl(oauthFlow.getTokenUrl(), results, true, TOKENURL, ValidationSeverity.ERROR);
    } else if (oauthFlow.getTokenUrl() != null) {
      results.addError(TOKEN_URL_NOT_ALLOWED, TOKENURL);
    }

    validateUrl(oauthFlow.getRefreshUrl(), results, false, REFRESHURL, ValidationSeverity.ERROR);
    validateMap(api, oauthFlow.getScopes(), results, true, SCOPES, Regexes.NOEXT_REGEX, null);
    validateField(api, oauthFlow.getExtensions(), results, false, EXTENSIONS, ExtensionsValidator.instance());
  }
}
