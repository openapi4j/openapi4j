# OpenAPI for java parser project home

This is the home page of the openapi4j parser project for Java (or JVM platform in general).

parser module includes the following features :
* [Open API specification](https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.2.md) parser and validator.
* Manipulate the models and serialize back the modified API description.

The validation is made internally by specific classes. Schema validator project could be used but many parts can't be checked via SchemaObject.  
That said, you can, if you want to, do the validation from it. Check at the integration tests to get an example.

## Installation

Add the following to your `pom.xml`:

```xml
<dependency>
    <groupId>org.openapi4j</groupId>
    <artifactId>openapi-parser</artifactId>
</dependency>
```
[![Release version](https://img.shields.io/nexus/r/org.openapi4j/openapi-schema-validator?style=for-the-badge&color=blue&label=Release&server=https%3A%2F%2Foss.sonatype.org)](https://search.maven.org/search?q=g:org.openapi4j%20a:openapi-parser)
[![Snapshot version](https://img.shields.io/nexus/s/org.openapi4j/openapi-schema-validator?style=for-the-badge&color=blue&label=Snapshot&server=https%3A%2F%2Foss.sonatype.org)](https://oss.sonatype.org/content/repositories/snapshots/org/openapi4j/openapi-parser/)

## Usage

```java
// Parse without validation, setting to true is strongly recommended for further data validation.
OpenApi3 api = new OpenApi3Parser().parse(specPath, false);
// Explicit validation of the API spec
ValidationResults results = OpenApi3Validator.instance().validate(api);
```

If your document has restricted access, you're able to load it with authentication values :
```java
// You must provide all the values for the expected chain of documents.
List<AuthOption> authOptions = new ArrayList<>();
// A value in query for requests
authOptions.add(new AuthOption(QUERY, "sessionId", "xyz"));
// A value in header for localhost only
authOptions.add(new AuthOption(HEADER, "api_key", "xyz", url -> url.getHost().equals("localhost")));
...

OpenApi3 api = new OpenApi3Parser().parse(specPath, authOptions, true);
```

## Limitations

* Reference : content is setup when built.

## Supported versions

[See main page](https://github.com/openapi4j/openapi4j#supported-versions)

## License

[See main page](https://github.com/openapi4j/openapi4j#license)
