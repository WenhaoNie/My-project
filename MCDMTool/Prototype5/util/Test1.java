package util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import company.Batch;
import company.Company;
import company.ManuFacility;
import data.CTData;
import data.ManuData;
import drugs.Drug;
import drugs.DrugFactory;

import events.CT;
import events.Event;
import events.EventFactory;
import events.Manu;

public class Test1 {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		HashMap<String, Object> cmyPara=new HashMap<String, Object>();
		cmyPara.put("companyName", "testCompany");
		cmyPara.put("companyType", "own");
		cmyPara.put("manuFacilities", null);
		cmyPara.put("drugs", null);
		
		Company cmy=new Company(cmyPara);
		ArrayList<Drug> drugs=new ArrayList<Drug>();
		HashMap<String, Object> dPara1=new HashMap<String, Object>();
		HashMap<String, Object> dPara2=new HashMap<String, Object>();
		HashMap<String, Object> dPara3=new HashMap<String, Object>();
		

		dPara1.put("drugID", "A");
		dPara1.put("drugNumber", 1);
		dPara1.put("launchTime", 0);
		dPara1.put("company", cmy);
		
		dPara2.put("drugID", "B");
		dPara2.put("drugNumber", 2);
		dPara2.put("launchTime", 14);
		dPara2.put("company", cmy);

		dPara3.put("drugID", "C");
		dPara3.put("drugNumber", 3);
		dPara3.put("launchTime", 24);
		dPara3.put("company", cmy);

		drugs.add(DrugFactory.createDrug("medium", dPara1));
		drugs.add(DrugFactory.createDrug("niche", dPara2));
		drugs.add(DrugFactory.createDrug("BB", dPara3));

		cmy.setParameter("drugs", drugs);
		CT.data_=CTData.createDummyData();
		Manu.data_=ManuData.createData();
		String[] ctNames={"Dis", "LM", "PC", "IND", "PhaseI", "PhaseII", "PhaseIII", "FDA"};
		String[] manuNames={"PC", "PhaseI", "PhaseII", "PhaseIII"};
		for(Drug d:drugs){
			ArrayList<Event> cts=new ArrayList<Event>();
			ArrayList<Event> manus=new ArrayList<Event>();
			ArrayList<Event> events=new ArrayList<Event>();
			/*
			 * generate cts
			 */
			for(String ctName:ctNames){
				HashMap<String, Object> eventPara=new HashMap<String, Object>();
				eventPara.put("eventName", ctName);
				eventPara.put("eventType", "CT");
				eventPara.put("drug", d);
				if(ctName=="Dis"){
					eventPara.put("startTime", d.getParameter("launchTime"));
					eventPara.put("noPrevious", true);//events with no previous events
				}
				else{
					eventPara.put("noPrevious", false);
				}
				cts.add(EventFactory.createEvent("CT", eventPara));
			}
			/*
			 * generate manus
			 */
			for(String manuName:manuNames){
				HashMap<String, Object> eventPara=new HashMap<String, Object>();
				eventPara.put("modelType", "BMM");
				eventPara.put("eventName", manuName);
				eventPara.put("eventType", "Manu");
				eventPara.put("drug", d);
				eventPara.put("noPrevious", true);
				if(manuName=="PhaseIII")
					eventPara.put("manuType", "big");
				else
					eventPara.put("manuType", "small");
				manus.add(EventFactory.createEvent("Manu", eventPara));
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
			}
			/*
			 * add dependencies for cts
			 */
			for(int i=0;i<cts.size()-1;i++){
				cts.get(i).setParameter("next", cts.get(i+1));
			}
			
			/*
			 * add all events to drug's parameter
			 */
			for(Event e:cts){
				events.add(e);
			}
			for(Event e:manus){
				events.add(e);
			}
			d.setParameter("events", events);
			d.setParameter("CTs", cts);
			d.setParameter("Manus", manus);
		}

		
		
		ArrayList<Manu> allManus=new ArrayList<Manu>();
		for(Drug d:drugs){
			Scheduling.CTScheduling((ArrayList<Event>)d.getParameter("CTs"));
			allManus.addAll((ArrayList<Manu>)d.getParameter("Manus"));
		}
		
		/*
		 * now add facilities to company, and scheduling manus according to the availability of facilities
		 */
		HashMap<String, Object> facilityPara1=new HashMap<String, Object>();
		HashMap<String, Object> facilityPara2=new HashMap<String, Object>();
		HashMap<String, Object> facilityPara3=new HashMap<String, Object>();
		facilityPara1.put("company", cmy);
		facilityPara1.put("earliestAvailableTime", -1);
		facilityPara1.put("manuFor", new ArrayList<Manu>());
		facilityPara1.put("facilityID", "Alpha");
		facilityPara1.put("facilityType", "small");
		
		facilityPara2.put("company", cmy);
		facilityPara2.put("earliestAvailableTime", -1);
		facilityPara2.put("manuFor", new ArrayList<Manu>());
		facilityPara2.put("facilityID", "Beta");
		facilityPara2.put("facilityType", "big");
		
		facilityPara3.put("company", cmy);
		facilityPara3.put("earliestAvailableTime", -1);
		facilityPara3.put("manuFor", new ArrayList<Manu>());
		facilityPara3.put("facilityID", "Gamma");
		facilityPara3.put("facilityType", "small");
		
		ArrayList<ManuFacility> facilities=new ArrayList<ManuFacility>();
		facilities.add(new ManuFacility(facilityPara1));
		facilities.add(new ManuFacility(facilityPara2));
		facilities.add(new ManuFacility(facilityPara3));
		cmy.setParameter("manuFacilities", facilities);
		
		//Scheduling.ManuScheduling(allManus, cmy);
		
		/*
		 * print results
		 */
		System.out.println("\nNew results\n");
		for(Drug d:drugs){
			String cts="Drug "+(String)d.getParameter("drugID")+"'s CT timings are: \n";
			String manus="Drug "+(String)d.getParameter("drugID")+"'s Manu timings are: \n";
			for(int i=0;i<8;i++){
				CT c=((ArrayList<CT>)d.getParameter("CTs")).get(i);
				String lengthElement="\t"+(String)c.getParameter("eventName")+": "+(Integer)c.getParameter("startTime")+"-"+(Integer)c.getParameter("endTime")+"; \n";
				cts=cts+lengthElement;
			}
			System.out.println(cts);
			for(int i=0;i<4;i++){
				Manu m=((ArrayList<Manu>)d.getParameter("Manus")).get(i);
				String lengthElement="\tManu For "+(String)m.getParameter("eventName")+": "+(Integer)m.getParameter("startTime")+"-"+(Integer)m.getParameter("endTime")+"; \n";
				manus=manus+lengthElement;
			}
			System.out.println(manus);
			System.out.println();
		}
		for(ManuFacility mf:(ArrayList<ManuFacility>)cmy.getParameter("manuFacilities")){
			String mfs=(String)mf.getParameter("facilityType")+" manufacturing facility "+(String)mf.getParameter("facilityID")+"'s scheduling is: \n";
			for(Manu m:(ArrayList<Manu>)mf.getParameter("manuFor")){
				String element="\t"+(String)((Drug)m.getParameter("drug")).getParameter("drugID")+"_"+(String)m.getParameter("eventName")+": "+(Integer)m.getParameter("startTime")+"-"+(Integer)m.getParameter("endTime")+"; \n";
				mfs=mfs+element;
			}
			System.out.println(mfs);
			System.out.println();
		}
		
		/*
		 * putting all events into a list
		 */
		ArrayList<Event> allEvents=new ArrayList<Event>();
		for(Drug d:drugs){
			for(Event e:(ArrayList<Event>)d.getParameter("events")){
				//e.RandomGeneration();
				/*System.out.println("Drug "+d.getParameter("drugID")+"'s "+e.getParameter("eventType")
						+" "+e.getParameter("eventName")+"'s duration is "+e.getStochastic("duration"));
				 */
				allEvents.add(e);
			}
		}
		
		/*
		 * Stochastic running
		 */
		System.out.println("\nStochastic results\n");
		Scheduling.tickTockSimulation(allEvents);
		
		/*
		 * print results
		 */
		System.out.println();
		for(Drug d:drugs){
			String cts="Drug "+(String)d.getParameter("drugID")+"'s CT timings are: \n";
			String manus="Drug "+(String)d.getParameter("drugID")+"'s Manu timings are: \n";
			for(int i=0;i<8;i++){
				CT c=((ArrayList<CT>)d.getParameter("CTs")).get(i);
				String lengthElement="\t"+(String)c.getParameter("eventName")+": "+(Integer)c.getStochastic("startTime")+"-"+(Integer)c.getStochastic("endTime")+"; \n";
				cts=cts+lengthElement;
			}
			System.out.println(cts);
			for(int i=0;i<4;i++){
				Manu m=((ArrayList<Manu>)d.getParameter("Manus")).get(i);
				String lengthElement="\tManu For "+(String)m.getParameter("eventName")+": "+(Integer)m.getStochastic("startTime")+"-"+(Integer)m.getStochastic("endTime")+"; \n";
				manus=manus+lengthElement;
			}
			System.out.println(manus);
			System.out.println();
		}
		for(ManuFacility mf:(ArrayList<ManuFacility>)cmy.getParameter("manuFacilities")){
			String mfs=(String)mf.getParameter("facilityType")+" manufacturing facility "+(String)mf.getParameter("facilityID")+"'s scheduling is: \n";
			for(Manu m:(ArrayList<Manu>)mf.getStochastic("manuFor")){
				String element="\t"+(String)((Drug)m.getParameter("drug")).getParameter("drugID")+"_"+(String)m.getParameter("eventName")+": "+(Integer)m.getStochastic("startTime")+"-"+(Integer)m.getStochastic("endTime")+"; \n";
				mfs=mfs+element;
			}
			System.out.println(mfs);
			System.out.println();
		}
		
		/**
		 * test codes to check if the results are good
		 */
		for(Drug d:drugs){
			boolean drugSuccessful=(Boolean)d.getStochastic("successful");
			System.out.println("\n"+drugSuccessful);
			for(Event e:(ArrayList<Event>)d.getParameter("events")){
				int startTime=(Integer)e.getStochastic("startTime");
				int endTime=(Integer)e.getStochastic("endTime");
				int duration=(Integer)e.getParameter("duration");
				int ongoing=(Integer)e.getStochastic("endTime")-(Integer)e.getStochastic("startTime");
				boolean eventTriggered=(Boolean)e.getStochastic("triggered");
				boolean eventSuccessful=(Boolean)e.getStochastic("successful");
				if(!eventTriggered){
					System.out.println(e.getParameter("eventType")+" \tevent "+e.getParameter("eventName")+" \tof drug "+d.getParameter("drugID")+" didn't triggered");
				}
				else if(!eventSuccessful){
					/*
					 * show the batches for manu events
					 */
					if((String)e.getParameter("eventType")=="Manu"){
						System.out.println("Manu for "+e.getParameter("eventName")+"'s batches are: (successful batch number - "+e.getStochastic("successfulBatchesDone"));
						ArrayList<Batch> batches=(ArrayList<Batch>)e.getStochastic("batches");
						for(int i=0;i<batches.size();i++){
							Batch batch=batches.get(i);
							System.out.println("\tBatch number: "+batch.getStochastic("order")+"; length: "+batch.getParameter("batchLength")+"; successful: "+batch.getStochastic("isSuccessful"));
						}
					}
					System.out.println(e.getParameter("eventType")+" \tevent "+e.getParameter("eventName")+" \tof drug "+d.getParameter("drugID")+" not successful. \tIt starts at "+startTime+"\tand failed at \t"+endTime+", duration "+duration+", \tongoing "+ongoing);
				}
				else{
					/*
					 * show the batches for manu events
					 */
					if((String)e.getParameter("eventType")=="Manu"){
						System.out.println("Manu for "+e.getParameter("eventName")+"'s batches are: (successful batch number - "+e.getStochastic("successfulBatchesDone"));
						ArrayList<Batch> batches=(ArrayList<Batch>)e.getStochastic("batches");
						for(int i=0;i<batches.size();i++){
							Batch batch=batches.get(i);
							System.out.println("\tBatch number: "+batch.getStochastic("order")+"; length: "+batch.getParameter("batchLength")+"; successful: "+batch.getStochastic("isSuccessful"));
						}
					}
					System.out.println(e.getParameter("eventType")+" \tevent "+e.getParameter("eventName")+" \tof drug "+d.getParameter("drugID")+" ok. \t\t\tIt starts at "+startTime+"\tand ends at \t"+endTime+", duration "+duration+", \tongoing "+ongoing);
				}
			}
		}
	}
}
