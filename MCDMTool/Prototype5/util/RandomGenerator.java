package util;

import java.util.ArrayList;

public class RandomGenerator {
	
	private static java.util.Random rnd=null;
	
	private RandomGenerator(){
		if(rnd==null){
			rnd=new java.util.Random();
		}
	}
	
	private static double getGaussian(double mean, double variance){
		if(rnd==null)
			new RandomGenerator();
		return mean+rnd.nextGaussian()*variance;
	}
	
	public static int getIntGaussian(double mean, double variance){//this method is for generating random duration
		double result=getGaussian(mean, variance);

		return (int)Math.round(result);
	}
	
	public static boolean getBoolean(double tp){
		if(rnd==null)
			new RandomGenerator();
		boolean successful;
		if(rnd.nextDouble()>tp)
			successful=false;
		else
			successful=true;
		return successful;
	}
	
	public static int ramdomPick(ArrayList<Integer> selections){
		if(rnd==null)
			new RandomGenerator();
		
		double random=rnd.nextDouble();
		
		int index=0;
		
		for(int i=0;i<selections.size();i++){
			if(random<1/selections.size()){
				index=i;
				break;
			}
		}
		
		return selections.get(index);
	}
	
	public static boolean getBoolean(cern.jet.random.Normal distribution, double time){
		if(rnd==null)
			new RandomGenerator();
		boolean stop;
		if(time==0)
			stop=false;
		//else if(rnd.nextDouble()<(distribution.cdf(time)-distribution.cdf(time-1))/(1-distribution.cdf(0)))
		else if(rnd.nextDouble()<1-(1-distribution.cdf(time))/(1-distribution.cdf(time-1)))
			stop=true;
		else
			stop=false;
		return stop;
	}
}
