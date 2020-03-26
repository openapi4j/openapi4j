package org.openapi4j.parser.validation.v3;

import org.openapi4j.core.exception.DecodeException;
import org.openapi4j.core.model.reference.Reference;
import org.openapi4j.core.validation.ValidationResult;
import org.openapi4j.core.validation.ValidationResults;
import org.openapi4j.parser.model.AbsRefOpenApiSchema;
import org.openapi4j.parser.model.OpenApiSchema;
import org.openapi4j.parser.model.v3.OpenApi3;
import org.openapi4j.parser.validation.ValidationContext;
import org.openapi4j.parser.validation.Validator;
import org.openapi4j.parser.validation.ValidatorBase;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Pattern;

import static org.openapi4j.core.validation.ValidationSeverity.ERROR;

abstract class Validator3Base<O extends OpenApi3, T> extends ValidatorBase<O, T> {
  private static final ValidationResult INVALID_URL = new ValidationResult(ERROR, 108, "Invalid URL '%s'");
  protected static final ValidationResult REF_MISSING = new ValidationResult(ERROR, 109, "Missing $ref '%s'");
  protected static final ValidationResult REF_CONTENT_UNREADABLE = new ValidationResult(ERROR, 110, "Unable to read $ref content for '%s' pointer.");
  private static final ValidationResult INVALID_RELATIVE_URL = new ValidationResult(ERROR, 145, "Invalid relative URL '%s', no server URL defined.");

  <F extends OpenApiSchema<F>> F getReferenceContent(final O api,
                                                     final AbsRefOpenApiSchema<F> schema,
                                                     final ValidationResults results,
                                                     final ValidationResults.CrumbInfo crumbInfo,
                                                     final Class<F> clazz) {

    Reference reference = schema.getReference(api.getContext());

    if (reference == null) {
      results.add(crumbInfo, REF_MISSING, schema.getRef());
    } else {
      try {
        return reference.getMappedContent(clazz);
      } catch (DecodeException e) {
        results.add(crumbInfo, REF_CONTENT_UNREADABLE, schema.getRef());
      }
    }

    return null;
  }

  <F extends OpenApiSchema<F>> void validateReference(final ValidationContext<OpenApi3> context,
                                                      final O api,
                                                      final AbsRefOpenApiSchema<F> schema,
                                                      final ValidationResults results,
                                                      final ValidationResults.CrumbInfo crumbInfo,
                                                      final Validator<OpenApi3, F> validator,
                                                      final Class<F> clazz) {

    F content = getReferenceContent(api, schema, results, crumbInfo, clazz);
    if (content != null) {
      context.validate(api, content, validator, results);
    }
  }

  @SuppressWarnings("SameParameterValue")
  protected void validateUrl(final O api,
                             final String value,
                             final ValidationResults results,
                             final boolean required,
                             final boolean allowRelative,
                             final ValidationResults.CrumbInfo crumbInfo) {

    validateString(value, results, required, (Pattern) null, crumbInfo);
    if (value != null) {
      checkUrl(api, value, allowRelative, results, crumbInfo);
    }
  }

  private void checkUrl(final O api,
                        final String urlSpec,
                        final boolean allowRelative,
                        final ValidationResults results,
                        final ValidationResults.CrumbInfo crumbInfo) {
    try {
      new URL(urlSpec);
    } catch (MalformedURLException e) {
      if (!allowRelative) {
        results.add(crumbInfo, INVALID_URL, urlSpec);
      } else if (!api.hasServers()) {
        results.add(crumbInfo, INVALID_RELATIVE_URL, urlSpec);
      } else {
        String serverUrl = api.getServers().get(0).getUrl();

        if (serverUrl == null) {
          results.add(crumbInfo, INVALID_RELATIVE_URL, urlSpec);
        } else {
          try {
            new URL(new URL(serverUrl), urlSpec);
          } catch (MalformedURLException ignored) {
            results.add(crumbInfo, INVALID_URL, serverUrl + urlSpec);
          }
        }
      }
    }
  }
}
