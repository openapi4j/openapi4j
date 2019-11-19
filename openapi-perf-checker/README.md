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
| Swagger           | 2.0.16        | 421,53 ms     | 1             | 1       |
| OpenApi4j         | 0.4-SNAPSHOT  | 207,33 ms     | 1             | 2,03    |
| Swagger           | 2.0.16        | 221,60 ms     | 10            | excl.   |
| OpenApi4j         | 0.4-SNAPSHOT  | 298,82 ms     | 10            | excl.   |

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
| Networknt         | 1.0.26        | 444,07 ms     | 1000          | 2,52    |
| OpenApi4j         | 0.4-SNAPSHOT  | 197,63 ms     | 1000          | 5,67    |
| Justify           | 1.1.0         | 1120,69 ms    | 1000          | 1       |
| JsonTools         | 2.2.11        | 900,81 ms     | 100           | excl.   |

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
| application/json  | 0.4-SNAPSHOT  | 210,73 ms     | 10000         | excl.   |
| form-urlencoded   | 0.4-SNAPSHOT  | 171,64 ms     | 10000         | excl.   |
| form-data         | 0.4-SNAPSHOT  | 864,93 ms     | 10000         | excl.   |
| multipart/mixed   | 0.4-SNAPSHOT  | 638,30 ms     | 10000         | excl.   |
| application/xml   | 0.4-SNAPSHOT  | 624,86 ms     | 10000         | excl.   |

## Usage

To check in your environment, clone this repository, build and launch :
```
git clone https://github.com/openapi4j/openapi4j.git
gradle installDist
build/install/openapi-perf-checker/bin/openapi-perf-checker (.bat)
```

## License

[See main page](https://github.com/openapi4j/openapi4j#license)
