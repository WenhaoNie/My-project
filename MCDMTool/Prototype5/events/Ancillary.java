package events;

import java.util.HashMap;

import util.RandomGenerator;

public class Ancillary extends Event {
	
	/*
	 * Ancillary events includes:
	 * 
	 * Pilot plant production related:
	 * BuildSmall (build pilot plant facility for small scale manufacturing purpose)
	 * TechLab (process development for small scale production)
	 * 
	 * CMO related: (CMO only covers large scale)
	 * CMO_Preparation (including source a CMO and negotiation for price, this takes no cost)
	 * TechTransfer (for CMO production, takes cost)
	 * 
	 * Large scale related:
	 * ScaleUp (process development for large scale production)
	 * BuildLarge (including construction, installation, qualification & validation)
	 * 
	 */
	
	/*
	 * order of ancillary events:
	 * 
	 * small scale:
	 * BuildSmall -> TechLab -> Manu_PCT, the production for PhaseI and PhaseII doesn't need extra TechLab process development
	 * 
	 * large scale(in-house):
	 * BuildLarge -> ScaleUp -> Manu_PhaseIII, the production for Market doesn't need extra ScaleUp study
	 * or
	 * BuildLarge -> Manu_Market (ScaleUp already done on CMO or other facilities)
	 * 
	 * large scale(CMO):
	 * CMO_Preparation -> TechTransfer -> ScaleUp -> Manu_PhaseIII
	 * or
	 * CMO_Preparation -> TechTransfer -> Manu_Market (ScaleUp should be done before PhaseIII production)
	 * 
	 */

	public static HashMap data_;
	
	public Ancillary(HashMap<String, Object> parameters) {
		super(parameters);
		FillData();
		// TODO Auto-generated constructor stub
	}

	@Override
	public void FillData() {
		if((String)this.getParameter("eventType")=="Build"){
			String key=(String)this.getParameter("eventType")+"_"
						+(String)this.getParameter("buildType")+"_";
			this.setParameter("duration", data_.get(key+"duration"));
			this.setParameter("cost", data_.get(key+"cost"));
		}
		else{
			String key=(String)(this.getParameter("eventName"))+"_";
			this.setParameter("duration", data_.get(key+"duration"));
			this.setParameter("cost", data_.get(key+"cost"));
		}
	}

	@Override
	public void RandomGeneration() {
		// TODO Auto-generated method stub
		
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
	public void Interrupted(int time) {
		this.setStochastic("ended", true);
		this.setStochastic("successful", false);
		this.setStochastic("endTime", time);
	}

	@Override
	public void Finishing(int time) {
		this.setStochastic("ended", true);
		this.setStochastic("endTime", time);
		this.setStochastic("successful", true);
		if(this.getParameter("next")!=null){
			Event nextEvent=(Event)this.getParameter("next");
			nextEvent.setStochastic("goAhead3", true);
		}
	}

}
