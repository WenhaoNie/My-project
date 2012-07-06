package util;

import java.util.Arrays;
import java.util.HashMap;

import mcdmProblem.BatchManufacturingModel;

public class Test5 {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		int[] decisionVariables=new int[23];
		int[] df=new int[23];
		solutions=new HashMap<String, Object>();
		decisionVariables[0]=	1;
		decisionVariables[1]=	5;
		decisionVariables[2]=	1;
		decisionVariables[3]=	2;
		decisionVariables[4]=	2;
				
		decisionVariables[5]=	0;
		decisionVariables[6]=	2;
		decisionVariables[7]=	1;
		decisionVariables[8]=	8;
		decisionVariables[9]=	7;

		decisionVariables[10]=	0;
		decisionVariables[11]=	1;
		decisionVariables[12]=	3;
		decisionVariables[13]=	5;
		decisionVariables[14]=	6;

		decisionVariables[15]=	2;
		decisionVariables[16]=	0;
		decisionVariables[17]=	-1;
		
		decisionVariables[18]=	2;
		decisionVariables[19]=	-1;
		decisionVariables[20]=	0;
		decisionVariables[21]=	2;
		decisionVariables[22]=	0;
		
		
		System.out.println(Arrays.toString(decisionVariables));
		solutions.put(Arrays.toString(decisionVariables), decisionVariables);
		System.out.println(solutions.containsKey(Arrays.toString(decisionVariables)));
		
		df[0]=	1;
		df[1]=	5;
		df[2]=	1;
		df[3]=	2;
		df[4]=	2;
				
		df[5]=	0;
		df[6]=	2;
		df[7]=	1;
		df[8]=	8;
		df[9]=	7;

		df[10]=	0;
		df[11]=	1;
		df[12]=	3;
		df[13]=	5;
		df[14]=	6;

		df[15]=	2;
		df[16]=	0;
		df[17]=	-1;
		
		df[18]=	2;
		df[19]=	-1;
		df[20]=	0;
		df[21]=	2;
		df[22]=	0;
		
		System.out.println(solutions.containsKey(Arrays.toString(df)));
		df[18]=3;
		System.out.println(solutions.containsKey(Arrays.toString(df)));
		solutions.put(Arrays.toString(df), df);
		System.out.println(solutions.containsKey(Arrays.toString(df)));
	}

	public static HashMap<String, Object> solutions;
	
	public static String IntegerToString(int[] inputs){
		
		String output=Arrays.toString(inputs).replace(", ", "_").replace("[", "").replace("]", "");
		return output;
		
	}
}
