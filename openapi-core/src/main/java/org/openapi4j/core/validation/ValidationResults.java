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
  // The schema breadcrumb
  private final Deque<CrumbInfo> crumbs = new ArrayDeque<>();
  // The current validation severity
  private ValidationSeverity validationSeverity = ValidationSeverity.NONE;

  /**
   * Add a result.
   *
   * @param validationResult validation result to append.
   * @param msgArgs          message arguments to get formatted message.
   */
  public void add(ValidationResult validationResult, Object... msgArgs) {
    items.add(new ValidationItem(validationResult, crumbs, msgArgs));
    if (validationResult.severity().gt(validationSeverity)) {
      validationSeverity = validationResult.severity();
    }
  }

  /**
   * Add a result.
   *
   * @param crumbInfo        path item to add the result.
   * @param validationResult validation result to append.
   * @param msgArgs          message arguments to get formatted message.
   */
  public void add(CrumbInfo crumbInfo, ValidationResult validationResult, Object... msgArgs) {
    items.add(new ValidationItem(validationResult, crumbs, crumbInfo, msgArgs));
    if (validationResult.severity().gt(validationSeverity)) {
      validationSeverity = validationResult.severity();
    }
  }

  /**
   * Append other results to the current stack.
   *
   * @param results The stack to append.
   */
  public void add(ValidationResults results) {
    items.addAll(results.items);

    if (results.severity().gt(validationSeverity)) {
      validationSeverity = results.severity();
    }
  }

  /**
   * Get the current breadcrumb as read-only.
   *
   * @return The current breadcrumb.
   */
  public Collection<CrumbInfo> crumbs() {
    return Collections.unmodifiableCollection(crumbs);
  }

  /**
   * Get the individual results as view.
   */
  public List<ValidationItem> items() {
    return Collections.unmodifiableList(items);
  }

  /**
   * Get the current highest severity of the results.
   *
   * @return {@code ValidationSeverity.NONE} to {@code ValidationSeverity.ERROR}
   */
  public ValidationSeverity severity() {
    return validationSeverity;
  }

  /**
   * Append a crumb and trigger the runnable code with this new context.
   *
   * @param crumbInfo The crumb to append.
   * @param code      The code to run with the appended crumb.
   */
  public void withCrumb(CrumbInfo crumbInfo, Runnable code) {
    if (crumbInfo != null) {
      crumbs.addLast(crumbInfo);
    }

    try {
      code.run();
    } finally {
      if (crumbInfo != null) {
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

    private static final String SCHEMA_CRUMB_START = "<";
    private static final String SCHEMA_CRUMB_END = ">";
    private static final String DOT = ".";
    private static final String SEMI_COLON = ": ";

    private final Collection<CrumbInfo> crumbs;

    ValidationItem(ValidationResult result, Collection<CrumbInfo> crumbs, Object... msgArgs) {
      this(result, crumbs, null, msgArgs);
    }

    ValidationItem(ValidationResult result, Collection<CrumbInfo> crumbs, CrumbInfo crumbInfo, Object... msgArgs) {
      super(
        result.severity(),
        result.code(),
        (msgArgs != null) ? String.format(result.message(), msgArgs) : result.message());

      this.crumbs = new ArrayList<>(crumbs);
      if (crumbInfo != null) {
        this.crumbs.add(crumbInfo);
      }
    }

    /**
     * Compile and get data path.
     * Warning, the compilation occurs at each call.
     *
     * @return The data path.
     */
    public String dataCrumbs() {
      StringJoiner joiner = new StringJoiner(DOT);

      for (CrumbInfo crumb : crumbs) {
        if (crumb.crumb() == null) continue;

        if (!crumb.isSchemaCrumb()) {
          joiner.add(crumb.crumb());
        }
      }

      return joiner.toString();
    }

    /**
     * Compile and get schema path definition.
     * Warning, the compilation occurs at each call.
     *
     * @return The schema path.
     */
    public String schemaCrumbs() {
      StringJoiner joiner = new StringJoiner(DOT);

      for (CrumbInfo crumb : crumbs) {
        if (crumb.crumb() == null) continue;

        if (crumb.isSchemaCrumb()) {
          joiner.add(SCHEMA_CRUMB_START + crumb.crumb() + SCHEMA_CRUMB_END);
        } else {
          joiner.add(crumb.crumb());
        }
      }

      return joiner.toString();
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
  }

  public static class CrumbInfo {
    private final String crumb;
    private final boolean isSchemaCrumb;

    public CrumbInfo(String crumb, boolean isSchemaCrumb) {
      this.crumb = crumb;
      this.isSchemaCrumb = isSchemaCrumb;
    }

    public String crumb() {
      return crumb;
    }

    public boolean isSchemaCrumb() {
      return isSchemaCrumb;
    }
  }
}
