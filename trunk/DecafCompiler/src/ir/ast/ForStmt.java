package ir.ast;

public class ForStmt extends Statement {
	private String id;
	private Expression initialValue;
	private Expression finalValue;
	private Block block;
	
	public ForStmt(String i, Expression init, Expression fin, Block bl) {
		this.id = i;
		this.initialValue = init;
		this.finalValue = fin;
		this.block = bl;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Expression getInitialValue() {
		return initialValue;
	}

	public void setInitialValue(Expression initialValue) {
		this.initialValue = initialValue;
	}

	public Expression getFinalValue() {
		return finalValue;
	}

	public void setFinalValue(Expression finalValue) {
		this.finalValue = finalValue;
	}

	public Block getBlock() {
		return block;
	}

	public void setBlock(Block block) {
		this.block = block;
	}
}
