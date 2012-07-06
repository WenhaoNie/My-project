/**
 * 
 */
package util;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import cern.jet.random.Normal;
import cern.jet.random.AbstractDistribution;
import events.Event;

/**
 * @author Wenhao
 *
 */
public class Test2 {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) {
		final int ITERA=100000;
		int[] results=new int[ITERA];
		int[] bin=new int[201];
		for(int i=0;i<201;i++){
			bin[i]=0;
		}
		int duration=100;
		double variance=((double)duration)/100;
		double mean=(double)duration;
		for(int i=0;i<ITERA;i++){
			int result=RandomGenerator.getIntGaussian(mean, variance);
			results[i]=result;
			if(result>=0 && result<=200){
				bin[result]=bin[result]+1;
			}
		}
		
		for(int i=0;i<201;i++){
			System.out.println(i+" - "+bin[i]);
		}
		System.out.println("\ntest results");
		
		
		
	}
	
	public static boolean stop(int time){
		int mean=9;
		double standardDeviation=(double)mean/10;
		Normal normal=new Normal(mean, standardDeviation, Normal.makeDefaultGenerator());
		return RandomGenerator.getBoolean(normal, time);
	}
}
