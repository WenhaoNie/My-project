package mcdmProblem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

import util.RandomGenerator;
import util.Scheduling;

import company.Company;
import company.CompanyFactory;

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

import jmetal.core.Problem;
import jmetal.core.Solution;
import jmetal.core.Variable;
import jmetal.encodings.solutionType.IntSolutionType;
import jmetal.util.JMException;

public class BatchManufacturingModel extends Problem {

	public BatchManufacturingModel(String solutionType, Integer numberOfVariables) throws ClassNotFoundException {
		numberOfVariables_		=numberOfVariables;
		numberOfObjectives_		=2;//npv and p(npv>0)
		numberOfConstraints_	=1;
		problemName_			="BatchManufacturingModel";
		
	    upperLimit_ = new double[numberOfVariables_];
	    lowerLimit_ = new double[numberOfVariables_];
	    
	    /*
	     * define the upperlimit and lowerlimit of variables
	     * first 5 variables(0-4), drug candidates, 1-10 drug
	     * next 5(5-9), start times, 1-5 representing different start times
	     * 0 - now
	     * 1 - 26 weeks later
	     * 2 - 52 weeks later (one year)
	     * and so on until 15 (15*26 = 390 weeks = 7.5 years)
	     * 
	     * next 5(10-14), late stage manufacturing strategies:
	     * 0 - all inhouse; 
	     * 1 - PhaseIII CMO, rest inhouse;
	     * 2 - PhaseIII + market 1st year CMO, rest inhouse;
	     * 3 - PhaseIII + market 2nd year CMO, rest inhouse;
	     * 4 - PhaseIII + market 3rd year CMO, rest inhouse;
	     * 5 - PhaseIII + market 4th year CMO, rest inhouse;
	     * 6 - all CMO
	     * 
	     * next 3(15-17), time to start building small facilities, range -1 - 5, -1 means no build, 0-5 representing different start times
	     *-1 - no build
	     * 0 - build now
	     * 1 - 26 weeks later
	     * 2 - 52 weeks later
	     * and so on until 8 (8*26 = 208 weeks = 4 years)
	     * 
	     * next 5(18-22), time to start building big facilities, range -1 - 5, -1 means no build, 0-5 representing different start times
	     *-1 - no build
	     * 0 - build now
	     * 1 - 26 weeks later
	     * 2 - 52 weeks later
	     * and so on until 8 (8*26 = 208 weeks = 4 years)
	     * 
	     */
	    
	    for(int i=0;i<5;i++){
	    	lowerLimit_[i]=1;
	    	upperLimit_[i]=10;
	    }//10 drug candidates
	    
	    for(int i=5;i<10;i++){
	    	lowerLimit_[i]=0;//starting Now
	    	upperLimit_[i]=15;//1 unit means half a year, which is 26 weeks
	    }//timing strategy
	    
	    for(int i=10;i<15;i++){
	    	lowerLimit_[i]=0;
	    	upperLimit_[i]=6;
	    }//manufacturing strategy (late stage)
	    
	    for(int i=15;i<23;i++){
	    	lowerLimit_[i]=-1;
	    	upperLimit_[i]=8;
	    }//manufacturing plant build time
	    
	    /*
	     * assign solution type
	     */
	    solutionType_=new IntSolutionType(this);
	    
	    
	    /*
	     * constraints here
	     * 1) drug candidates can't be the same
	     * 2) starting time in increasing order
	     * 3) manufacturing plant build time in increasing order
	     * 
	     */
	    
	    
	    
	    
	}
	
	@Override
	public void evaluateConstraints(Solution solution) throws JMException {
		Variable[] dVs=solution.getDecisionVariables();
		int[] decisionVariables=new int[numberOfVariables_];
		
		for(int var=0;var<numberOfVariables_;var++){
			decisionVariables[var]=(int)dVs[var].getValue();
		}
		
		decisionVariables=mendPortfolioSelection(decisionVariables);
		decisionVariables=mendTimingStrategy(decisionVariables);
		decisionVariables=mendBuildSmall(decisionVariables);
		decisionVariables=mendBuildBig(decisionVariables);
		
		for(int var=0;var<numberOfVariables_;var++){
			solution.getDecisionVariables()[var].setValue(decisionVariables[var]);
		}
	} // evaluateConstraints
	
	
	
	public static int[] mendPortfolioSelection(int[] decisionVariables){
		
		int startPosition=0;
		int numOfVariables=5;
		ArrayList<Integer> selections=new ArrayList<Integer>();
		for(int i=0;i<10;i++){
			selections.add(i+1);
		}
		Collections.shuffle(selections);
		
		for(int i=startPosition;i<startPosition+numOfVariables;i++){
			if(selections.contains(decisionVariables[i])){
				selections.remove((Integer)decisionVariables[i]);
			}
			else{
				decisionVariables[i]=selections.get(0);
				selections.remove(0);
			}
		}
		
		return decisionVariables;
	}
	
	public static int[] mendTimingStrategy(int[] decisionVariables){
		int startPosition=5;
		int numOfVariables=5;
		
		for(int i=startPosition+1;i<startPosition+numOfVariables;i++){
			if(decisionVariables[i]<decisionVariables[i-1])
				decisionVariables[i]=decisionVariables[i-1];
		}
		
		return decisionVariables;
	}
	
	public static int[] mendBuildSmall(int[] decisionVariables){
		
		int startPosition=15;
		int numOfVariables=3;
		int[] sortArray=new int[numOfVariables];
		
		int index=0;
		for(int i=startPosition;i<startPosition+numOfVariables;i++){
			sortArray[index]=decisionVariables[i];
			index++;
		}
		
		Arrays.sort(sortArray);
		index=0;
		for(int i=startPosition;i<startPosition+numOfVariables;i++){
			decisionVariables[i]=sortArray[index];
			index++;
		}
		
		return decisionVariables;
	}
	
	public static int[] mendBuildBig(int[] decisionVariables){
		
		int startPosition=18;
		int numOfVariables=5;
		int[] sortArray=new int[numOfVariables];
		
		int index=0;
		for(int i=startPosition;i<startPosition+numOfVariables;i++){
			sortArray[index]=decisionVariables[i];
			index++;
		}
		
		Arrays.sort(sortArray);
		index=0;
		for(int i=startPosition;i<startPosition+numOfVariables;i++){
			decisionVariables[i]=sortArray[index];
			index++;
		}
		return decisionVariables;
	}

	
	@Override
	public void evaluate(Solution solution) throws JMException {
		//convert solution into int[] decisionVariables
		Variable[] dVs=solution.getDecisionVariables();
		int[] decisionVariables=new int[numberOfVariables_];
		
		for(int var=0;var<numberOfVariables_;var++){
			decisionVariables[var]=(int)dVs[var].getValue();
		}
		
		//load data
		CT.data_		=CTData.createData();
		Manu.data_		=ManuData.createData();
		Ancillary.data_	=AncillaryData.createData();
		Market.data_	=MarketData.createData();
		
		//create company
		Company company=CompanyFactory.createCompany(decisionVariables);
		
		//assemble drugs and events
		DrugFactory.AssembleDrugs(company, decisionVariables);
		
		//deterministic planning
		ArrayList<Drug> drugs=(ArrayList<Drug>)company.getParameter("drugs");
		for(Drug d:drugs){
			ArrayList<Event> cts=(ArrayList<Event>)d.getParameter("CTs");
			Scheduling.CTScheduling(cts);
		}
		
		//MonteCarlo simulation
		HashMap<String, Object> result=MonteCarlo.MonteCarlo(company, 1000);//running average shows 500 is enough
		double ENPV=(Double)result.get("ENPV");
		double pNPV=(Double)result.get("p(NPV>0)");
		/*
		 * for Monte Carlo Running Average Only
		 * start
		 */
		double[] NPVs=(double[])result.get("NPVs");
		double[] runMean=(double[])result.get("runMean");
		double[] runSTD=(double[])result.get("runSTD");
		int converge=(Integer)result.get("converge");
		solution.NPVs=NPVs;
		solution.converge=converge;
		solution.runMean=runMean;
		solution.runSTD=runSTD;
		
		/*
		 * for Monte Carlo Running Average Only
		 * end
		 */
		
		
		
		solution.setObjective(0, ENPV);
		solution.setObjective(1, pNPV);
		
		//test//start
		//print solution and objective to console
		String print="ENPV is "+ENPV+", p(NPV>0) is "+pNPV+" decision variables are ";
		for(int i=0;i<decisionVariables.length;i++){
			print=print+"_"+decisionVariables[i];
		}
		System.out.println(print);
		//test//end

	}

}
