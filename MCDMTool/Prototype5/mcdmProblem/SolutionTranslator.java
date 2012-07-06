package mcdmProblem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/*
 * this class is dedicated to translate solutions into specific decisions
 */

public class SolutionTranslator {
	
	public static int[] TranslatePortfolio(int[] solution){
		//don't need it just yet
		return null;
	}
	
	public static int[] TranslateLaunchTime(int[] solution){
		//launch time
		
		/*
		 * position 5-9
		 * 0 - now
		 * 1 - 26 weeks
		 * 2 - 52 weeks 
		 * and so on
		 * 
		 */
		final int num_Of_Variables=5; //could be different if change the solution
		final int start_Position=5; //could be different as well
		
		int[] launchTimes=new int[num_Of_Variables];
		for(int i=0;i<num_Of_Variables;i++){
			launchTimes[i]=solution[start_Position+i]*26;
		}
		
		return launchTimes;
		
	}
	
	public static ArrayList<HashMap> TranslateManuStrat(int[] solution){
		/* 
		 * give the event names , eventTypes and status in 3 ArrayList<String> in the HashMap
		 * and the length of market_1, market_2 for manu and CTs
		 * 
		 */
		ArrayList<HashMap> attributes=new ArrayList<HashMap>();
		
		final int num_Of_Variables=5;
		final int start_Position=10; //could be different
		for(int i=0;i<num_Of_Variables;i++){
			HashMap<String, Object> attribute=new HashMap<String, Object>();
			int CMOMarketTime=0;
			String[] eNameArray={	"Dis"		,//1
									"LM"		,//2
									"PC"		,//3
									"IND"		,//4
									"PhaseI"	,//5
									"PhaseII"	,//6
									"PhaseIII"	,//7
									"FDA"		,//8
									"PC"		,//9 manu starts here
									"PhaseI"	,//10
									"PhaseII"	,//11
									//"PhaseIII"	,//12
									"TechLab"	};//13 PD starts here
			String[] eTypesArray={	"CT"		,//1
									"CT"		,//2
									"CT"		,//3
									"CT"		,//4
									"CT"		,//5
									"CT"		,//6
									"CT"		,//7
									"CT"		,//8
									"Manu"		,//9
									"Manu"		,//10
									"Manu"		,//11
									//"Manu"		,//12
									"PD"		};//13
			String[] statusArray={	"in-house"	,//1
									"in-house"	,//2
									"in-house"	,//3
									"in-house"	,//4
									"in-house"	,//5
									"in-house"	,//6
									"in-house"	,//7
									"in-house"	,//8
									"in-house"	,//9
									"in-house"	,//10
									"in-house"	,//11
									//"in-house"	,//12
									"in-house"	,};//13
			ArrayList<String> eventNames=new ArrayList<String>(Arrays.asList(eNameArray));
			ArrayList<String> eventTypes=new ArrayList<String>(Arrays.asList(eTypesArray));
			ArrayList<String> status	=new ArrayList<String>(Arrays.asList(statusArray));
			if(solution[i+start_Position]==0){//all in-house
				
				eventNames.add("Market");//CT market
				eventNames.add("PhaseIII");//Manu PhaseIII
				eventNames.add("Market");//Manu market
				eventNames.add("ScaleUp");//PD
				
				eventTypes.add("Market");//market eventType
				eventTypes.add("Manu");
				eventTypes.add("Manu");
				eventTypes.add("PD");
				
				status.add("in-house");
				status.add("in-house");
				status.add("in-house");
				status.add("in-house");
			}
			else if(solution[i+start_Position]==1){//phase III CMO
				
				eventNames.add("Market");//CT market
				eventNames.add("PhaseIII");//Manu PhaseIII
				eventNames.add("Market");//Manu market
				eventNames.add("ScaleUp");//PD
				eventNames.add("CMO_Preparation");
				eventNames.add("TechTransfer");
				
				eventTypes.add("Market");
				eventTypes.add("Manu");
				eventTypes.add("Manu");
				eventTypes.add("PD");
				eventTypes.add("Ancillary");//CMO_Preparation
				eventTypes.add("Ancillary");//TechTransfer
				
				status.add("in-house");
				status.add("CMO");//Manu for PhaseIII
				status.add("in-house");
				status.add("CMO");//PD
				status.add("CMO");
				status.add("CMO");
				
			}
			else if(solution[i+start_Position]>=2 && solution[i+start_Position]<6){//phase III and market first several years CMO
				
				eventNames.add("Market_1");//CT market supplied by CMO
				eventNames.add("Market_2");//CT market supplied by in-house
				eventNames.add("PhaseIII");//Manu PhaseIII
				eventNames.add("Market_1");//Manu market by CMO
				eventNames.add("Market_2");//Manu market by in-house
				eventNames.add("ScaleUp");//PD
				eventNames.add("CMO_Preparation");
				eventNames.add("TechTransfer");
				
				eventTypes.add("Market");
				eventTypes.add("Market");
				eventTypes.add("Manu");
				eventTypes.add("Manu");
				eventTypes.add("Manu");
				eventTypes.add("PD");
				eventTypes.add("Ancillary");//CMO_Preparation
				eventTypes.add("Ancillary");//TechTransfer
				
				status.add("in-house");
				status.add("in-house");
				status.add("CMO");//Manu for PhaseIII
				status.add("CMO");//Manu for Market_1
				status.add("in-house");//Manu for Market_2
				status.add("CMO");//PD
				status.add("CMO");
				status.add("CMO");
				
				CMOMarketTime=(solution[i+start_Position]-1)*52; //could be different if change decision variables
			}
			else if(solution[i+start_Position]==6){//Phase III and market all CMO
				
				eventNames.add("Market");//CT market
				eventNames.add("PhaseIII");//Manu PhaseIII
				eventNames.add("Market");//Manu market
				eventNames.add("ScaleUp");//PD
				eventNames.add("CMO_Preparation");
				eventNames.add("TechTransfer");
				
				eventTypes.add("Market");
				eventTypes.add("Manu");
				eventTypes.add("Manu");
				eventTypes.add("PD");
				eventTypes.add("Ancillary");//CMO_Preparation
				eventTypes.add("Ancillary");//TechTransfer
				
				status.add("in-house");
				status.add("CMO");
				status.add("CMO");
				status.add("CMO");
				status.add("CMO");
				status.add("CMO");
				
				CMOMarketTime=52*8; //8 years of using CMO market
				
			}
			else{
				System.out.println("Decision Variable "+(i+start_Position)+" error!");
			}
			
			attribute.put("eventNames", 	eventNames);
			attribute.put("eventTypes", 	eventTypes);
			attribute.put("status", 		status);
			attribute.put("CMOMarketTime", 	CMOMarketTime);
			
			attributes.add(i, attribute);
		}
		
		return attributes;
	}
	
	public static int[] TranslateBuildSmall(int[] solution){
		//startTimes of build small events
		
		/*
		 * translate the decision variables
		 * position 15-17 is about small facilities
		 */
		
		int numSmall=0;
		for(int i=15;i<18;i++){
			if(solution[i]!=-1){
				numSmall=numSmall+1;
			}
		}
		
		int[] startSmall=new int[numSmall];
		int incCount=0;
		for(int i=15;i<18;i++){
			if(solution[i]!=-1){
				startSmall[incCount]=solution[i]*26;
				incCount=incCount+1;
			}
		}//startTime for build small done
		
		return startSmall;
	}
	
	public static int[] TranslateBuildBig(int[] solution){
		//startTimes of build big events
		/*
		 * translate the decision variables
		 * position 18-22 is about big facilities
		 * 
		 */
		int startingPosition=18;
		int numDecisionVariables=5;
		
		int numBig=0;
		for(int i=startingPosition;i<startingPosition+numDecisionVariables;i++){
			if(solution[i]!=-1){
				numBig=numBig+1;
			}
		}
		
		int[] startBig=new int[numBig];
		int incCount=0;
		for(int i=startingPosition;i<startingPosition+numDecisionVariables;i++){
			if(solution[i]!=-1){
				startBig[incCount]=solution[i]*26;
				incCount=incCount+1;
			}
		}//startTime for build small done
		
		return startBig;
		
	}
	
}
