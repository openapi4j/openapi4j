package org.openapi4j.operation.validator.adapters.spring.mock;

import static org.junit.Assert.*;

import java.io.IOException;
import org.junit.Test;
import org.mockito.Mockito;
import org.openapi4j.parser.model.v3.OpenApi3;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

public class OpenApiCacheTest {
  private static final ClassPathResource SPEC = new ClassPathResource("openapi3.yaml");

  private OpenApiCache tested = new OpenApiCache();

  @Test
  public void validSpecIsLoaded() {
    OpenApi3 actual = tested.loadApi(SPEC);

    assertNotNull(actual.getInfo());
    assertNull(actual.getServers());
  }

  @Test
  public void aSpecIsLoadedOnlyOnce() throws IOException {
    Resource spec = Mockito.mock(Resource.class);
    Mockito.when(spec.getURL())
      .thenReturn(SPEC.getURL());

    OpenApi3 actual1 = tested.loadApi(spec);
    OpenApi3 actual2 = tested.loadApi(spec);

    assertSame(actual1, actual2);
    Mockito.verify(spec).getURL();
    Mockito.verifyNoMoreInteractions(spec);
  }
}
