package org.openapi4j.parser.validation.v3;

import org.openapi4j.core.validation.ValidationResults;
import org.openapi4j.parser.model.v3.Discriminator;
import org.openapi4j.parser.model.v3.OpenApi3;
import org.openapi4j.parser.validation.Validator;

import java.util.Map;

import static org.openapi4j.parser.validation.v3.OAI3Keywords.MAPPING;
import static org.openapi4j.parser.validation.v3.OAI3Keywords.PROPERTYNAME;

class DiscriminatorValidator extends Validator3Base<OpenApi3, Discriminator> {
  private static final String UNKNOWN_SCHEMA_REF = "Unknown schema reference '%s'";
  private static final String UNKNOWN_SCHEMA_NAME = "Unknown schema name '%s'";

  private static final Validator<OpenApi3, Discriminator> INSTANCE = new DiscriminatorValidator();

  private DiscriminatorValidator() {
  }

  public static Validator<OpenApi3, Discriminator> instance() {
    return INSTANCE;
  }

  @Override
  public void validate(OpenApi3 api, Discriminator discriminator, ValidationResults results) {
    Map<String, String> mapping = discriminator.getMapping();
    if (mapping != null) {
      for (Map.Entry<String, String> entry : mapping.entrySet()) {
        // value as it can be schema name or reference
        String $ref = entry.getValue();
        if ($ref.contains("/")) {
          if (api.getContext().getReferenceRegistry().getRef($ref) == null) {
            results.addError(String.format(UNKNOWN_SCHEMA_REF, $ref), MAPPING);
          }
        } else if (api.getComponents() == null || !api.getComponents().hasSchema($ref)) {
          results.addError(String.format(UNKNOWN_SCHEMA_NAME, $ref), MAPPING);
        }
      }
    }

    validateString(discriminator.getPropertyName(), results, true, PROPERTYNAME);
  }
}
