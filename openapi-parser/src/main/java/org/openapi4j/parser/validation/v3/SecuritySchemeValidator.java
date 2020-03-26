package org.openapi4j.parser.validation.v3;

import org.openapi4j.core.validation.ValidationResult;
import org.openapi4j.core.validation.ValidationResults;
import org.openapi4j.parser.model.v3.OAuthFlow;
import org.openapi4j.parser.model.v3.OAuthFlows;
import org.openapi4j.parser.model.v3.OpenApi3;
import org.openapi4j.parser.model.v3.SecurityScheme;
import org.openapi4j.parser.validation.ValidationContext;
import org.openapi4j.parser.validation.Validator;

import java.util.regex.Pattern;

import static org.openapi4j.core.validation.ValidationSeverity.ERROR;
import static org.openapi4j.parser.validation.v3.OAI3Keywords.*;

class SecuritySchemeValidator extends Validator3Base<OpenApi3, SecurityScheme> {
  private static final ValidationResult OAUTH_FLOW_REQUIRED = new ValidationResult(ERROR, 141, "At least one OAuth flow is required");

  private static final Pattern TYPE_REGEX = Pattern.compile(String.join("|", APIKEY, HTTP, OAUTH2, OPENIDCONNECT));
  private static final Pattern IN_REGEX = Pattern.compile(String.join("|", QUERY, HEADER, COOKIE));

  private static final Validator<OpenApi3, SecurityScheme> INSTANCE = new SecuritySchemeValidator();

  private SecuritySchemeValidator() {
  }

  public static Validator<OpenApi3, SecurityScheme> instance() {
    return INSTANCE;
  }

  @Override
  public void validate(ValidationContext<OpenApi3> context, OpenApi3 api, SecurityScheme securityScheme, ValidationResults results) {
    if (securityScheme.isRef()) {
      validateReference(context, api, securityScheme, results, CRUMB_$REF, SecuritySchemeValidator.instance(), SecurityScheme.class);
    } else {
      // omitted fields : description, bearerFormat
      validateString(securityScheme.getType(), results, true, TYPE_REGEX, CRUMB_TYPE);
      if (securityScheme.getType() != null) {
        String s = securityScheme.getType();
        if (HTTP.equals(s)) {
          validateString(securityScheme.getScheme(), results, true, CRUMB_SCHEME);

        } else if (APIKEY.equals(s)) {
          validateString(securityScheme.getName(), results, true, CRUMB_NAME);
          validateString(securityScheme.getIn(), results, true, IN_REGEX, CRUMB_IN);

        } else if (OAUTH2.equals(s)) {
          final OAuthFlows flows = securityScheme.getFlows();
          final Validator<OpenApi3, OAuthFlow> flowValidator = OAuthFlowValidator.instance();
          validateField(context, api, flows.getAuthorizationCode(), results, false, CRUMB_AUTHORIZATIONCODE, flowValidator);
          validateField(context, api, flows.getClientCredentials(), results, false, CRUMB_CLIENTCREDENTIALS, flowValidator);
          validateField(context, api, flows.getImplicit(), results, false, CRUMB_IMPLICIT, flowValidator);
          validateField(context, api, flows.getPassword(), results, false, CRUMB_PASSWORD, flowValidator);
          validateMap(context, api, flows.getExtensions(), results, false, CRUMB_EXTENSIONS, Regexes.EXT_REGEX, null);

          if (flows.getAuthorizationCode() == null &&
            flows.getClientCredentials() == null &&
            flows.getImplicit() == null &&
            flows.getPassword() == null) {
            results.add(CRUMB_FLOWS, OAUTH_FLOW_REQUIRED);
          }
        } else if (OPENIDCONNECT.equals(s)) {
          validateUrl(api, securityScheme.getOpenIdConnectUrl(), results, true, true, CRUMB_OPENIDCONNECTURL);

        }
      }
      validateMap(context, api, securityScheme.getExtensions(), results, false, CRUMB_EXTENSIONS, Regexes.EXT_REGEX, null);
    }
  }
}
