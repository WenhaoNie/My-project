package events;

import java.util.ArrayList;
import java.util.HashMap;

import mcdmProblem.SolutionTranslator;

import drugs.Drug;


public class EventFactory {

	public static Event createEvent(String eventType,HashMap<String,Object> parameters){
		if(eventType.equalsIgnoreCase("CT")){
			parameters.put("eventType", "CT");
			return new CT(parameters);
		}
		else if(eventType.equalsIgnoreCase("Manu")){
			parameters.put("eventType", "Manu");
			return new Manu(parameters);
		}
		else if(eventType.equalsIgnoreCase("Ancillary")){
			parameters.put("eventType", "Ancillary");
			return new Ancillary(parameters);
		}
		else if(eventType.equalsIgnoreCase("Build")){
			parameters.put("eventType", "Build");
			return new Build(parameters);
		}
		else if(eventType.equalsIgnoreCase("PD")){
			parameters.put("eventType", "PD");
			return new PD(parameters);
		}
		else if(eventType.equalsIgnoreCase("Market")){
			parameters.put("eventType", "Market");
			return new Market(parameters);
		}
		else{
			System.out.println("eventType error!!"+eventType);
			return null;
		}
	}
	
	public static void AssembleEvents(Drug drug, int[] decisionVariable){//this method assembles events for drug given the decisionVariable
		/*
		 * translate manu strategy solutions
		 */
		ArrayList<HashMap> attributes=SolutionTranslator.TranslateManuStrat(decisionVariable);
		int drugNumber=(Integer)drug.getParameter("drugNumber");
		HashMap<String, Object> attribute=attributes.get(drugNumber-1);
		ArrayList<String> eventNames=(ArrayList<String>)attribute.get("eventNames");
		ArrayList<String> eventTypes=(ArrayList<String>)attribute.get("eventTypes");
		ArrayList<String> status	=(ArrayList<String>)attribute.get("status");
		int CMOMarketTime			=(Integer)attribute.get("CMOMarketTime");
		
		ArrayList<Event> cts		=new ArrayList<Event>();
		ArrayList<Event> manus		=new ArrayList<Event>();
		ArrayList<Event> ancillarys	=new ArrayList<Event>();
		ArrayList<Event> events		=new ArrayList<Event>();
		
		int manu_PC_Index=-1;
		int manu_PhaseIII_Index=-1;
		
		for(int i=0;i<eventNames.size();i++){
			HashMap<String, Object> eventPara=new HashMap<String, Object>();
			String eventName	=eventNames.get(i);
			String eventType	=eventTypes.get(i);
			String eventStatus	=status.get(i);
			
			eventPara.put("eventName",	eventName);
			eventPara.put("drug",		drug);
			eventPara.put("eventType",	eventType);
			eventPara.put("eventStatus",eventStatus);
			
			if(eventType=="CT"){
				if(eventName=="Dis"){
					eventPara.put("startTime",	drug.getParameter("launchTime"));
					eventPara.put("noPrevious",	true);
				}
				else{
					eventPara.put("noPrevious", false);
				}
				cts.add(createEvent(eventType, eventPara));
			}
			else if(eventType=="Market"){
				eventPara.put("noPrevious", false);
				/*
				 * split the timing now, but split the requirement and sales later in the constructor
				 */
				if(eventName=="Market_1")
					eventPara.put("duration", CMOMarketTime);
				else if(eventName=="Market_2"){
					String key=(String)drug.getParameter("drugType")+"_"+"Market_duration";
					int duration=(Integer)CT.data_.get(key)-CMOMarketTime;
					eventPara.put("duration", duration);
				}
				cts.add(createEvent(eventType, eventPara));
			}
			else if(eventType=="Manu"){
				eventPara.put("noPrevious", true);
				/*
				 * PC and PhaseIII manu has previous
				 */
				if(eventName=="PC" || eventName=="PhaseIII")
					eventPara.put("noPrevious", false);//
				
				eventPara.put("modelType", "BMM");
				if(eventName=="PhaseIII"
						|| eventName=="Market"
						|| eventName=="Market_1"
						|| eventName=="Market_2"){
					eventPara.put("manuType", "Big");
					/*
					 * split the timing now, but split the parallel batch number and handle the inventory problem later
					 */
					if(eventName=="Market"){
						String key=(String)drug.getParameter("drugType")+"_"+"Market_duration";
						eventPara.put("duration", (Integer)CT.data_.get(key));
					}
					else if(eventName=="Market_1")
						eventPara.put("duration", CMOMarketTime);
					else if(eventName=="Market_2"){
						String key=(String)drug.getParameter("drugType")+"_"+"Market_duration";
						int duration=(Integer)CT.data_.get(key)-CMOMarketTime;//notice here using CT's database instead of Manu's 
						eventPara.put("duration", duration);
					}
				}
				else
					eventPara.put("manuType", "Small");
				
				manus.add(createEvent("Manu", eventPara));
				if(eventName=="PC")
					manu_PC_Index=manus.size()-1;
				
				if(eventName=="PhaseIII")
					manu_PhaseIII_Index=manus.size()-1;
			}
			else{//ancillary events and PD events
				eventPara.put("noPrevious", false);
				if(eventStatus=="in-house"){
					if(eventName=="TechLab"){
						eventPara.put("manuType", "Small");
						eventPara.put("noPrevious",	true);
					}
					else if(eventName=="ScaleUp"){
						eventPara.put("manuType", "Big");
						eventPara.put("noPrevious", true);
					}
				}
				else if(eventStatus=="CMO"){
					if(eventName=="CMO_Preparation")
						eventPara.put("noPrevious", true);
					else if(eventName=="ScaleUp")
						eventPara.put("manuType", "Big");
				}
				ancillarys.add(createEvent(eventType, eventPara));
			}
		}
		
		/*
		 * add dependencies for manus and cts
		 */
		for(Event ct:cts){
			ct.setParameter("manu", null);
			for(Event manu:manus){
				if((String)ct.getParameter("eventName")==(String)manu.getParameter("eventName")){
					ct.setParameter("manu", manu);
					manu.setParameter("next", ct);
				}
			}
		}//
		/*
		 * add dependencies for cts
		 */
		
		for(int i=0;i<cts.size()-1;i++){
			cts.get(i).setParameter("next", cts.get(i+1));
		}//
		
		/*
		 * add dependencies for ancillarys and PDs
		 */
		Event smallManu			=manus.get(manu_PC_Index);
		Event bigManu			=manus.get(manu_PhaseIII_Index);
		Event techLab			=null;
		Event scaleUp			=null;
		Event CMO_Preparation	=null;
		Event techTransfer		=null;
		
		for(Event e:ancillarys){
			if((String)e.getParameter("eventName")=="TechLab")
				techLab			=e;
			else if((String)e.getParameter("eventName")=="ScaleUp")
				scaleUp			=e;
			else if((String)e.getParameter("eventName")=="CMO_Preparation")
				CMO_Preparation	=e;
			else if((String)e.getParameter("eventName")=="TechTransfer")
				techTransfer	=e;
			else
				System.out.println("Create Ancillary Events error!");
		}
		techLab.setParameter("next", smallManu);
		smallManu.setParameter("PD", techLab);
		scaleUp.setParameter("next", bigManu);
		bigManu.setParameter("PD", scaleUp);
		if(CMO_Preparation!=null && techTransfer!=null){
			techTransfer.setParameter("next", scaleUp);
			CMO_Preparation.setParameter("next", techTransfer);
		}//
		
		
		
		
		/*
		 * add all events to drug's parameter
		 */
		for(Event e:cts){
			events.add(e);
		}
		for(Event e:manus){
			events.add(e);
		}
		for(Event e:ancillarys){
			events.add(e);
		}
		drug.setParameter("events", events);
		drug.setParameter("CTs", cts);
		drug.setParameter("Ancillarys", ancillarys);
		drug.setParameter("Manus", manus);
		
		//}
		
	}
}
