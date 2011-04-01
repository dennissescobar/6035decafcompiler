package decaf.dataflow.global;

import decaf.codegen.flattener.ProgramFlattener;
import decaf.dataflow.cfg.CFGBuilder;

public class GlobalOptimizer {
	private GlobalCSEOptimizer cse;

	public GlobalOptimizer(CFGBuilder cb, ProgramFlattener pf) {
		cse = new GlobalCSEOptimizer(cb.getCfgMap(), pf);
	}
	
	public void optimizeBlocks(boolean[] opts) {
		if(opts[1]) { // CSE
			cse.performGlobalCSE();
		} else if(opts[2]) { // CP
			//perform CP here
		}
	}
	
	public GlobalCSEOptimizer getCse() {
		return cse;
	}

	public void setCse(GlobalCSEOptimizer cse) {
		this.cse = cse;
	}
}
