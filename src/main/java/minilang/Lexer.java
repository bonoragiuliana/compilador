//El corazón del análisis léxico. Se encarga de leer el código fuente carácter por carácter y devolver los tokens.

package minilang;

import java.util.ArrayList;
import java.util.List;

public class Lexer {

    private final String source;  // código fuente completo
    private final List<Token> tokens = new ArrayList<>();
    private final List<LexError> errors = new ArrayList<>();

    private int position = 0;
    private int line = 1;
    private int column = 1;

    private int currentTokenIndex = 0; // para ir devolviendo tokens uno a uno

    public Lexer(String source) {
        this.source = source;
    }

    public List<Token> getTokens() {
        return tokens;
    }

    public List<LexError> getErrors() {
        return errors;
    }

    // Método principal para escanear todo el código
    public void tokenize() {
        while (!isAtEnd()) {
            char current = peek();

            if (Character.isWhitespace(current)) {
                handleWhitespace(current);
            } else if (Character.isLetter(current) || current == '_') {
                handleIdentifierOrKeyword();
            } else if (Character.isDigit(current)) {
                handleNumber();
            } else if (current == '"') {
                handleString();
            } else if (current == '/' && peekNext() == '/') {
                handleSingleLineComment();
            } else if (current == '/' && peekNext() == '*') {
                handleMultiLineComment();
            } else {
                handleSymbol();
            }
        }

        tokens.add(new Token(TokenType.EOF, "", line, column));
    }

    // ==================== MANEJO DE TIPOS DE TOKENS ==================== //

    private void handleWhitespace(char c) {
        if (c == '\n') {
            line++;
            column = 1;
        } else {
            column++;
        }
        advance();
    }

    private void handleIdentifierOrKeyword() {
        int start = position;
        while (!isAtEnd() && (Character.isLetterOrDigit(peek()) || peek() == '_')) {
            advance();
        }
        String text = source.substring(start, position);

        TokenType type = switch (text) {
            case "long" -> TokenType.LONG;
            case "double" -> TokenType.DOUBLE;
            case "if" -> TokenType.IF;
            case "then" -> TokenType.THEN;
            case "else" -> TokenType.ELSE;
            case "while" -> TokenType.WHILE;
            case "break" -> TokenType.BREAK;
            case "read" -> TokenType.READ;
            case "write" -> TokenType.WRITE;
            case "true", "false" -> TokenType.BOOLEAN_CONST;
            default -> TokenType.IDENTIFIER;
        };

        tokens.add(new Token(type, text, line, column));
        column += text.length();
    }

    private void handleNumber() {
        int start = position;
        boolean isReal = false;

        while (!isAtEnd() && Character.isDigit(peek())) {
            advance();
        }

        if (!isAtEnd() && peek() == '.') {
            isReal = true;
            advance();
            while (!isAtEnd() && Character.isDigit(peek())) {
                advance();
            }
        }

        String number = source.substring(start, position);
        TokenType type = isReal ? TokenType.REAL_CONST : TokenType.INTEGER_CONST;
        tokens.add(new Token(type, number, line, column));
        column += number.length();
    }

    private void handleString() {
        advance(); // saltar la comilla inicial
        int start = position;

        while (!isAtEnd() && peek() != '"') {
            if (peek() == '\n') line++;
            advance();
        }

        if (isAtEnd()) {
            errors.add(new LexError(line, column, "Cadena sin cierre de comillas"));
            return;
        }

        String text = source.substring(start, position);
        advance(); // cerrar comillas
        tokens.add(new Token(TokenType.STRING_CONST, text, line, column));
        column += text.length() + 2;
    }

    private void handleSingleLineComment() {
        while (!isAtEnd() && peek() != '\n') {
            advance();
        }
        tokens.add(new Token(TokenType.COMMENT_SINGLE, "//", line, column));
    }

    private void handleMultiLineComment() {
        advance(); // /
        advance(); // *
        int startLine = line;
        int startColumn = column;
        while (!isAtEnd()) {
            if (peek() == '*' && peekNext() == '/') {
                advance();
                advance();
                tokens.add(new Token(TokenType.COMMENT_MULTI, "/*...*/", startLine, startColumn));
                return;
            }
            if (peek() == '\n') {
                line++;
                column = 1;
            }
            advance();
        }

        // Si sale del bucle sin cerrar el comentario:
        errors.add(new LexError(startLine, startColumn, "Comentario multilínea sin cierre"));
    }

    private void handleSymbol() {
        char current = advance();
        TokenType type = switch (current) {
            case '+' -> TokenType.PLUS;
            case '-' -> TokenType.MINUS;
            case '*' -> TokenType.MULTIPLY;
            case '/' -> TokenType.DIVIDE;
            case '>' -> match('=') ? TokenType.GREATER_EQUAL : TokenType.GREATER;
            case '<' -> match('=') ? TokenType.LESS_EQUAL : TokenType.LESS;
            case '=' -> match('=') ? TokenType.EQUAL : TokenType.ASSIGN;
            case '!' -> match('=') ? TokenType.NOT_EQUAL : TokenType.NOT;
            case '(' -> TokenType.LPAREN;
            case ')' -> TokenType.RPAREN;
            case '{' -> TokenType.LBRACE;
            case '}' -> TokenType.RBRACE;
            case ';' -> TokenType.SEMICOLON;
            case ',' -> TokenType.COMMA;
            default -> TokenType.UNKNOWN;
        };

        tokens.add(new Token(type, String.valueOf(current), line, column));
        column++;
    }

    // ==================== FUNCIONES AUXILIARES ==================== //

    private boolean isAtEnd() {
        return position >= source.length();
    }

    private char advance() {
        position++;
        return source.charAt(position - 1);
    }

    private char peek() {
        return source.charAt(position);
    }

    private char peekNext() {
        return (position + 1 >= source.length()) ? '\0' : source.charAt(position + 1);
    }

    private boolean match(char expected) {
        if (isAtEnd() || source.charAt(position) != expected) return false;
        position++;
        return true;
    }

    // ==================== NUEVO: MÉTODOS PÚBLICOS DE ACCESO ==================== //

    /** Devuelve el siguiente token sin volver a tokenizar todo. */
    public Token getNextToken() {
        if (tokens.isEmpty()) {
            tokenize();
        }
        if (currentTokenIndex < tokens.size()) {
            return tokens.get(currentTokenIndex++);
        } else {
            return new Token(TokenType.EOF, "", line, column);
        }
    }

    /** Método alias usado por Main o Parser */
    public Token nextToken() {
        return getNextToken();
    }
}

