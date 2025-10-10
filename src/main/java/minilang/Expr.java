package minilang;

public abstract class Expr {
    public interface Visitor<R> {
        R visitBinaryExpr(Binary expr);
        R visitLiteralExpr(Literal expr);
        R visitUnaryExpr(Unary expr);
        R visitVariableExpr(Variable expr);
        R visitGroupingExpr(Grouping expr);
    }

    public static class Binary extends Expr {
        public final Expr left;
        public final Token operator;
        public final Expr right;
        public Binary(Expr left, Token operator, Expr right) { this.left = left; this.operator = operator; this.right = right; }
        @Override public <R> R accept(Visitor<R> v) { return v.visitBinaryExpr(this); }
        @Override public String toString() { return "(" + left + " " + operator.lexeme + " " + right + ")"; }
    }

    public static class Literal extends Expr {
        public final Object value;
        public Literal(Object value) { this.value = value; }
        @Override public <R> R accept(Visitor<R> v) { return v.visitLiteralExpr(this); }
        @Override public String toString() { return value == null ? "null" : value.toString(); }
    }

    public static class Unary extends Expr {
        public final Token operator;
        public final Expr right;
        public Unary(Token operator, Expr right) { this.operator = operator; this.right = right; }
        @Override public <R> R accept(Visitor<R> v) { return v.visitUnaryExpr(this); }
        @Override public String toString() { return "(" + operator.lexeme + right + ")"; }
    }

    public static class Variable extends Expr {
        public final Token name;
        public Variable(Token name) { this.name = name; }
        @Override public <R> R accept(Visitor<R> v) { return v.visitVariableExpr(this); }
        @Override public String toString() { return name.lexeme; }
    }

    public static class Grouping extends Expr {
        public final Expr expression;
        public Grouping(Expr expression) { this.expression = expression; }
        @Override public <R> R accept(Visitor<R> v) { return v.visitGroupingExpr(this); }
        @Override public String toString() { return "(group " + expression + ")"; }
    }

    public abstract <R> R accept(Visitor<R> visitor);
}
