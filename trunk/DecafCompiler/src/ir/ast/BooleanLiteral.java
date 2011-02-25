package ir.ast;

public class BooleanLiteral extends Literal {
	public static int TRUE = 1;
	public static int FALSE = 0;
	
	private int value;
	
	/*
	 * Constructor for bool literal that takes a string as an input
	 * @param: String integer
	 */
	public BooleanLiteral(String inp){
		if (inp.equals("true")) {
			value = BooleanLiteral.TRUE;
		}
		else if (inp.equals("false")) {
			value = BooleanLiteral.FALSE;
		}
		else {
			value = -1; // invalid literal!
		}
	}
	
	public void setValue(String inp) {
		if (inp.equals("true")) {
			value = BooleanLiteral.TRUE;
		}
		else if (inp.equals("false")) {
			value = BooleanLiteral.FALSE;
		}
		else {
			value = -1; // invalid literal!
		}
	}
	
	public int getValue() {
		return value;
	}

	@Override
	public Type getType() {
		return Type.BOOLEAN;
	}
}
