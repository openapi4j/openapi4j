package org.openapi4j.core.validation;

import org.junit.Test;

import static org.junit.Assert.*;

public class ValidationExceptionTest {
  @Test
  public void constructors() {
    ValidationException ex1 = new ValidationException("msg");
    assertEquals("msg", ex1.getMessage());
    assertNull(ex1.results());

    ValidationException ex2 = new ValidationException(new Exception("msg"));
    assertEquals("msg", ex2.getCause().getMessage());
    assertNull(ex2.results());

    ValidationResults results = new ValidationResults();
    results.add(new ValidationResult(ValidationSeverity.ERROR, 15, "an error"));
    ValidationException ex3 = new ValidationException("msg", results);
    assertEquals("msg", ex3.getMessage());
    assertEquals(results, ex3.results());
    assertTrue(ex3.toString().contains("msg"));
    assertTrue(ex3.toString().contains("an error"));

    assertNotNull(new ValidationException((String) null).toString());
    assertNotNull(new ValidationException(null, results).toString());
  }
}
