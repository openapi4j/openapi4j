## Installation

Add the following to your `pom.xml` :

[See main page for version](https://github.com/openapi4j/openapi4j)
```xml
<dependency>
    <groupId>org.openapi4j</groupId>
    <artifactId>openapi4j-operation-undertow</artifactId>
</dependency>
```

## Usage

Requests and responses are wrapped with the specific adapter.
```java
// Undertow
Request request = UndertowRequest.of(HttpServerExchange hse);
// validate...
```

| Library         | Version     | Client | Server                  | Dependency                   |
|-----------------|-------------|--------|-------------------------|------------------------------|
| Undertow        | `> 2.0`     | No     | UndertowRequest         | io.undertow:undertow-core    |

## License

[See main page](https://github.com/openapi4j/openapi4j#license)
