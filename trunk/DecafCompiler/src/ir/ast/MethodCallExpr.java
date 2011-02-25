package ir.ast;

import java.util.ArrayList;
import java.util.List;

public class MethodCallExpr extends CallExpr {
	private String name;
	private List<Expression> args;
	
	public MethodCallExpr(String name) {
		this.name = name;
		this.args = new ArrayList<Expression>();
	}

	public MethodCallExpr(String name, List<Expression> a) {
		this.name = name;
		this.args = a;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Expression> getArgs() {
		return args;
	}

	public void setArgs(List<Expression> args) {
		this.args = args;
	}
	
	@Override
	public String toString() {
		return name + "(" + args + ")";
	}
}
