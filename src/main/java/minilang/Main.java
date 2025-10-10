package minilang;

import java.nio.file.*;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            System.out.println("Uso: java Main <archivo.min>");
            return;
        }

        String path = args[0];
        String input = Files.readString(Path.of(path));

        Lexer lexer = new Lexer(input);
        Token token;

        System.out.println("Analizando: " + path);
        while ((token = lexer.nextToken()).type != TokenType.EOF) {
            System.out.println(token);
        }
    }
}

