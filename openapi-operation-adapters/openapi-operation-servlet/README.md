## Installation

Add the following to your `pom.xml` :

[See main page for version](https://github.com/openapi4j/openapi4j)
```xml
<dependency>
    <groupId>org.openapi4j</groupId>
    <artifactId>openapi4j-operation-servlet</artifactId>
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

[See main page](https://github.com/openapi4j/openapi4j#license)
