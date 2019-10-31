# OpenAPI for java parser project home

This is the home page of the openapi4j parser project for Java (or JVM platform in general).

parser module includes the following features :
* [Open API specification](https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.2.md) parser and validator.

The validation is made internally by specific classes. Schema validator project could be used but some parts can't be defined via JSON Schema.  
That said, you can, if you want to, do the validation from it. Check at the integration tests to get an example.

## Installation

Add the following to your `pom.xml`:

```xml
<dependency>
    <groupId>org.openapi4j</groupId>
    <artifactId>openapi4j-parser</artifactId>
    <version>VERSION</version>
</dependency>
```

## Usage

```java
// Parse without validation, setting to true is recommended in most cases.
OpenApi3 api = new OpenApi3Parser().parse(specPath, false);
// Explicit validation of the API spec
ValidationResults results = OpenApi3Validator.instance().validate(api);
```

## TODO

* Enable loading documents with security requirements.
* More tests.

## Limitations

* Schema Object : default value is only checked by its type.
* Reference : content is setup when built.

## Supported versions

OpenAPI Specification (OAS) version 3.0.2.  
See related projects for limitations and issues.

## License

openapi4j and all the modules are released under the Apache 2.0 license. See [LICENSE](#) for details.
