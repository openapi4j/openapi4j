[![Build Status](https://travis-ci.org/openapi4j/openapi4j.svg?branch=master)](https://travis-ci.org/openapi4j/openapi4j)

# OpenAPI for java project home

This is the home page of the openapi4j project for Java (or JVM platform in general).

openapi4j is a suite of tools for Java (and the JVM platform), including the following :
* [Open API specification](https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.2.md) parser and validator.
* Open API [Schema Object](https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.2.md#schemaObject) validator.
* [JSON reference](https://tools.ietf.org/html/draft-pbryan-zyp-json-ref-03) implementation.
* Operation validator for requests and/or responses. 
* For internal use only, performance project reports some numbers to 'manually' check any improvements or regressions between versions.

## Modules

* [OpenAPI parser](https://github.com/openapi4j/openapi4j/tree/master/openapi-parser) allows the (de-)serialization and manipulation of the schema and its validation.
* [Schema Object validator](https://github.com/openapi4j/openapi4j/tree/master/openapi-schema-validator) allows the validation of data against a given schema.
* [Operation Validator](https://github.com/openapi4j/openapi4j/tree/master/openapi-operation-validator) is high level module to manage validation for requests and/or responses. More details in the related project.
* [Operation Validator Adapters](https://github.com/openapi4j/openapi4j/tree/master/openapi-operation-adapters) is the repository of specific adapters to wrap requests and and responses.

## Versioning and compatibility

All modules follow the [Semantic Versioning 2.0.0](https://semver.org) and are aligned on each release even there's no changes.

## Performance

Check [perf-checker](https://github.com/openapi4j/openapi4j/tree/master/openapi-perf-checker) project for some values.

## Supported versions

The modules currently support the OpenAPI Specification (OAS) version 3.0.2.   
See related projects for limitations and issues.

## Roadmap

Until version 1.0 :
* Squashing bugs
* Missing validations
* Quality of life accessors/features elected as easy and not risky.

## Contributing

Reporting issues, making comments, ... Any help is welcome !

We accept Pull Requests via GitHub. There are some guidelines which will make applying PRs easier for us :

* Respect the code style and indentation. .editorconfig file is provided to not be worried about this.
* Create minimal diffs - disable on save actions like reformat source code or organize imports. If you feel the source code should be reformatted create a separate PR for this change.
* Provide JUnit tests for your changes and make sure your changes don't break anything by running `gradlew clean testClasses`.
* Provide a self explainatory but brief commit message with issue reference if any, as it will be reported directly for release changelog.

## License

openapi4j and all the modules are released under the Apache 2.0 license. See [LICENSE](https://github.com/openapi4j/openapi4j/blob/master/LICENSE.md) for details.
