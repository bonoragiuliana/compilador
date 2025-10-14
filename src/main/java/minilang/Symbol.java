package minilang;

public class Symbol {
    private final String name;
    private final String type;
    private final String scope;
    private Object value;
    private final int line;

    public Symbol(String name, String type, String scope, Object value, int line) {
        this.name = name;
        this.type = type;
        this.scope = scope;
        this.value = value;
        this.line = line;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getScope() {
        return scope;
    }

    public int getLine() {
        return line;
    }


    @Override
    public String toString() {

        String valorStr = (value != null) ? value.toString() : "null";

        return String.format("%-10s %-10s %-10s %-10s %d",
                name,
                type,
                scope,
                valorStr, // Usamos el string formateado aquí
                line
        );
    }
}
