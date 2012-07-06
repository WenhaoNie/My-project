package events;

import java.util.ArrayList;
import java.util.HashMap;

import util.RandomGenerator;

import drugs.Drug;


public class CT extends Event {

	/*
	 * @parameters_: default
	 * manu		=null 
	 * double TP;
	 * 
	 * @stochastic_: default
	 * 
	 * 
	 */
	
	
	public CT(HashMap<String, Object> parameters) {
		super(parameters);
	}
	
	public static HashMap data_;//deterministic input data
	
	@Override
	public void FillData(){
		Drug d=(Drug)this.getParameter("drug");
		String key=(String)(d.getParameter("drugType"))+"_"+(String)(this.getParameter("eventName"))+"_";
		
		if((String)this.getParameter("eventName")=="Market"
				|| (String)this.getParameter("eventName")=="Market_1"
				|| (String)this.getParameter("eventName")=="Market_2"){
			
			key=(String)(d.getParameter("drugType"))+"_Market";
			
			//do nothing atm, the duration is already assigned to it
			
		}
		else{
			this.setParameter("duration", data_.get(key+"duration"));
			this.setParameter("cost", data_.get(key+"cost"));
			this.setParameter("TP", data_.get(key+"TP"));
			
		}
	}
	

	
	@Override
	public void Triggering(int currentTime){
		/*
		 * here a judgement of whether CT requires manu should be made.
		 */
		if(!(Boolean)this.getStochastic("triggered")
				&& (Boolean)this.getStochastic("goAhead1")){
			if((this.getParameter("manu")==null)
					||(Boolean)this.getStochastic("goAhead2")){
				this.setStochastic("triggered", true);
				this.setStochastic("startTime", currentTime);
				double mean=(double)(Integer)this.getParameter("duration");
				double variance=mean/10;
				int duration=RandomGenerator.getIntGaussian(mean, variance);
				this.setStochastic("duration", duration);
				this.setStochastic("ongoing", 0);//when triggered, it no longer is -1;
			}
		}
	}
	
	@Override
	public void Finishing(int time){
		this.setStochastic("ended", true);
		this.setStochastic("endTime", time);
		double tp=(Double)this.getParameter("TP");
		boolean successful=RandomGenerator.getBoolean(tp);
		this.setStochastic("successful", successful);
		Event event=(Event)this.getParameter("next");
		if((Boolean)this.getStochastic("successful")){
			//test//record FDA approval time
			if((String)this.getParameter("eventName")=="FDA"){
				Drug drug=(Drug)this.getParameter("drug");
				drug.setStochastic("FDA_Approval", time);
			}
			//test//end
			
			if(event!=null){
				event.setStochastic("goAhead1", true);
			}
		}
		else{		
			/*
			 * kill all other ongoing events
			 */

			Drug drug=(Drug)this.getParameter("drug");
			drug.setStochastic("successful", false);
			//test//start
			//System.out.println("Drug "+drug.getParameter("drugID")+"'s "
					//+this.getParameter("eventName")+" has failed.");
			//test//end
			/*
			 * kill all ongoing events
			 */
			for(Event everyEvent:(ArrayList<Event>)drug.getParameter("events")){
				if((Boolean)everyEvent.getStochastic("triggered")&&!(Boolean)everyEvent.getStochastic("ended")){
					everyEvent.Interrupted(time);
				}
			}
		}
	}
	
	@Override
	public void Interrupted(int time){
		//do nothing for CTs for now
	}

	@Override
	public void RandomGeneration() {
		double duration=(double)(Integer)this.getParameter("duration");
		double variance=duration/10;//should be decided by duration
		this.setStochastic("duration", RandomGenerator.getIntGaussian(duration, variance));
	}
}
