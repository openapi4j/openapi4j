package org.openapi4j.core.validation;

import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class ValidationResultsTest {
  @Test
  public void add() {
    ValidationResults results = new ValidationResults();
    results.add(new ValidationResult(ValidationSeverity.INFO, 1, "info"));
    results.add("crumb", new ValidationResult(ValidationSeverity.WARNING, 2, "warn"));

    ValidationResults others = new ValidationResults();
    others.add(new ValidationResult(ValidationSeverity.ERROR, 3, "error"));
    others.add(new ValidationResult(ValidationSeverity.INFO, 4, "info2"));
    results.add(others);

    assertEquals(4, results.size());
    assertEquals(4, results.getItems().size());
    assertEquals(ValidationSeverity.ERROR, results.getSeverity());
    assertFalse(results.isValid());
  }

  @Test
  public void withCrumb() throws Exception {
    ValidationResults results = new ValidationResults();
    CountDownLatch latch = new CountDownLatch(1);
    results.withCrumb("crumb", latch::countDown);
    latch.await(2, TimeUnit.SECONDS);
    assertEquals(0, results.size());

    latch = new CountDownLatch(1);
    results.withCrumb(null, latch::countDown);
    latch.await(2, TimeUnit.SECONDS);
  }

  @Test
  public void provideString() {
    ValidationResults results = new ValidationResults();
    results.add("crumb", new ValidationResult(ValidationSeverity.WARNING, 1, "msg2"));
    results.add(new ValidationResult(ValidationSeverity.ERROR, 2, "msg"));
    results.add(new ValidationResult(ValidationSeverity.INFO, 3, "msg"));
    assertNotNull(results.toString());

    assertEquals("msg2", results.getItems().iterator().next().message());
    assertEquals(ValidationSeverity.WARNING, results.getItems().iterator().next().severity());
    assertEquals("crumb", results.getItems().iterator().next().crumbs());
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


    assertFalse(ValidationSeverity.WARNING.ge(ValidationSeverity.ERROR));
    assertFalse(ValidationSeverity.INFO.gt(ValidationSeverity.WARNING));
    assertFalse(ValidationSeverity.ERROR.le(ValidationSeverity.WARNING));
    assertFalse(ValidationSeverity.ERROR.lt(ValidationSeverity.WARNING));
  }
}
