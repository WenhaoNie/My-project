package mcdmProblem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import util.Scheduling;

import company.Company;
import company.CompanyFactory;
import company.ManuFacility;

import drugs.Drug;
import drugs.DrugFactory;
import events.Event;

import jmetal.core.Problem;
import jmetal.core.Solution;
import jmetal.core.Variable;
import jmetal.encodings.solutionType.IntSolutionType;
import jmetal.util.JMException;

public class CMOvsInHouseProblem extends Problem {

	
	public static int numOfFacility; //the number of big facility the company has
	public static int extent;// the extent of the scarcity of in-house capacity
	public static int portfolioComposition;//1 - niche; 2 - medium; 3 - BB
	//0 - full capacity
	//1 - slightly limited
	//2 - limited
	//3 - very limited
	//4 - nearly no capacity
	//5 - no capacity
	
	public CMOvsInHouseProblem(String solutionType, Integer numberOfVariables) throws ClassNotFoundException{
		numberOfVariables_	=numberOfVariables;//5 late stage build event in this case
		numberOfObjectives_	=2;
		numberOfConstraints_=0;
		problemName_		="CMOvsInHouseProblem";
		
	    upperLimit_ = new double[numberOfVariables_];
	    lowerLimit_ = new double[numberOfVariables_];
	    
	    for(int i=0;i<numberOfVariables_;i++){
	    	lowerLimit_[i]=0;
	    	upperLimit_[i]=6;
	    }
	    
	    solutionType_=new IntSolutionType(this);
	}
	
	
	
	@Override
	public void evaluate(Solution solution) throws JMException {
		Variable[] dVs=solution.getDecisionVariables();
		int[] partialDV=new int[numberOfVariables_];
		
		for(int var=0;var<numberOfVariables_;var++){
			partialDV[var]=(int)dVs[var].getValue();
		}
		
		//add other decision variables
		int[] decisionVariables=new int[23];
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
		int startPosManu=10;
		for(int i=0;i<numberOfVariables_;i++){
			decisionVariables[i+startPosManu]=partialDV[i];
		}
		
		/*
		 * makeup some build manufacturing strategies to test  
		 * build two small facilities and four big facilities
		 * starts at 0 and 52 weeks for small
		 * and 		 104 and 208 weeks for big
		 */
		decisionVariables[15]=	-1;
		decisionVariables[16]=	0;
		decisionVariables[17]=	2;
		/*
		int[] buildPartial=CapacityLimitation();
		
		int startPosBuild=18;
		for(int i=0;i<buildPartial.length;i++){
			decisionVariables[i+startPosBuild]=buildPartial[i];
		}
		*/
		
		int startPosBuild=18;
		
		for(int i=0;i<numberOfVariables_;i++){
			decisionVariables[i+startPosBuild]=-1;//no build for all
		}
		

		//
		
		Company company=CompanyFactory.createCompany(decisionVariables);
		
		DrugFactory.AssembleDrugs(company, decisionVariables);
		
		ArrayList<Drug> drugs=(ArrayList<Drug>)company.getParameter("drugs");
		for(Drug d:drugs){
			ArrayList<Event> cts=(ArrayList<Event>)d.getParameter("CTs");
			Scheduling.CTScheduling(cts);
		}
		
		//MonteCarlo simulation
		HashMap<String, Object> result=MonteCarlo.MonteCarlo(company, 800);//running average shows 500 is enough
		double ENPV=(Double)result.get("ENPV");
		double pNPV=(Double)result.get("p(NPV>0)");
		double[] aveTimeToMarket=(double[])result.get("timeToMarket");
		double[] aveTimeFDA_Approval=(double[])result.get("FDA_Approval");
		double[] successCount=(double[])result.get("successCount");
		solution.setObjective(0, -ENPV);
		solution.setObjective(1, -pNPV);
		/*
		 * for recording ave time to market only
		 */
		solution.timeToMarket=aveTimeToMarket;
		solution.successCount=successCount;
		solution.timeFDA_Approval=aveTimeFDA_Approval;
		/*
		 * for recording ave time to market only
		 * end
		 */
		
		//test//start
		//print solution and objective to console
		/*
		String print="ENPV is "+ENPV+", p(NPV>0) is "+pNPV+" decision variables are ";
		for(int i=0;i<decisionVariables.length;i++){
			print=print+"_"+decisionVariables[i];
		}
		System.out.println(print);
		*/
	}

	public static int[] mendBuildBig(int[] decisionVariables){
		
		int startPosition=0;
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
	
	public static ArrayList<ManuFacility> MakeFacilities(Company company){
		ArrayList<ManuFacility> mf=new ArrayList<ManuFacility>();
		String[] nameList={"ALPHA", "BETA", "GAMMA", "DELTA", "EPSILON"};
		for(int i=0;i<numOfFacility;i++){
			HashMap<String, Object> facilityPara=new HashMap<String, Object>();
			facilityPara.put("company", company);
			facilityPara.put("openTime",0);
			facilityPara.put("facilityType", "Big");
			facilityPara.put("facilityID", nameList[i]);
			mf.add(new ManuFacility(facilityPara));
		}
		return mf;
	}
	
	//0 - full capacity
	//1 - slightly limited
	//2 - limited
	//3 - very limited
	//4 - nearly no capacity
	//5 - no capacity
	public static int[] CapacityLimitation(){
		int[] partial=new int[5];
		if(extent==0){
			partial[0]=3;
			partial[1]=3;
			partial[2]=4;
			partial[3]=4;
			partial[4]=4;
		}
		else if(extent==1){
			partial[0]=3;
			partial[1]=4;
			partial[2]=5;
			partial[3]=5;
			partial[4]=6;
		}
		else if(extent==2){
			partial[0]=-1;
			partial[1]=-1;
			partial[2]=2;
			partial[3]=2;
			partial[4]=5;
		}
		else if(extent==3){
			partial[0]=-1;
			partial[1]=-1;
			partial[2]=-1;
			partial[3]=5;
			partial[4]=6;
		}
		else if(extent==4){
			partial[0]=-1;
			partial[1]=-1;
			partial[2]=-1;
			partial[3]=-1;
			partial[4]=4;
		}
		else{
			partial[0]=-1;
			partial[1]=-1;
			partial[2]=-1;
			partial[3]=-1;
			partial[4]=-1;
		}
		
		return partial;
	}
	
	public static String[] changePortfolioComposition(){
		if(portfolioComposition==1){//all niche
			return new String[]{"niche","niche","niche","niche","niche","niche","niche","niche","niche","niche"};
		}
		else if(portfolioComposition==2){//all medium
			return new String[]{"medium","medium","medium","medium","medium","medium","medium","medium","medium","medium"};
		}
		else if(portfolioComposition==3){//all BB
			return new String[]{"BB","BB","BB","BB","BB","BB","BB","BB","BB","BB"};
		}
		else
			return null;
	}
	
}
