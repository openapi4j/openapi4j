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
  protected static final ValidationResult INVALID_URL = new ValidationResult(ERROR, 108, "Invalid URL '%s'");
  protected static final ValidationResult REF_MISSING = new ValidationResult(ERROR, 109, "Missing $ref '%s'");
  protected static final ValidationResult REF_CONTENT_UNREADABLE = new ValidationResult(ERROR, 110, "Unable to read $ref content for '%s' pointer.");
  private static final ValidationResult INVALID_RELATIVE_URL = new ValidationResult(ERROR, 145, "Invalid relative URL '%s', no server URL defined.");

  private static final Pattern ABSOLUTE_URL_PATTERN = Pattern.compile("\\A[a-z0-9.+-]+://.*", Pattern.CASE_INSENSITIVE);

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
                             final ValidationResults.CrumbInfo crumbInfo) {

    validateString(value, results, required, null, crumbInfo);
    if (value != null) {
      checkUrl(api, value, results, crumbInfo);
    }
  }

  private void checkUrl(final O api,
                        final String urlSpec,
                        final ValidationResults results,
                        final ValidationResults.CrumbInfo crumbInfo) {

    if (isAbsoluteUrl(urlSpec)) {
      try {
        new URL(urlSpec);
      } catch (MalformedURLException mue) {
        results.add(crumbInfo, INVALID_URL, urlSpec);
      }
    } else if (!api.hasServers()) {
      results.add(crumbInfo, INVALID_RELATIVE_URL, urlSpec);
    } else {
      String serverUrl = api.getServers().get(0).getUrl();

      if (isAbsoluteUrl(serverUrl)) {
        // Server URL is absolute, check with it
        try {
          new URL(new URL(serverUrl), urlSpec);
        } catch (MalformedURLException mue2) {
          results.add(crumbInfo, INVALID_URL, serverUrl + urlSpec);
        }
      } else {
        // Server URL is relative, check with context base URL
        // Unwrap server relative url
        final URL contextBaseUrl = api.getContext().getBaseUrl();
        final URL absServerUrl;
        try {
          absServerUrl = new URL(contextBaseUrl, serverUrl);
        } catch (MalformedURLException ignored) {
          results.add(crumbInfo, INVALID_URL, contextBaseUrl.toString() + serverUrl);
          return;
        }
        // Server URL is now absolute, check with it
        try {
          new URL(absServerUrl, urlSpec);
        } catch (MalformedURLException ignored) {
          results.add(crumbInfo, INVALID_URL, absServerUrl.toString() + urlSpec);
        }
      }
    }
  }

  /**
   * Decides if a URL is absolute based on whether it contains a valid scheme name, as
   * defined in RFC 1738.
   */
  protected boolean isAbsoluteUrl(String url) {
    return ABSOLUTE_URL_PATTERN.matcher(url).matches();
  }
}
