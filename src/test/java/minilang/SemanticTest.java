package minilang;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class SemanticTest {
    public static void main(String[] args) {
        String filePath = "src/test/java/minilang/archivo.min";
        try {
            String source = Files.readString(Paths.get(filePath));

            // Fases del compilador
            Lexer lexer = new Lexer(source);
            lexer.tokenize();
            List<Token> tokens = lexer.getTokens();

            Parser parser = new Parser(tokens);
            List<Stmt> stmts = parser.parse();

            SemanticAnalyzer semantic = new SemanticAnalyzer();
            semantic.analyze(stmts);

            // Resultados
            System.out.println("=== ERRORES SEMÁNTICOS ===");
            for (SemanticError e : semantic.getErrors()) {
                System.out.println(e);
            }

            System.out.println("\n=== TABLA DE SÍMBOLOS ===");
            System.out.printf("%-10s %-10s %-10s %-10s %s\n", "Nombre", "Tipo", "Ámbito", "Valor", "Línea");
            semantic.getSymbolTable().getSymbols().forEach((name, sym) -> {
                System.out.println(sym);
            });

        } catch (IOException e) {
            System.err.println("Error al leer el archivo: " + e.getMessage());
        }
    }
}


