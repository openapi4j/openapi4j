import org.junit.Test;
import org.openapi4j.core.exception.ResolutionException;
import org.openapi4j.operation.validator.adapters.server.vertx.v3.OpenApi3RouterFactory;

import java.net.URL;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.ext.web.Router;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class OpenApi3RouterFactoryTest {
  private OpenApi3RouterFactory routerFactory;

  @Test
  public void loadSpecFromFile() throws Exception {
    URL specPath = OpenApi3RouterFactoryTest.class.getResource("/api.yaml");

    CountDownLatch latch = new CountDownLatch(1);
    OpenApi3RouterFactory.create(Vertx.vertx(), specPath,
      result -> {
        assertTrue(result.succeeded());
        assertNotNull(result.result());
        latch.countDown();
      });
    latch.await(10, TimeUnit.SECONDS);
  }

  @Test
  public void loadWrongSpecFromFile() throws Exception {
    URL specPath = OpenApi3RouterFactoryTest.class.getResource("/wrong_path.yaml");

    CountDownLatch latch = new CountDownLatch(1);
    OpenApi3RouterFactory.create(Vertx.vertx(), specPath,
      result -> {
        assertTrue(result.failed());
        assertEquals(ResolutionException.class, result.cause().getClass());
        latch.countDown();
      });
    latch.await(10, TimeUnit.SECONDS);
  }

  @Test
  public void mountHandlerTest() throws Exception {
    URL specPath = OpenApi3RouterFactoryTest.class.getResource("/api.yaml");

    Vertx vertx = Vertx.vertx();

    CountDownLatch latch = new CountDownLatch(1);
    OpenApi3RouterFactory.create(vertx, specPath, result -> {
      routerFactory = result.result();
      latch.countDown();
    });

    latch.await(10, TimeUnit.SECONDS);

    routerFactory.addOperationHandler("list-searchable-fields", routingContext -> routingContext
      .response()
      .setStatusCode(200)
      .setStatusMessage("OK")
      .end());

    Router router = routerFactory.getRouter();

    //startServer(vertx, router);
  }

  private void startServer(Vertx vertx, Router router) throws InterruptedException {
    HttpServer server = vertx.createHttpServer(new HttpServerOptions().setPort(8080).setHost("localhost"));
    CountDownLatch latch = new CountDownLatch(1);
    server.requestHandler(router).listen(res -> latch.countDown());
    latch.await(10, TimeUnit.SECONDS);
  }
}
