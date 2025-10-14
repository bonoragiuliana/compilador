package minilang;
import java.util.HashMap;
import java.util.Map;

public class SymbolTable {
    private final Map<String, Symbol> symbols = new HashMap<>();

    public void declare(String name, String type, String scope, Object initialValue, int line) {
        symbols.put(name, new Symbol(name, type, scope, initialValue, line));
    }

    public boolean isDeclared(String name) {
        return symbols.containsKey(name);
    }

    public String getType(String name) {
        Symbol sym = symbols.get(name);
        return (sym != null) ? sym.getType() : null;
    }

    public Map<String, Symbol> getSymbols() {
        return symbols;
    }

    public void printTable() {
        System.out.println("Nombre\t\tTipo");
        System.out.println("----------------------");
        for (Map.Entry<String, Symbol> entry : symbols.entrySet()) {
            System.out.println(entry.getKey() + "\t\t" + entry.getValue().getType());
        }
    }
}
