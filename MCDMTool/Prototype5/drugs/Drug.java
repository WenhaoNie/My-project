package drugs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import events.Event;


public abstract class Drug{

	/*
	 * @parameters_: default
	 * company		=null
	 * drugID		=null
	 * drugNumber	=null
	 * drugType		=null
	 * launchTime	=-1;
	 * ArrayList<Event>	events	=null;
	 * ArrayList<CT> CTs		=null;
	 * ArrayList<Manu> Manus	=null;
	 * ArrayList<Ancillary> Ancillarys=null;
	 */
	protected Map<String, Object> parameters_;//parameters can include many things in deterministic forms
	
	/*
	 * @stochastic_: default
	 * successful	=true
	 * int timeToMarket=-1
	 * int FDA_Approval=-1
	 */
	
	protected Map<String, Object> stochastic_;//parameters can include many things in stochastic forms

	//constructor
	public Drug(HashMap<String,Object> parameters){
		parameters_=parameters;
		/*
		this.setParameter("launchTime", -1);
		*/
		this.stochastic_=new HashMap<String, Object>();
		this.setStochastic("successful", true);
		//test//record time to market and FDA approval time
		this.setStochastic("timeToMarket", -1);
		this.setStochastic("FDA_Approval", -1);
		//test//end
	}
	
	//accessor
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
	
	public void ResetStochastic(){
		this.setStochastic("successful", true);
		this.setStochastic("timeToMarket", -1);
	}
	
	public abstract Event createEvent(String eventType, String eventName);
}
