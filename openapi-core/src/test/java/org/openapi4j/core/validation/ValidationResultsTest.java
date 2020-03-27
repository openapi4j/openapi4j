package org.openapi4j.core.validation;

import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public class ValidationResultsTest {

  @Test
  public void anonymousConstructor() {
    ValidationResult result = new ValidationResult();
    assertEquals(ValidationSeverity.NONE, result.severity());
    assertNull(result.code());
    assertNull(result.message());
  }

  @Test
  public void add() {
    ValidationResults results = new ValidationResults();
    results.add(new ValidationResult(ValidationSeverity.INFO, 1, "info"));
    results.add(new ValidationResults.CrumbInfo("crumb", false), new ValidationResult(ValidationSeverity.WARNING, 2, "warn"));

    ValidationResults others = new ValidationResults();
    others.add(new ValidationResult(ValidationSeverity.ERROR, 3, "error"));
    others.add(new ValidationResult(ValidationSeverity.INFO, 4, "info2"));
    results.add(others);

    assertEquals(4, results.size());
    assertEquals(4, results.items().size());
    assertEquals(ValidationSeverity.ERROR, results.severity());
    assertFalse(results.isValid());
  }

  @Test
  public void withCrumb() throws Exception {
    ValidationResults results = new ValidationResults();
    final CountDownLatch latch = new CountDownLatch(1);

    results.withCrumb(new ValidationResults.CrumbInfo("crumb", false), () -> {
      assertEquals(1, results.crumbs().size());
      assertEquals("crumb", results.crumbs().iterator().next().crumb());
      latch.countDown();
    });
    latch.await(2, TimeUnit.SECONDS);
    assertEquals(0, results.size());

    CountDownLatch latch2 = new CountDownLatch(1);
    results.withCrumb(new ValidationResults.CrumbInfo(null, false), latch2::countDown);
    latch2.await(2, TimeUnit.SECONDS);
  }

  @Test
  public void provideString() {
    ValidationResults results = new ValidationResults();
    results.add(new ValidationResults.CrumbInfo("crumb", false), new ValidationResult(ValidationSeverity.WARNING, 1, "msg2"));
    results.add(new ValidationResult(ValidationSeverity.ERROR, 2, "msg"));
    results.add(new ValidationResult(ValidationSeverity.INFO, 3, "msg"));
    assertNotNull(results.toString());

    assertEquals("msg2", results.items().iterator().next().message());
    assertEquals(ValidationSeverity.WARNING, results.items().iterator().next().severity());
    assertEquals("crumb", results.items().iterator().next().schemaCrumbs());
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
