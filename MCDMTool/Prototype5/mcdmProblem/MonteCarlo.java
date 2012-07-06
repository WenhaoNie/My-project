package mcdmProblem;

import java.util.ArrayList;
import java.util.HashMap;

import company.Company;
import company.ManuFacility;
import drugs.Drug;

import util.Scheduling;
import util.Test3;

import events.Event;
import events.Manu;

public class MonteCarlo {
	
	public static HashMap<String, Object> MonteCarlo(Company company, int numOfMC){
		/*
		 * result:
		 * double "ENPV"
		 * double "p(NPV>0)"
		 * 
		 * double[] "NPVs"
		 * double[] "runMean"
		 * double[] "runSTD"
		 * int		converge
		 */
		HashMap<String, Object> result=new HashMap<String, Object>();
		
		ArrayList<Event> allEvents	=(ArrayList<Event>)company.getParameter("allEvents");
		ArrayList<Drug>  drugs		=(ArrayList<Drug>)company.getParameter("drugs");
		
		double positiveCount=0;
		double totalNPV=0;
		double squares=0;
		double[] NPVs=new double[numOfMC];
		
		//test//running average
		double[] runMean=new double[numOfMC];
		double[] runSTD=new double[numOfMC];
		int converge=0;
		//test//running average end
		
		//test//time to market
		double[] timeToMarket=new double[5];
		double[] timeFDA_Approval=new double[5];
		double[] aveTimeToMarket=new double[5];
		double[] aveTimeFDA_Approval=new double[5];
		double[] successCount=new double[5];
		//test//end
		
		
		//test//calculating time of delay and idle
		
		Manu.Time_Of_Delay=0;
		Manu.Time_Of_Idle=0;
		//test//end
		
		for(int i=0;i<numOfMC;i++){
			//stochastic simulation
			double NPV=Scheduling.tickTockSimulation(company);
			NPVs[i]=NPV;
			//test//start
			//System.out.println("The "+(i+1)+"'s MC events are: " );
			
			//Test3.printEventResults(company);
			
			//System.out.println("Monte Carlo simulation "+(i+1)+"'s \tNPV is: "+NPV/1000);
			//test//end
			
			if(NPV>0){
				positiveCount=positiveCount+1;
			}
			totalNPV=totalNPV+NPV;
			squares=squares+Math.pow(NPV, 2);
			
			//test//running average
			if(i>0){
				runMean[i]=totalNPV/i;
				runSTD[i]=Math.sqrt(squares/i-Math.pow(runMean[i], 2));
				
				if(runSTD[i]<0.2*runMean[i] && converge==0){
					converge=i;
				}
			}
			
			//test//running average end
			
			//clear stochastic results of drugs
			int count=0;
			for(Drug drug:drugs){
				//test//record time to market
				if((Integer)drug.getStochastic("timeToMarket")!=-1){
					timeToMarket[count]=(Integer)drug.getStochastic("timeToMarket")+timeToMarket[count];
					timeFDA_Approval[count]=(Integer)drug.getStochastic("FDA_Approval")+timeFDA_Approval[count];
					successCount[count]=successCount[count]+1;
				}
				count++;
				//test//end
				drug.ResetStochastic();
			}
			
			//clear stochastic results of events
			for(Event event:allEvents){
				event.ResetStochastic();
			}
			
			//clear manufacilities
			company.ResetStochastic();
		}
		

		
		double pNPV=positiveCount/numOfMC;
		double ENPV=totalNPV/numOfMC;
		
		
		result.put("ENPV", ENPV);
		result.put("p(NPV>0)", pNPV);
		//test//start//running average
		result.put("NPVs", NPVs);
		result.put("runMean", runMean);
		result.put("runSTD", runSTD);
		result.put("converge", converge);
		//test//end
		
		//test//record time to market
		for(int i=0;i<5;i++){
			if(successCount[i]!=0){
				aveTimeToMarket[i]=timeToMarket[i]/successCount[i];
				aveTimeFDA_Approval[i]=timeFDA_Approval[i]/successCount[i];
			}
			else{
				aveTimeToMarket[i]=-1;
				aveTimeFDA_Approval[i]=-1;
			}
		}
		result.put("timeToMarket", aveTimeToMarket);
		result.put("FDA_Approval", aveTimeFDA_Approval);
		result.put("successCount", successCount);
		//test//end
		
		//test//start//delay time and idle time only
		
		//System.out.println("The overall idle time is "+Manu.Time_Of_Idle+"; and delay time is: "+Manu.Time_Of_Delay);
		
		//test//end
		
		return result;
	}

}
