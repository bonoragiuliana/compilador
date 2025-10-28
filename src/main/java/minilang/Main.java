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
            // === Lectura del archivo fuente ===
            String source = Files.readString(Path.of(filePath));

            // === Análisis Léxico ===
            Lexer lexer = new Lexer(source);
            lexer.tokenize();
            List<Token> tokens = lexer.getTokens();

            System.out.println("\n === ANÁLISIS LÉXICO ===");
            if (tokens.isEmpty()) {
                System.out.println(" No se detectaron tokens.");
            } else {
                for (Token token : tokens) {
                    System.out.println(token);
                }
            }

            // Mostrar errores léxicos
            System.out.println("\n === ERRORES LÉXICOS ===");
            if (lexer.getErrors().isEmpty()) {
                System.out.println(" No se encontraron errores léxicos.");
            } else {
                for (LexError e : lexer.getErrors()) {
                    System.out.println(e);
                }
                // Si hay errores léxicos graves, no seguir
                System.out.println("\n Se detiene el análisis por errores léxicos.");
                return;
            }

            // === Análisis Sintáctico ===
            Parser parser = new Parser(tokens);
            List<Stmt> statements = parser.parse();

            System.out.println("\n === ANÁLISIS SINTÁCTICO ===");
            if (parser.getErrors().isEmpty()) {
                System.out.println(" Estructura sintáctica correcta. No se encontraron errores.");
            } else {
                for (SyntaxError e : parser.getErrors()) {
                    System.out.println(e);
                }
                System.out.println("\n Se detiene el análisis por errores sintácticos.");
                return;
            }

            // === Análisis Semántico ===
            SemanticAnalyzer semantic = new SemanticAnalyzer();
            semantic.analyze(statements);

            System.out.println("\n === ANÁLISIS SEMÁNTICO ===");
            if (semantic.getErrors().isEmpty()) {
                System.out.println(" No se encontraron errores semánticos.");
            } else {
                for (SemanticError e : semantic.getErrors()) {
                    System.out.println(e);
                }
            }

            // === Tabla de Símbolos ===
            System.out.println("\n === TABLA DE SÍMBOLOS ===");
            semantic.getSymbolTable().printTable();

            System.out.println("\n Análisis completo finalizado con éxito.");

        } catch (IOException e) {
            System.err.println(" Error al leer el archivo: " + e.getMessage());
        } catch (Exception e) {
            System.err.println(" Error general: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
