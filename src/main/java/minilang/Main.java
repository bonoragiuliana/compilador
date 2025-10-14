package minilang;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Uso: java minilang.Main <ruta_del_archivo.min>");
            return;
        }

        String filePath = args[0];
        System.out.println(" Analizando archivo: " + filePath);

        try {
            // 1 Leer archivo fuente
            String source = Files.readString(Path.of(filePath));

            //2 Análisis léxico
            Lexer lexer = new Lexer(source);
            lexer.tokenize();
            List<Token> tokens = lexer.getTokens();

            System.out.println("\n=== TOKENS ===");
            for (Token token : tokens) {
                System.out.println(token);
            }

            // 3 Análisis sintáctico
            Parser parser = new Parser(tokens);
            List<Stmt> statements = parser.parse();

            // 4 Análisis semántico
            SemanticAnalyzer semantic = new SemanticAnalyzer();
            semantic.analyze(statements);

            // 5 Mostrar errores semánticos (si hay)
            System.out.println("\n=== ERRORES SEMÁNTICOS ===");
            if (semantic.getErrors().isEmpty()) {
                System.out.println(" No se encontraron errores semánticos.");
            } else {
                for (SemanticError e : semantic.getErrors()) {
                    System.out.println(e);
                }
            }

            // 6 Mostrar tabla de símbolos
            System.out.println("\n=== TABLA DE SÍMBOLOS ===");
            semantic.getSymbolTable().printTable();

        } catch (IOException e) {
            System.err.println("Error al leer el archivo: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error general: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

