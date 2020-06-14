---
layout: default
title: Core
nav_order: 6
---

# Core
{:.no_toc}

## Table of contents
{: .no_toc .text-delta }

1. TOC
{:toc}

---

## Features

* [JSON reference](https://tools.ietf.org/html/draft-pbryan-zyp-json-ref-03) implementation.
* Definitions of OpenAPI versions and contexts.
* Various utility classes.

JSON reference implementation always throws a ResolutionException if :
* The JSON pointer leads to a dead end.
* The reference and its subsequents references are cycling.

## License

[See main page](index.md#license)
