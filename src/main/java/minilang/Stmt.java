package minilang;

import java.util.List;

public abstract class Stmt {
    public interface Visitor<R> {
        R visitVarStmt(Var stmt);
        R visitExpressionStmt(Expression stmt);
        R visitAssignStmt(Assign stmt);
        R visitReadStmt(Read stmt);
        R visitWriteStmt(Write stmt);
        R visitBlockStmt(Block stmt);
        R visitIfStmt(If stmt);
        R visitWhileStmt(While stmt);
    }

    public static class Var extends Stmt {
        public final Token typeToken; // LONG or DOUBLE
        public final Token name;
        public Var(Token typeToken, Token name) { this.typeToken = typeToken; this.name = name; }
        @Override public <R> R accept(Visitor<R> v) { return v.visitVarStmt(this); }
        @Override public String toString() { return String.format("VarDecl(%s %s)", typeToken.lexeme, name.lexeme); }
    }

    public static class Expression extends Stmt {
        public final Expr expression;
        public Expression(Expr expression) { this.expression = expression; }
        @Override public <R> R accept(Visitor<R> v) { return v.visitExpressionStmt(this); }
        @Override public String toString() { return "ExprStmt(" + expression + ")"; }
    }

    public static class Assign extends Stmt {
        public final Token name;
        public final Token operator; // ASSIGN, PLUS_ASSIGN, etc.
        public final Expr value;
        public Assign(Token name, Token operator, Expr value) { this.name = name; this.operator = operator; this.value = value; }
        @Override public <R> R accept(Visitor<R> v) { return v.visitAssignStmt(this); }
        @Override public String toString() { return "Assign(" + name.lexeme + " " + operator.lexeme + " " + value + ")"; }
    }

    public static class Read extends Stmt {
        public final Token name;
        public Read(Token name) { this.name = name; }
        @Override public <R> R accept(Visitor<R> v) { return v.visitReadStmt(this); }
        @Override public String toString() { return "Read(" + name.lexeme + ")"; }
    }

    public static class Write extends Stmt {
        public final Expr expression;
        public Write(Expr expression) { this.expression = expression; }
        @Override public <R> R accept(Visitor<R> v) { return v.visitWriteStmt(this); }
        @Override public String toString() { return "Write(" + expression + ")"; }
    }

    public static class Block extends Stmt {
        public final List<Stmt> statements;
        public Block(List<Stmt> statements) { this.statements = statements; }
        @Override public <R> R accept(Visitor<R> v) { return v.visitBlockStmt(this); }
        @Override public String toString() { return "Block(" + statements + ")"; }
    }

    public static class If extends Stmt {
        public final Expr condition;
        public final Stmt thenBranch;
        public final Stmt elseBranch; // may be null
        public If(Expr condition, Stmt thenBranch, Stmt elseBranch) { this.condition = condition; this.thenBranch = thenBranch; this.elseBranch = elseBranch; }
        @Override public <R> R accept(Visitor<R> v) { return v.visitIfStmt(this); }
        @Override public String toString() { return "If(" + condition + ", " + thenBranch + (elseBranch != null ? ", " + elseBranch : "") + ")"; }
    }

    public static class While extends Stmt {
        public final Expr condition;
        public final Stmt body;
        public While(Expr condition, Stmt body) { this.condition = condition; this.body = body; }
        @Override public <R> R accept(Visitor<R> v) { return v.visitWhileStmt(this); }
        @Override public String toString() { return "While(" + condition + ", " + body + ")"; }
    }

    public abstract <R> R accept(Visitor<R> visitor);
}
