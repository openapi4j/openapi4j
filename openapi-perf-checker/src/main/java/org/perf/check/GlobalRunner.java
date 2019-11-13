package org.perf.check;

import org.perf.check.operation.OperationPerfRunner;
import org.perf.check.parser.ParserPerfRunner;
import org.perf.check.schema.SchemaPerfRunner;

import java.util.Scanner;

public class GlobalRunner {
  public static void main(String[] args) throws Exception {
    Scanner sc = new Scanner(System.in);

    while (true) {
      System.out.println("\nEnter performance runner index + ENTER :");
      System.out.println("1 - parser\n2 - schema\n3 - operation\nAny other key - exit");
      if (sc.hasNextInt()) {
        switch (sc.nextInt()) {
          case 1:
            ParserPerfRunner.main();
            break;
          case 2:
            SchemaPerfRunner.main();
            break;
          case 3:
            OperationPerfRunner.main();
            break;
          default:
            return;
        }
      } else {
        return;
      }
    }
  }
}
