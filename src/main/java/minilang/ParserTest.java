package minilang;

import java.util.List;

public class ParserTest {
    public static void main(String[] args) {
        String source = """
        long _x, _y;
        double _prom;
        read(_x);
        read(_y);
        if (_x > _y) then
          _prom = (_x + _y) / 2;
        else
          _prom = (_y - _x) / 2;
        write(_prom);
        """;

        Lexer lexer = new Lexer(source);
        lexer.tokenize();
        List<Token> tokens = lexer.getTokens();
        List<LexError> lexErrors = lexer.getErrors();

        System.out.println("LEX ERRORS:");
        for (LexError e : lexErrors) System.out.println(e);
        System.out.println();

        Parser parser = new Parser(tokens);
        List<Stmt> stmts = parser.parse();
        List<SyntaxError> synErrs = parser.getErrors();

        System.out.println("PARSER ERRORS:");
        for (SyntaxError e : synErrs) System.out.println(e);

        System.out.println("\nAST (statements):");
        for (Stmt s : stmts) {
            System.out.println(s);
        }
    }
}
