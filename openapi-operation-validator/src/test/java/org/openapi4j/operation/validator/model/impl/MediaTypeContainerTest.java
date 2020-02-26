package org.openapi4j.operation.validator.model.impl;

import org.junit.Test;

import static org.junit.Assert.*;

public class MediaTypeContainerTest {
    @Test
    public void match() {
      MediaTypeContainer definition = MediaTypeContainer.create("text/plain; charset=UTF-8");
      MediaTypeContainer value = MediaTypeContainer.create("text/PLAIN; charset=utf-8");
      assertTrue(definition.match(definition));
      assertTrue(definition.match(value));
    }

    @Test
    public void testEquals() {
      MediaTypeContainer definition = MediaTypeContainer.create("text/plain; charset=UTF-8");
      assertEquals(definition, definition);
      MediaTypeContainer value = MediaTypeContainer.create("text/plain; charset=UTF-8");
      assertEquals(definition, value);

      value = MediaTypeContainer.create("text/plain; charset=UTF-16");
      assertNotEquals(definition, value);

      value = MediaTypeContainer.create("application/json; charset=UTF-8");
      assertNotEquals(definition, value);

      assertNotEquals(definition, null);
      assertNotEquals(definition, new Object());
    }
}
