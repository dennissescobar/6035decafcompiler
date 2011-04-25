package decaf.dataflow.cfg;

import java.util.ArrayList;
import java.util.List;

import decaf.codegen.flatir.LIRStatement;

public class CFGBlock {
	private LIRStatement leader;
	private List<LIRStatement> statements;
	private List<CFGBlock> predecessors;
	private List<CFGBlock> successors;
	private int index;
	private String methodName;
	
	public CFGBlock(String methodName) {
		this.methodName = methodName;
		this.leader = null;
		this.statements = new ArrayList<LIRStatement>();
		this.predecessors = new ArrayList<CFGBlock>();
		this.successors = new ArrayList<CFGBlock>();
	}

	public LIRStatement getLeader() {
		return leader;
	}

	public void setLeader(LIRStatement leader) {
		this.leader = leader;
	}

	public List<LIRStatement> getStatements() {
		return statements;
	}

	public void setStatements(List<LIRStatement> statements) {
		this.statements = statements;
	}
	
	public void addStatement(LIRStatement stmt) {
		this.statements.add(stmt);
	}

	public List<CFGBlock> getPredecessors() {
		return predecessors;
	}

	public void setPredecessors(List<CFGBlock> predecessors) {
		this.predecessors = predecessors;
	}
	
	public void addPredecessor(CFGBlock block) {
		this.predecessors.add(block);
	}

	public List<CFGBlock> getSuccessors() {
		return successors;
	}

	public void setSuccessors(List<CFGBlock> successors) {
		this.successors = successors;
	}
	
	public void addSuccessor(CFGBlock block) {
		this.successors.add(block);
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public int getIndex() {
		return index;
	}
	
	@Override
	public String toString() {
		String rtn = "ID:" + index + "; SUCC:{";
		for (CFGBlock cfg: this.successors) {
			rtn += cfg.getIndex() + ",";
		}
		
		if (rtn.charAt(rtn.length()-1) == ',') {
			rtn = rtn.substring(0, rtn.length() -1);
		}
		
		rtn += "}";
		
		rtn += "; PRE:{";
		for (CFGBlock cfg: this.predecessors) {
			rtn += cfg.getIndex() + ",";
		}
		
		if (rtn.charAt(rtn.length()-1) == ',') {
			rtn = rtn.substring(0, rtn.length() -1);
		}
		
		rtn += "}";
		
		for (LIRStatement stmt: this.statements) {
			rtn += "\n\t" + stmt;
		}
		
		return rtn;
	}
	
	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	@Override
	public int hashCode() {
		return this.index + 17*this.methodName.hashCode();
	}
}