package edu.uiuc.zenvisage.zqlcomplete.querygraph;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;

import edu.uiuc.zenvisage.data.remotedb.SQLQueryExecutor;
import edu.uiuc.zenvisage.data.remotedb.VisualComponentList;
import edu.uiuc.zenvisage.zqlcomplete.executor.Processe;
import edu.uiuc.zenvisage.zqlcomplete.executor.ZQLExecutor;
import edu.uiuc.zenvisage.zqlcomplete.executor.ZQLRow;
import edu.uiuc.zenvisage.zqlcomplete.executor.ZQLRowResult;
import edu.uiuc.zenvisage.zqlcomplete.executor.ZQLTableResult;
import edu.uiuc.zenvisage.zqlcomplete.querygraph.QueryNode.State;

/**
 * @author Edward Xue
 * ProcessNodes are nodes that contains the process/task component
 */
public class ProcessNode extends QueryNode {

	private Processe process;
	
	public Processe getProcess() {
		return process;
	}

	public void setProcess(Processe process) {
		this.process = process;
	}

	public ProcessNode(Processe process_) {
		super();
		process = process_;
	}
	
	public ProcessNode(Processe process_, LookUpTable table) {
		super(table);
		this.process = process_;
	}
	
	
	@Override
	public void execute() {
		// MAKE this argument something for VCNode only! in constructor!
		if (isBlocked()) {
			this.state = State.BLOCKED;
			return;
		}
		
		this.state = State.RUNNING;
		AxisVariableScores axisVariableScores = executeProcess();

		// mock 
	}
	
	public AxisVariableScores executeProcess() {
		AxisVariableScores axisVariableScores = null;
		System.out.println(process.getMethod());
		// TODO: handle different types of D, T, and R
		
		// TODO: Handling "dissimilar" as method input for now
		// probably should be parsed to D?
		if (process.getMethod().toLowerCase().equals("similar") || process.getMethod().toLowerCase().equals("dissimilar") || process.getMethod().equals("D")) {
			axisVariableScores = executeDMethod();
		}
		if (process.getMethod().equals("T")) {
			axisVariableScores = executeTMethod();
		}
		if (process.getMethod().equals("R")){
			axisVariableScores = executeRMethod();
		}
		if (axisVariableScores == null) {
			return null;
		}
		
		axisVariableScores = filterScores(axisVariableScores);
		System.out.println("Process information:");
		System.out.println(process.getVariables());
		System.out.println(process.getArguments());
		// Every variable of a process statement should have a corresponding list of variables in axisVariableScores
		for (int i = 0; i < process.getVariables().size(); i++) {
			String varName = process.getVariables().get(i);
			// get the axivariable in the z column, 
			
		    List<String> values = axisVariableScores.getAxisvars().get(i);
		    
		    //TODO fix state to actual axis variable
			AxisVariable axisVar = new AxisVariable("state", values);
			lookuptable.put(varName, axisVar);
		}
		return axisVariableScores;
	}	
	
	private AxisVariableScores executeDMethod() {
		DEuclidean d = new DEuclidean();
		
		// Cannot process D on this incorrect input
		if (process.getArguments().size() != 2) {
			// TODO Just returning for now
			return null;
		}
		
		String name1 = process.getArguments().get(0);
		String name2 = process.getArguments().get(1);
		// Basic user error checking
		if(name1.equals("") || name2.equals("")){
			return null;
		}
		
		Object object1 = lookuptable.get(name1);
		if (! (object1 instanceof VisualComponentList)) {
			return null;
		}
		Object object2 = lookuptable.get(name2);
		if (! (object2 instanceof VisualComponentList)) {
			return null;
		}
		
		VisualComponentList f1 = (VisualComponentList) object1;
		VisualComponentList f2 = (VisualComponentList) object2;
		
		// axis: eg v1
		System.out.println(process.getAxis());
	
		//Tarique: I changed it to list of lists. If there are two lists we need to take
		// the cross-product for comparisons across f1,f2; for one list we should do the point-wise comparison across f1 and f2.
		// for multiple values in one list -- we take the cross-product, but compare point-wise if there is one list.
		
		//We need to fix the next few lines, after we have the parsing working.
		List<List<AxisVariable>> something = new ArrayList<List<AxisVariable>>();
		List<AxisVariable> firstAxisVarible =  new ArrayList<AxisVariable>();
		firstAxisVarible.add(new AxisVariable("aa","aa", new ArrayList<>())); //to fix
		List<AxisVariable> secondAxisVarible =  new ArrayList<AxisVariable>();
		something.add(firstAxisVarible);
		
		
		return d.execute(f1, f2, something);		

	}
	
	private AxisVariableScores executeTMethod() {
		TIncreasingness t = new TIncreasingness();
		
		if (process.getArguments().size() != 1) {
			return null;
		}
		String name1 = process.getArguments().get(0);
		
		if(name1.equals("")) {
			return null;
		}
		Object object1 = lookuptable.get(name1);
		if (! (object1 instanceof VisualComponentList)) {
			return null;
		}
		VisualComponentList f1 = (VisualComponentList) object1;
		
		//TO fix
		List<AxisVariable> something = new ArrayList<AxisVariable>();
		
		return t.execute(f1, something);
	}
	
	// TODO: complete
	private AxisVariableScores executeRMethod() {
		//R(k,v,f)
		//compute set of k-representative viz (int)
		// axisvar = v (string? one axisvar?)
		// visualcomponentlist = f
		return null;
		
	}
	private AxisVariableScores filterScores(AxisVariableScores axisVariableScores) {
		if (process.getMethod().toLowerCase().equals("dissimilar") || process.getMetric().equals("argmax")) {
			ArgMaxSortFilterPrimitive argmaxFilter = new ArgMaxSortFilterPrimitive();
			// TODO: count is stored as? "10"? "k=10"?
			axisVariableScores = argmaxFilter.execute(axisVariableScores, Integer.parseInt(process.getCount()));
		}
		if (process.getMethod().toLowerCase().equals("similar") || process.getMetric().equals("argmin")) {
			ArgMinSortFilterPrimitive argminFilter = new ArgMinSortFilterPrimitive();
			axisVariableScores = argminFilter.execute(axisVariableScores, Integer.parseInt(process.getCount()));
		}
		if (process.getMetric().equals("argany")) {
			ArgAnySortFilterPrimitive arganyFilter = new ArgAnySortFilterPrimitive();
			axisVariableScores = arganyFilter.execute(axisVariableScores, Integer.parseInt(process.getCount()));
			
		}
		return axisVariableScores;
	}	
	
	@Override
	public ZQLRow buildRowFromNode() {
		ZQLRow result = new ZQLRow(null, null, null, null, null);
		result.setProcesse(process);
		return result;
	}

}
