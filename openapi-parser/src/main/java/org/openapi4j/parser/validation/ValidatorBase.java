package org.openapi4j.parser.validation;

import org.openapi4j.core.model.OAI;
import org.openapi4j.core.validation.ValidationResults;
import org.openapi4j.core.validation.ValidationSeverity;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Base validation class for common validations.
 *
 * @param <O> The Open API version type.
 * @param <T> The Open API sub-object.
 */
public abstract class ValidatorBase<O extends OAI, T> implements Validator<O, T> {
  private static final String MALFORMED_SPEC = "Malformed spec around '%s'";
  private static final String DUPLICATED_VALUES = "Duplicated values in '%s'";
  private static final String INVALID_PATTERN = "Pattern '%s' is not valid";
  private static final String POSITIVE_STRICT_VALUE = "Value '%s' must be strictly positive";
  private static final String POSITIVE_VALUE = "Value '%s' must be positive or 0";
  private static final String MISSING_REQUIRED_FIELD = "Required field is missing '%s'";
  private static final String PATTERN_NOT_MATCHED = "String value '%s' does not match required pattern '%s'";
  private static final String INVALID_KEY = "Invalid key '%s' in map '%s'";
  private static final String INVALID_URL = "Invalid URL '%s'";

  protected <F> void validate(final O api,
                              final F value,
                              final ValidationResults results,
                              final String crumb,
                              final Validator<O, F> validator) {

    if (validator != null) {
      if (value == null) {
        results.addError(String.format(MALFORMED_SPEC, crumb));
      } else {
        validator.validate(api, value, results);
      }
    }
  }

  protected <F> void validateField(final O api,
                                   final F value,
                                   final ValidationResults results,
                                   final boolean required,
                                   final String crumb,
                                   final Validator<O, F> validator) {

    results.withCrumb(crumb, () -> {
      if (validateRequired(value, results, required, crumb)) {
        validate(api, value, results, crumb, validator);
      }
    });
  }

  protected <V> void validateList(final O api,
                                  final Collection<? extends V> value,
                                  final ValidationResults results,
                                  final boolean required,
                                  final String crumb,
                                  final Validator<O, V> validator) {

    results.withCrumb(crumb, () -> {
      if (validateRequired(value, results, required, crumb)) {
        Set<? extends V> set = new HashSet<>(value);
        if (set.size() != value.size()) {
          results.addError(String.format(DUPLICATED_VALUES, crumb), crumb);
        }

        if (validator != null) {
          for (V element : value) {
            validate(api, element, results, crumb, validator);
          }
        }
      }
    });
  }

  protected void validatePattern(final String pattern,
                                 final ValidationResults results,
                                 final boolean required,
                                 final String crumb) {

    if (validateRequired(pattern, results, required, crumb)) {
      try {
        Pattern.compile(pattern);
      } catch (PatternSyntaxException e) {
        results.addError(String.format(INVALID_PATTERN, pattern), crumb);
      }
    }
  }

  protected <N extends Number> void validatePositive(final N value,
                                                     final ValidationResults results,
                                                     final boolean required,
                                                     final String crumb) {

    if (validateRequired(value, results, required, crumb) && value.intValue() <= 0) {
      results.addError(String.format(POSITIVE_STRICT_VALUE, value), crumb);
    }
  }

  protected <N extends Number> void validateNonNegative(final N value,
                                                        final ValidationResults results,
                                                        final boolean required,
                                                        final String crumb) {

    if (validateRequired(value, results, required, crumb) && value.intValue() < 0) {
      results.addError(String.format(POSITIVE_VALUE, value), crumb);
    }
  }

  protected boolean validateRequired(final Object value,
                                     final ValidationResults results,
                                     final boolean required,
                                     final String crumb) {
    boolean isPresent = value != null;

    if (required && !isPresent) {
      results.addError(String.format(MISSING_REQUIRED_FIELD, crumb));
      return false;
    }

    return isPresent;
  }

  protected void validateString(final String value,
                                final ValidationResults results,
                                final boolean required,
                                final String crumb) {

    validateString(value, results, required, (Pattern) null, crumb);
  }

  protected void validateString(final String value,
                                final ValidationResults results,
                                final boolean required,
                                final String pattern,
                                final String crumb) {

    validateString(value, results, required, Pattern.compile(pattern), crumb);
  }

  protected void validateString(final String value,
                                final ValidationResults results,
                                final boolean required,
                                final Pattern pattern,
                                final String crumb) {

    if (validateRequired(value, results, required, crumb) && pattern != null && !pattern.matcher(value).matches()) {
      results.addError(String.format(PATTERN_NOT_MATCHED, value, pattern), crumb);
    }
  }

  protected void validateUrl(final String value,
                             final ValidationResults results,
                             final boolean required,
                             final String crumb,
                             final ValidationSeverity severity) {

    validateString(value, results, required, (Pattern) null, crumb);
    if (value != null) {
      checkUrl(value, results, crumb, severity);
    }
  }

  protected void validateEmail(final String value,
                               final ValidationResults results,
                               final boolean required,
                               final String crumb) {

    validateString(value, results, required, (Pattern) null, crumb);
    if (value != null) {
      checkEmail(value, results, crumb);
    }
  }

  protected abstract void checkEmail(String email, ValidationResults results, String crumb);

  protected <V> void validateMap(final O api,
                                 final Map<String, ? extends V> value,
                                 final ValidationResults results,
                                 final boolean required,
                                 final String crumb,
                                 final Pattern pattern,
                                 final Validator<O, V> validator) {

    results.withCrumb(crumb, () -> {
      if (validateRequired(value, results, required, crumb)) {
        for (final Map.Entry<String, ? extends V> entry : value.entrySet()) {
          results.withCrumb(entry.getKey(), () -> {
            checkKey(entry.getKey(), pattern, results);
            validate(api, entry.getValue(), results, crumb, validator);
          });
        }
      }
    });
  }

  private void checkKey(final String key,
                        final Pattern pattern,
                        final ValidationResults results) {

    if (pattern != null && !pattern.matcher(key).matches()) {
      results.addError(String.format(INVALID_KEY, key, pattern));
    }
  }

  private void checkUrl(final String urlSpec,
                        final ValidationResults results,
                        final String crumb,
                        final ValidationSeverity severity) {
    try {
      new URL(urlSpec);
    } catch (MalformedURLException e) {
      results.add(severity, String.format(INVALID_URL, urlSpec), crumb);
    }
  }
}
