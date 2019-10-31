package org.openapi4j.parser.validation.v3;

import org.openapi4j.core.validation.ValidationResults;
import org.openapi4j.parser.model.v3.OpenApi3;
import org.openapi4j.parser.model.v3.SecurityRequirement;
import org.openapi4j.parser.validation.Validator;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.openapi4j.parser.validation.v3.OAI3Keywords.OAUTH2;
import static org.openapi4j.parser.validation.v3.OAI3Keywords.OPENIDCONNECT;

class SecurityRequirementValidator extends Validator3Base<OpenApi3, SecurityRequirement> {
  private static final String SCHEME_NOT_DEFINED = "Security scheme '%s' is not defined in components";
  private static final String SEC_REQ_NOT_ALLOWED = "Security requirement parameters in '%s' not allowed with scheme type '%s'";

  private static final Validator<OpenApi3, SecurityRequirement> INSTANCE = new SecurityRequirementValidator();

  private SecurityRequirementValidator() {
  }

  public static Validator<OpenApi3, SecurityRequirement> instance() {
    return INSTANCE;
  }

  @Override
  public void validate(OpenApi3 api, SecurityRequirement securityRequirement, ValidationResults results) {
    Set<String> schemes = api.getComponents().getSecuritySchemes().keySet();

    for (Map.Entry<String, List<String>> entry : securityRequirement.getRequirements().entrySet()) {
      if (!schemes.contains(entry.getKey())) {
        results.addError(String.format(SCHEME_NOT_DEFINED, entry.getKey()));
      } else {
        String type = api.getComponents().getSecurityScheme(entry.getKey()).getType();
        if (type == null) {
          continue;
        }

        if (OAUTH2.equals(type) || OPENIDCONNECT.equals(type)) {
          continue;
        }

        if (!OpenApi3Validator.Config.SECURITY_REQ_SCOPES_STRICT) {
          break;
        }

        if (!entry.getValue().isEmpty()) {
          results.addError(String.format(SEC_REQ_NOT_ALLOWED, entry.getKey(), type));
        }
      }
    }
  }
}
