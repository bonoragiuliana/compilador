//Pequeño programa para probar el Lexer con un archivo o texto de prueba y ver los tokens generados.

package minilang;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class LexerTest {
    public static void main(String[] args) {
        String filePath = "src/test/java/minilang/archivo.min";
        try {
            String source = Files.readString(Paths.get(filePath));

            Lexer lexer = new Lexer(source);
            lexer.tokenize();

            System.out.println("=== TOKENS ===");
            for (Token token : lexer.getTokens()) {
                System.out.println(token);
            }

            System.out.println("\n=== ERRORES ===");
            for (LexError error : lexer.getErrors()) {
                System.out.println(error);
            }
        } catch (IOException e) {
            System.err.println("Error al leer el archivo: " + e.getMessage());
        }
    }
}

