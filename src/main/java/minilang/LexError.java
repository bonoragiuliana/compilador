//Clase para modelar un error léxico (ej. “comentario sin cierre”, “identificador inválido”).

package minilang;

public class LexError {
    private final int line;
    private final int column;
    private final String message;

    public LexError(int line, int column, String message) {
        this.line = line;
        this.column = column;
        this.message = message;
    }

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return String.format("Error léxico [línea %d, columna %d]: %s", line, column, message);
    }
}
