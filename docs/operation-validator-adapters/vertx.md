---
layout: default
title: Vert.x
parent: Operation validator adapters
---

# Vert.x
{:.no_toc}

## Table of contents
{: .no_toc .text-delta }

1. TOC
{:toc}

---

## Installation

Add the following to your `pom.xml` :

```xml
<dependency>
    <groupId>org.openapi4j</groupId>
    <artifactId>openapi-operation-vertx</artifactId>
</dependency>
```
[![Release version](https://img.shields.io/nexus/r/org.openapi4j/openapi-schema-validator?style=for-the-badge&color=blue&label=Release&server=https%3A%2F%2Foss.sonatype.org)](https://search.maven.org/search?q=g:org.openapi4j%20a:openapi-operation-vertx)
[![Snapshot version](https://img.shields.io/nexus/s/org.openapi4j/openapi-schema-validator?style=for-the-badge&color=blue&label=Snapshot&server=https%3A%2F%2Foss.sonatype.org)](https://oss.sonatype.org/content/repositories/snapshots/org/openapi4j/openapi-operation-vertx/)

## Usage

Requests and responses are wrapped with the specific adapter.
```java
// Vert.x
Request request = VertxRequest.of(RoutingContext rc);
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

Getting the request parameters :
```java
routerFactory.addOperationHandler("my_op", commonBodyHandler, routingContext -> {
  RequestParameters rqParameters = rc.get("rqParameters"); // Access the mapped parameters.
  // Mapped as JsonNode to get direct structured value primitive, array or object.
  JsonNode ... = rqParameters.getPathParameter("your_spec_param");
  JsonNode ... = rqParameters.getQueryParameter("your_spec_param");
  JsonNode ... = rqParameters.getHeaderParameter("your_spec_param");
  JsonNode ... = rqParameters.getCookieParameter("your_spec_param");
});
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

[See main page](../index.md#license)
