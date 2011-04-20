package decaf.dataflow.global;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import decaf.codegen.flatir.CallStmt;
import decaf.codegen.flatir.ConstantName;
import decaf.codegen.flatir.LIRStatement;
import decaf.codegen.flatir.Name;
import decaf.codegen.flatir.QuadrupletStmt;
import decaf.codegen.flattener.ProgramFlattener;
import decaf.dataflow.cfg.CFGBlock;

public class GlobalConstantPropagationOptimizer {
	private HashMap<String, List<CFGBlock>> cfgMap;
	private BlockReachingDefinitionGenerator reachingDefGenerator;
	
	public GlobalConstantPropagationOptimizer(HashMap<String, List<CFGBlock>> cfgMap) {
		this.cfgMap = cfgMap;
		this.reachingDefGenerator = new BlockReachingDefinitionGenerator(cfgMap);
		this.reachingDefGenerator.generate();
	}
	
	public void performGlobalConstantProp() {
		if (reachingDefGenerator.getTotalDefinitions() == 0)
			return;

		for (String s: this.cfgMap.keySet()) {
			if (s.equals(ProgramFlattener.exceptionHandlerLabel)) continue;
			
			// Optimize blocks
			for (CFGBlock block: this.cfgMap.get(s)) {
				optimize(block);
			}
		}
	}
	
	private void optimize(CFGBlock block) {
		BlockDataFlowState bFlow = reachingDefGenerator.getBlockReachingDefs().get(block);
		
		for (LIRStatement stmt: block.getStatements()) {
			// Reset kill set
			bFlow.getKill().clear();
			if (stmt.getClass().equals(CallStmt.class)) {
				// Update BlockDataFlowState kill set
				reachingDefGenerator.invalidateContextSwitch(bFlow);
				// Update BlockDataFlowState in set by using updated kill set
				bFlow.getIn().xor(bFlow.getKill());
			} else if (stmt.getClass().equals(QuadrupletStmt.class)){
				QuadrupletStmt qStmt = (QuadrupletStmt)stmt;
				// For each use of a Name, see all the reaching definitions for that Name
				// If all reaching definitions assign the Name to the same constant, replace Name with that constant
				ConstantName arg1 = reachingDefsHaveSameConstant(qStmt.getArg1(), bFlow);
				ConstantName arg2 = reachingDefsHaveSameConstant(qStmt.getArg2(), bFlow);
				if (arg1 != null)
					// Set arg1 to Constant
					qStmt.setArg1(arg1);
				if (arg2 != null)
					// Set arg2 to Constant
					qStmt.setArg2(arg2);
				// Update BlockDataFlowState kill set
				reachingDefGenerator.updateKillSet(qStmt.getDestination(), bFlow);
				// Update BlockDataFlowState in set by using updated kill set
				bFlow.getIn().xor(bFlow.getKill());
			}
		}		
	}
	
	// Returns a ConstantName if all reaching definitions assign arg to the same constant, null otherwise
	private ConstantName reachingDefsHaveSameConstant(Name arg, BlockDataFlowState bFlow) {
		if (arg == null)
			return null;
		HashMap<Name, HashSet<QuadrupletStmt>> nameToStmts = reachingDefGenerator.getNameToQStmts();
		HashSet<QuadrupletStmt> stmtsForName = nameToStmts.get(arg);
		if (stmtsForName == null)
			return null;
		
		ConstantName cName = null;
		for (QuadrupletStmt qStmt : stmtsForName) {
			if (bFlow.getIn().get(qStmt.getMyId())) {
				// Check if statement is of type : arg = constant
				Name arg1 = qStmt.getArg1();
				if (arg1.getClass().equals(ConstantName.class) && qStmt.getArg2() == null) {
					if (cName == null) {
						cName = (ConstantName)arg1;
					} else {
						if (!((ConstantName)arg1).equals(cName)) {
							return null;
						}
					}
				} else {
					// Statement is not of type arg = constant
					return null;
				}
			}
		}
		return cName;
	}
	
	public HashMap<String, List<CFGBlock>> getCfgMap() {
		return cfgMap;
	}

	public BlockReachingDefinitionGenerator getReachingDefGenerator() {
		return reachingDefGenerator;
	}
}
