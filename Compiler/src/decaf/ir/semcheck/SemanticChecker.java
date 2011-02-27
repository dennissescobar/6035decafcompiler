package decaf.ir.semcheck;

import java.util.List;

import decaf.ir.ast.ClassDecl;
import decaf.ir.ast.MethodDecl;
import decaf.test.PrettyPrintVisitor;
import decaf.test.Error;

public class SemanticChecker {

	public static void performSemanticChecks(ClassDecl cd) {
		PrettyPrintVisitor pv = new PrettyPrintVisitor();
		cd.accept(pv);
		
		// Check integer overflow (must do before symbol table generation)
		IntOverflowCheckVisitor ibv = new IntOverflowCheckVisitor();
		cd.accept(ibv);
		System.out.println("Integer overflow check:");
		System.out.println(ibv.getErrors());

		// Generate SymbolTables
		SymbolTableGenerationVisitor stv = new SymbolTableGenerationVisitor();
		cd.accept(stv);
		System.out.println("Symbol table generation:");
		System.out.println(stv.getErrors());

		// Type checking and evaluation
		TypeEvaluationVisitor tev = new TypeEvaluationVisitor(
				stv.getClassDescriptor());
		cd.accept(tev);
		System.out.println("Type checking and evaluation:");
		System.out.println(tev.getErrors());

		// Method calls and return statement type checking
		System.out.println("Method argument and return type matching:");
		ProperMethodCallCheckVisitor pmv = new ProperMethodCallCheckVisitor(
				stv.getClassDescriptor());
		cd.accept(pmv);
		System.out.println(pmv.getErrors());

		// Check if main method with no params exists
		System.out.println("'main' method check:");
		Error mainMethodError = checkMainMethod(cd);
		if (mainMethodError != null) {
			System.out.println(mainMethodError);
		}

		// Break Continue check
		System.out.println("Break/continue statement check:");
		BreakContinueStmtCheckVisitor tc = new BreakContinueStmtCheckVisitor();
		cd.accept(tc);
		System.out.println(tc.getErrors());

		// Array Size check
		System.out.println("Array size check:");
		ArraySizeCheckVisitor av = new ArraySizeCheckVisitor();
		cd.accept(av);
		System.out.println(av.getErrors());
	}

	// Checks whether the program contains a main method with no parameters
	private static Error checkMainMethod(ClassDecl cd) {
		List<MethodDecl> methodDecls = cd.getMethodDeclarations();
		for (MethodDecl md : methodDecls) {
			if (md.getId().equals("main")) {
				if (md.getParameters().size() == 0) {
					return null;
				}
			}
		}
		return new Error(cd.getLineNumber(), cd.getColumnNumber(),
				"Class does not contain 'main' method with no parameters.");
	}
}
