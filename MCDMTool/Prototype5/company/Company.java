package company;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import mcdmProblem.CMOvsInHouseProblem;

public class Company {

	//test//if company already owns some facilities
	public static ArrayList<ManuFacility> mf=new ArrayList<ManuFacility>();
	//test//end
	
	
	/*
	 * @parameters_:default
	 * companyName	=null;
	 * companyType	=null;(could be in_house, CMO or partner)
	 * drugs			=ArrayList<Drug> null;
	 * allEvents		=ArrayList<Event> null;
	 * allCTs			=ArrayList<Event> null;
	 * manuEquivalents	=ArrayList<Event> null; //manu and process development events
	 */
	protected Map<String, Object> parameters_;
	
	/*
	 * @stochastic_:default
	 * ArrayList<ManuFacility> manuFacilities=null;
	 */
	
	protected Map<String, Object> stochastic_;
	
	public Company(HashMap<String, Object> parameters){
		parameters_=parameters;
		this.stochastic_=new HashMap<String, Object>();
		
		//for every Monte Carlo simulation, give the company a virtual CMO facility
		
		HashMap<String, Object> facilityPara=new HashMap<String, Object>();
		facilityPara.put("company", this);
		facilityPara.put("openTime",0);
		facilityPara.put("facilityType", "Big");
		facilityPara.put("facilityID", "CMOFacility");
		
		//test//if company already owns some facilities
		mf= CMOvsInHouseProblem.MakeFacilities(this);
		//test//end
		
		//ArrayList<ManuFacility> mf=new ArrayList<ManuFacility>();
		mf.add(new ManuFacility(facilityPara));
		
		this.setStochastic("manuFacilities", mf);
	}
	
	public void ResetStochastic(){
		HashMap<String, Object> facilityPara=new HashMap<String, Object>();
		facilityPara.put("company", this);
		facilityPara.put("openTime",0);
		facilityPara.put("facilityType", "Big");
		facilityPara.put("facilityID", "CMOFacility");
		
		mf=CMOvsInHouseProblem.MakeFacilities(this);
		mf.add(new ManuFacility(facilityPara));
		
		this.setStochastic("manuFacilities", mf);
	}
	
	//accessors
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
