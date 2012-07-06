package events;

import java.util.ArrayList;
import java.util.HashMap;

import util.RandomGenerator;

import company.Company;
import company.ManuFacility;

import drugs.Drug;

public class Build extends Ancillary {
	
	/*
	 * parameters:
	 * String buildType	=Big; Small
	 */

	public Build(HashMap<String, Object> parameters) {
		super(parameters);
	}
	
	@Override
	public void Triggering(int currentTime) {
		//similar to CTs
		if(!(Boolean)this.getStochastic("triggered")
			&& (Boolean)this.getStochastic("goAhead3")){
			this.setStochastic("triggered", true);
			this.setStochastic("startTime", currentTime);
			double mean=(double)(Integer)this.getParameter("duration");
			double variance=mean/10;//or it can be defined here
			int duration=RandomGenerator.getIntGaussian(mean, variance);
			this.setStochastic("duration", duration);
			this.setStochastic("ongoing", 0);
		}
	}
	
	@Override
	public void Finishing(int time){
		this.setStochastic("ended", true);
		this.setStochastic("endTime", time);
		this.setStochastic("successful", true);
		
		Company cmy=(Company)this.getParameter("company");
		String facilityType=(String)this.getParameter("buildType");
		String facilityID=(String)this.getParameter("eventName");
		
		HashMap<String,Object> facilityPara=new HashMap<String, Object>();
		facilityPara.put("company", cmy);
		facilityPara.put("openTime", time);
		facilityPara.put("facilityType", facilityType);
		facilityPara.put("facilityID", facilityID);
		
		ArrayList<ManuFacility> mf=(ArrayList<ManuFacility>)cmy.getStochastic("manuFacilities");
		mf.add(new ManuFacility(facilityPara));
	}
}
