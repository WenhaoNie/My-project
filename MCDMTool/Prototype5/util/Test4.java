package util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import events.Event;

import mcdmProblem.SolutionTranslator;

public class Test4 {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
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
		decisionVariables[6]=	2;
		decisionVariables[7]=	4;
		decisionVariables[8]=	8;
		decisionVariables[9]=	15;
		//
		
		/*
		 * manu strategy
		 * 0, 1, 3, 5, 6
		 * 
		 */
		decisionVariables[10]=	0;
		decisionVariables[11]=	1;
		decisionVariables[12]=	3;
		decisionVariables[13]=	5;
		decisionVariables[14]=	6;
		//
		
		
		/*
		 * makeup some build manufacturing strategies to test  
		 * build two small facilities and two big facilities
		 * starts at 0 and 52 weeks for small
		 * and 		 104 and 208 weeks for big
		 */
		decisionVariables[15]=	-1;
		decisionVariables[16]=	0;
		decisionVariables[17]=	2;
		
		decisionVariables[18]=	-1;
		decisionVariables[19]=	-1;
		decisionVariables[20]=	-1;
		decisionVariables[21]=	2;
		decisionVariables[22]=	4;
		
		//test manu translator
		ArrayList<HashMap> attributes=SolutionTranslator.TranslateManuStrat(decisionVariables);
		int numDV=0;
		for(HashMap attribute:attributes){
			numDV=numDV+1;
			ArrayList<String> eventNames=(ArrayList<String>)attribute.get("eventNames");
			ArrayList<String> eventTypes=(ArrayList<String>)attribute.get("eventTypes");
			ArrayList<String> eventStatus=(ArrayList<String>)attribute.get("status");
			int CMOMarketTime=(Integer)attribute.get("CMOMarketTime");
			
			System.out.println("DV: "+numDV+"\n");
			
			System.out.println("CMO market time: "+CMOMarketTime+"\n");
			for(int i=0;i<eventNames.size();i++){
				System.out.println("eventName: "+eventNames.get(i)+"\t eventType: "+eventTypes.get(i)+"\t eventStatus: "+eventStatus.get(i));
			}
			System.out.println();
		}
		
		System.out.println();
		
		//test build_large translator
		int[] buildLarge=SolutionTranslator.TranslateBuildBig(decisionVariables);
		
		for(int i=0;i<buildLarge.length;i++){
			System.out.println(i+" - "+buildLarge[i]);
		}
		
		//test null object in HashMap
		HashMap<String, Object> testNull=new HashMap<String, Object>();
		testNull.put("testnull", new ArrayList<Event>());
		
		//copyOfRange test
		int[] intArray_1={0,1,2,3,4,5,6,7,8};
		System.out.println(intArray_1.length);
		
		int[] intArray_2=Arrays.copyOfRange(intArray_1, 0, 4);
		System.out.println(intArray_2.length);
		System.out.println(intArray_2[intArray_2.length-1]);
		
		
		//boolean object test
		boolean bool_1=true;
		Boolean BOOL_1=true;
		boolean bool_2=false;
		Boolean BOOL_2=false;
		
		if(bool_1&&!BOOL_2){
			System.out.println("both true");
		}
		
		
	}

}
