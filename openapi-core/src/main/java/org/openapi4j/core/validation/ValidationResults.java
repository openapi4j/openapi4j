package org.openapi4j.core.validation;

import java.io.Serializable;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.List;

/**
 * Representation of results from a validation process.
 */
public class ValidationResults implements Serializable {
  private static final long serialVersionUID = 1905122041950251284L;

  private static final String LINE_SEPARATOR = String.format("%n");
  private static final String ERROR_TITLE = "Validation error(s) :" + LINE_SEPARATOR;
  private static final String WARNING_TITLE = "Validation warning(s) :" + LINE_SEPARATOR;
  private static final String INFO_TITLE = "Validation info(s) :" + LINE_SEPARATOR;

  // The validation items
  private final List<ValidationItem> items;
  // The breadcrumb
  private final Deque<String> crumbs;
  // The current validation severity
  private ValidationSeverity validationSeverity;

  public ValidationResults() {
    this.crumbs = new ArrayDeque<>();
    this.items = new ArrayList<>();
    this.validationSeverity = ValidationSeverity.NONE;
  }

  public ValidationResults(ValidationResults results,
                           boolean itemsCopy, boolean severityCopy) {
    this.crumbs = new ArrayDeque<>(results.crumbs);
    if (itemsCopy) {
      this.items = new ArrayList<>(results.items);
    } else {
      this.items = new ArrayList<>();
    }
    if (severityCopy) {
      this.validationSeverity = results.validationSeverity;
    } else {
      this.validationSeverity = ValidationSeverity.NONE;
    }
  }

  /**
   * Add a result.
   *
   * @param validationResult validation result to append.
   * @param msgArgs          message arguments to get formatted message.
   */
  public void add(ValidationResult validationResult, Object... msgArgs) {
    items.add(new ValidationItem(validationResult, crumbs, msgArgs));
    if (validationResult.severity().getValue() > validationSeverity.getValue()) {
      validationSeverity = validationResult.severity();
    }
  }

  /**
   * Add a result.
   *
   * @param crumb            path item to add the result.
   * @param validationResult validation result to append.
   * @param msgArgs          message arguments to get formatted message.
   */
  public void add(String crumb, ValidationResult validationResult, Object... msgArgs) {
    items.add(new ValidationItem(validationResult, crumbs, crumb, msgArgs));
    if (validationResult.severity().getValue() > validationSeverity.getValue()) {
      validationSeverity = validationResult.severity();
    }
  }

  /**
   * Append other results to the current stack.
   *
   * @param results The stack to append.
   */
  public void add(ValidationResults results) {
    for (ValidationItem item : results.items) {
      items.add(item);
      if (item.severity().getValue() > validationSeverity.getValue()) {
        validationSeverity = item.severity();
      }
    }
  }

  /**
   * Get the individual results as view.
   */
  public List<ValidationItem> getItems() {
    return Collections.unmodifiableList(items);
  }

  /**
   * Get the current highest severity of the results.
   *
   * @return {@code ValidationSeverity.NONE} to {@code ValidationSeverity.ERROR}
   */
  public ValidationSeverity getSeverity() {
    return validationSeverity;
  }

  /**
   * Append a crumb to the current and trigger the runnable code with this new context.
   *
   * @param crumb The crumb to append.
   * @param code  The code to run with the appended crumb.
   */
  public void withCrumb(String crumb, Runnable code) {
    boolean append = false;

    if (crumb != null) {
      crumbs.addLast(crumb);
      append = true;
    }

    try {
      code.run();
    } finally {
      if (append) {
        crumbs.pollLast();
      }
    }
  }

  /**
   * Check if the results are below the {@code ValidationSeverity.ERROR}
   *
   * @return {@code true} if the results are below {@code ValidationSeverity.ERROR}.
   */
  public boolean isValid() {
    return validationSeverity != ValidationSeverity.ERROR;
  }

  /**
   * Get the number of items in the current stack.
   *
   * @return The number of items in the current stack.
   */
  public int size() {
    return items.size();
  }

  /**
   * Summarize all the results with errors, warnings and info sections.
   */
  @Override
  public String toString() {
    StringBuilder errBuilder = new StringBuilder();
    StringBuilder warnBuilder = new StringBuilder();
    StringBuilder infoBuilder = new StringBuilder();

    for (ValidationResults.ValidationItem item : items) {
      switch (item.severity()) {
        case ERROR:
          errBuilder.append(item.toString()).append(LINE_SEPARATOR);
          break;
        case WARNING:
          warnBuilder.append(item.toString()).append(LINE_SEPARATOR);
          break;
        default://case INFO:
          infoBuilder.append(item.toString()).append(LINE_SEPARATOR);
          break;
      }
    }

    if (errBuilder.length() != 0) {
      errBuilder.insert(0, ERROR_TITLE);
    }
    if (warnBuilder.length() != 0) {
      warnBuilder.insert(0, WARNING_TITLE);
    }
    if (infoBuilder.length() != 0) {
      infoBuilder.insert(0, INFO_TITLE);
    }

    return errBuilder.append(warnBuilder).append(infoBuilder).toString();
  }

  /**
   * Validation result with crumbs and values to format message.
   */
  public static class ValidationItem extends ValidationResult implements Serializable {
    private static final long serialVersionUID = 7905122048950251207L;

    private static final String DOT = ".";
    private static final String SEMI_COLON = " : ";

    private final String crumbs;

    ValidationItem(ValidationResult result, Collection<String> crumbs, Object... msgArgs) {
      this(result, crumbs, null, msgArgs);
    }

    ValidationItem(ValidationResult result, Collection<String> crumbs, String crumb, Object... msgArgs) {
      super(
        result.severity(),
        result.code(),
        (msgArgs != null) ? String.format(result.message(), msgArgs) : result.message());

      this.crumbs = joinCrumbs(crumbs, crumb);
    }

    public String crumbs() {
      return crumbs;
    }

    @Override
    public String toString() {
      StringBuilder strBuilder = new StringBuilder();

      if (!crumbs.isEmpty()) {
        strBuilder.append(crumbs).append(SEMI_COLON);
      }

      strBuilder.append(message());
      strBuilder.append(" (code: ").append(code()).append(")");

      return strBuilder.toString();
    }

    private String joinCrumbs(Collection<String> crumbs, String additionalCrumb) {
      String result = String.join(DOT, crumbs);

      if (additionalCrumb != null) {
        return result.length() != 0 ? result + DOT + additionalCrumb : additionalCrumb;
      }

      return result;
    }
  }
}
