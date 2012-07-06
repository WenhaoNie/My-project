package events;

import java.util.ArrayList;
import java.util.HashMap;

import util.RandomGenerator;

import company.Company;
import company.ManuFacility;
import drugs.Drug;

public class PD extends Ancillary {

	public PD(HashMap<String, Object> parameters) {
		super(parameters);
	}
	
	@Override
	public void Triggering(int currentTime){
		if(!(Boolean)this.getStochastic("triggered")
				//requires Ancillary events
				&&(Boolean)this.getStochastic("goAhead3")
				){
			Drug drug=(Drug)this.getParameter("drug");
			Company cmy=(Company)drug.getParameter("company");
			ArrayList<ManuFacility> mfs=(ArrayList<ManuFacility>)cmy.getStochastic("manuFacilities");
			
			//test//start
			boolean test=true;
			//test//end
			if(this.getParameter("eventStatus")=="in-house"){

				if(mfs!=null){
					for(ManuFacility facility:mfs){
						if((String)facility.getParameter("facilityType")==(String)this.getParameter("manuType")
								&&!(Boolean)facility.getStochastic("isOccupied")
								&&(String)facility.getParameter("facilityID")!="CMOFacility"){
							//test//start
							boolean test_1=true;
							//test//end
							
							this.setStochastic("triggered", true);
							this.setStochastic("startTime", currentTime);
							this.setStochastic("facility", facility);
							double mean=(double)(Integer)this.getParameter("duration");
							double variance=mean/10; //or can be defined otherwise
							int duration=RandomGenerator.getIntGaussian(mean, variance);
							this.setStochastic("duration", duration);
							this.setStochastic("ongoing", 0);//when triggered, ongoing is 0;
							facility.addManu(this);//will set isOccupied to be true
							
							break;
						}
						//test//start//calculating delay time only
						else if((String)this.getParameter("manuType")=="Big"){
							Manu.Time_Of_Delay=Manu.Time_Of_Delay+1;
						}
						//test//end
					}
				}
			}
			else{
				this.setStochastic("triggered", true);
				this.setStochastic("startTime", currentTime);
				for(ManuFacility facility:mfs){
					if((String)facility.getParameter("facilityID")=="CMOFacility"){
						this.setStochastic("facility", facility);
						double mean=(double)(Integer)this.getParameter("duration");
						double variance=mean/10; //or can be defined otherwise
						int duration=RandomGenerator.getIntGaussian(mean, variance);
						this.setStochastic("duration", duration);
						this.setStochastic("ongoing", 0);//when triggered, ongoing is 0;
						facility.addManu(this);//will set isOccupied to be true
					
						break;
					}
				}
			}
		}
	}
	
	@Override
	public void Finishing(int time){
		this.setStochastic("ended", true);
		this.setStochastic("endTime", time);
		this.setStochastic("successful", true);
		((ManuFacility)this.getStochastic("facility")).setStochastic("isOccupied", false);
		/*
		 * give permission for next
		 */
		if(this.getParameter("next")!=null){
			Event next=(Event)this.getParameter("next");
			next.setStochastic("goAhead3", true);
		}
	}
	

	@Override
	public void Interrupted(int time){
		this.setStochastic("ended", true);
		this.setStochastic("endTime", time);
		this.setStochastic("successful", false);
		((ManuFacility)this.getStochastic("facility")).setStochastic("isOccupied", false);
	}	

}
