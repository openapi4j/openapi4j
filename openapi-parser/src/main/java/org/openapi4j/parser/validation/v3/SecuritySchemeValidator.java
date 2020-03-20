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

import static org.openapi4j.core.model.v3.OAI3SchemaKeywords.$REF;
import static org.openapi4j.core.validation.ValidationSeverity.ERROR;
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
      validateReference(context, api, securityScheme, results, $REF, SecuritySchemeValidator.instance(), SecurityScheme.class);
    } else {
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
          validateField(context, api, flows.getAuthorizationCode(), results, false, AUTHORIZATIONCODE, flowValidator);
          validateField(context, api, flows.getClientCredentials(), results, false, CLIENTCREDENTIALS, flowValidator);
          validateField(context, api, flows.getImplicit(), results, false, IMPLICIT, flowValidator);
          validateField(context, api, flows.getPassword(), results, false, PASSWORD, flowValidator);
          validateMap(context, api, flows.getExtensions(), results, false, EXTENSIONS, Regexes.EXT_REGEX, null);

          if (flows.getAuthorizationCode() == null &&
            flows.getClientCredentials() == null &&
            flows.getImplicit() == null &&
            flows.getPassword() == null) {
            results.add(FLOWS, OAUTH_FLOW_REQUIRED);
          }
        } else if (OPENIDCONNECT.equals(s)) {
          validateUrl(api, securityScheme.getOpenIdConnectUrl(), results, true, true, OPENIDCONNECTURL);

        }
      }
      validateMap(context, api, securityScheme.getExtensions(), results, false, EXTENSIONS, Regexes.EXT_REGEX, null);
    }
  }
}
