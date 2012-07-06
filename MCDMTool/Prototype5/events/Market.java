package events;

import java.util.Arrays;
import java.util.HashMap;

import drugs.Drug;

public class Market extends CT {

	public static final double MARGIN=0.5;
	
	public static HashMap<String, Object> data_;
	/*
	 * @stochastic:
	 * int inventory: 
	 * int ongoing
	 */
	
	public Market(HashMap<String, Object> parameters) {
		super(parameters);
		
		this.setStochastic("inventory", 0);
		
	}
	
	@Override
	public void FillData(){
		/*
		 * split the market revenue and material requirement
		 */
		Drug d=(Drug)this.getParameter("drug");
		String key		=(String)d.getParameter("drugType")+"_";
		String eventName=(String)this.getParameter("eventName");
		int[] revenue	=(int[])data_.get(key+"revenue");
		int[] matReq	=(int[])data_.get(key+"matReq");
		
		if(eventName=="Market"){
			this.setParameter("matReq", matReq);
			this.setParameter("revenue", revenue);
		}
		else if(eventName=="Market_1"){
			int duration=(Integer)this.getParameter("duration");
			this.setParameter("revenue", Arrays.copyOfRange(revenue, 0, duration));
			this.setParameter("matReq", Arrays.copyOfRange(matReq, 0, duration));
		}
		else if(eventName=="Market_2"){
			int duration=(Integer)this.getParameter("duration");
			this.setParameter("revenue", Arrays.copyOfRange(revenue, revenue.length-duration, revenue.length));
			this.setParameter("matReq", Arrays.copyOfRange(matReq, matReq.length-duration, matReq.length));
		}
		else
			System.out.println("Market eventName error!");
		
		//now load duration, but market_1 and market_2's duration's already been assigned
		if(eventName=="Market"){
			key=(String)(d.getParameter("drugType"))+"_"+(String)(this.getParameter("eventName"))+"_duration";
			this.setParameter("duration", CT.data_.get(key));
		}
		
	}
	
	@Override
	public void Triggering(int currentTime){
		//market events all need manu to back up
		if(!(Boolean)this.getStochastic("triggered")
				&&(Boolean)this.getStochastic("goAhead1")    //need the last CT to be finished
				&&(Boolean)this.getStochastic("goAhead2")){  //need the manu to be finished

			
			this.setStochastic("triggered", true);
			this.setStochastic("startTime", currentTime);
			//no stochastic time length here
			int duration=(Integer)this.getParameter("duration");
			this.setStochastic("duration", duration);
			this.setStochastic("ongoing", 0);
		}
		
	}

	@Override
	public int Progressing(int currentTime){
		
		int cashFlow=0;
		
		if((Boolean)this.getStochastic("triggered")&& !(Boolean)this.getStochastic("ended")){

			int ongoing		=(Integer)this.getStochastic("ongoing");
			int matReq		=((int[])this.getParameter("matReq"))[ongoing];
			int inventory	=(Integer)this.getStochastic("inventory");
			if(inventory>=matReq){
				inventory=inventory-matReq;
				//test//recording the time to market
				Drug d=(Drug)this.getParameter("drug");
				if((Integer)d.getStochastic("timeToMarket")==-1){
					d.setStochastic("timeToMarket", currentTime);
				}
				//test//end
				cashFlow=CashFlow();
				this.setStochastic("inventory", inventory);
			}
			ongoing=ongoing+1;
			this.setStochastic("ongoing", ongoing);
			int duration=(Integer)this.getStochastic("duration");
			if(ongoing==duration){
				this.Finishing(currentTime);
			}
			
		}
		
		return cashFlow;

	}
	
	@Override
	public void Finishing(int currentTime){
		this.setStochastic("ended", true);
		this.setStochastic("endTime", currentTime);
		if(this.getParameter("next")!=null){
			Event nextEvent=(Event)this.getParameter("next");
			nextEvent.setStochastic("goAhead1", true);
			nextEvent.setStochastic("inventory", this.getStochastic("inventory"));//pass inventory to the next
		}
	}
	
	@Override
	public int CashFlow(){
		int ongoing=(Integer)this.getStochastic("ongoing");
		int cashFlow=(int)(((int[])this.getParameter("revenue"))[ongoing]*MARGIN);//change the margin to 0.5 for CMO vs Inhouse		
		return cashFlow;
	}
}