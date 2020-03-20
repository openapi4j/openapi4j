package org.openapi4j.parser.validation;

import org.openapi4j.core.model.OAI;
import org.openapi4j.core.validation.ValidationResult;
import org.openapi4j.core.validation.ValidationResults;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import static org.openapi4j.core.validation.ValidationSeverity.ERROR;

/**
 * Base validation class for common validations.
 *
 * @param <O> The Open API version type.
 * @param <T> The Open API sub-object.
 */
public abstract class ValidatorBase<O extends OAI, T> implements Validator<O, T> {
  private static final ValidationResult MALFORMED_SPEC = new ValidationResult(ERROR, 100, "Malformed spec around '%s'");
  private static final ValidationResult DUPLICATED_VALUES = new ValidationResult(ERROR, 101, "Duplicated values in '%s'");
  private static final ValidationResult INVALID_PATTERN = new ValidationResult(ERROR, 102, "Pattern '%s' is not valid");
  private static final ValidationResult POSITIVE_STRICT_VALUE = new ValidationResult(ERROR, 103, "Value '%s' must be strictly positive");
  private static final ValidationResult POSITIVE_VALUE = new ValidationResult(ERROR, 104, "Value '%s' must be positive or 0");
  private static final ValidationResult MISSING_REQUIRED_FIELD = new ValidationResult(ERROR, 105, "Required field is missing '%s'");
  private static final ValidationResult PATTERN_NOT_MATCHED = new ValidationResult(ERROR, 106, "String value '%s' does not match required pattern '%s'");
  private static final ValidationResult INVALID_KEY = new ValidationResult(ERROR, 107, "Invalid key '%s' in map '%s'");
  private static final ValidationResult INVALID_URL = new ValidationResult(ERROR, 108, "Invalid URL '%s'");

  protected static final Pattern EMAIL_REGEX = Pattern.compile("(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9]" +
    "(?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:" +
    "(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])");

  protected <V> void validate(final ValidationContext<O> context,
                              final O api,
                              final V value,
                              final ValidationResults results,
                              final String crumb,
                              final Validator<O, V> validator) {

    if (validator != null) {
      if (value == null) {
        results.add(MALFORMED_SPEC, crumb);
      } else {
        context.validate(api, value, validator, results);
      }
    }
  }

  protected <V> void validateField(final ValidationContext<O> context,
                                   final O api,
                                   final V value,
                                   final ValidationResults results,
                                   final boolean required,
                                   final String crumb,
                                   final Validator<O, V> validator) {

    results.withCrumb(crumb, () -> {
      if (validateRequired(value, results, required, crumb)) {
        validate(context, api, value, results, crumb, validator);
      }
    });
  }

  protected <V> void validateList(final ValidationContext<O> context,
                                  final O api,
                                  final Collection<? extends V> value,
                                  final ValidationResults results,
                                  final boolean required,
                                  final String crumb,
                                  final Validator<O, V> validator) {

    results.withCrumb(crumb, () -> {
      if (validateRequired(value, results, required, crumb)) {
        Set<? extends V> set = new HashSet<>(value);
        if (set.size() != value.size()) {
          results.add(crumb, DUPLICATED_VALUES, crumb);
        }

        if (validator != null) {
          for (V element : value) {
            validate(context, api, element, results, crumb, validator);
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
        results.add(crumb, INVALID_PATTERN, pattern);
      }
    }
  }

  protected <V extends Number> void validatePositive(final V value,
                                                     final ValidationResults results,
                                                     final boolean required,
                                                     final String crumb) {

    if (validateRequired(value, results, required, crumb) && value.doubleValue() <= 0) {
      results.add(crumb, POSITIVE_STRICT_VALUE, value);
    }
  }

  protected <V extends Number> void validateNonNegative(final V value,
                                                        final ValidationResults results,
                                                        final boolean required,
                                                        final String crumb) {

    if (validateRequired(value, results, required, crumb) && value.doubleValue() < 0) {
      results.add(crumb, POSITIVE_VALUE, value);
    }
  }

  protected boolean validateRequired(final Object value,
                                     final ValidationResults results,
                                     final boolean required,
                                     final String crumb) {
    boolean isPresent = value != null;

    if (required && !isPresent) {
      results.add(crumb, MISSING_REQUIRED_FIELD, crumb);
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
      results.add(crumb, PATTERN_NOT_MATCHED, value, pattern);
    }
  }

  protected void validateUrl(final String value,
                             final ValidationResults results,
                             final boolean required,
                             final boolean allowRelative,
                             final String crumb) {

    validateString(value, results, required, (Pattern) null, crumb);
    if (value != null) {
      checkUrl(value, allowRelative, results, crumb);
    }
  }

  protected <V> void validateMap(final ValidationContext<O> context,
                                 final O api,
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
            validate(context, api, entry.getValue(), results, crumb, validator);
          });
        }
      }
    });
  }

  private void checkKey(final String key,
                        final Pattern pattern,
                        final ValidationResults results) {

    if (pattern != null && !pattern.matcher(key).matches()) {
      results.add(INVALID_KEY, key, pattern);
    }
  }

  private void checkUrl(final String urlSpec,
                        final boolean allowRelative,
                        final ValidationResults results,
                        final String crumb) {
    try {
      new URL(urlSpec.replace("{", "").replace("}", ""));
    } catch (MalformedURLException e) {
      if (!allowRelative) {
        results.add(crumb, INVALID_URL, urlSpec);
      }
    }
  }
}
