package org.openapi4j.core.model;

import org.junit.Test;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

public class AuthOptionTest {
  @Test
  public void defaultAuthOption() {
    AuthOption option1 = new AuthOption(AuthOption.Type.HEADER, "myHeader", "myValue");
    AuthOption option2 = new AuthOption(AuthOption.Type.HEADER, "myHeader", "myValue");

    Set<AuthOption> options = new HashSet<>();
    assertTrue(options.add(option1));
    assertFalse(options.add(option2));
  }

  @Test
  public void paramAuthOption() throws MalformedURLException {
    URL url = new URL("http://foo.com");
    Predicate<URL> urlMatcher = urlPredicate -> urlPredicate.equals(url);

    AuthOption option1 = new AuthOption(AuthOption.Type.HEADER, "myHeader", "myValue", urlMatcher);
    AuthOption option2 = new AuthOption(AuthOption.Type.HEADER, "myHeader", "myValue", urlMatcher);

    assertEquals(option1, option2);

    Set<AuthOption> options = new HashSet<>();
    assertTrue(options.add(option1));
    assertFalse(options.add(option2));
  }

  @Test
  public void equalsTest() {
    AuthOption option1 = new AuthOption(AuthOption.Type.HEADER, "myHeader", "myValue");
    AuthOption option2 = new AuthOption(AuthOption.Type.HEADER, "myHeader", "myValue");

    assertEquals(option1, option1);
    assertEquals(option1, option2);
    assertNotEquals(option1, null);
    assertNotEquals(option1, new Object());

    assertNotEquals(option1, new AuthOption(AuthOption.Type.QUERY, "myHeader", "myValue"));
    assertNotEquals(option1, new AuthOption(AuthOption.Type.HEADER, "myFooHeader", "myValue"));
    assertNotEquals(option1, new AuthOption(AuthOption.Type.HEADER, "myHeader", "myFooValue"));
  }
}
