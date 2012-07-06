package util;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

import jmetal.util.Configuration;

import mcdmProblem.CMOvsInHouseProblem;
import mcdmProblem.MonteCarlo;

import company.Company;
import company.CompanyFactory;
import company.ManuFacility;
import data.AncillaryData;
import data.CTData;
import data.ManuData;
import data.MarketData;
import drugs.Drug;
import drugs.DrugFactory;
import events.Ancillary;
import events.CT;
import events.Event;
import events.Manu;
import events.Market;
import geneticAlgorithm.CMOvsInHouse_main;

public class Test3 {

	//test//record cashflow
	public static double[] cashFlow;
	//test//end
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		CT.data_		=CTData.createData();
		Manu.data_		=ManuData.createData();
		Ancillary.data_	=AncillaryData.createData();
		Market.data_	=MarketData.createData();
		//test//create 0 market sales
		createFakeData();
		//test//end
		
		CMOvsInHouseProblem.portfolioComposition=3;//all BB
		CMOvsInHouse_main.SuccessPortfolio();
		CMOvsInHouse_main.applyScenario(4);
		CMOvsInHouseProblem.numOfFacility=5;
		
		int[] decisionVariables=new int[23];
		for(int i=0;i<21;i++){
			decisionVariables[i]=0;
		}
		/*
		 * portfolio selections
		 * 1,2,3,4,5
		 */
		for(int i=0;i<5;i++){
			decisionVariables[i]=i+1;
		}//
				
		/*
		 * make up 5 launch times
		 * starts at 0, 52 weeks, 104 weeks, 208 weeks and 390 weeks
		 * so decision variables should be
		 * 			 0, 2, 4, 8, 15
		 */
		decisionVariables[5]=	0;
		decisionVariables[6]=	0;
		decisionVariables[7]=	0;
		decisionVariables[8]=	0;
		decisionVariables[9]=	0;
		//
		
		/*
		 * manu strategy
		 * 0, 1, 3, 5, 6
		 * 
		 */
		decisionVariables[10]=	0;
		decisionVariables[11]=	0;
		decisionVariables[12]=	0;
		decisionVariables[13]=	0;
		decisionVariables[14]=	0;
		//
		
		
		/*
		 * makeup some build manufacturing strategies to test  
		 * build two small facilities and four big facilities
		 */
		decisionVariables[15]=	-1;
		decisionVariables[16]=	0;
		decisionVariables[17]=	2;
		
		decisionVariables[18]=	-1;
		decisionVariables[19]=	-1;
		decisionVariables[20]=	-1;
		decisionVariables[21]=	-1;
		decisionVariables[22]=	-1;
		//
		


		Company company=CompanyFactory.createCompany(decisionVariables);
		
		DrugFactory.AssembleDrugs(company, decisionVariables);
		
		
		
		
		ArrayList<Drug> drugs=(ArrayList<Drug>)company.getParameter("drugs");
		for(Drug d:drugs){
			ArrayList<Event> cts=(ArrayList<Event>)d.getParameter("CTs");
			Scheduling.CTScheduling(cts);
		}
		ArrayList<Event> manuEquivalents=(ArrayList<Event>)company.getParameter("manuEquivalents");
		//Scheduling.ManuScheduling(manuEquivalents, company);
		
		/*
		 * print results
		 */
		for(Event e:(ArrayList<Event>)company.getParameter("allEvents")){
			if((String)e.getParameter("eventType")=="Build"){
				System.out.println((String)company.getParameter("companyName")+"'s build event: "+(String)e.getParameter("eventName")+" starts at "+(Integer)e.getParameter("startTime")+" ends at "+(Integer)e.getParameter("endTime")+"\n");
			}
		}
		for(Drug d:drugs){
			String cts="Drug "+(String)d.getParameter("drugID")+"'s CT timings are: \n";
			String manus="Drug "+(String)d.getParameter("drugID")+"'s Manu timings are: ("+(String)d.getParameter("drugType")+")\n";
			String ancillarys="Drug "+(String)d.getParameter("drugID")+"'s Ancillary timings are: \n";
			ArrayList<Event> clinicalTrials	=(ArrayList<Event>)d.getParameter("CTs");
			ArrayList<Event> manufacturings	=(ArrayList<Event>)d.getParameter("Manus");
			ArrayList<Event> ancillaryEvents=(ArrayList<Event>)d.getParameter("Ancillarys");
			for(Event c:clinicalTrials){
				String lengthElement="\t"+(String)c.getParameter("eventName")+": "+(Integer)c.getParameter("startTime")+"-"+(Integer)c.getParameter("endTime")+"; \n";
				cts=cts+lengthElement;
			}
			System.out.println(cts);
			for(Event m:manufacturings){
				String lengthElement="\tManu For "+(String)m.getParameter("eventName")+": "+(Integer)m.getParameter("startTime")+"-"+(Integer)m.getParameter("endTime")+"; \tbatchNumber: "+(Integer)m.getParameter("batchNumber")+"\tminBatchRequired: "+(Integer)m.getParameter("minBatchRequired")+"\n";
				manus=manus+lengthElement;
			}
			System.out.println(manus);
			for(Event a:ancillaryEvents){
				String lengthElement="\tAncillary event "+(String)a.getParameter("eventName")+": "+(Integer)a.getParameter("startTime")+"-"+(Integer)a.getParameter("endTime")+"; \n";
				ancillarys=ancillarys+lengthElement;
			}
			System.out.println(ancillarys);
			System.out.println();
		}
				
		//now test the ticktok simulation
		ArrayList<Event> allEvents=(ArrayList<Event>)company.getParameter("allEvents");
		
		//HashMap<String,Object> result=MonteCarlo.MonteCarlo(company, 1000);
		
		//System.out.println(result.get("ENPV"));
		//System.out.println(result.get("p(NPV>0)"));
		
	
		//printMCResults(result);
		double NPV=Scheduling.tickTockSimulation(company);
		System.out.println("NPV is "+NPV/1000);
		for(int i=0;i<cashFlow.length;i++){
			System.out.println(i+" - "+cashFlow[i]);
		}
		
		
		//print results
		printEventResults(company);
		

			
	}

	  public static void printObjectivesToFile(String path, double[] results){
		    try {
		      /* Open the file */
		      FileOutputStream fos   = new FileOutputStream(path)     ;
		      OutputStreamWriter osw = new OutputStreamWriter(fos)    ;
		      BufferedWriter bw      = new BufferedWriter(osw)        ;
		                        
		      for (int i = 0; i < results.length; i++) {
		    	bw.write(Double.toString(results[i]));
		        bw.newLine();
		      }
		      
		      /* Close the file */
		      bw.close();
		    }catch (IOException e) {
		      Configuration.logger_.severe("Error acceding to the file");
		      e.printStackTrace();
		    }
		  } // printObjectivesToFile
	  
	  public static void printMCResults(HashMap<String, Object> result){
		  double[] timeToMarket=(double[])result.get("timeToMarket");
		  double[] successCount=(double[])result.get("successCount");
		  for(int i=0;i<timeToMarket.length;i++){
			  System.out.println("Drug "+(i+1)+"'s average time to market is "
		  +timeToMarket[i]+", success count is "+successCount[i]);
		  }
	  }
	  
	  public static void printEventResults(Company company){
			/*
			 * print results
			 */
			ArrayList<Drug> drugs=(ArrayList<Drug>)company.getParameter("drugs");
			
			System.out.println();
			for(Drug d:drugs){
				ArrayList<Event> allCTs			=(ArrayList<Event>)d.getParameter("CTs");
				ArrayList<Event> allManus		=(ArrayList<Event>)d.getParameter("Manus");
				ArrayList<Event> allAncillarys	=(ArrayList<Event>)d.getParameter("Ancillarys");
				
				String cts="Drug "+(String)d.getParameter("drugID")+"'s CT timings are: \n";
				String manus="Drug "+(String)d.getParameter("drugID")+"'s Manu timings are: \n";
				String ancillarys="Drug "+(String)d.getParameter("drugID")+"'s Ancillary timings are: \n";
				
				
				//show cts
				for(Event c:allCTs){
					String nextEventName=null;
					if(c.getParameter("next")!=null){
						Event next=(Event)c.getParameter("next");
						nextEventName=(String)next.getParameter("eventType")+"_"+(String)next.getParameter("eventName");
					}
					String lengthElement="\t"+(String)c.getParameter("eventName")+": "+(Integer)c.getStochastic("startTime")
							+"-"+(Integer)c.getStochastic("endTime")
							+"\t triggered?: "+c.getStochastic("triggered")
							+"\t successful?: "+c.getStochastic("successful")
							+"\t stochastic duration: "+c.getStochastic("duration")
							+",\t deterministic duration: "+c.getParameter("duration")
							+"\t next event is "+nextEventName
							+"; \n";
					cts=cts+lengthElement;
				}
				System.out.println(cts);
				
				//show manus
				for(Event m:allManus){
					String nextEventName=null;
					if(m.getParameter("next")!=null){
						Event next=(Event)m.getParameter("next");
						nextEventName=(String)next.getParameter("eventType")+"_"+(String)next.getParameter("eventName");
					}
					String lengthElement="\tManu For "+(String)m.getParameter("eventName")
							+": "+(Integer)m.getStochastic("startTime")+"-"+(Integer)m.getStochastic("endTime")
							+"\t successful batches: "+m.getStochastic("successfulBatchesDone")
							+"\t next event is "+nextEventName
							+"; \n";
					manus=manus+lengthElement;
				}
				System.out.println(manus);
				
				//show ancillarys
				for(Event a:allAncillarys){
					String nextEventName=null;
					if(a.getParameter("next")!=null){
						Event next=(Event)a.getParameter("next");
						nextEventName=(String)next.getParameter("eventType")+"_"+(String)next.getParameter("eventName");
					}
					String lengthElement="\t"+(String)a.getParameter("eventName")+": "+(Integer)a.getStochastic("startTime")
							+"-"+(Integer)a.getStochastic("endTime")
							+"\t triggered?: "+a.getStochastic("triggered")
							+"\t successful?: "+a.getStochastic("successful")
							+"\t stochastic duration: "+a.getStochastic("duration")
							+",\t deterministic duration: "+a.getParameter("duration")
							+"\t next event is "+nextEventName
							+"; \n";
					ancillarys=ancillarys+lengthElement;
				}
				System.out.println(ancillarys);
				
				
				System.out.println();
			}
			for(ManuFacility mf:(ArrayList<ManuFacility>)company.getStochastic("manuFacilities")){
				String mfs=(String)mf.getParameter("facilityType")+" manufacturing facility "+(String)mf.getParameter("facilityID")+"'s scheduling is: \n";
				for(Event m:(ArrayList<Event>)mf.getStochastic("manuFor")){
					String element="\t"+(String)((Drug)m.getParameter("drug")).getParameter("drugID")+"_"+(String)m.getParameter("eventName")+": "+(Integer)m.getStochastic("startTime")+"-"+(Integer)m.getStochastic("endTime")+"; \n";
					mfs=mfs+element;
				}
				System.out.println(mfs);
				System.out.println();
			}
			
	  }
	  
		public static void createFakeData(){
			int numOfMarketYears=8;
			int[] bigSales		=new int[numOfMarketYears*52];
			int[] mediumSales	=new int[numOfMarketYears*52];
			int[] nicheSales	=new int[numOfMarketYears*52];
			
			Market.data_.put("BB_revenue", bigSales);
			Market.data_.put("medium_revenue", mediumSales);
			Market.data_.put("niche_revenue", nicheSales);
		}
}
