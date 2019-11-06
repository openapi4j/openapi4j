import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.json.JSONException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openapi4j.core.exception.ResolutionException;
import org.openapi4j.operation.validator.adapters.server.vertx.v3.OpenApi3RouterFactory;
import org.openapi4j.operation.validator.model.impl.RequestParameters;
import org.skyscreamer.jsonassert.JSONCompare;
import org.skyscreamer.jsonassert.JSONCompareMode;

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
  public void mountSimpleHandlerTest(TestContext context) throws Exception {
    loadDefaultSpec(context);

    routerFactory.addOperationHandler("simple", rc -> {
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
    testRequest(context, HttpMethod.GET, "/simple", 200, "OK");
  }

  @Test
  public void mountRegexHandlerTest(TestContext context) throws Exception {
    loadDefaultSpec(context);

    routerFactory.addOperationHandler("regex", rc -> {
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

    Router router = routerFactory.getRouter();

    startServer(context, vertx, router);
    testRequest(context, HttpMethod.GET, "/fixed/foo/fixed/bar/fields/", 200, "OK");
  }

  @Test
  public void mountLabelMatrixHandlerTest(TestContext context) throws Exception {
    loadDefaultSpec(context);

    ObjectNode labelObject = JsonNodeFactory.instance.objectNode();
    labelObject.put("name", "Alex");
    labelObject.put("role", 123);

    ArrayNode matrixArray = JsonNodeFactory.instance.arrayNode();
    matrixArray.add("4").add("5").add("3");

    routerFactory.addOperationHandler("label-matrix", rc -> {
      RequestParameters rqParameters = rc.get("rqParameters");
      context.assertNotNull(rqParameters);

      try {
        JSONCompare.compareJSON(labelObject.toString(), rqParameters.getPathParameter("label").toString(), JSONCompareMode.STRICT);
        JSONCompare.compareJSON(matrixArray.toString(), rqParameters.getPathParameter("matrix").toString(), JSONCompareMode.STRICT);
      } catch (JSONException e) {
        context.fail(e);
      }

      rc
        .response()
        .setStatusCode(200)
        .setStatusMessage("OK")
        .end();
    });

    Router router = routerFactory.getRouter();

    startServer(context, vertx, router);
    testRequest(context, HttpMethod.GET, "/.role,123,name,Alex/;matrix=3,4,5/foo/", 200, "OK");
  }

  private void loadDefaultSpec(TestContext context) {
    URL specPath = OpenApi3RouterFactoryTest.class.getResource("/api.yaml");

    Async async = context.async();
    OpenApi3RouterFactory.create(vertx, specPath, result -> {
      routerFactory = result.result();
      async.complete();
    });

    async.await();
  }
}
