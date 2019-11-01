## Installation

Add the following to your `pom.xml` :

```xml
<dependency>
    <groupId>org.openapi4j</groupId>
    <artifactId>openapi4j-operation-servlet</artifactId>
    <version>VERSION</version>
</dependency>
```

## Usage

Requests and responses are wrapped with the specific adapter.
```java
// Servlet
Request request = ServletRequest.of(HttpServletRequest hsr);
// validate...
```

| Library         | Version     | Client | Server                  | Dependency                   |
|-----------------|-------------|--------|-------------------------|------------------------------|
| Servlet         | `>= 2.0`    | No     | ServletRequest          | The spec dependency you use  |

## License

openapi4j and all the modules are released under the Apache 2.0 license. See [LICENSE](https://github.com/openapi4j/openapi4j/blob/master/LICENSE.md) for details.
