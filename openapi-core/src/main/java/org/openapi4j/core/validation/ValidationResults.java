package org.openapi4j.core.validation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@SuppressWarnings("unused")
public class ValidationResults {
  // The validation items
  private final List<ValidationItem> items = new ArrayList<>();
  // The breadcrumb
  // We use treemap for performance to avoid array copies here
  private final Map<Integer, String> crumbs = new TreeMap<>();

  public void add(ValidationSeverity severity, String msg) {
    items.add(new ValidationItem(severity, msg, crumbs));
  }

  public void add(ValidationSeverity severity, String msg, String crumb) {
    items.add(new ValidationItem(severity, msg, crumbs, crumb));
  }

  public void addInfo(String msg) {
    items.add(new ValidationItem(ValidationSeverity.INFO, msg, crumbs));
  }

  public void addInfo(String msg, String crumb) {
    items.add(new ValidationItem(ValidationSeverity.INFO, msg, crumbs, crumb));
  }

  public void addWarning(String msg) {
    items.add(new ValidationItem(ValidationSeverity.WARNING, msg, crumbs));
  }

  public void addWarning(String msg, String crumb) {
    items.add(new ValidationItem(ValidationSeverity.WARNING, msg, crumbs, crumb));
  }

  public void addError(String msg) {
    items.add(new ValidationItem(ValidationSeverity.ERROR, msg, crumbs));
  }

  public void addError(String msg, String crumb) {
    items.add(new ValidationItem(ValidationSeverity.ERROR, msg, crumbs, crumb));
  }

  public void add(ValidationResults results) {
    items.addAll(results.getItems());
  }

  public Collection<ValidationItem> getItems() {
    return items;
  }

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

  public boolean isValid() {
    return getSeverity() != ValidationSeverity.ERROR;
  }

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

  public static class ValidationItem {
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
      StringBuilder stringBuilder = new StringBuilder();

      for (String crumb : crumbs) {
        stringBuilder.append(crumb).append(DOT);
      }

      if (additionalCrumb != null) {
        stringBuilder.append(additionalCrumb);
      } else if (crumbs.size() != 0) {
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
      }

      return stringBuilder.toString();
    }
  }
}
