package org.openapi4j.parser.validation.v3;

import org.openapi4j.core.validation.ValidationResults;
import org.openapi4j.core.validation.ValidationSeverity;
import org.openapi4j.parser.model.v3.OAuthFlow;
import org.openapi4j.parser.model.v3.OAuthFlows;
import org.openapi4j.parser.model.v3.OpenApi3;
import org.openapi4j.parser.model.v3.SecurityScheme;
import org.openapi4j.parser.validation.Validator;

import java.util.regex.Pattern;

import static org.openapi4j.parser.validation.v3.OAI3Keywords.APIKEY;
import static org.openapi4j.parser.validation.v3.OAI3Keywords.AUTHORIZATIONCODE;
import static org.openapi4j.parser.validation.v3.OAI3Keywords.CLIENTCREDENTIALS;
import static org.openapi4j.parser.validation.v3.OAI3Keywords.COOKIE;
import static org.openapi4j.parser.validation.v3.OAI3Keywords.EXTENSIONS;
import static org.openapi4j.parser.validation.v3.OAI3Keywords.FLOWS;
import static org.openapi4j.parser.validation.v3.OAI3Keywords.HEADER;
import static org.openapi4j.parser.validation.v3.OAI3Keywords.HTTP;
import static org.openapi4j.parser.validation.v3.OAI3Keywords.IMPLICIT;
import static org.openapi4j.parser.validation.v3.OAI3Keywords.IN;
import static org.openapi4j.parser.validation.v3.OAI3Keywords.NAME;
import static org.openapi4j.parser.validation.v3.OAI3Keywords.OAUTH2;
import static org.openapi4j.parser.validation.v3.OAI3Keywords.OPENIDCONNECT;
import static org.openapi4j.parser.validation.v3.OAI3Keywords.OPENIDCONNECTURL;
import static org.openapi4j.parser.validation.v3.OAI3Keywords.PASSWORD;
import static org.openapi4j.parser.validation.v3.OAI3Keywords.QUERY;
import static org.openapi4j.parser.validation.v3.OAI3Keywords.SCHEME;
import static org.openapi4j.parser.validation.v3.OAI3Keywords.TYPE;

class SecuritySchemeValidator extends Validator3Base<OpenApi3, SecurityScheme> {
  private static final Pattern TYPE_REGEX = Pattern.compile(
    String.join("|", APIKEY, HTTP, OAUTH2, OPENIDCONNECT));
  private static final Pattern IN_REGEX = Pattern.compile(
    String.join("|", QUERY, HEADER, COOKIE));

  private static final String OAUTH_FLOW_REQUIRED = "At least one OAuth flow is required";

  private static final Validator<OpenApi3, SecurityScheme> INSTANCE = new SecuritySchemeValidator();

  private SecuritySchemeValidator() {
  }

  public static Validator<OpenApi3, SecurityScheme> instance() {
    return INSTANCE;
  }

  @Override
  public void validate(OpenApi3 api, SecurityScheme securityScheme, ValidationResults results) {
    // omitted fields : description, bearerFormat
    validateString(securityScheme.getType(), results, true, TYPE_REGEX, TYPE);
    if (securityScheme.getType() != null) {
      String s = securityScheme.getType();
      if (HTTP.equals(s)) {
        validateString(securityScheme.getScheme(), results, true, SCHEME);

      } else if (APIKEY.equals(s)) {
        validateString(securityScheme.getName(), results, true, NAME);
        validateString(securityScheme.getIn(), results, true, IN_REGEX, IN);

      } else if (OAUTH2.equals(s)) {
        final OAuthFlows flows = securityScheme.getFlows();
        final Validator<OpenApi3, OAuthFlow> flowValidator = OAuthFlowValidator.instance();
        validateField(api, flows.getAuthorizationCode(), results, false, AUTHORIZATIONCODE, flowValidator);
        validateField(api, flows.getClientCredentials(), results, false, CLIENTCREDENTIALS, flowValidator);
        validateField(api, flows.getImplicit(), results, false, IMPLICIT, flowValidator);
        validateField(api, flows.getPassword(), results, false, PASSWORD, flowValidator);

        if (flows.getAuthorizationCode() == null &&
          flows.getClientCredentials() == null &&
          flows.getImplicit() == null &&
          flows.getPassword() == null) {
          results.addError(OAUTH_FLOW_REQUIRED, FLOWS);
        }
      } else if (OPENIDCONNECT.equals(s)) {
        validateUrl(securityScheme.getOpenIdConnectUrl(), results, true, OPENIDCONNECTURL, ValidationSeverity.ERROR);

      }
    }
    validateField(api, securityScheme.getExtensions(), results, false, EXTENSIONS, ExtensionsValidator.instance());
  }
}
