package events;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import util.RandomGenerator;

import company.Batch;
import company.Company;
import company.ManuFacility;

import drugs.Drug;

public class Manu extends Event {

	//test//start
	public static int Time_Of_Delay;//for calculating manu delay only
	public static int Time_Of_Idle;//for calculating facility idle only
	//test//end
	
	/*
	 * @parameters_: default
	 * 
	 * 
	 * 
	 * if we use BMM, these parameters must be given:
	 * int batchNumber;
	 * int batchLength;
	 * int batchUnitCost; //in terms of unit cost
	 * double batchSuccessRate;
	 * int minBatchRequired; the batch number required before CT can be started
	 * PD PD; process development events for manu, should be null for PhaseI, PhaseII and Market
	 * int ctLead; for planning only. calculate start times of manu based on its ct
	 * 
	 * manuType	=String null(big/small)
	 * 
	 * 
	 * @stochastic_: default
	 * ManuFacility	facility=null;
	 * Batch currentBatch
	 * ArrayList<Batch> batches
	 * int successfulBatchesDone
	 */
	
	public Manu(HashMap<String, Object> parameters) {
		super(parameters);
		setParameter("eventType", "Manu");
		//stochastic_=new HashMap<String, Object>();
		FillData();
		if(this.getParameter("modelType")=="BMM"){
			ArrayList<Batch> batches=new ArrayList<Batch>();
			setStochastic("batches", batches);
			if((String)this.getParameter("eventName")=="Market"
					|| (String)this.getParameter("eventName")=="Market_1"
					|| (String)this.getParameter("eventName")=="Market_2"){
				ArrayList<Batch> currentbatches=new ArrayList<Batch>();
				setStochastic("currentBatch", currentbatches);
			}
		}
	}
	
	@Override
	public void ResetStochastic(){
		super.ResetStochastic();
		if(this.getParameter("modelType")=="BMM"){
			ArrayList<Batch> batches=new ArrayList<Batch>();
			setStochastic("batches", batches);
			if((String)this.getParameter("eventName")=="Market"
					|| (String)this.getParameter("eventName")=="Market_1"
					|| (String)this.getParameter("eventName")=="Market_2"){
				ArrayList<Batch> currentbatches=new ArrayList<Batch>();
				setStochastic("currentBatch", currentbatches);
			}
			this.setStochastic("successfulBatchesDone", 0);
		}
	}
	
	public static HashMap data_;
	
	@Override
	public void FillData(){
		/*
		 * fill dummy data for test purpose
		 */
		String eventName		=(String)this.getParameter("eventName");
		String eventStatus		=(String)this.getParameter("eventStatus");
		
		String key				=null;
		String ctLead			="ctLead";//duration=minBatchRequired*batchLength
		String batchParallel	="batchParallel";
		String batchLength		="batchLength";
		String batchNumber		="batchNumber";
		String batchUnitCost	="batchUnitCost";
		String batchSuccessRate	="batchSuccessRate";
		String minBatchRequired	="minBatchRequired";
		/*
		 * these are for the BMM model
		 */
		if(eventName=="Market"){
			key=(String)((Drug)this.getParameter("drug")).getParameter("drugType")
					+"_"+eventName;
			this.setParameter(batchParallel, data_.get(key+"_"+batchParallel));
			this.setParameter(batchNumber,	data_.get(key+"_"+batchNumber));
		}
		else if(eventName=="Market_1"){
			key=(String)((Drug)this.getParameter("drug")).getParameter("drugType")
					+"_Market";
			int manuDuration=(Integer)this.getParameter("duration");
			int[] parallel=(int[])data_.get(key+"_batchParallel");
			this.setParameter(batchParallel, Arrays.copyOfRange(parallel, 0, manuDuration));
			int[] cumBatch=(int[])data_.get(key+"_cumBatch");
			int year=manuDuration/52; //calculate how many years that manu for market goes
			this.setParameter(batchNumber, cumBatch[year-1]);
		}
		else if(eventName=="Market_2"){
			key=(String)((Drug)this.getParameter("drug")).getParameter("drugType")
					+"_Market";
			int manuDuration=(Integer)this.getParameter("duration");
			int[] parallel=(int[])data_.get(key+"_batchParallel");
			this.setParameter(batchParallel, Arrays.copyOfRange(parallel, parallel.length-manuDuration, parallel.length));
			int[] cumBatch=(int[])data_.get(key+"_cumBatch");
			int totalBatch=(Integer)data_.get(key+"_"+batchNumber);
			int year=manuDuration/52; //calculate how many years that manu for market goes
			this.setParameter(batchNumber, totalBatch-cumBatch[cumBatch.length-year-1]);
		}		
		else{
			key=(String)((Drug)this.getParameter("drug")).getParameter("drugType")+"_"+(String)this.getParameter("eventName");
			this.setParameter(batchNumber, data_.get(key+"_"+batchNumber));
		}
		this.setParameter(batchLength,		data_.get(key+"_"+batchLength));
		this.setParameter(batchUnitCost,	data_.get(key+"_"+eventStatus+"_"+batchUnitCost));
		this.setParameter(batchSuccessRate, data_.get(key+"_"+batchSuccessRate));	
		this.setParameter(minBatchRequired, data_.get(key+"_"+minBatchRequired));
		this.setParameter(ctLead, (Integer)this.getParameter(batchLength)*(Integer)this.getParameter(minBatchRequired));//duration=batchLength*minBatchRequired for planning purpose
		//this.setParameter(actualDuration, (Integer)this.getParameter(batchLength)*(Integer)this.getParameter(batchNumber));//actualDuration=batchLength*batchNumber;			
		this.setStochastic("successfulBatchesDone", 0);
	}
	
	@Override
	public void Triggering(int time){//decides in stochastic simulation how a manu event is triggered
		if(!(Boolean)this.getStochastic("triggered")
				//requires Ancillary events
				&&(Boolean)this.getStochastic("goAhead3")
				){
			Drug drug=(Drug)this.getParameter("drug");
			Company cmy=(Company)drug.getParameter("company");
			ArrayList<ManuFacility> mfs=(ArrayList<ManuFacility>)cmy.getStochastic("manuFacilities");
			
			if(this.getParameter("eventStatus")=="in-house"){

				if(mfs!=null){
					//test//start
					boolean test=true;
					//test//end
					for(ManuFacility facility:mfs){
						if((String)facility.getParameter("facilityType")==(String)this.getParameter("manuType")
								&&!(Boolean)facility.getStochastic("isOccupied")
								&&(String)facility.getParameter("facilityID")!="CMOFacility"){
							//test//start
							boolean test_1=true;
							//test//end
							
							this.setStochastic("triggered", true);
							this.setStochastic("startTime", time);
							this.setStochastic("facility", facility);
							//this.setStochastic("ongoing", 0);//when triggered, ongoing is 0;
							facility.addManu(this);
							if(this.getParameter("modelType")=="BMM"){
								if((String)this.getParameter("eventName")=="Market"
										|| (String)this.getParameter("eventName")=="Market_1"
										|| (String)this.getParameter("eventName")=="Market_2"){
									int parallel=((int[])this.getParameter("batchParallel"))[0];
									for(int i=0;i<parallel;i++){
										Batch newBatch=facility.startNewBatch(this);
										((ArrayList<Batch>)this.getStochastic("batches")).add(newBatch);
										((ArrayList<Batch>)this.getStochastic("currentBatch")).add(newBatch);
									}							
								}
								else{
									Batch newBatch=facility.startNewBatch(this);
									((ArrayList<Batch>)this.getStochastic("batches")).add(newBatch);//start the first batch
									this.setStochastic("currentBatch", newBatch);
								}
							}
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
				this.setStochastic("startTime", time);
				for(ManuFacility facility:mfs){
					if((String)facility.getParameter("facilityID")=="CMOFacility"){
						this.setStochastic("facility", facility);
						facility.addManu(this);
						if(this.getParameter("modelType")=="BMM"){
							if((String)this.getParameter("eventName")=="Market"
									|| (String)this.getParameter("eventName")=="Market_1"
									|| (String)this.getParameter("eventName")=="Market_2"){
								int parallel=((int[])this.getParameter("batchParallel"))[0];
								for(int i=0;i<parallel;i++){
									Batch newBatch=facility.startNewBatch(this);
									((ArrayList<Batch>)this.getStochastic("batches")).add(newBatch);
									((ArrayList<Batch>)this.getStochastic("currentBatch")).add(newBatch);
								}							
							}
							else{
								Batch newBatch=facility.startNewBatch(this);
								((ArrayList<Batch>)this.getStochastic("batches")).add(newBatch);//start the first batch
								this.setStochastic("currentBatch", newBatch);
							}
						}
						break;
					}
				}
			}
		}
	}
	
	public int BMMProgressing(int time){// progressing of manu events when modelType is BMM
		
		int cashFlow=0;
		
		if((String)this.getParameter("eventName")=="Market"
				|| (String)this.getParameter("eventName")=="Market_1"
				|| (String)this.getParameter("eventName")=="Market_2"){
			cashFlow=-ParallelProgressing(time);
		}
		else{
			Batch currentBatch=(Batch)this.getStochastic("currentBatch");
			CT ct=(CT)this.getParameter("next");
			int successfulBatchesDone=(Integer)this.getStochastic("successfulBatchesDone");
			cashFlow=-currentBatch.Progressing();
			if((Boolean)currentBatch.getStochastic("ended")){
				if((Boolean)currentBatch.getStochastic("isSuccessful")){
					successfulBatchesDone=successfulBatchesDone+1;
					this.setStochastic("successfulBatchesDone", successfulBatchesDone);
					/*
					 * important: this decides if the ct can be triggered from the already done batches
					 */
					if(successfulBatchesDone==(Integer)this.getParameter("minBatchRequired")){
						ct.setStochastic("goAhead2",true);
					}//
					
					if(successfulBatchesDone==(Integer)this.getParameter("batchNumber")){
						this.Finishing(time);
					}
					else{
						Batch newBatch=((ManuFacility)this.getStochastic("facility")).startNewBatch(this);
						((ArrayList<Batch>)this.getStochastic("batches")).add(newBatch);
						this.setStochastic("currentBatch", newBatch);
					}
				}
				else{
					Batch newBatch=((ManuFacility)this.getStochastic("facility")).startNewBatch(this);
					((ArrayList<Batch>)this.getStochastic("batches")).add(newBatch);
					this.setStochastic("currentBatch", newBatch);
				}
			}
			this.setStochastic("successfulBatchesDone", successfulBatchesDone);
		}
		
		return cashFlow;
	}
	
	public int ParallelProgressing(int time){// progressing of manu events when parallel batches are run
		
		int cost=0;
		
		int ongoing=(Integer)this.getStochastic("ongoing")+1;//ongoing+1
		ArrayList<Batch> currentBatches=(ArrayList<Batch>)this.getStochastic("currentBatch");
		CT ct=(CT)this.getParameter("next");
		int inventory=(Integer)ct.getStochastic("inventory");
		int sucBatchesDone=(Integer)this.getStochastic("successfulBatchesDone");
		//progressing the current batches
		ArrayList<Batch> batchesToRemove=new ArrayList<Batch>();
		for(Batch currentBatch:currentBatches){
			cost=cost+currentBatch.Progressing();
			if((Boolean)currentBatch.getStochastic("ended")){
				if((Boolean)currentBatch.getStochastic("isSuccessful")){
					sucBatchesDone=sucBatchesDone+1;
					inventory=inventory+1000; //1000 here is the per batch contribution in terms of unit consumption
				}
				batchesToRemove.add(currentBatch);//no matter it is finished or not, remove it from current batches
			}
		}
		currentBatches.removeAll(batchesToRemove);
		batchesToRemove.clear();
		
		//give all inventory to ct
		ct.setStochastic("inventory", inventory);
		
		//decide if it is enough batches
		if(sucBatchesDone>=(Integer)this.getParameter("minBatchRequired"))
			ct.setStochastic("goAhead2",true);
		
		//decide if manu should be finished
		if(sucBatchesDone>=(Integer)this.getParameter("batchNumber")){
			this.Finishing(time);
		}
		else{ //if not finished, check if needs to start more batches
			int parallel=((int[])this.getParameter("batchParallel"))[ongoing];
			int different=parallel-currentBatches.size();
			if(different!=0){
				for(int i=0;i<different;i++){
					Batch newBatch=((ManuFacility)this.getStochastic("facility")).startNewBatch(this);
					((ArrayList<Batch>)this.getStochastic("batches")).add(newBatch);
					currentBatches.add(newBatch);
				}
			}
		}
		
		//return ongoing and successful batches
		this.setStochastic("ongoing", ongoing);
		this.setStochastic("successfulBatchesDone", sucBatchesDone);
		
		return cost;
	}
	
	@Override
	public void Finishing(int time){
		this.setStochastic("ended", true);
		this.setStochastic("endTime", time);
		this.setStochastic("successful", true);
		((ManuFacility)this.getStochastic("facility")).setStochastic("isOccupied", false);
		if((String)this.getParameter("modelType")!="BMM"){
			CT ct=(CT)this.getParameter("next");
			ct.setStochastic("goAhead2", true);
		}
	}
	
	@Override
	public void Interrupted(int time){
		this.setStochastic("ended", true);
		this.setStochastic("endTime", time);
		this.setStochastic("successful", false);
		((ManuFacility)this.getStochastic("facility")).setStochastic("isOccupied", false);
		if((String)this.getParameter("modelType")=="BMM"){
			this.setStochastic("currentBatch", null);
		}
	}

	@Override
	public void RandomGeneration() {
		double duration=(double)(Integer)this.getParameter("duration");
		double variance=duration/10;//should be decided by duration
		this.setStochastic("duration", RandomGenerator.getIntGaussian(duration, variance));
	}
}
