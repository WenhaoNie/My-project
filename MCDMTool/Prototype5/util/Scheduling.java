package util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import company.Company;
import company.ManuFacility;
import drugs.Drug;

import events.Ancillary;
import events.CT;
import events.Event;
import events.Manu;

public class Scheduling {

	public static final int ALL_YEARS=20;
	public static final int ALL_TIME=1040;//20 years*52 weeks
	public static final double DISCOUNT_RATE=0.1;
	
	
	/* deterministic planning for CTs and manus for ONE drug
	 * should be the first method to call in scheduling process
	 * also works for aggressive and conservative development pathways
	 */
	public static void CTScheduling(ArrayList<Event> events){
		Event currentCT=null;
		int startTime=-1;
		for(Event ct:events){
			if((Integer)ct.getParameter("startTime")!=-1){
				currentCT=ct;
				startTime=(Integer)ct.getParameter("startTime");
				break;
			}
		}
		if(startTime==-1)
			System.out.println("Start time not initiated!");
		Forward_With_Manus(currentCT, startTime);
	}
	
	/* deterministic planning for manufacturing events for all drugs
	 * before using this method, the start and end time for all manus 
	 * should be determined by their correponding cts.
	 */

	public static void ManuScheduling(ArrayList<Event> manuEquivalents, Company cmy){
		ArrayList<ManuFacility> manuFacilities=(ArrayList<ManuFacility>)cmy.getParameter("manuFacilities");
		while(manuEquivalents.size()!=0){
			Collections.sort(manuEquivalents, new StartTimeComparator());
			Event m=manuEquivalents.get(0);
			String manuType=(String)m.getParameter("manuType");
			boolean arranged=false;
			for(int i=0;i<manuFacilities.size();i++){
				/*
				 * check if the facility is capable of manufacturing for this manu event
				 */
				if(manuType==manuFacilities.get(i).getParameter("facilityType")){
					/*
					 * check if the facility is available
					 */
					boolean available=true;
					for(Event existingManu:(ArrayList<Manu>)manuFacilities.get(i).getParameter("manuFor")){
						if(OverlapJudge.isOverlapped(m, existingManu)){
							available=false;
							break;
						}
					}
					/*
					 * if it is also available, put this manu into the facility's manuFor parameter
					 */
					if(available){
						manuFacilities.get(i).addManu(m, false);
						arranged=true;
						break;
					}
				}
			}
			/*
			 * if there's no space available for this manu it has to wait 
			 */
			if(!arranged){
				ArrayList<ManuFacility> suitableFacilities=new ArrayList<ManuFacility>();
				for(int i=0;i<manuFacilities.size();i++){
				/*
				 * check if the facility is capable of manufacturing for this manu event
				 */
					if(manuType==manuFacilities.get(i).getParameter("facilityType"))
						suitableFacilities.add(manuFacilities.get(i));
				}
				Collections.sort(suitableFacilities, new EarliestTimeComparator());
				int earliestAvailableTime=(Integer)suitableFacilities.get(0).getParameter("earliestAvailableTime");
				m.setParameter("startTime", earliestAvailableTime);
				int duration=-1;
				if((String)m.getParameter("eventType")=="Manu"&&(String)m.getParameter("modelType")=="BMM"){
					duration=(Integer)m.getParameter("actualDuration"); //actualDuration is used here
				}
				else{
					duration=(Integer)m.getParameter("duration");
				}
				m.setParameter("endTime", earliestAvailableTime+duration);
				suitableFacilities.get(0).addManu(m, false);
				/*
				 * adjust all cts and manus according to this delay
				 */
				Forward_With_Manus((Event)m.getParameter("next"), earliestAvailableTime+duration);
			}
			/*
			 * remove the current manu from unarranged manus
			 */
			manuEquivalents.remove(0);
		}
	}
	
	/* calculation of start times working forwards
	 * RECURSIVE
	 */
	public static void Forward(Event event, int startTime){
		event.setParameter("startTime", startTime);
		int endTime=(Integer)event.getParameter("duration")+startTime;
		event.setParameter("endTime", endTime);
		if(event.getParameter("next")!=null)
			Forward((Event)event.getParameter("next"),endTime);
	}
	
	/* calculation of start times working forwards, also calculate timing with manus
	 * RECURSIVE
	 */
	public static void Forward_With_Manus(Event event, int startTime){
		event.setParameter("startTime", startTime);
		if((Integer)event.getParameter("duration")!=null){
			int endTime=(Integer)event.getParameter("duration")+startTime;
			event.setParameter("endTime", endTime);
			if(event.getParameter("next")!=null)
				Forward_With_Manus((Event)event.getParameter("next"),endTime);
		}
		CTToManu(event);
	}
	
	/*
	 * knowing the timing for manu -> calculate the timing for ct
	 */
	public static void ManuToCT(Manu m){
		if(m.getParameter("ct")!=null){
			int endTimeOfManu=(Integer)m.getParameter("endTime");
			if((String)m.getParameter("modelType")=="BMM"){
				endTimeOfManu=(Integer)m.getParameter("startTime")+(Integer)m.getParameter("duration");
			}
			int duration=(Integer)((CT)m.getParameter("ct")).getParameter("duration");
			((CT)m.getParameter("ct")).setParameter("startTime", endTimeOfManu);
			((CT)m.getParameter("ct")).setParameter("endTime", endTimeOfManu+duration);
		}
	}
	
	/*
	 * knowing the timing for ct -> calculate the timing for manu
	 */
	public static void CTToManu(Event c){
		if(c.getParameter("manu")!=null){
			Manu manu=(Manu)c.getParameter("manu");
			int startTimeOfCT=(Integer)c.getParameter("startTime");
			int ctLead=(Integer)manu.getParameter("ctLead");
			int startTime=startTimeOfCT-ctLead;
			manu.setParameter("startTime", startTime);
			/*
			 * there is no need to calculate end time for manu
			 
			if((String)manu.getParameter("modelType")=="BMM"){
				manu.setParameter("endTime", startTime+(Integer)manu.getParameter("actualDuration"));
			}
			else{
				manu.setParameter("endTime", startTimeOfCT);
			}
			*/
			
			ManuToPD(manu);
		}
	}
	
	/*
	 * knowing the timing for manu -> calculate the timing for process development
	 */
	public static void ManuToPD(Event c){
		if(c.getParameter("PD")!=null){
			Ancillary pd=(Ancillary)c.getParameter("PD");
			int startTimeOfManu=(Integer)c.getParameter("startTime");
			int duration=(Integer)pd.getParameter("duration");
			pd.setParameter("endTime", startTimeOfManu);
			pd.setParameter("startTime", startTimeOfManu-duration);
			if((String)c.getParameter("eventStatus")=="CMO")
				ScaleUpToCMO(pd);
		}
	}
	
	/*
	 * knowing the timing for ScaleUp, if CMO is the option, calculate timing for techTransfer and CMO_Preparation
	 */
	public static void ScaleUpToCMO(Event scaleUp){
		if((String)scaleUp.getParameter("eventStatus")=="CMO"){
			Drug drug=(Drug)scaleUp.getParameter("drug");
			ArrayList<Event> ancillarys=(ArrayList<Event>)drug.getParameter("Ancillarys");
			Event CMO_Preparation	=null;
			Event techTransfer		=null;
			for(Event e:ancillarys){
				if((String)e.getParameter("eventName")=="CMO_Preparation")
					CMO_Preparation=e;
				else if((String)e.getParameter("eventName")=="TechTransfer")
					techTransfer=e;
			}
			techTransfer.setParameter("endTime", (Integer)scaleUp.getParameter("startTime"));
			techTransfer.setParameter("startTime", (Integer)techTransfer.getParameter("endTime")-(Integer)techTransfer.getParameter("duration"));
			CMO_Preparation.setParameter("endTime", (Integer)techTransfer.getParameter("startTime"));
			CMO_Preparation.setParameter("startTime", (Integer)CMO_Preparation.getParameter("endTime")-(Integer)CMO_Preparation.getParameter("duration"));
		}
	}
	
	
	public static void AncillaryScheduling(ArrayList<Ancillary> ancs){
		
	}
	
	
	
	//tickTock simulation for stochastic running
	public static double tickTockSimulation(Company company){
		ArrayList<Event> events	=(ArrayList<Event>)company.getParameter("allEvents");

		
		
		/*
		 * result = NPV
		 */
		double NPV				=0;
		double[] cashFlow		=new double[ALL_TIME];
		double[] annualCashFlow	=new double[ALL_YEARS];
		
		for(int currentTime=0; currentTime<ALL_TIME;currentTime++){// current time= the tick
			//test//calculate facility idle only
			
			ArrayList<ManuFacility> mfs=(ArrayList<ManuFacility>)company.getStochastic("manuFacilities");
			for(ManuFacility mf:mfs){
				if((String)mf.getParameter("facilityID")!="CMOFacility" 
						&&(String)mf.getParameter("facilityType")=="Big"){
					if(!(Boolean)mf.getStochastic("isOccupied")){
						Manu.Time_Of_Idle=Manu.Time_Of_Idle+1;
					}
				}
			}
			
			//test//end
			
			
			/*
			 * events progressing and finishing
			 */
			for(Event event:events){
				cashFlow[currentTime]=cashFlow[currentTime]+(double)event.Progressing(currentTime);
			}
			
			/*
			 * triggering events
			 */
			for(Event event:events){
				if((Boolean)event.getParameter("noPrevious")&&
						(Integer)event.getParameter("startTime")==currentTime){
					event.setStochastic("goAhead1", true);
					event.setStochastic("goAhead2", true);
					event.setStochastic("goAhead3", true);
				}
				Drug d=(Drug)event.getParameter("drug");
				if(d==null || (Boolean)d.getStochastic("successful")){
					event.Triggering(currentTime);
				}
			}
		}
		
		//convert weekly cashflow into annual cash flow and calculate NPV
		for(int i=0;i<ALL_YEARS;i++){
			for(int j=0;j<52;j++){//52 weeks in one year
				annualCashFlow[i]=annualCashFlow[i]+cashFlow[i*52+j];

			}
			//test//start
			//System.out.println("The "+(i+1)+"'s \tyear's cash flow is "+annualCashFlow[i]/1000);
			//test//end
			
			NPV=NPV+annualCashFlow[i]/Math.pow(1+DISCOUNT_RATE, (double)i);
		}
		
		//Test3.printObjectivesToFile("CashFlows", annualCashFlow);
		
		//test//record cashflow
		Test3.cashFlow=cashFlow;
		//test//end
		
		
		return NPV;
	}
	
	
	
}
