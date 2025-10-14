package minilang;

import java.util.ArrayList;
import java.util.List;

public class SemanticAnalyzer implements Stmt.Visitor<Void>, Expr.Visitor<String> {

    private final SymbolTable symbols = new SymbolTable();
    private final List<SemanticError> errors = new ArrayList<>();
    private final String currentScope = "global"; // de momento, todo es ámbito global

    public List<SemanticError> getErrors() {
        return errors;
    }

    public SymbolTable getSymbolTable() {
        return symbols;
    }

    public void analyze(List<Stmt> statements) {
        for (Stmt stmt : statements) {
            stmt.accept(this);
        }
    }

    // ======================
    // VISITORS PARA STMTs
    // ======================

    @Override
    public Void visitVarStmt(Stmt.Var stmt) {
        String varName = stmt.name.lexeme;
        String varType = stmt.typeToken.lexeme;

        if (symbols.isDeclared(varName)) {
            errors.add(new SemanticError("Variable '" + varName + "' redeclarada.", stmt.name.line));
        } else {
            symbols.declare(varName, varType, currentScope, null, stmt.name.line);
        }
        return null;
    }

    @Override
    public Void visitAssignStmt(Stmt.Assign stmt) {
        String varName = stmt.name.lexeme;

        if (!symbols.isDeclared(varName)) {
            errors.add(new SemanticError("Variable '" + varName + "' no declarada.", stmt.name.line));
            return null;
        }

        String leftType = symbols.getType(varName);
        String rightType = stmt.value.accept(this);

        if (rightType != null && !leftType.equals(rightType)) {
            errors.add(new SemanticError("Asignación incompatible: " + leftType + " = " + rightType, stmt.name.line));
        }

        return null;
    }

    @Override
    public Void visitExpressionStmt(Stmt.Expression stmt) {
        stmt.expression.accept(this);
        return null;
    }

    @Override
    public Void visitReadStmt(Stmt.Read stmt) {
        String varName = stmt.name.lexeme;
        if (!symbols.isDeclared(varName)) {
            errors.add(new SemanticError("Variable '" + varName + "' no declarada antes del read.", stmt.name.line));
        }
        return null;
    }

    @Override
    public Void visitWriteStmt(Stmt.Write stmt) {
        stmt.expression.accept(this);
        return null;
    }

    @Override
    public Void visitBlockStmt(Stmt.Block stmt) {
        for (Stmt s : stmt.statements) {
            s.accept(this);
        }
        return null;
    }

    @Override
    public Void visitIfStmt(Stmt.If stmt) {
        stmt.condition.accept(this);
        stmt.thenBranch.accept(this);
        if (stmt.elseBranch != null)
            stmt.elseBranch.accept(this);
        return null;
    }

    @Override
    public Void visitWhileStmt(Stmt.While stmt) {
        stmt.condition.accept(this);
        stmt.body.accept(this);
        return null;
    }

    // ======================
    // VISITORS PARA EXPRs
    // ======================

    @Override
    public String visitBinaryExpr(Expr.Binary expr) {
        String leftType = expr.left.accept(this);
        String rightType = expr.right.accept(this);

        if (leftType == null || rightType == null) return null;

        if (!leftType.equals(rightType)) {
            errors.add(new SemanticError("Operación incompatible: " + leftType + " " + expr.operator.lexeme + " " + rightType, expr.operator.line));
        }

        return leftType;
    }

    @Override
    public String visitLiteralExpr(Expr.Literal expr) {
        Object value = expr.value;
        if (value instanceof Integer) return "long";
        if (value instanceof Double) return "double";
        return null;
    }

    @Override
    public String visitVariableExpr(Expr.Variable expr) {
        String varName = expr.name.lexeme;
        if (!symbols.isDeclared(varName)) {
            errors.add(new SemanticError("Variable '" + varName + "' usada sin declarar.", expr.name.line));
            return null;
        }
        return symbols.getType(varName);
    }

    @Override
    public String visitGroupingExpr(Expr.Grouping expr) {
        return expr.expression.accept(this);
    }

    @Override
    public String visitUnaryExpr(Expr.Unary expr) {
        return expr.right.accept(this);
    }



}
