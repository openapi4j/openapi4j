package org.openapi4j.core.validation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Representation of results from a validation process.
 */
@SuppressWarnings("unused")
public class ValidationResults implements Serializable {
  private static final long serialVersionUID = 1905122041950251207L;

  // The validation items
  private final List<ValidationItem> items = new ArrayList<>();
  // The breadcrumb
  // We use treemap for performance to avoid array copies here
  private final Map<Integer, String> crumbs = new TreeMap<>();

  /**
   * Add a result.
   *
   * @param severity The given severity.
   * @param msg      The associated message.
   */
  public void add(ValidationSeverity severity, String msg) {
    items.add(new ValidationItem(severity, msg, crumbs));
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
  }

  /**
   * Add an info.
   *
   * @param msg The associated message.
   */
  public void addInfo(String msg) {
    items.add(new ValidationItem(ValidationSeverity.INFO, msg, crumbs));
  }

  /**
   * Add an info.
   *
   * @param msg   The associated message.
   * @param crumb The path item to add the result.
   */
  public void addInfo(String msg, String crumb) {
    items.add(new ValidationItem(ValidationSeverity.INFO, msg, crumbs, crumb));
  }

  /**
   * Add a warning.
   *
   * @param msg The associated message.
   */
  public void addWarning(String msg) {
    items.add(new ValidationItem(ValidationSeverity.WARNING, msg, crumbs));
  }

  /**
   * Add a warning.
   *
   * @param msg   The associated message.
   * @param crumb The path item to add the result.
   */
  public void addWarning(String msg, String crumb) {
    items.add(new ValidationItem(ValidationSeverity.WARNING, msg, crumbs, crumb));
  }

  /**
   * Add an error.
   *
   * @param msg The associated message.
   */
  public void addError(String msg) {
    items.add(new ValidationItem(ValidationSeverity.ERROR, msg, crumbs));
  }

  /**
   * Add an error.
   *
   * @param msg   The associated message.
   * @param crumb The path item to add the result.
   */
  public void addError(String msg, String crumb) {
    items.add(new ValidationItem(ValidationSeverity.ERROR, msg, crumbs, crumb));
  }

  /**
   * Append other results to the current stack.
   *
   * @param results The stack to append.
   */
  public void add(ValidationResults results) {
    items.addAll(results.getItems());
  }

  /**
   * Get the individual results.
   */
  public Collection<ValidationItem> getItems() {
    return items;
  }

  /**
   * Get the current highest severity of the results.
   *
   * @return {@code ValidationSeverity.NONE} to {@code ValidationSeverity.ERROR}
   */
  public ValidationSeverity getSeverity() {
    ValidationSeverity severity = ValidationSeverity.NONE;
    for (ValidationItem item : items) {
      if (item.severity().gt(severity)) {
        severity = item.severity();
        if (severity == ValidationSeverity.ERROR) {
          break;
        }
      }
    }
    return severity;
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
      append = appendCrumb(crumb);
      code.run();
    } finally {
      if (append) {
        crumbs.remove(crumbs.size() - 1);
      }
    }
  }

  private boolean appendCrumb(String crumb) {
    if (crumb != null) {
      crumbs.put(crumbs.size() + 1, crumb);
      return true;
    }

    return false;
  }

  /**
   * Check if the results are below the {@code ValidationSeverity.ERROR}
   *
   * @return {@code true} if the results are below {@code ValidationSeverity.ERROR}.
   */
  public boolean isValid() {
    return getSeverity() != ValidationSeverity.ERROR;
  }

  /**
   * Get the number of items in the current stack.
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

    for (ValidationResults.ValidationItem item : getItems()) {
      switch (item.severity()) {
        case ERROR:
          errBuilder.append(item.toString()).append("\n");
          break;
        case WARNING:
          warnBuilder.append(item.toString()).append("\n");
          break;
        default://case INFO:
          infoBuilder.append(item.toString()).append("\n");
          break;
      }
    }

    if (errBuilder.length() != 0) {
      errBuilder.insert(0, "Validation error(s) :\n");
    }
    if (warnBuilder.length() != 0) {
      warnBuilder.insert(0, "Validation warning(s) :\n");
    }
    if (infoBuilder.length() != 0) {
      infoBuilder.insert(0, "Validation info(s) :\n");
    }

    return errBuilder.append(warnBuilder).append(infoBuilder).toString();
  }

  public static class ValidationItem implements Serializable {
    private static final long serialVersionUID = 7905122048950251207L;

    private static final String DOT = ".";

    private final ValidationSeverity severity;
    private final String msg;
    private final String crumbs;

    ValidationItem(ValidationSeverity severity, String msg, Map<Integer, String> crumbs) {
      this.severity = severity;
      this.msg = msg;
      this.crumbs = joinCrumbs(crumbs.values(), null);
    }

    ValidationItem(ValidationSeverity severity, String msg, Map<Integer, String> crumbs, String crumb) {
      this.severity = severity;
      this.msg = msg;
      this.crumbs = joinCrumbs(crumbs.values(), crumb);
    }

    @SuppressWarnings("WeakerAccess")
    public ValidationSeverity severity() {
      return severity;
    }

    public String message() {
      return msg;
    }

    public String crumbs() {
      return crumbs;
    }

    @Override
    public String toString() {
      String label = !crumbs.isEmpty() ? crumbs + " : " : "";
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
