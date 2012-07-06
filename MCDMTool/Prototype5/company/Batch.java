package company;

import java.util.HashMap;

import events.Manu;

import util.RandomGenerator;

/*
 * this class represents the fermentation batches in stochastic simulation
 * only exists when BMM is implemented
 */
public class Batch {
	
	/*
	 * @parameters:
	 * int batchLength
	 * int batchUnitCost
	 * double batchSuccessRate
	 */
	
	protected HashMap<String, Object> parameters_;
	
	/*
	 * @stochastic:
	 * Manu manu //the manufacturing event it serving
	 * int ongoing //how long has the manufacturing last
	 * boolean isSuccessful
	 * boolean ended
	 * 
	 */
	
	protected HashMap<String, Object> stochastic_;
	
	public Batch(HashMap<String, Object> parameters, Manu manu){
		parameters_=parameters;
		stochastic_=new HashMap<String, Object>();
		//setStochastic("isSuccess", false);
		setStochastic("ongoing", 0);
		setStochastic("Manu", manu);
	}
	
	public int Progressing(){//return the cost generated from progressing this batch
		int ongoing=(Integer)this.getStochastic("ongoing")+1;
		this.setStochastic("ongoing", ongoing);
		if((Integer)this.getParameter("batchLength")==ongoing){
			if(RandomGenerator.getBoolean((Double)this.getParameter("batchSuccessRate"))){
				this.setStochastic("isSuccessful", true);
			}
			else{
				this.setStochastic("isSuccessful", false);
			}
			this.setStochastic("ended", true);
		}
		else
			this.setStochastic("ended", false);
		return (Integer)this.getParameter("batchUnitCost");
	}
	
	public void setParameter(String name, Object value){
		parameters_.put(name, value);
	}
	
	public Object getParameter(String name){
		return parameters_.get(name);
	}
	
	public void setStochastic(String name, Object value){
		stochastic_.put(name, value);
	}
	
	public Object getStochastic(String name){
		return stochastic_.get(name);
	}

}
