//Enum que define todos los tipos de tokens válidos (palabras reservadas, operadores, identificadores, constantes, etc.).

package minilang;

public enum TokenType {
    // Palabras reservadas
    LONG, DOUBLE, IF, THEN, ELSE, WHILE, BREAK, READ, WRITE,

    // Operadores aritméticos
    PLUS, MINUS, MULTIPLY, DIVIDE,

    // Operadores relacionales y lógicos
    GREATER, LESS, GREATER_EQUAL, LESS_EQUAL, EQUAL, NOT_EQUAL, DIFFERENT,

    // Asignación y agrupación
    ASSIGN, LPAREN, RPAREN, LBRACE, RBRACE, SEMICOLON, COMMA,

    // Opcionales
    PLUS_ASSIGN, MINUS_ASSIGN, MULT_ASSIGN, DIV_ASSIGN,
    AND, OR, NOT,

    // Identificadores y constantes
    IDENTIFIER, INTEGER_CONST, REAL_CONST, STRING_CONST, BOOLEAN_CONST,

    // Comentarios
    COMMENT_SINGLE, COMMENT_MULTI,

    // Fin de archivo
    EOF,

    // Errores
    UNKNOWN
}
