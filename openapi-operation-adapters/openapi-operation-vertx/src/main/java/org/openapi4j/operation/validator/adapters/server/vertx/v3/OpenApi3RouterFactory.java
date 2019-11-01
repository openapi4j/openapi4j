package org.openapi4j.operation.validator.adapters.server.vertx.v3;

import org.openapi4j.core.exception.ResolutionException;
import org.openapi4j.core.validation.ValidationException;
import org.openapi4j.operation.validator.adapters.server.vertx.v3.impl.OpenApi3RouterFactoryImpl;
import org.openapi4j.parser.OpenApi3Parser;
import org.openapi4j.parser.model.v3.OpenApi3;

import java.net.URL;

import io.vertx.codegen.annotations.Fluent;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

public interface OpenApi3RouterFactory {
  /**
   * Create a new router factory.
   *
   * @param vertx The current Vert.x instance.
   * @param url   location of your spec. It can be an absolute path, a local path or remote url (with HTTP protocol)
   * @return future When specification is loaded, this future will be called with AsyncResult<OpenApi3RouterFactory>
   */
  static Future<OpenApi3RouterFactory> create(Vertx vertx, URL url) {
    Promise<OpenApi3RouterFactory> promise = Promise.promise();
    create(vertx, url, promise);
    return promise.future();
  }

  /**
   * Create a new router factory.
   *
   * @param vertx   The current Vert.x instance.
   * @param url     location of your spec. It can be an absolute path, a local path or remote url (with HTTP protocol)
   * @param handler When specification is loaded, this handler will be called with AsyncResult<OpenApi3RouterFactory>
   */
  static void create(Vertx vertx,
                     URL url,
                     Handler<AsyncResult<OpenApi3RouterFactory>> handler) {

    vertx.executeBlocking((Promise<OpenApi3RouterFactory> future) -> {
      try {
        OpenApi3 openApi = new OpenApi3Parser().parse(url, true);
        future.complete(new OpenApi3RouterFactoryImpl(vertx, openApi));
      } catch (ResolutionException | ValidationException e) {
        future.fail(e);
      }
    }, handler);
  }

  /**
   * Adds a security handler with the given schema name.
   *
   * @param securityRequirementName The given requirement name to mount.
   * @param handler                 The corresponding handler to be called.
   * @return this
   */
  @Fluent
  OpenApi3RouterFactory addSecurityHandler(String securityRequirementName, Handler<RoutingContext> handler);

  /**
   * Adds a security handler with the given requirement name.
   *
   * @param securityRequirementName The given requirement name to mount.
   * @param scopeName               The given OAuth2 scope to mount.
   * @param handler                 The corresponding handler to be called.
   * @return this
   */
  @Fluent
  OpenApi3RouterFactory addSecurityScopedHandler(String securityRequirementName, String scopeName, Handler<RoutingContext> handler);

  /**
   * Add operation handler from operationId field in Operation object.
   *
   * @param operationId The ID to identify the operation.
   * @param handler     The handler associated to process the request.
   * @return this
   */
  @Fluent
  OpenApi3RouterFactory addOperationHandler(String operationId, Handler<RoutingContext> handler) throws ResolutionException;

  /**
   * Add operation handler from operationId field in Operation object.
   *
   * @param operationId The ID to identify the operation.
   * @param bodyHandler The dedicated body handler to this operation. Otherwise the global handler will be used.
   * @param handler     The handler associated to process the request.
   * @return this
   */
  @Fluent
  OpenApi3RouterFactory addOperationHandler(String operationId, BodyHandler bodyHandler, Handler<RoutingContext> handler) throws ResolutionException;

  /**
   * Construct a new router based on spec. It will fail if you are trying to mount a spec with security schemes
   * without assigned handlers<br/>
   *
   * @return The built router.
   * @throws ResolutionException
   */
  Router getRouter() throws ResolutionException;
}
