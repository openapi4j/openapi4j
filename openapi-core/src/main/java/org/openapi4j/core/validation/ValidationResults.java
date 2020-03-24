package org.openapi4j.core.validation;

import java.io.Serializable;
import java.util.*;

/**
 * Representation of results from a validation process.
 */
public class ValidationResults implements Serializable {
  private static final long serialVersionUID = 1905122041950251284L;

  private static final String LINE_SEPARATOR = String.format("%n");
  private static final String CODE_START_LBL = " (code: ";
  private static final String CODE_END_LBL = ")";
  private static final String FROM = "From: ";

  private static final String ERROR_TITLE = "Validation error(s) :" + LINE_SEPARATOR;
  private static final String WARNING_TITLE = "Validation warning(s) :" + LINE_SEPARATOR;
  private static final String INFO_TITLE = "Validation info(s) :" + LINE_SEPARATOR;

  // The validation items
  private final List<ValidationItem> items = new ArrayList<>();
  // The data breadcrumb
  private final Deque<String> dataCrumbs = new ArrayDeque<>();
  // The schema breadcrumb
  private final Deque<String> schemaCrumbs = new ArrayDeque<>();
  // The current validation severity
  private ValidationSeverity validationSeverity = ValidationSeverity.NONE;

  /**
   * Add a result.
   *
   * @param validationResult validation result to append.
   * @param msgArgs          message arguments to get formatted message.
   */
  public void add(ValidationResult validationResult, Object... msgArgs) {
    items.add(new ValidationItem(validationResult, schemaCrumbs, dataCrumbs, msgArgs));
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
    items.add(new ValidationItem(validationResult, schemaCrumbs, dataCrumbs, crumb, msgArgs));
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
   * @param crumb           The crumb to append.
   * @param isSchemaKeyword Flag to know if this crumb should be appended to the current queue.
   * @param code            The code to run with the appended crumb.
   */
  public void withCrumb(String crumb, boolean isSchemaKeyword, Runnable code) {
    boolean append = false;

    if (crumb != null) {
      schemaCrumbs.addLast(crumb);

      if (!isSchemaKeyword) {
        dataCrumbs.addLast(crumb);
      }

      append = true;
    }

    try {
      code.run();
    } finally {
      if (append) {
        schemaCrumbs.pollLast();

        if (!isSchemaKeyword) {
          dataCrumbs.pollLast();
        }
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

    private final String dataCrumbs;
    private final String schemaCrumbs;

    ValidationItem(ValidationResult result, Collection<String> schemaCrumbs, Collection<String> dataCrumbs, Object... msgArgs) {
      this(result, schemaCrumbs, dataCrumbs, null, msgArgs);
    }

    ValidationItem(ValidationResult result, Collection<String> schemaCrumbs, Collection<String> dataCrumbs, String crumb, Object... msgArgs) {
      super(
        result.severity(),
        result.code(),
        (msgArgs != null) ? String.format(result.message(), msgArgs) : result.message());

      this.schemaCrumbs = joinCrumbs(schemaCrumbs, crumb);
      this.dataCrumbs = joinCrumbs(dataCrumbs, null);
    }

    /**
     * Get data path.
     * Note that array indexes are not part of this path.
     *
     * @return The data path.
     */
    public String dataCrumbs() {
      return dataCrumbs;
    }

    /**
     * Get schema path definition.
     *
     * @return The schema path.
     */
    public String schemaCrumbs() {
      return schemaCrumbs;
    }

    @Override
    public String toString() {
      StringBuilder strBuilder = new StringBuilder();

      if (!dataCrumbs().isEmpty()) {
        strBuilder.append(dataCrumbs()).append(SEMI_COLON);
      }

      strBuilder.append(message());
      strBuilder.append(CODE_START_LBL).append(code()).append(CODE_END_LBL);
      strBuilder.append(LINE_SEPARATOR).append(FROM).append(schemaCrumbs());

      return strBuilder.toString();
    }

    private String joinCrumbs(Collection<String> crumbs, String additionalCrumb) {
      StringJoiner joiner = new StringJoiner(DOT);

      for (String crumb : crumbs) {
        joiner.add(crumb);
      }

      if (additionalCrumb != null) {
        joiner.add(additionalCrumb);
      }

      return joiner.toString();
    }
  }
}
