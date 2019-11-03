package org.perf.check;

import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import org.openapi4j.core.exception.ResolutionException;
import org.openapi4j.core.validation.ValidationException;
import org.perf.check.operation.OperationPerfRunner;
import org.perf.check.parser.ParserPerfRunner;
import org.perf.check.schema.SchemaPerfRunner;

import java.io.IOException;
import java.util.Scanner;

public class GlobalRunner {
  public static void main(String[] args) throws IOException, ProcessingException, ValidationException, ResolutionException {
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
            System.exit(0);
        }
      } else {
        System.exit(0);
      }
    }
  }
}
