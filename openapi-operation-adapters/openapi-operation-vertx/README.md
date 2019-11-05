## Installation

Add the following to your `pom.xml` :

[See main page for version](https://github.com/openapi4j/openapi4j)
```xml
<dependency>
    <groupId>org.openapi4j</groupId>
    <artifactId>openapi4j-operation-vertx</artifactId>
</dependency>
```

## Usage

Requests and responses are wrapped with the specific adapter.
```java
// Vert.x
Request request = VertxRequest.of(HttpServerExchange hse);
// validate...
```

| Library         | Version     | Client | Server                  | Dependency                   |
|-----------------|-------------|--------|-------------------------|------------------------------|
| Vert.x          | `>= 3.?`    | No     | VertxRequest            | io.vertx:vertx-web           |

## Router factory

Like Vert.x vertx-web-api-contract router factory, this adapter comes with its equivalent adapted to this toolset.

Load the specification :
```java
OpenApi3RouterFactory.create(vertx, "src/main/resources/api.yaml", ar -> {
  if (ar.succeeded()) {
    OpenApi3RouterFactory routerFactory = ar.result();
  } else {
    // Something went wrong...
  }
});
```

Mounting the handlers :
```java
BodyHandler commonBodyHandler = BodyHandler.create();
// This body handler won't be used as this operation uses GET HTTP method.
routerFactory.addOperationHandler("operationIdGet", commonBodyHandler, routingContext -> {
  // Do something with the validated request
});
...
// The body handler can be dedicated
BodyHandler restrictedBodyHandler = BodyHandler.create().setBodyLimit(150);
routerFactory.addOperationHandler("operationIdPost", restrictedBodyHandler, foo::processPost);
```

Mounting the security handlers :  

You must conform to your specification when an operation declares security requirements.  
Otherwise, the router generation (#.getRouter()) will fail by throwing a ResolutionException.
```java
// security handlers can be custom or official handlers from the Vert.x stack
routerFactory.addSecurityHandler("security_scheme_name", securityHandler);
// OAuth2 scope
routerFactory.addSecurityScopedHandler("security_scheme_name", "read:document" securityHandler);
```

Generate the router :
```java
Router router = routerFactory.getRouter();
HttpServer server = vertx.createHttpServer(new HttpServerOptions().setPort(8080).setHost("localhost"));
server.requestHandler(router).listen();
```

## License

[See main page](https://github.com/openapi4j/openapi4j#license)
