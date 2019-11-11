package org.openapi4j.core.validation;

import org.junit.Test;

import static org.junit.Assert.*;

public class ValidationExceptionTest {
  @Test
  public void constructors() {
    ValidationException ex1 = new ValidationException("msg");
    assertEquals("msg", ex1.getMessage());
    assertNull(ex1.getResults());

    ValidationException ex2 = new ValidationException(new Exception("msg"));
    assertEquals("msg", ex2.getCause().getMessage());
    assertNull(ex2.getResults());

    ValidationResults results = new ValidationResults();
    results.addError("an error");
    ValidationException ex3 = new ValidationException("msg", results);
    assertEquals("msg", ex3.getMessage());
    assertEquals(results, ex3.getResults());
    assertTrue(ex3.toString().contains("msg"));
    assertTrue(ex3.toString().contains("an error"));
  }
}
