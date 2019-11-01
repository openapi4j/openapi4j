## Installation

Add the following to your `pom.xml` :

```xml
<dependency>
    <groupId>org.openapi4j</groupId>
    <artifactId>openapi4j-operation-undertow</artifactId>
    <version>VERSION</version>
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

openapi4j and all the modules are released under the Apache 2.0 license. See [LICENSE](https://github.com/openapi4j/openapi4j/blob/master/LICENSE.md) for details.
