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
  public void withCrumb() throws InterruptedException {
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
    results.add(ValidationSeverity.WARNING, "msg2", "crumb");
    results.add(ValidationSeverity.ERROR, "msg");
    results.add(ValidationSeverity.INFO, "msg");
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
  }
}
