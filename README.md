[![Build Status](https://travis-ci.com/openapi4j/openapi4j.svg?branch=master)](https://travis-ci.com/openapi4j/openapi4j)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=org.openapi4j%3Aopenapi4j&metric=sqale_rating)](https://sonarcloud.io/dashboard?id=org.openapi4j%3Aopenapi4j)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=org.openapi4j%3Aopenapi4j&metric=security_rating)](https://sonarcloud.io/dashboard?id=org.openapi4j%3Aopenapi4j)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=org.openapi4j%3Aopenapi4j&metric=coverage)](https://sonarcloud.io/dashboard?id=org.openapi4j%3Aopenapi4j)

__This repository is now archived. I don't have enough spare time to maintain this project (well actually revamp) and follow OAI specs. This project deserves much more that I can give to source code and followers to provide appropriate output.__

# OpenAPI for java project home

This is the home page of the openapi4j project for Java (Jakarta or JVM platform in general).

openapi4j is a suite of tools, including the following :
* [Open API specification](https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.3.md) parser and validator.
* Open API [Schema Object](https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.3.md#schemaObject) validator.
* [JSON reference](https://tools.ietf.org/html/draft-pbryan-zyp-json-ref-03) implementation.
* Request/response validator against operation.
* For internal use only, performance project reports some numbers to 'manually' check any improvements or regressions between versions.

## Modules

* [Parser](openapi-parser) allows the (de-)serialization and manipulation of the schema and its validation.
* [Schema validator](openapi-schema-validator) allows the validation of data against a given schema.
* [Request validator](openapi-operation-validator) is high level module to manage validation for requests and/or responses against operations. More details in the related project.
* [Request adapters](openapi-operation-adapters) is the repository of specific adapters to wrap requests and responses.

## Documentation

The documentation for all modules is available [here](https://openapi4j.github.io/openapi4j/).

## Versioning and compatibility

All modules follow the [Semantic Versioning 2.0.0](https://semver.org) and are aligned on each release even there's no changes.

```xml
<dependency>
    <groupId>org.openapi4j</groupId>
    <artifactId>openapi-[module]</artifactId>
</dependency>
```

[![Release version](https://img.shields.io/nexus/r/org.openapi4j/openapi-operation-validator?style=for-the-badge&color=blue&label=Release&server=https%3A%2F%2Foss.sonatype.org)](https://search.maven.org/search?q=g:org.openapi4j)
[![Snapshot version](https://img.shields.io/nexus/s/org.openapi4j/openapi-operation-validator?style=for-the-badge&color=blue&label=Snapshot&server=https%3A%2F%2Foss.sonatype.org)](https://oss.sonatype.org/content/repositories/snapshots/org/openapi4j/)

Snapshot is available for latest valid commit on 'master' branch.

## Performance

Check [here](https://www.openapi4j.org/perf-values.html) for some values.

## Native compilation (GraalVM)

From version 0.7, the toolset is fully compliant with native compilation (AOT).
This was tested with GraalVM 19.3.1.
No further configuration or directive is needed to include the modules if available on classpath.

```shell script
native-image -H:+ReportExceptionStackTraces --no-fallback -jar your-app.jar
```

FYI, testing runs made don't show much performance improvements but parser module.

## Supported versions

The modules currently support the OpenAPI Specification (OAS) version 3.0.x.

OAI 3.1.0 has been released as candidate.  
There's too much changes too keep code on same basis and keep a fairly low level of complexity.  
As a consequence, OAI 3.1.x support will be made in a version 2 of openapi4j.  

As my time is very limited, version 1 should be considered as freezed for now.

See related projects for limitations and issues.

## Contributing

Reporting issues, making comments, ... Any help is welcome !

We accept Pull Requests via GitHub. There are some guidelines which will make applying PRs easier for us :

* Respect the code style and indentation. .editorconfig file is provided to not be worried about this.
* Create minimal diffs - disable on save actions like reformat source code or organize imports. If you feel the source code should be reformatted create a separate PR for this change.
* Provide JUnit tests for your changes and make sure your changes don't break anything by running `gradlew clean check`.
* Provide a self explanatory but brief commit message with issue reference if any, as it will be reported directly for release changelog.

## License

openapi4j and all the modules are released under the Apache 2.0 license. See [LICENSE](https://github.com/openapi4j/openapi4j/blob/master/LICENSE.md) for details.
