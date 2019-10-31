# OpenAPI for java core project home

This is the home page of the openapi4j core project for Java (or JVM platform in general).

core module includes the following features :
* [JSON reference](https://tools.ietf.org/html/draft-pbryan-zyp-json-ref-03) implementation.
* Definitions of OpenAPI versions and contexts.
* Various utility classes.

JSON reference implementation always throws a ResolutionException if :
* The JSON pointer leads to a dead end.
* The reference and its subsequents references are cycling.

## Supported versions

OpenAPI Specification (OAS) version 3.0.2.  
See related projects for limitations and issues.

## License

openapi4j and all the modules are released under the Apache 2.0 license. See [LICENSE](https://github.com/openapi4j/openapi4j/blob/master/LICENSE.md) for details.
