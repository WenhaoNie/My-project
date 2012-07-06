package geneticAlgorithm;

import java.util.HashMap;

import util.SystemParameters;

import data.AncillaryData;
import data.CTData;
import data.ManuData;
import data.MarketData;

import events.Ancillary;
import events.CT;
import events.Manu;
import events.Market;

import jmetal.core.Algorithm;
import jmetal.core.Operator;
import jmetal.core.Problem;
import jmetal.core.SolutionSet;
import jmetal.metaheuristics.ibea.IBEA;
import jmetal.metaheuristics.nsgaII.NSGAII;
import jmetal.operators.crossover.CrossoverFactory;
import jmetal.operators.mutation.MutationFactory;
import jmetal.operators.selection.BinaryTournament;
import jmetal.operators.selection.SelectionFactory;
import jmetal.util.JMException;
import jmetal.util.comparators.FitnessComparator;
import mcdmProblem.BatchManufacturingModel;
import mcdmProblem.CMOvsInHouseProblem;

public class CMOvsInHouse_main {

	/**
	 * @param args
	 * @throws ClassNotFoundException 
	 * @throws JMException 
	 */
	public static void main(String[] args) throws ClassNotFoundException, JMException {
	    Problem   problem   ;         // The problem to solve
	    Algorithm algorithm ;         // The algorithm to use
	    Operator  crossover ;         // Crossover operator
	    Operator  mutation  ;         // Mutation operator
	    Operator  selection ;         // Selection operator
	    
	    HashMap  parameters ; // Operator parameters
	    
	    problem		=new CMOvsInHouseProblem("Integer", 5);
	    
	    if(SystemParameters.running_GA.equalsIgnoreCase("NSGAII")){
		    algorithm	=new GA_NSGAII(problem);
		    
		    algorithm.setInputParameter("populationSize", 50);
		    algorithm.setInputParameter("maxEvaluations", 1000);
		    
		    parameters = new HashMap() ;
		    parameters.put("probability", 0.9) ;
		    parameters.put("distributionIndex", 20.0) ;
		    crossover = CrossoverFactory.getCrossoverOperator("SinglePointCrossover", parameters);                   

		    parameters = new HashMap() ;
		    parameters.put("probability", 1.0/problem.getNumberOfVariables()) ;
		    parameters.put("distributionIndex", 20.0) ;
		    mutation = MutationFactory.getMutationOperator("BitFlipMutation", parameters);                    
		    
		    // Selection Operator 
		    parameters = null ;
		    selection = SelectionFactory.getSelectionOperator("BinaryTournament2", parameters) ;                           

		    // Add the operators to the algorithm
		    algorithm.addOperator("crossover",crossover);
		    algorithm.addOperator("mutation",mutation);
		    algorithm.addOperator("selection",selection);
	    }
	    else{
	    	
	        algorithm = new GA_IBEA(problem);

	        // Algorithm parameters
	        algorithm.setInputParameter("populationSize",50);
	        algorithm.setInputParameter("archiveSize",50);
	        algorithm.setInputParameter("maxEvaluations",2000);

	        // Mutation and Crossover for Real codification 
	        parameters = new HashMap() ;
	        parameters.put("probability", 0.9) ;
	        parameters.put("distributionIndex", 20.0) ;
	        crossover = CrossoverFactory.getCrossoverOperator("SinglePointCrossover", parameters);                   

	        parameters = new HashMap() ;
	        parameters.put("probability", 1.0/problem.getNumberOfVariables()) ;
	        parameters.put("distributionIndex", 20.0) ;
	        mutation = MutationFactory.getMutationOperator("BitFlipMutation", parameters);         

	        /* Selection Operator */
	        parameters = new HashMap() ; 
	        parameters.put("comparator", new FitnessComparator()) ;
	        selection = new BinaryTournament(parameters);
	        
	        // Add the operators to the algorithm
	        algorithm.addOperator("crossover",crossover);
	        algorithm.addOperator("mutation",mutation);
	        algorithm.addOperator("selection",selection);
	    	
	    }

	    
	    //all BB for start
	    CMOvsInHouseProblem.portfolioComposition=3;
	    
	    int numOfExtent=6;
	    int numOfScenarios=4;
	    String thisComputer="Tianyi's Computer";
	    CMOvsInHouseProblem.extent=0;
	    for(int var=0;var<numOfExtent;var++){
	    	//int var=2;
	    	if(applyExtent(var, thisComputer)){
		    	CMOvsInHouseProblem.numOfFacility=var;
		    	for(int i=2;i<numOfScenarios;i++){
		    		//int i=1;
		    		
		    		if(SystemParameters.running_GA.equalsIgnoreCase("NSGAII")){
		    			//test//for printing scenario
		    			GA_NSGAII.extent=var;
		    			GA_NSGAII.scenario=i+1;
		    			//end
		    		}
		    		else{
		    			GA_IBEA.extent=var;
		    			GA_IBEA.scenario=i+1;
		    		}
		    		
					//CT.data_		=CTData.createData();
					CT.data_		=CTData.createCMOvsInHouseData();
		    		Manu.data_		=ManuData.createData();
					Ancillary.data_	=AncillaryData.createData();
					Market.data_	=MarketData.createData();
			    	
					
					applyScenario(i+1);
					SuccessPortfolio();
				    // Execute the Algorithm
				    long initTime = System.currentTimeMillis();
				    SolutionSet population = algorithm.execute();
				    long estimatedTime = System.currentTimeMillis() - initTime;
				    
				    System.out.println("The estimatedTime is for extent "+var+ ", scenario "+(i+1)+" is "+estimatedTime);
				    
				    population.printSuccessToFile("CMOvsInHouseE_"+var+"_S_"+(i+1));
			    }
	    	}
	    }
	}
	
	public static boolean applyExtent(int var, String computer){
		//var can only be 0,1,2
		
		if(var==2){
			if(computer=="Tianyi's Computer"){
				System.out.println("Doing extent "+var+" on "+computer);
				return true;
			}
			else
				return false;
		}
		else if(var==1){
			if(computer=="My Own Computer"){
				System.out.println("Doing extent "+var+" on "+computer);
				return true;
			}
			else
				return false;
		}
		else if(var==0){
			if(computer=="My Work Computer"){
				System.out.println("Doing extent "+var+" on "+computer);
				return true;
			}
			else
				return false;
		}
		else
			return false;
	}
	
	public static void applyScenario(int scenario){
		
		int BB_Small		=125;
		int BB_Big			=375;
		int medium_Small	=75;
		int medium_Big		=175;
		int niche_Small		=50;
		int niche_Big		=125;
		
		int inHouseToCMO;
		if(scenario==1){//scenario 1, CMO:inHouse=1:1
			inHouseToCMO=1;
		}
		else if(scenario==2){//scenario 2, CMO:inhouse=2:1
			inHouseToCMO=2;
		}
		else if(scenario==3){//scenario 3, CMO:inhouse=3:1
			inHouseToCMO=3;
		}
		else if(scenario==4){//scenario 4
			inHouseToCMO=4;
		}
		else{ //scenario 5
			inHouseToCMO=10;
		}
		Manu.data_.put("BB_PC_in-house_batchUnitCost",		BB_Small);
		Manu.data_.put("BB_PhaseI_in-house_batchUnitCost",	BB_Small);
		Manu.data_.put("BB_PhaseII_in-house_batchUnitCost",	BB_Small);
		Manu.data_.put("BB_PhaseIII_in-house_batchUnitCost",BB_Big);
		Manu.data_.put("BB_Market_in-house_batchUnitCost",	BB_Big);
		
		Manu.data_.put("niche_PC_in-house_batchUnitCost",		niche_Small);
		Manu.data_.put("niche_PhaseI_in-house_batchUnitCost",	niche_Small);
		Manu.data_.put("niche_PhaseII_in-house_batchUnitCost",	niche_Small);
		Manu.data_.put("niche_PhaseIII_in-house_batchUnitCost",	niche_Big);
		Manu.data_.put("niche_Market_in-house_batchUnitCost",	niche_Big);
		
		Manu.data_.put("medium_PC_in-house_batchUnitCost",		medium_Small);
		Manu.data_.put("medium_PhaseI_in-house_batchUnitCost",	medium_Small);
		Manu.data_.put("medium_PhaseII_in-house_batchUnitCost",	medium_Small);
		Manu.data_.put("medium_PhaseIII_in-house_batchUnitCost",medium_Big);
		Manu.data_.put("medium_Market_in-house_batchUnitCost",	medium_Big);
		
		Manu.data_.put("BB_PC_CMO_batchUnitCost",			medium_Big*inHouseToCMO);
		Manu.data_.put("BB_PhaseI_CMO_batchUnitCost",		medium_Big*inHouseToCMO);
		Manu.data_.put("BB_PhaseII_CMO_batchUnitCost",		medium_Big*inHouseToCMO);
		Manu.data_.put("BB_PhaseIII_CMO_batchUnitCost",		BB_Big*inHouseToCMO);
		Manu.data_.put("BB_Market_CMO_batchUnitCost",		BB_Big*inHouseToCMO);
		
		Manu.data_.put("niche_PC_CMO_batchUnitCost",		niche_Small*inHouseToCMO);
		Manu.data_.put("niche_PhaseI_CMO_batchUnitCost",	niche_Small*inHouseToCMO);
		Manu.data_.put("niche_PhaseII_CMO_batchUnitCost",	niche_Small*inHouseToCMO);
		Manu.data_.put("niche_PhaseIII_CMO_batchUnitCost",	niche_Big*inHouseToCMO);
		Manu.data_.put("niche_Market_CMO_batchUnitCost",	niche_Big*inHouseToCMO);
		
		Manu.data_.put("medium_PC_CMO_batchUnitCost",		medium_Small*inHouseToCMO);
		Manu.data_.put("medium_PhaseI_CMO_batchUnitCost",	medium_Small*inHouseToCMO);
		Manu.data_.put("medium_PhaseII_CMO_batchUnitCost",	medium_Small*inHouseToCMO);
		Manu.data_.put("medium_PhaseIII_CMO_batchUnitCost",	medium_Big*inHouseToCMO);
		Manu.data_.put("medium_Market_CMO_batchUnitCost",	medium_Big*inHouseToCMO);
		
	}
	
	//make all drugs to success
	public static void SuccessPortfolio(){
		CT.data_.put("BB_Dis_TP",			0.88d);
		CT.data_.put("BB_LM_TP",			0.88d);
		CT.data_.put("BB_PC_TP",			0.88d);
		CT.data_.put("BB_IND_TP",			0.88d);
		CT.data_.put("BB_PhaseI_TP",		0.88d);
		CT.data_.put("BB_PhaseII_TP",		0.88d);
		CT.data_.put("BB_PhaseIII_TP",		0.88d);
		CT.data_.put("BB_FDA_TP",			0.88d);
		
		CT.data_.put("medium_Dis_TP",			0.88d);
		CT.data_.put("medium_LM_TP",			0.88d);
		CT.data_.put("medium_PC_TP",			0.88d);
		CT.data_.put("medium_IND_TP",			0.88d);
		CT.data_.put("medium_PhaseI_TP",		0.88d);
		CT.data_.put("medium_PhaseII_TP",		0.88d);
		CT.data_.put("medium_PhaseIII_TP",		0.88d);
		CT.data_.put("medium_FDA_TP",			0.88d);
		
		CT.data_.put("niche_Dis_TP",			0.88d);
		CT.data_.put("niche_LM_TP",				0.88d);
		CT.data_.put("niche_PC_TP",				0.88d);
		CT.data_.put("niche_IND_TP",			0.88d);
		CT.data_.put("niche_PhaseI_TP",			0.88d);
		CT.data_.put("niche_PhaseII_TP",		0.88d);
		CT.data_.put("niche_PhaseIII_TP",		0.88d);
		CT.data_.put("niche_FDA_TP",			0.88d);
	}
}
