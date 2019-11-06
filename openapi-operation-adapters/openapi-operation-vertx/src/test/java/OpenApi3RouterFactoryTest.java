import com.fasterxml.jackson.databind.node.JsonNodeFactory;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.openapi4j.core.exception.ResolutionException;
import org.openapi4j.operation.validator.adapters.server.vertx.v3.OpenApi3RouterFactory;
import org.openapi4j.operation.validator.adapters.server.vertx.v3.impl.RequestParameters;

import java.net.URL;

import io.vertx.core.http.HttpMethod;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.ext.web.Router;

@RunWith(VertxUnitRunner.class)
public class OpenApi3RouterFactoryTest extends VertxTestBase {
  private OpenApi3RouterFactory routerFactory;

  @Test
  public void loadSpecFromFile(TestContext context) {
    URL specPath = OpenApi3RouterFactoryTest.class.getResource("/api.yaml");

    Async async = context.async();
    OpenApi3RouterFactory.create(vertx, specPath,
      result -> {
        context.assertTrue(result.succeeded());
        context.assertNotNull(result.result());
        async.complete();
      });
    async.await();
  }

  @Test
  public void loadWrongSpecFromFile(TestContext context) {
    URL specPath = OpenApi3RouterFactoryTest.class.getResource("/wrong_path.yaml");

    Async async = context.async();
    OpenApi3RouterFactory.create(vertx, specPath,
      result -> {
        context.assertTrue(result.failed());
        context.assertEquals(ResolutionException.class, result.cause().getClass());
        async.complete();
      });
    async.await();
  }

  @Test
  public void mountHandlerTest(TestContext context) throws Exception {
    URL specPath = OpenApi3RouterFactoryTest.class.getResource("/api.yaml");

    Async async = context.async();
    OpenApi3RouterFactory.create(vertx, specPath, result -> {
      routerFactory = result.result();
      async.complete();
    });

    async.await();

    routerFactory.addOperationHandler("list-searchable-fields", rc -> {
      RequestParameters rqParameters = rc.get("rqParameters");
      context.assertNotNull(rqParameters);
      context.assertEquals(JsonNodeFactory.instance.textNode("foo"), rqParameters.getPathParameter("dataset"));
      context.assertEquals(JsonNodeFactory.instance.textNode("bar"), rqParameters.getPathParameter("version"));

      rc
        .response()
        .setStatusCode(200)
        .setStatusMessage("OK")
        .end();
    });

    routerFactory.addOperationHandler("records", rc -> {
      RequestParameters rqParameters = rc.get("rqParameters");
      context.assertNotNull(rqParameters);
      rc
        .response()
        .setStatusCode(200)
        .setStatusMessage("OK")
        .end();
    });

    Router router = routerFactory.getRouter();

    startServer(context, vertx, router);
    testRequest(context, HttpMethod.GET, "/fixed/foo/fixed/bar/fields/", 200, "OK");
    testRequest(context, HttpMethod.GET, "/records", 200, "OK");
  }
}
