package company;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import events.Event;
import events.Manu;

public class ManuFacility {
	
	/*
	 * @parameters_:default
	 * company		=null;
	 * facilityID	=null;
	 * facilityType	=null;
	 * manuFor		=ArrayList<Event> null;//including process development and manufacturing event
	 * int earliestAvailableTime=-1;
	 * int openTime; //the time it is built
	 */
	//protected Map<String, Object> parameters_; //facility doesn't need deterministic parameters under the new planning format
	
	/*
	 * @stochastic_: default
	 * boolean isOccupied		=false;
	 * ArrayList<Event> manuFor	=null;//including process development and manufacturing event			
	 */
	
	protected Map<String, Object> parameters_;
	protected Map<String, Object> stochastic_;
	
	public ManuFacility(HashMap<String, Object> parameters){
		parameters_=parameters;
		this.stochastic_=new HashMap<String, Object>();
		this.setStochastic("manuFor", new ArrayList<Event>());
		this.setStochastic("isOccupied", false);
	}
	
	public void addManu(Event manuEquivalent){
		if(manuEquivalent!=null){
			if((String)this.getParameter("facilityID")!="CMOFacility"){
				this.setStochastic("isOccupied", true);
			}
			else{
				this.setStochastic("isOccupied", false);
			}
			((ArrayList<Event>)this.getStochastic("manuFor")).add(manuEquivalent);
			if((String)manuEquivalent.getParameter("eventType")=="Manu" && (String)manuEquivalent.getParameter("modelType")=="BMM"){
				this.setStochastic("batchLength", (Integer)manuEquivalent.getParameter("batchLength"));
				//this.setStochastic("batchNumber", (Integer)manuEquivalent.getParameter("batchNumber"));
				this.setStochastic("batchUnitCost", (Integer)manuEquivalent.getParameter("batchUnitCost"));
				this.setStochastic("batchSuccessRate", (Double)manuEquivalent.getParameter("batchSuccessRate"));
			}
		}
	}
	
	public Batch startNewBatch(Manu manu){//add a new batch to manufacture
		HashMap<String, Object> parameters=new HashMap<String, Object>();
		parameters.put("batchLength", this.getStochastic("batchLength"));
		parameters.put("batchUnitCost", this.getStochastic("batchUnitCost"));
		parameters.put("batchSuccessRate", this.getStochastic("batchSuccessRate"));
		return new Batch(parameters, manu);
	}
	
	
	public void setParameter(String name, Object value){
		parameters_.put(name, value);
	}
	
	public Object getParameter(String name){
		return parameters_.get(name);
	}


	public void setStochastic(String name, Object value){
		stochastic_.put(name, value);
	}
	
	public Object getStochastic(String name){
		return stochastic_.get(name);
	}
}
