package org.openapi4j.core.validation;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class ValidationResultsTest {
  @Test
  public void add() {
    ValidationResults results = new ValidationResults();
    results.add(ValidationSeverity.ERROR, "msg");
    results.add(ValidationSeverity.WARNING, "msg2", "crumb");

    ValidationResults others = new ValidationResults();
    results.add(ValidationSeverity.INFO, "msg");
    results.add(others);

    assertEquals(3, results.size());
    assertEquals(3, results.getItems().size());
    assertEquals(ValidationSeverity.ERROR, results.getSeverity());
    assertFalse(results.isValid());
  }

  @Test
  public void addInfo() {
    ValidationResults results = new ValidationResults();
    results.addInfo("msg");
    results.addInfo("msg2", "crumb");
    assertEquals(2, results.size());
    assertTrue(results.isValid());
  }

  @Test
  public void addWarning() {
    ValidationResults results = new ValidationResults();
    results.addWarning("msg");
    results.addWarning("msg2", "crumb");
    assertEquals(2, results.size());
    assertTrue(results.isValid());
  }

  @Test
  public void addError() {
    ValidationResults results = new ValidationResults();
    results.addError("msg");
    results.addError("msg2", "crumb");
    assertEquals(2, results.size());
    assertFalse(results.isValid());
  }

  @Test
  public void withCrumb() {
    ValidationResults results = new ValidationResults();
    results.withCrumb("crumb", () -> {
      // Do nothing
    });
    assertEquals(0, results.size());

    results.withCrumb(null, () -> {
      // Do nothing
    });
  }

  @Test
  public void provideString() {
    ValidationResults results = new ValidationResults();
    results.add(ValidationSeverity.ERROR, "msg");
    results.add(ValidationSeverity.WARNING, "msg2", "crumb");
    results.add(ValidationSeverity.INFO, "msg");
    assertNotNull(results.toString());
  }

  @Test
  public void severityPriority() {
    assertTrue(ValidationSeverity.ERROR.ge(ValidationSeverity.WARNING));
    assertTrue(ValidationSeverity.WARNING.ge(ValidationSeverity.INFO));
    assertTrue(ValidationSeverity.WARNING.ge(ValidationSeverity.WARNING));

    assertTrue(ValidationSeverity.ERROR.gt(ValidationSeverity.WARNING));
    assertTrue(ValidationSeverity.WARNING.gt(ValidationSeverity.INFO));

    assertTrue(ValidationSeverity.WARNING.le(ValidationSeverity.ERROR));
    assertTrue(ValidationSeverity.INFO.le(ValidationSeverity.WARNING));
    assertTrue(ValidationSeverity.WARNING.le(ValidationSeverity.WARNING));

    assertTrue(ValidationSeverity.WARNING.lt(ValidationSeverity.ERROR));
    assertTrue(ValidationSeverity.INFO.lt(ValidationSeverity.WARNING));
  }
}
