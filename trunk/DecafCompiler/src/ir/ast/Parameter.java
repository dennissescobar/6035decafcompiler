package ir.ast;

public class Parameter {
	private Type type;
	private String id;
	
	public Parameter(Type t, String i) {
		type = t;
		id = i;
	}
	
	public void setType(Type t) {
		type = t;
	}
	
	public Type getType() {
		return type;
	}
	
	public void setId(String i) {
		id = i;
	}
	
	public String getId() {
		return id;
	}
}