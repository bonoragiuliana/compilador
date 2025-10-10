package minilang;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Parser {
    private final List<Token> tokens;
    private int current = 0;
    private final List<SyntaxError> errors = new ArrayList<>();

    public Parser(List<Token> tokens) { this.tokens = tokens; }

    public List<SyntaxError> getErrors() { return errors; }

    public List<Stmt> parse() {
        List<Stmt> statements = new ArrayList<>();
        while (!isAtEnd()) {
            try {
                Stmt decl = declaration();
                if (decl != null) statements.add(decl);
            } catch (ParseError e) {
                synchronize();
            }
        }
        return statements;
    }

    // ---------- Declarations ----------
    private Stmt declaration() {
        if (match(TokenType.LONG, TokenType.DOUBLE)) {
            Token typeToken = previous();
            return varDeclaration(typeToken);
        }
        return statement();
    }

    private Stmt varDeclaration(Token typeToken) {
        List<Stmt> vars = new ArrayList<>();
        Token name = consume(TokenType.IDENTIFIER, "Se esperaba un identificador en la declaración.");
        vars.add(new Stmt.Var(typeToken, name));
        while (match(TokenType.COMMA)) {
            Token n = consume(TokenType.IDENTIFIER, "Se esperaba un identificador después de ','.");
            vars.add(new Stmt.Var(typeToken, n));
        }
        consume(TokenType.SEMICOLON, "Se esperaba ';' al final de la declaración de variables.");
        if (vars.size() == 1) return vars.get(0);
        return new Stmt.Block(vars); // grupo de declaraciones var
    }

    // ---------- Statements ----------
    private Stmt statement() {
        if (match(TokenType.READ)) return readStatement();
        if (match(TokenType.WRITE)) return writeStatement();
        if (match(TokenType.IF)) return ifStatement();
        if (match(TokenType.WHILE)) return whileStatement();
        if (match(TokenType.LBRACE)) return new Stmt.Block(block());

        if (check(TokenType.IDENTIFIER) && isAssignmentOperator(peekNext().type)) {
            return assignStatement();
        }

        // expression statement
        return expressionStatement();
    }

    private Stmt expressionStatement() {
        Expr expr = expression();
        consume(TokenType.SEMICOLON, "Se esperaba ';' después de la expresión.");
        return new Stmt.Expression(expr);
    }

    private Stmt assignStatement() {
        Token name = consume(TokenType.IDENTIFIER, "Se esperaba identificador en la asignación.");
        if (!match(TokenType.ASSIGN, TokenType.PLUS_ASSIGN, TokenType.MINUS_ASSIGN, TokenType.MULT_ASSIGN, TokenType.DIV_ASSIGN)) {
            throw error(peek(), "Se esperaba un operador de asignación después del identificador.");
        }
        Token operator = previous();
        Expr value = expression();
        consume(TokenType.SEMICOLON, "Se esperaba ';' después de la asignación.");
        return new Stmt.Assign(name, operator, value);
    }

    private Stmt readStatement() {
        consume(TokenType.LPAREN, "Se esperaba '(' después de read.");
        Token name = consume(TokenType.IDENTIFIER, "Se esperaba un identificador dentro de read().");
        consume(TokenType.RPAREN, "Se esperaba ')' después de read(identifier).");
        consume(TokenType.SEMICOLON, "Se esperaba ';' después de read(...).");
        return new Stmt.Read(name);
    }

    private Stmt writeStatement() {
        consume(TokenType.LPAREN, "Se esperaba '(' después de write.");
        Expr expr = expression();
        consume(TokenType.RPAREN, "Se esperaba ')' después de write(expression).");
        consume(TokenType.SEMICOLON, "Se esperaba ';' después de write(...).");
        return new Stmt.Write(expr);
    }

    private Stmt ifStatement() {
        consume(TokenType.LPAREN, "Se esperaba '(' después de if.");
        Expr condition = expression();
        consume(TokenType.RPAREN, "Se esperaba ')' después de la condición del if.");
        consume(TokenType.THEN, "Se esperaba 'then' después de if(...).");
        Stmt thenBranch = statement();
        Stmt elseBranch = null;
        if (match(TokenType.ELSE)) {
            elseBranch = statement();
        }
        return new Stmt.If(condition, thenBranch, elseBranch);
    }

    private Stmt whileStatement() {
        consume(TokenType.LPAREN, "Se esperaba '(' después de while.");
        Expr condition = expression();
        consume(TokenType.RPAREN, "Se esperaba ')' después de la condición del while.");
        Stmt body = statement();
        return new Stmt.While(condition, body);
    }

    private List<Stmt> block() {
        List<Stmt> statements = new ArrayList<>();
        while (!check(TokenType.RBRACE) && !isAtEnd()) {
            try {
                Stmt d = declaration();
                if (d != null) statements.add(d);
            } catch (ParseError e) {
                synchronize();
            }
        }
        consume(TokenType.RBRACE, "Se esperaba '}' al final del bloque.");
        return statements;
    }

    // ---------- Expressions (precedence climbing with recursive descent structure) ----------
    private Expr expression() { return logicalOr(); }

    private Expr logicalOr() {
        Expr expr = logicalAnd();
        while (match(TokenType.OR)) {
            Token op = previous();
            Expr right = logicalAnd();
            expr = new Expr.Binary(expr, op, right);
        }
        return expr;
    }

    private Expr logicalAnd() {
        Expr expr = equality();
        while (match(TokenType.AND)) {
            Token op = previous();
            Expr right = equality();
            expr = new Expr.Binary(expr, op, right);
        }
        return expr;
    }

    private Expr equality() {
        Expr expr = relational();
        while (match(TokenType.EQUAL, TokenType.NOT_EQUAL)) {
            Token op = previous();
            Expr right = relational();
            expr = new Expr.Binary(expr, op, right);
        }
        return expr;
    }

    private Expr relational() {
        Expr expr = additive();
        while (match(TokenType.GREATER, TokenType.GREATER_EQUAL, TokenType.LESS, TokenType.LESS_EQUAL)) {
            Token op = previous();
            Expr right = additive();
            expr = new Expr.Binary(expr, op, right);
        }
        return expr;
    }

    private Expr additive() {
        Expr expr = multiplicative();
        while (match(TokenType.PLUS, TokenType.MINUS)) {
            Token op = previous();
            Expr right = multiplicative();
            expr = new Expr.Binary(expr, op, right);
        }
        return expr;
    }

    private Expr multiplicative() {
        Expr expr = unary();
        while (match(TokenType.MULTIPLY, TokenType.DIVIDE)) {
            Token op = previous();
            Expr right = unary();
            expr = new Expr.Binary(expr, op, right);
        }
        return expr;
    }

    private Expr unary() {
        if (match(TokenType.NOT, TokenType.MINUS)) {
            Token op = previous();
            Expr right = unary();
            return new Expr.Unary(op, right);
        }
        return primary();
    }

    private Expr primary() {
        if (match(TokenType.INTEGER_CONST)) {
            Object v = Integer.parseInt(previous().getLexeme());
            return new Expr.Literal(v);
        }
        if (match(TokenType.REAL_CONST)) {
            Object v = Double.parseDouble(previous().getLexeme());
            return new Expr.Literal(v);
        }
        if (match(TokenType.STRING_CONST)) {
            return new Expr.Literal(previous().getLexeme());
        }
        if (match(TokenType.BOOLEAN_CONST)) {
            return new Expr.Literal(Boolean.parseBoolean(previous().getLexeme()));
        }
        if (match(TokenType.IDENTIFIER)) {
            return new Expr.Variable(previous());
        }
        if (match(TokenType.LPAREN)) {
            Expr expr = expression();
            consume(TokenType.RPAREN, "Se esperaba ')' después de la expresión.");
            return new Expr.Grouping(expr);
        }
        throw error(peek(), "Se esperaba una expresión.");
    }

    // ---------- Helpers ----------
    private boolean match(TokenType... types) {
        for (TokenType t : types) {
            if (check(t)) { advance(); return true; }
        }
        return false;
    }

    private Token consume(TokenType type, String message) {
        if (check(type)) return advance();
        throw error(peek(), message);
    }

    private ParseError error(Token token, String message) {
        errors.add(new SyntaxError(token.line, token.column, message + " (token: '" + token.lexeme + "')"));
        return new ParseError();
    }

    private void synchronize() {
        advance();
        while (!isAtEnd()) {
            if (previous().type == TokenType.SEMICOLON) return;
            TokenType t = peek().type;
            if (t == TokenType.LONG || t == TokenType.DOUBLE || t == TokenType.IF ||
                    t == TokenType.WHILE || t == TokenType.READ || t == TokenType.WRITE) {
                return;
            }
            advance();
        }
    }

    private boolean check(TokenType type) {
        if (isAtEnd()) return false;
        return peek().type == type;
    }

    private Token advance() {
        if (!isAtEnd()) current++;
        return previous();
    }

    private boolean isAtEnd() {
        return peek().type == TokenType.EOF;
    }

    private Token peek() {
        return tokens.get(current);
    }

    private Token peekNext() {
        if (current + 1 >= tokens.size()) return tokens.get(tokens.size() - 1);
        return tokens.get(current + 1);
    }

    private Token previous() {
        return tokens.get(current - 1);
    }

    private boolean isAssignmentOperator(TokenType t) {
        return t == TokenType.ASSIGN || t == TokenType.PLUS_ASSIGN || t == TokenType.MINUS_ASSIGN
                || t == TokenType.MULT_ASSIGN || t == TokenType.DIV_ASSIGN;
    }

    // exception used for control flow on parse errors
    private static class ParseError extends RuntimeException {}
}
