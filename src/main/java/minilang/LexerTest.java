//Pequeño programa para probar el Lexer con un archivo o texto de prueba y ver los tokens generados.

package minilang;

public class LexerTest {
    public static void main(String[] args) {
        String source = """
                /* Programa de ejemplo */
                long _x, _y;
                double _prom;
                read(_x);
                read(_y);
                if (_x > _y) then
                    _prom = (_x + _y) / 2;
                else
                    _prom = (_y - _x) / 2;
                write(_prom);
                // Fin del programa
                """;

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
    }
}
