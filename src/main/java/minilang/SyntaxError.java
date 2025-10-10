package minilang;

public class SyntaxError {
    public final int line;
    public final int column;
    public final String message;

    public SyntaxError(int line, int column, String message) {
        this.line = line;
        this.column = column;
        this.message = message;
    }

    @Override
    public String toString() {
        return String.format("Error sintáctico [línea %d, col %d]: %s", line, column, message);
    }
}
