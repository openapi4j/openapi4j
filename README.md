[![Build Status](https://travis-ci.org/openapi4j/openapi4j.svg?branch=master)](https://travis-ci.org/openapi4j/openapi4j)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=org.openapi4j%3Aopenapi4j&metric=sqale_rating)](https://sonarcloud.io/dashboard?id=org.openapi4j%3Aopenapi4j)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=org.openapi4j%3Aopenapi4j&metric=security_rating)](https://sonarcloud.io/dashboard?id=org.openapi4j%3Aopenapi4j)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=org.openapi4j%3Aopenapi4j&metric=coverage)](https://sonarcloud.io/dashboard?id=org.openapi4j%3Aopenapi4j)

# OpenAPI for java project home

This is the home page of the openapi4j project for Java (Jakarta or JVM platform in general).

openapi4j is a suite of tools, including the following :
* [Open API specification](https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.2.md) parser and validator.
* Open API [Schema Object](https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.2.md#schemaObject) validator.
* [JSON reference](https://tools.ietf.org/html/draft-pbryan-zyp-json-ref-03) implementation.
* Request/response validator against operation.
* For internal use only, performance project reports some numbers to 'manually' check any improvements or regressions between versions.

## Modules

* [Parser](https://github.com/openapi4j/openapi4j/tree/master/openapi-parser) allows the (de-)serialization and manipulation of the schema and its validation.
* [Schema validator](https://github.com/openapi4j/openapi4j/tree/master/openapi-schema-validator) allows the validation of data against a given schema.
* [Request validator](https://github.com/openapi4j/openapi4j/tree/master/openapi-operation-validator) is high level module to manage validation for requests and/or responses against operations. More details in the related project.
* [Request adapters](https://github.com/openapi4j/openapi4j/tree/master/openapi-operation-adapters) is the repository of specific adapters to wrap requests and responses.

## Versioning and compatibility

All modules follow the [Semantic Versioning 2.0.0](https://semver.org) and are aligned on each release even there's no changes.

```xml
<dependency>
    <groupId>org.openapi4j</groupId>
    <artifactId>openapi4j-...</artifactId>
    <!-- Current release -->
    <version>0.3</version>
    <!-- Next. Available on each master commit. -->
    <version>0.4-SNAPSHOT</version>
</dependency>
```

## Performance

Check [perf-checker](https://github.com/openapi4j/openapi4j/tree/master/openapi-perf-checker) project for some values.

## Supported versions

The modules currently support the OpenAPI Specification (OAS) version 3.x.x.  

This project has been developed while reading 3.0.2.  
See related projects for limitations and issues.

## Roadmap

- 0.1 :
    - First beta release.  
- 0.2 : 
    - Rework on some naive code parts that could lead to NPE mostly.  
    -  Complete the multipart/mixed implementation.  
- 0.3 :
    - Complete response validation implementation.
- 0.4 :
    - Validate parameter from content property.  
- Next :
    - Ready for 1.0 candidate ?  

Common until version 1.0 :
* bug fixing / code coverage.
* Quality of life accessors/features elected as easy and not risky.

## Contributing

Reporting issues, making comments, ... Any help is welcome !

We accept Pull Requests via GitHub. There are some guidelines which will make applying PRs easier for us :

* Respect the code style and indentation. .editorconfig file is provided to not be worried about this.
* Create minimal diffs - disable on save actions like reformat source code or organize imports. If you feel the source code should be reformatted create a separate PR for this change.
* Provide JUnit tests for your changes and make sure your changes don't break anything by running `gradlew clean check`.
* Provide a self explanatory but brief commit message with issue reference if any, as it will be reported directly for release changelog.

## License

openapi4j and all the modules are released under the Apache 2.0 license. See [LICENSE](https://github.com/openapi4j/openapi4j/blob/master/LICENSE.md) for details.
