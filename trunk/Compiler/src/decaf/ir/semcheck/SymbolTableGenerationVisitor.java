package decaf.ir.semcheck;

import java.util.ArrayList;
import java.util.List;
import decaf.test.Error;
import decaf.ir.ASTVisitor;
import decaf.ir.ast.AST;
import decaf.ir.ast.ArrayLocation;
import decaf.ir.ast.AssignStmt;
import decaf.ir.ast.BinOpExpr;
import decaf.ir.ast.Block;
import decaf.ir.ast.BooleanLiteral;
import decaf.ir.ast.BreakStmt;
import decaf.ir.ast.CalloutArg;
import decaf.ir.ast.CalloutExpr;
import decaf.ir.ast.CharLiteral;
import decaf.ir.ast.ClassDecl;
import decaf.ir.ast.ContinueStmt;
import decaf.ir.ast.Expression;
import decaf.ir.ast.Field;
import decaf.ir.ast.FieldDecl;
import decaf.ir.ast.ForStmt;
import decaf.ir.ast.IfStmt;
import decaf.ir.ast.IntLiteral;
import decaf.ir.ast.InvokeStmt;
import decaf.ir.ast.MethodCallExpr;
import decaf.ir.ast.MethodDecl;
import decaf.ir.ast.Parameter;
import decaf.ir.ast.ReturnStmt;
import decaf.ir.ast.Statement;
import decaf.ir.ast.UnaryOpExpr;
import decaf.ir.ast.VarDecl;
import decaf.ir.ast.VarLocation;
import decaf.ir.desc.ClassDescriptor;
import decaf.ir.desc.FieldDescriptor;
import decaf.ir.desc.GenericSymbolTable;
import decaf.ir.desc.MethodDescriptor;
import decaf.ir.desc.MethodSymbolTable;
import decaf.ir.desc.ParameterDescriptor;
import decaf.ir.desc.VariableDescriptor;

public class SymbolTableGenerationVisitor implements ASTVisitor<Integer> {
	private ClassDescriptor classDescriptor;
	private GenericSymbolTable currentScope;
	private List<Error> errors;
	private MethodDescriptor inMethod = null;

	public SymbolTableGenerationVisitor() {
		setClassDescriptor(new ClassDescriptor());
		setErrors(new ArrayList<Error>());
		currentScope = null;
	}

	@Override
	public Integer visit(ArrayLocation loc) {
		FieldDescriptor fd = (FieldDescriptor) classDescriptor
				.getFieldSymbolTable().get(loc.getId());

		if (fd == null) {
			addError(loc, "'" + loc.getId() + "'" + " is not declared");
		} else if (!fd.isArray()) {
			addError(loc, "'" + loc.getId() + "'" + " is not an array");
		}

		loc.getExpr().accept(this);

		return 0;
	}

	@Override
	public Integer visit(AssignStmt stmt) {
		stmt.getLocation().accept(this);
		stmt.getExrpression().accept(this);

		return 0;
	}

	@Override
	public Integer visit(BinOpExpr expr) {
		expr.getLeftOperand().accept(this);
		expr.getRightOperand().accept(this);

		return 0;
	}

	@Override
	public Integer visit(Block block) {
		GenericSymbolTable newScope = new GenericSymbolTable(currentScope);
		currentScope = newScope;

		classDescriptor.getScopeTable().put(block.getBlockId(), currentScope);

		if (inMethod != null) {
			inMethod.setLocalSymbolTable(currentScope);
			inMethod = null;
		}

		for (VarDecl v : block.getVarDeclarations()) {
			v.accept(this);
		}

		for (Statement s : block.getStatements()) {
			s.accept(this);
		}

		currentScope = currentScope.getParent();

		return 0;
	}

	@Override
	public Integer visit(BooleanLiteral lit) {
		return 0;
	}

	@Override
	public Integer visit(BreakStmt stmt) {
		return 0;
	}

	@Override
	public Integer visit(CalloutArg arg) {
		if (!arg.isString()) {
			arg.getExpression().accept(this);
		}

		return 0;
	}

	@Override
	public Integer visit(CalloutExpr expr) {
		for (CalloutArg arg : expr.getArguments()) {
			arg.accept(this);
		}
		return 0;
	}

	@Override
	public Integer visit(CharLiteral lit) {
		return 0;
	}

	@Override
	public Integer visit(ClassDecl cd) {
		currentScope = classDescriptor.getFieldSymbolTable();

		for (FieldDecl fd : cd.getFieldDeclarations()) {
			fd.accept(this);
		}

		for (MethodDecl md : cd.getMethodDeclarations()) {
			md.accept(this);
		}

		return 0;
	}

	@Override
	public Integer visit(ContinueStmt stmt) {
		return 0;
	}

	@Override
	public Integer visit(Field f) {
		MethodSymbolTable methodTable = classDescriptor.getMethodSymbolTable();

		if (currentScope.containsKey(f.getId())) {
			addError(f, "'" + f.getId() + "'" + " is already declared");
		} else if (methodTable.containsKey(f.getId())) {
			addError(f, "'" + f.getId() + "'" + " is a method");
		} else {
			currentScope.put(f.getId(), new FieldDescriptor(f.getId(),
					f.getType(), f.isArray(), (f.isArray() ? f.getArrayLength()
							.getValue() : -1)));
		}

		return 0;
	}

	@Override
	public Integer visit(FieldDecl fd) {
		for (Field field : fd.getFields()) {
			field.accept(this);
		}

		return 0;
	}

	@Override
	public Integer visit(ForStmt stmt) {
		if (!isIdDeclared(stmt.getId())) {
			addError(stmt, "loop id " + "'" + stmt.getId() + "'" + " is not declared");
		}

		stmt.getInitialValue().accept(this);
		stmt.getFinalValue().accept(this);
		stmt.getBlock().accept(this);

		return 0;
	}

	@Override
	public Integer visit(IfStmt stmt) {
		stmt.getCondition().accept(this);
		stmt.getIfBlock().accept(this);

		if (stmt.getElseBlock() != null) {
			stmt.getElseBlock().accept(this);
		}

		return 0;
	}

	@Override
	public Integer visit(IntLiteral lit) {
		return 0;
	}

	@Override
	public Integer visit(InvokeStmt stmt) {
		stmt.getMethodCall().accept(this);

		return 0;
	}

	@Override
	public Integer visit(MethodCallExpr expr) {
		MethodSymbolTable methodTable = classDescriptor.getMethodSymbolTable();

		if (!methodTable.containsKey(expr.getName())) {
			addError(expr, "'" + expr.getName() + "'" + " method is not defined");
		}

		for (Expression e : expr.getArguments()) {
			e.accept(this);
		}

		return 0;
	}

	@Override
	public Integer visit(MethodDecl md) {
		MethodSymbolTable methodTable = classDescriptor.getMethodSymbolTable();

		if (currentScope.containsKey(md.getId())) {
			addError(md, "'" + md.getId() + "'" + " is a field");
		} else if (methodTable.containsKey(md.getId())) {
			addError(md, "'" + md.getId() + "'" + " is already declared");
		} else {
			MethodDescriptor mDesc = new MethodDescriptor(md.getId(), md
					.getReturnType());
			methodTable.put(md.getId(), mDesc);

			GenericSymbolTable pTable = new GenericSymbolTable(currentScope);
			mDesc.setParameterSymbolTable(pTable);

			currentScope = pTable;

			for (Parameter p : md.getParameters()) {
				p.accept(this);
			}

			inMethod = mDesc;
			md.getBlock().accept(this);

			currentScope = currentScope.getParent();
		}

		return 0;
	}

	@Override
	public Integer visit(Parameter param) {
		if (currentScope.containsKey(param.getId())) {
			addError(param, "'" + param.getId()
					+ "'" + " is used multiple times as method parameter");
		} else {
			currentScope.put(param.getId(), new ParameterDescriptor(param.getId(),
					param.getType()));
		}

		return 0;
	}

	@Override
	public Integer visit(ReturnStmt stmt) {
		return 0;
	}

	@Override
	public Integer visit(UnaryOpExpr expr) {
		expr.getExpression().accept(this);

		return 0;
	}

	@Override
	public Integer visit(VarDecl vd) {
		for (String var : vd.getVariables()) {
			if (currentScope.containsKey(var)) {
				addError(vd, "'" + var + "'" + " is already declared");
			} else {
				currentScope.put(var, new VariableDescriptor(var, vd.getType()));
			}
		}

		return 0;
	}

	@Override
	public Integer visit(VarLocation loc) {
		if (!isIdDeclared(loc.getId())) {
			addError(loc, "'" + loc.getId() + "'" + " is not declared");
		}

		return 0;
	}

	public void setClassDescriptor(ClassDescriptor classDescriptor) {
		this.classDescriptor = classDescriptor;
	}

	public ClassDescriptor getClassDescriptor() {
		return classDescriptor;
	}

	public void setErrors(List<Error> errors) {
		this.errors = errors;
	}

	public List<Error> getErrors() {
		return errors;
	}

	private boolean isIdDeclared(String id) {
		GenericSymbolTable scope = currentScope;

		while (scope != null) {
			if (scope.containsKey(id)) {
				return true;
			}
			scope = currentScope.getParent();
		}

		return false;
	}

	private void addError(AST a, String desc) {
		errors.add(new Error(a.getLineNumber(), a.getColumnNumber(), desc));
	}
}