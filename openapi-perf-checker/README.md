# OpenAPI for java performance check project home

This is the home page of the openapi4j performance check project for Java (or JVM platform in general).

This project is available for **internal test use only** to check any pitfall before releasing.

Values are globally slightly inaccurate but all tests are made in the same environment/block, so we're still comparing on the same basis.
Other libraries are included to keep the ratio and mitigate the fact that I may change computer in the future.

## Parser reports

Notes :
* Swagger validates at parsing time, openapi4j as a second operation.
* All parsers have validation option enabled. Still, Swagger validation seems incomplete.

| Library           | Version       | Time          | Iterations    | Gain    |
|-------------------|---------------|---------------|---------------|---------|
| Swagger           | 2.0.15        | 460,31 ms     | 1             | 1       |
| OpenApi4j         | 0.1           | 227,84 ms     | 1             | 2.02    |
| Swagger           | 2.0.15        | 198,42 ms     | 10            | excl.   |
| OpenApi4j         | 0.1           | 215,42 ms     | 10            | excl.   |

| Library           | Version       | Time          | Iterations    | Gain    |
|-------------------|---------------|---------------|---------------|---------|
| Swagger           | 2.0.15        | 413,37 ms     | 1             | 1       |
| OpenApi4j         | 0.2           | 209,23 ms     | 1             | 1.98    |
| Swagger           | 2.0.15        | 240,47 ms     | 10            | excl.   |
| OpenApi4j         | 0.2           | 256,44 ms     | 10            | excl.   |

| Library           | Version       | Time          | Iterations    | Gain    |
|-------------------|---------------|---------------|---------------|---------|
| Swagger           | 2.0.16        | 435,82 ms     | 1             | 1       |
| OpenApi4j         | 0.3           | 204,29 ms     | 1             | 2,13    |
| Swagger           | 2.0.16        | 269,92 ms     | 10            | excl.   |
| OpenApi4j         | 0.3           | 333,24 ms     | 10            | excl.   |

| Library           | Version       | Time          | Iterations    | Gain    |
|-------------------|---------------|---------------|---------------|---------|
| Swagger           | 2.0.16        | 416,97 ms     | 1             | 1       |
| OpenApi4j         | 0.4           | 211,93 ms     | 1             | 1,97    |
| Swagger           | 2.0.16        | 222,47 ms     | 10            | excl.   |
| OpenApi4j         | 0.4           | 250,54 ms     | 10            | excl.   |

| Library           | Version       | Time          | Iterations    | Gain    |
|-------------------|---------------|---------------|---------------|---------|
| Swagger           | 2.0.16        | 489,60 ms     | 1             | 1       |
| OpenApi4j         | 0.5           | 243,73 ms     | 1             | 2,01    |
| Swagger           | 2.0.16        | 283,25 ms     | 10            | excl.   |
| OpenApi4j         | 0.5           | 364,69 ms     | 10            | excl.   |

| Library           | Version       | Time          | Iterations    | Gain    |
|-------------------|---------------|---------------|---------------|---------|
| Swagger           | 2.0.18        | 502,30 ms     | 1             | 1       |
| OpenApi4j         | 0.7           | 248,57 ms     | 1             | 2,02    |
| Swagger           | 2.0.18        | 325,65 ms     | 10            | excl.   |
| OpenApi4j         | 0.7           | 461,44 ms     | 10            | excl.   |

| Library           | Version       | Time          | Iterations    | Gain    |
|-------------------|---------------|---------------|---------------|---------|
| Swagger           | 2.0.18        | 577,09 ms     | 1             | 1       |
| OpenApi4j         | 0.8           | 280,38 ms     | 1             | 2,06    |
| Swagger           | 2.0.18        | 352,14 ms     | 10            | excl.   |
| OpenApi4j         | 0.8           | 515,18 ms     | 10            | excl.   |

## Schema reports

Notes :
* JsonTools has only 100 iterations.
* Networknt values are not so consistent.


| Library           | Version       | Time          | Iterations    | Gain    |
|-------------------|---------------|---------------|---------------|---------|
| Networknt         | 1.0.26        | 361,14 ms     | 1000          | 1       |
| OpenApi4j         | 0.1           | 198,46 ms     | 1000          | 1.82    |
| JsonTools         | 2.2.11        | 755,55 ms     | 100           | excl.   |

| Library           | Version       | Time          | Iterations    | Gain    |
|-------------------|---------------|---------------|---------------|---------|
| Networknt         | 1.0.26        | 431,79 ms     | 1000          | 2.60    |
| OpenApi4j         | 0.2           | 204,24 ms     | 1000          | 5.50    |
| Justify           | 1.1.0         | 1124,20 ms    | 1000          | 1       |
| JsonTools         | 2.2.11        | 879,43 ms     | 100           | excl.   |

| Library           | Version       | Time          | Iterations    | Gain    |
|-------------------|---------------|---------------|---------------|---------|
| Networknt         | 1.0.26        | 445,28 ms     | 1000          | 2.44    |
| OpenApi4j         | 0.3           | 194,46 ms     | 1000          | 5.60    |
| Justify           | 1.1.0         | 1090,65 ms    | 1000          | 1       |
| JsonTools         | 2.2.11        | 912,47 ms     | 100           | excl.   |

| Library           | Version       | Time          | Iterations    | Gain    |
|-------------------|---------------|---------------|---------------|---------|
| Networknt         | 1.0.26        | 453,71 ms     | 1000          | 2,52    |
| OpenApi4j         | 0.4           | 219,83 ms     | 1000          | 5,19    |
| Justify           | 1.1.0         | 1141,52 ms    | 1000          | 1       |
| JsonTools         | 2.2.11        | 887,77 ms     | 100           | excl.   |

| Library           | Version       | Time          | Iterations    | Gain    |
|-------------------|---------------|---------------|---------------|---------|
| Networknt         | 1.0.26        | 452,04 ms     | 1000          | 2,53    |
| OpenApi4j         | 0.5           | 186,83 ms     | 1000          | 6,13    |
| Justify           | 1.1.0         | 1145,21 ms    | 1000          | 1       |
| JsonTools         | 2.2.11        | 960,87 ms     | 100           | excl.   |

| Library           | Version       | Time          | Iterations    | Gain    |
|-------------------|---------------|---------------|---------------|---------|
| Networknt         | 1.0.29        | 430,06 ms     | 1000          | 2,58    |
| OpenApi4j         | 0.7           | 214,22 ms     | 1000          | 5,19    |
| Justify           | 1.1.0         | 1111,41 ms    | 1000          | 1       |
| JsonTools         | 2.2.11        | 932,68 ms     | 100           | excl.   |

| Library           | Version       | Time          | Iterations    | Gain    |
|-------------------|---------------|---------------|---------------|---------|
| Networknt         | 1.0.29        | 566,10 ms     | 1000          | 2,82    |
| OpenApi4j         | 0.8           | 243,39 ms     | 1000          | 6,56    |
| Justify           | 1.1.0         | 1597,34 ms    | 1000          | 1       |
| JsonTools         | 2.2.11        | 1515,00 ms    | 100           | excl.   |

## Operation reports
* Replaying multiple times this process shows much shorter time values, but it's not the point here.
We stick with the first showed values for version comparison.

| Content type      | Version       | Time          | Iterations    | Gain    |
|-------------------|---------------|---------------|---------------|---------|
| application/json  | 0.2           | 283,81 ms     | 10000         | excl.   |
| form-urlencoded   | 0.2           | 200,88 ms     | 10000         | excl.   |
| form-data         | 0.2           | 1076,23 ms    | 10000         | excl.   |
| multipart/mixed   | 0.2           | 705,87 ms     | 10000         | excl.   |
| application/xml   | 0.2           | 421,05 ms     | 10000         | excl.   |

| Library           | Version       | Time          | Iterations    | Gain    |
|-------------------|---------------|---------------|---------------|---------|
| application/json  | 0.3           | 189,94 ms     | 10000         | excl.   |
| form-urlencoded   | 0.3           | 209,47 ms     | 10000         | excl.   |
| form-data         | 0.3           | 946,24 ms     | 10000         | excl.   |
| multipart/mixed   | 0.3           | 675,33 ms     | 10000         | excl.   |
| application/xml   | 0.3           | 483,93 ms     | 10000         | excl.   |

| Library           | Version       | Time          | Iterations    | Gain    |
|-------------------|---------------|---------------|---------------|---------|
| application/json  | 0.4           | 270,73 ms     | 10000         | excl.   |
| form-urlencoded   | 0.4           | 218,47 ms     | 10000         | excl.   |
| form-data         | 0.4           | 875,94 ms     | 10000         | excl.   |
| multipart/mixed   | 0.4           | 641,77 ms     | 10000         | excl.   |
| application/xml   | 0.4           | 482,71 ms     | 10000         | excl.   |

| Library           | Version       | Time          | Iterations    | Gain    |
|-------------------|---------------|---------------|---------------|---------|
| application/json  | 0.5           | 275,72 ms     | 10000         | excl.   |
| form-urlencoded   | 0.5           | 274,72 ms     | 10000         | excl.   |
| form-data         | 0.5           | 1024,62 ms    | 10000         | excl.   |
| multipart/mixed   | 0.5           | 729,33 ms     | 10000         | excl.   |
| application/xml   | 0.5           | 493,96 ms     | 10000         | excl.   |

| Library           | Version       | Time          | Iterations    | Gain    |
|-------------------|---------------|---------------|---------------|---------|
| application/json  | 0.7           | 221,24 ms     | 10000         | excl.   |
| form-urlencoded   | 0.7           | 350,98 ms     | 10000         | excl.   |
| form-data         | 0.7           | 1065,83 ms    | 10000         | excl.   |
| multipart/mixed   | 0.7           | 629,08 ms     | 10000         | excl.   |
| application/xml   | 0.7           | 636,69 ms     | 10000         | excl.   |

| Library           | Version       | Time          | Iterations    | Gain    |
|-------------------|---------------|---------------|---------------|---------|
| application/json  | 0.8           | 336,15 ms     | 10000         | excl.   |
| form-urlencoded   | 0.8           | 445,58 ms     | 10000         | excl.   |
| form-data         | 0.8           | 1409,93 ms    | 10000         | excl.   |
| multipart/mixed   | 0.8           | 1047,05 ms    | 10000         | excl.   |
| application/xml   | 0.8           | 909,57 ms     | 10000         | excl.   |

## Usage

To check in your environment, clone this repository, build and launch :
```
git clone https://github.com/openapi4j/openapi4j.git
gradle installDist
build/install/openapi-perf-checker/bin/openapi-perf-checker (.bat)
```

## License

[See main page](https://github.com/openapi4j/openapi4j#license)
