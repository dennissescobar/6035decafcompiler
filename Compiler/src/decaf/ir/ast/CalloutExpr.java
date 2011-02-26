package decaf.ir.ast;

import java.util.List;

import decaf.ir.semcheck.ASTVisitor;

public class CalloutExpr extends CallExpr {
	
	private String methodName;
	private List<CalloutArg> args;
	
	public CalloutExpr(String name, List<CalloutArg> a) {
		this.methodName = name;
		this.args = a;
	}
	
	public void addArgument(String arg) {
		this.args.add(new CalloutArg(arg));
	}
	
	public void addArgument(Expression arg) {
		this.args.add(new CalloutArg(arg));
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}
	
	@Override
	public String toString() {
		return "callout (" + methodName + ", " + args + ")";
	}

	@Override
	public <T> T accept(ASTVisitor<T> v) {
		// TODO Auto-generated method stub
		return null;
	}
}
