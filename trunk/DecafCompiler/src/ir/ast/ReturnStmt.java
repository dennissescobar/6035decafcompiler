package ir.ast;

public class ReturnStmt extends Statement {
	private Expression expression; // the return expression
	
	public ReturnStmt(Expression e) {
		this.expression = e;
	}
	
	public ReturnStmt() {
		this.expression = null;
	}

	public Expression getExpression() {
		return expression;
	}

	public void setExpression(Expression expression) {
		this.expression = expression;
	}
}
