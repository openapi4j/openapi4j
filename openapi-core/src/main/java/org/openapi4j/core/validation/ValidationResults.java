package org.openapi4j.core.validation;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

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
   * @param result  validation result to append.
   * @param msgArgs message arguments to get formatted message.
   */
  public void add(ValidationResult result, Object... msgArgs) {
    items.add(new ValidationItem(result, crumbs, msgArgs));
    if (result.severity().gt(validationSeverity)) {
      validationSeverity = result.severity();
    }
  }

  /**
   * Add a result.
   *
   * @param crumbInfo path item to add the result.
   * @param result    validation result to append.
   * @param msgArgs   message arguments to get formatted message.
   */
  public void add(CrumbInfo crumbInfo, ValidationResult result, Object... msgArgs) {
    items.add(new ValidationItem(result, crumbs, crumbInfo, msgArgs));
    if (result.severity().gt(validationSeverity)) {
      validationSeverity = result.severity();
    }
  }

  /**
   * Append other results to the current stack.
   *
   * @param results The stack to append. Must be non {@code null}.
   */
  public void add(ValidationResults results) {
    items.addAll(results.items);

    if (results.severity().gt(validationSeverity)) {
      validationSeverity = results.severity();
    }
  }

  /**
   * Append other results to the current stack with given parent crumbs.
   *
   * @param parentCrumbs The given parent crumbs to insert. Must be non {@code null}.
   * @param results      The stack to append. Must be non {@code null}.
   */
  public void add(Collection<CrumbInfo> parentCrumbs, ValidationResults results) {
    // Add parent crumbs
    for (ValidationItem item : results.items) {
      item.crumbs.addAll(0, parentCrumbs);
    }

    add(results);
  }

  /**
   * Append other results to the current stack with given parent crumbs.
   *
   * @param parentCrumbs The given parent crumbs to insert. Must be non {@code null}.
   * @param resultItems  The stack to append. Must be non {@code null}.
   */
  public void add(Collection<ValidationResults.CrumbInfo> parentCrumbs, Collection<ValidationResults.ValidationItem> resultItems) {
    // Add parent crumbs & update severity
    for (ValidationItem item : resultItems) {
      item.crumbs.addAll(0, parentCrumbs);

      if (item.severity().gt(validationSeverity)) {
        validationSeverity = item.severity();
      }
    }

    items.addAll(resultItems);
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
   * Get the individual results as view.
   *
   * @param severity filter items with the given severity.
   */
  public List<ValidationItem> items(ValidationSeverity severity) {
    return Collections.unmodifiableList(items
      .stream()
      .filter(item -> severity == item.severity())
      .collect(Collectors.toList()));
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
    private static final String SLASH = "/";
    private static final String SEMI_COLON = ": ";

    private final List<CrumbInfo> crumbs;

    ValidationItem(ValidationResult result, Collection<CrumbInfo> crumbs, Object... msgArgs) {
      this(result, crumbs, null, msgArgs);
    }

    ValidationItem(ValidationResult result, Collection<CrumbInfo> crumbs, CrumbInfo crumbInfo, Object... msgArgs) {
      super(
        result.severity(),
        result.code(),
        (msgArgs.length != 0) ? String.format(result.message(), msgArgs) : result.message());

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
     * Compile and get data path as JSON string representation.<br/>
     * https://tools.ietf.org/html/rfc6901<br/>
     * Warning, the compilation occurs at each call.
     *
     * @return The data path as JSON string representation.
     */
    public String dataJsonPointer() {
      StringJoiner joiner = new StringJoiner(SLASH);
      boolean hasCrumb = false;

      for (CrumbInfo crumb : crumbs) {
        if (crumb.crumb() == null) continue;

        if (!crumb.isSchemaCrumb()) {
          hasCrumb = true;
          joiner.add(escapeJsonPointerFragment(crumb.crumb()));
        }
      }

      if (hasCrumb) {
        return SLASH + joiner.toString();
      } else {
        return joiner.toString();
      }
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
      if (code() != null) {
        strBuilder.append(CODE_START_LBL).append(code()).append(CODE_END_LBL);
      }
      String schemaCrumbs = schemaCrumbs();
      if (schemaCrumbs != null && !schemaCrumbs.isEmpty()) {
        strBuilder.append(LINE_SEPARATOR).append(FROM).append(schemaCrumbs());
      }

      return strBuilder.toString();
    }

    private static String escapeJsonPointerFragment(String fragment) {
      StringBuilder sb = new StringBuilder();

      for (int i = 0, end = fragment.length(); i < end; ++i) {
        char c = fragment.charAt(i);
        if (c == '/') {
          sb.append("~1");
        } else if (c == '~') {
          sb.append("~0");
        } else {
          sb.append(c);
        }
      }

      return sb.toString();
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
