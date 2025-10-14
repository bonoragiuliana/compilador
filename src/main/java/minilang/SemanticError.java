package minilang;

public class SemanticError {
    private final String message;
    private final int line;

    public SemanticError(String message, int line) {
        this.message = message;
        this.line = line;
    }

    @Override
    public String toString() {
        return "Semantic Error (line " + line + "): " + message;
    }
}
