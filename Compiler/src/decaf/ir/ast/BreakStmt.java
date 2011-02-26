package decaf.ir.ast;

import decaf.ir.semcheck.ASTVisitor;

public class BreakStmt extends Statement {
	public BreakStmt() { }
	
	@Override
	public String toString() {
		return "break";
	}

	@Override
	public <T> T accept(ASTVisitor<T> v) {
		// TODO Auto-generated method stub
		return null;
	}
}
