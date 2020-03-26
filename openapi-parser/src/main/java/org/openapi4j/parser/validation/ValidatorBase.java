package org.openapi4j.parser.validation;

import org.openapi4j.core.model.OAI;
import org.openapi4j.core.validation.ValidationResult;
import org.openapi4j.core.validation.ValidationResults;

import java.net.URI;
import java.net.URISyntaxException;
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
  private static final ValidationResult INVALID_URI = new ValidationResult(ERROR, 144, "Invalid (or not absolute) URI '%s'");

  protected static final Pattern EMAIL_REGEX = Pattern.compile("(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9]" +
    "(?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:" +
    "(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)])");

  protected <V> void validate(final ValidationContext<O> context,
                              final O api,
                              final V value,
                              final ValidationResults results,
                              final ValidationResults.CrumbInfo crumbInfo,
                              final Validator<O, V> validator) {

    if (validator != null) {
      if (value == null) {
        results.add(MALFORMED_SPEC, crumbInfo.crumb());
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
                                   final ValidationResults.CrumbInfo crumbInfo,
                                   final Validator<O, V> validator) {

    results.withCrumb(crumbInfo, () -> {
      if (validateRequired(value, results, required, crumbInfo)) {
        validate(context, api, value, results, crumbInfo, validator);
      }
    });
  }

  @SuppressWarnings("SameParameterValue")
  protected <V> void validateList(final ValidationContext<O> context,
                                  final O api,
                                  final Collection<? extends V> value,
                                  final ValidationResults results,
                                  final boolean required,
                                  final ValidationResults.CrumbInfo crumbInfo,
                                  final Validator<O, V> validator) {

    results.withCrumb(crumbInfo, () -> {
      if (validateRequired(value, results, required, crumbInfo)) {
        Set<? extends V> set = new HashSet<>(value);
        if (set.size() != value.size()) {
          results.add(crumbInfo, DUPLICATED_VALUES, crumbInfo.crumb());
        }

        if (validator != null) {
          for (V element : value) {
            validate(context, api, element, results, crumbInfo, validator);
          }
        }
      }
    });
  }

  @SuppressWarnings("SameParameterValue")
  protected void validatePattern(final String pattern,
                                 final ValidationResults results,
                                 final boolean required,
                                 final ValidationResults.CrumbInfo crumbInfo) {

    if (validateRequired(pattern, results, required, crumbInfo)) {
      try {
        Pattern.compile(pattern);
      } catch (PatternSyntaxException e) {
        results.add(crumbInfo, INVALID_PATTERN, pattern);
      }
    }
  }

  @SuppressWarnings("SameParameterValue")
  protected <V extends Number> void validatePositive(final V value,
                                                     final ValidationResults results,
                                                     final boolean required,
                                                     final ValidationResults.CrumbInfo crumbInfo) {

    if (validateRequired(value, results, required, crumbInfo) && value.doubleValue() <= 0) {
      results.add(crumbInfo, POSITIVE_STRICT_VALUE, value);
    }
  }

  @SuppressWarnings("SameParameterValue")
  protected <V extends Number> void validateNonNegative(final V value,
                                                        final ValidationResults results,
                                                        final boolean required,
                                                        final ValidationResults.CrumbInfo crumbInfo) {

    if (validateRequired(value, results, required, crumbInfo) && value.doubleValue() < 0) {
      results.add(crumbInfo, POSITIVE_VALUE, value);
    }
  }

  protected boolean validateRequired(final Object value,
                                     final ValidationResults results,
                                     final boolean required,
                                     final ValidationResults.CrumbInfo crumbInfo) {
    boolean isPresent = value != null;

    if (required && !isPresent) {
      results.add(crumbInfo, MISSING_REQUIRED_FIELD, crumbInfo.crumb());
      return false;
    }

    return isPresent;
  }

  protected void validateString(final String value,
                                final ValidationResults results,
                                final boolean required,
                                final ValidationResults.CrumbInfo crumbInfo) {

    validateString(value, results, required, (Pattern) null, crumbInfo);
  }

  protected void validateString(final String value,
                                final ValidationResults results,
                                final boolean required,
                                final String pattern,
                                final ValidationResults.CrumbInfo crumbInfo) {

    validateString(value, results, required, Pattern.compile(pattern), crumbInfo);
  }

  protected void validateString(final String value,
                                final ValidationResults results,
                                final boolean required,
                                final Pattern pattern,
                                final ValidationResults.CrumbInfo crumbInfo) {

    if (validateRequired(value, results, required, crumbInfo) && pattern != null && !pattern.matcher(value).matches()) {
      results.add(crumbInfo, PATTERN_NOT_MATCHED, value, pattern);
    }
  }

  protected <V> void validateMap(final ValidationContext<O> context,
                                 final O api,
                                 final Map<String, ? extends V> value,
                                 final ValidationResults results,
                                 final boolean required,
                                 final ValidationResults.CrumbInfo crumbInfo,
                                 final Pattern pattern,
                                 final Validator<O, V> validator) {

    results.withCrumb(crumbInfo, () -> {
      if (validateRequired(value, results, required, crumbInfo)) {
        for (final Map.Entry<String, ? extends V> entry : value.entrySet()) {
          results.withCrumb(new ValidationResults.CrumbInfo(entry.getKey(), false), () -> {
            checkKey(entry.getKey(), pattern, results);
            validate(context, api, entry.getValue(), results, crumbInfo, validator);
          });
        }
      }
    });
  }

  @SuppressWarnings("SameParameterValue")
  protected void validateUri(final String value,
                             final ValidationResults results,
                             final boolean required,
                             final boolean allowRelative,
                             final ValidationResults.CrumbInfo crumbInfo) {

    validateString(value, results, required, (Pattern) null, crumbInfo);
    if (value != null) {
      try {
        URI uri = new URI(value);
        if (!allowRelative && !uri.isAbsolute()) {
          results.add(crumbInfo, INVALID_URI, value);
        }
      } catch (URISyntaxException e) {
        results.add(crumbInfo, INVALID_URI, value);
      }
    }
  }

  private void checkKey(final String key,
                        final Pattern pattern,
                        final ValidationResults results) {

    if (pattern != null && !pattern.matcher(key).matches()) {
      results.add(INVALID_KEY, key, pattern);
    }
  }
}
