package events;

import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import company.Batch;

import util.RandomGenerator;

import drugs.Drug;

public abstract class Event {
	
	/*
	 * @parameters_=default
	 * 
	 * string modelType =DDM(direct duration model);or BMM(batch manufacturing model)
	 * 
	 * drug
	 * 
	 * eventType
	 * eventName
	 * 
	 * startTime=-1
	 * duration=-1
	 * endTime=-1
	 * 
	 * Event next=null;//with Manu it means the CT it's manufacturing for; with CTs it means the nextCT
	 * boolean noPrevious=false;
	 * 
	 * cost=-1
	 * income=-1
	 * 
	 * String status; could be in-house, CMO or partner
	 * 
	 */
	
	
	protected Map<String, Object> parameters_;//parameters that used to generate deterministic model

	/*
	 * @stochastic_=default
	 * 
	 * startTime=-1
	 * endTime=-1
	 * ongoing=-1 //meaning at the start of event, the event has finished 0 time
	 * 
	 * boolean goAhead1=false	//previous CT is unsuccessful
	 * boolean goAhead2=false	//previous Manu is unsuccessful
	 * boolean goAhead3=false	//previous Ancillary is unsuccessful
	 * 
	 * triggered=false //for noPrevious=true events, this parameter should be turned to true once the tick==startTime.
	 * successful=false
	 * boolean ended=false //for all events, indicating it's ending
	 * 
	 * cost=-1
	 * income=-1
	 * 
	 * int totalCost=0
	 * 
	 */
	protected Map<String, Object> stochastic_;//parameters that used in stochastic optimisation
	
	
	
	//protected ArrayList<Event> previous_;
	
	//protected ArrayList<Event> next_;
	
	//constructor
	public Event(HashMap<String,Object> parameters){
		parameters_=parameters;
		this.stochastic_=new HashMap<String, Object>();
		/*
		this.setParameter("startTime", -1);
		this.setParameter("duration", -1);
		this.setParameter("endTime", -1);
		this.setParameter("noPrevious", false);
		this.setParameter("cost", -1);
		this.setParameter("income", -1);
		this.setParameter("TP", 1);
		*/
		
		this.setStochastic("startTime",		-1);
		this.setStochastic("endTime",		-1);
		this.setStochastic("ongoing",		-1);
		this.setStochastic("goAhead1",		false);
		this.setStochastic("goAhead2",		false);
		this.setStochastic("goAhead3",		false);
		this.setStochastic("triggered",		false);
		this.setStochastic("successful",	false);
		this.setStochastic("ended",			false);
		this.setStochastic("cost",			-1);
		//test//monitor total cost
		//this.setStochastic("totalCost", 0);
		//test//end
		FillData();
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
	
	public abstract void FillData();
	
	public abstract void RandomGeneration(); 
	
	public abstract void Triggering(int time);//decide in stochastic simulation how a event is triggered
	
	public abstract void Interrupted(int time);
	
	public abstract void Finishing(int time);//for CTs to check it's successful. for other events, simply put successful into their parameters
	
	public int CashFlow(){
		int cost=0;
		cost=(Integer)this.getParameter("cost");
		
		return -cost;
	}
	
	public int Progressing(int time){//return the cash flow of progressing
		int cashFlow=0;
		if((Boolean)this.getStochastic("triggered")&& !(Boolean)this.getStochastic("ended")){
			if((String)this.getParameter("eventType")=="Manu" && (String)this.getParameter("modelType")=="BMM"){
				cashFlow=((Manu)this).BMMProgressing(time);
			}
			else{
				int ongoing=(Integer)this.getStochastic("ongoing")+1;
				int duration=(Integer)this.getStochastic("duration");
				this.setStochastic("ongoing", ongoing);
				cashFlow=this.CashFlow();
				if(ongoing==duration){
					this.Finishing(time);
				}
			}
		}
		return cashFlow;
	}
	
	public void ResetStochastic(){
		this.setStochastic("startTime",		-1);
		this.setStochastic("endTime",		-1);
		this.setStochastic("ongoing",		-1);
		this.setStochastic("goAhead1",		false);
		this.setStochastic("goAhead2",		false);
		this.setStochastic("goAhead3",		false);
		this.setStochastic("triggered",		false);
		this.setStochastic("successful",	false);
		this.setStochastic("ended",			false);
		this.setStochastic("cost",			-1);
	}
}
