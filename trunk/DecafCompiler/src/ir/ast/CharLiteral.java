package ir.ast;

public class CharLiteral extends Literal {
	private String value;

	/*
	 * Constructor for int literal that takes a string as an input
	 * @param: String integer
	 */
	public CharLiteral(String inp){
		value = inp;
	}
	
	@Override
	public Type getType() {
		return Type.CHAR;
	}
	
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
