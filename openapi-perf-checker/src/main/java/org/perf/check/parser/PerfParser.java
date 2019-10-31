package org.perf.check.parser;

interface PerfParser {
  String load(String schemaFile);

  String getVersion();
}
