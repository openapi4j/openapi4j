import org.junit.After;
import org.junit.Before;

import java.util.function.Consumer;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.HttpClientRequest;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.web.Router;

import static org.junit.Assert.fail;

class VertxTestBase {
  Vertx vertx;
  HttpClient client;

  @Before
  public void before() {
    vertx = Vertx.vertx();
    client = vertx.createHttpClient(new HttpClientOptions().setDefaultPort(8080));
  }

  @After
  public void after() {
    if (client != null) {
      client.close();
    }

    if (vertx != null) {
      vertx.close();
    }
  }

  protected void startServer(TestContext context, Vertx vertx, Router router) {
    HttpServer server = vertx.createHttpServer(new HttpServerOptions().setPort(8080).setHost("localhost"));
    Async async = context.async();
    server.requestHandler(router).listen(onSuccess(res -> async.complete()));
    async.await(5_000);
  }

  protected void testRequest(TestContext context, HttpMethod method, String path, int statusCode) {
    testRequest(context, client, method, 8080, path, statusCode, null, null, null);
  }

  protected void testRequest(TestContext context, HttpMethod method, String path, int statusCode, String contentType, Buffer requestBodyBuffer) {
    testRequest(context, client, method, 8080, path, statusCode, contentType, requestBodyBuffer, null);
  }

  protected void testRequest(TestContext context,
                             HttpClient client,
                             HttpMethod method,
                             int port,
                             String path,
                             int statusCode,
                             String contentType,
                             Buffer requestBodyBuffer,
                             Buffer responseBodyBuffer) {

    Async async = context.async();
    HttpClientRequest req = client.request(method, port, "localhost", path, resp -> {
      context.assertEquals(statusCode, resp.statusCode());

      if (responseBodyBuffer == null) {
        async.complete();
      } else {
        resp.bodyHandler(buff -> {
          context.assertEquals(responseBodyBuffer, buff);
          async.complete();
        });
      }
    });

    if (requestBodyBuffer != null) {
      req.putHeader("Content-Type", contentType);
      req.end(requestBodyBuffer);
    } else {
      req.end();
    }

    async.await(5_000);
  }

  protected <T> Handler<AsyncResult<T>> onSuccess(Consumer<T> consumer) {
    return result -> {
      if (result.failed()) {
        result.cause().printStackTrace();
        fail(result.cause().getMessage());
      } else {
        consumer.accept(result.result());
      }
    };
  }
}
