# OpenAPI for java performance check project home

This is the home page of the openapi4j performance check project for Java (or JVM platform in general).

This project is available for **internal test use only** to check any pitfall before releasing.  

Values are globally slightly inaccurate but all tests are made in the same environment/block, so we're still comparing on the same basis.
Other libraries are included to keep the ratio and mitigate the fact that I may change computer in the future.

## Parser reports

Notes :
* Swagger validates at parsing time, openapi4j as a second operation.
* All parsers have validation option enabled. Still, Swagger validation seems incomplete.

| Library           | Version       | Time          | Iterations    | % time  |
|-------------------|---------------|---------------|---------------|---------|
| Swagger           | 2.0.15        | 460,31 ms     | 1             | 100,0   |
| OpenApi4j         | 0.1           | 227,84 ms     | 1             | 49,5    |
| Swagger           | 2.0.15        | 198,42 ms     | 10            | excl.   |
| OpenApi4j         | 0.1           | 215,42 ms     | 10            | excl.   |

| Library           | Version       | Time          | Iterations    | % time  |
|-------------------|---------------|---------------|---------------|---------|
| Swagger           | 2.0.15        | 413,37 ms     | 1             | 100,0   |
| OpenApi4j         | 0.2           | 209,23 ms     | 1             | 50,6    |
| Swagger           | 2.0.15        | 240,47 ms     | 10            | excl.   |
| OpenApi4j         | 0.2           | 256,44 ms     | 10            | excl.   |

## Schema reports

Notes :  
* JsonTools has only 100 iterations.
* Networknt values are not so consistent. Ratio is between 40-65%


| Library           | Version       | Time          | Iterations    | % time  |
|-------------------|---------------|---------------|---------------|---------|
| Networknt         | 1.0.26        | 361,14 ms     | 1000          | 100,0   |
| OpenApi4j         | 0.1           | 198,46 ms     | 1000          | 55,0    |
| JsonTools         | 2.2.11        | 755,55 ms     | 100           | excl.   |

| Library           | Version       | Time          | Iterations    | % time  |
|-------------------|---------------|---------------|---------------|---------|
| Networknt         | 1.0.26        | 431,79 ms     | 1000          | 100,0   |
| OpenApi4j         | 0.2           | 204,24 ms     | 1000          | 47,3    |
| Justify           | 1.1.0         | 1124,20 ms    | 1000          | excl.   |
| JsonTools         | 2.2.11        | 879,43 ms     | 100           | excl.   |

## Operation reports
* First iteration to explicitly see operation validators warm up.
* Iteration includes request wrapping but not operation lookup.

| Library           | Version       | Time          | Iterations    | % time  |
|-------------------|---------------|---------------|---------------|---------|
| OpenApi4j         | 0.1           | 10,47 ms      | 1             | 100,0   |
| OpenApi4j         | 0.1           | 159,80 ms     | 10000         | excl.   |

## Usage

To check in your environment, clone this repository, build and launch :
```
git clone https://github.com/openapi4j/openapi4j.git
gradle installDist
build/install/openapi-perf-checker/bin/openapi-perf-checker (.bat)
```

## License

[See main page](https://github.com/openapi4j/openapi4j#license)
