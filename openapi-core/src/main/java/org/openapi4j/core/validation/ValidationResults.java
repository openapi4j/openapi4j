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
@SuppressWarnings("unused")
public class ValidationResults implements Serializable {
  private static final long serialVersionUID = 1905122041950251207L;

  private static final String LINE_SEPARATOR = String.format("%n");
  private static final String ERROR_TITLE = "Validation error(s) :" + LINE_SEPARATOR;
  private static final String WARNING_TITLE = "Validation warning(s) :" + LINE_SEPARATOR;
  private static final String INFO_TITLE = "Validation info(s) :" + LINE_SEPARATOR;

  // The validation items
  private final List<ValidationItem> items = new ArrayList<>();
  // The breadcrumb
  private final Deque<String> crumbs = new ArrayDeque<>();
  // The current validation severity
  private ValidationSeverity validationSeverity = ValidationSeverity.NONE;

  /**
   * Add a result.
   *
   * @param severity The given severity.
   * @param msg      The associated message.
   */
  public void add(ValidationSeverity severity, String msg) {
    items.add(new ValidationItem(severity, msg, crumbs));
    if (severity.getValue() > validationSeverity.getValue()) {
      validationSeverity = severity;
    }
  }

  /**
   * Add a result.
   *
   * @param severity The given severity.
   * @param msg      The associated message.
   * @param crumb    The path item to add the result.
   */
  public void add(ValidationSeverity severity, String msg, String crumb) {
    items.add(new ValidationItem(severity, msg, crumbs, crumb));
    if (severity.getValue() > validationSeverity.getValue()) {
      validationSeverity = severity;
    }
  }

  /**
   * Add an info.
   *
   * @param msg The associated message.
   */
  public void addInfo(String msg) {
    add(ValidationSeverity.INFO, msg);
  }

  /**
   * Add an info.
   *
   * @param msg   The associated message.
   * @param crumb The path item to add the result.
   */
  public void addInfo(String msg, String crumb) {
    add(ValidationSeverity.INFO, msg, crumb);
  }

  /**
   * Add a warning.
   *
   * @param msg The associated message.
   */
  public void addWarning(String msg) {
    add(ValidationSeverity.WARNING, msg);
  }

  /**
   * Add a warning.
   *
   * @param msg   The associated message.
   * @param crumb The path item to add the result.
   */
  public void addWarning(String msg, String crumb) {
    add(ValidationSeverity.WARNING, msg, crumb);
  }

  /**
   * Add an error.
   *
   * @param msg The associated message.
   */
  public void addError(String msg) {
    add(ValidationSeverity.ERROR, msg);
  }

  /**
   * Add an error.
   *
   * @param msg   The associated message.
   * @param crumb The path item to add the result.
   */
  public void addError(String msg, String crumb) {
    add(ValidationSeverity.ERROR, msg, crumb);
  }

  /**
   * Append other results to the current stack.
   *
   * @param results The stack to append.
   */
  public void add(ValidationResults results) {
    for (ValidationItem item : results.items) {
      items.add(item);
      if (item.severity.getValue() > validationSeverity.getValue()) {
        validationSeverity = item.severity;
      }
    }
  }

  /**
   * Get the individual results as view.
   */
  public Collection<ValidationItem> getItems() {
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
    try {
      if (crumb != null) {
        crumbs.addLast(crumb);
        append = true;
      }
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

  @Override
  public String toString() {
    StringBuilder errBuilder = new StringBuilder();
    StringBuilder warnBuilder = new StringBuilder();
    StringBuilder infoBuilder = new StringBuilder();

    for (ValidationResults.ValidationItem item : items) {
      switch (item.severity) {
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

  public static class ValidationItem implements Serializable {
    private static final long serialVersionUID = 7905122048950251207L;

    private static final String DOT = ".";
    private static final String SEMI_COLON = " : ";
    private static final String EMPTY = "";

    private final ValidationSeverity severity;
    private final String msg;
    private final String crumbs;

    ValidationItem(ValidationSeverity severity, String msg, Collection<String> crumbs) {
      this(severity, msg, crumbs, null);
    }

    ValidationItem(ValidationSeverity severity, String msg, Collection<String> crumbs, String crumb) {
      this.severity = severity;
      this.msg = msg;
      this.crumbs = joinCrumbs(crumbs, crumb);
    }

    @Override
    public String toString() {
      String label = !crumbs.isEmpty() ? crumbs + SEMI_COLON : EMPTY;
      return label + msg;
    }

    private String joinCrumbs(Collection<String> crumbs, String additionalCrumb) {
      String result = String.join(DOT, crumbs);

      if (additionalCrumb != null) {
        if (result.length() != 0) {
          return result + DOT + additionalCrumb;
        }
        return additionalCrumb;
      }
      return result;
    }
  }
}
