package data;

import java.util.HashMap;

public class ManuData {

	public static HashMap<String, Object> createData(){
		
		HashMap<String, Object> data=new HashMap<String, Object>();
		/*
		data.put("BB_PC_duration",			53);
		data.put("BB_PhaseI_duration", 		66);
		data.put("BB_PhaseII_duration",		84);
		data.put("BB_PhaseIII_duration",	106);
		//dummy market manu data
		data.put("BB_Market_duration",		300);
		
		data.put("niche_PC_duration",		31);
		data.put("niche_PhaseI_duration",	 9);
		data.put("niche_PhaseII_duration",	27);
		data.put("niche_PhaseIII_duration",	53);
		//dummy market manu data
		data.put("niche_Market_duration",	300);
		
		data.put("medium_PC_duration",		35);
		data.put("medium_PhaseI_duration",	13);
		data.put("medium_PhaseII_duration",	35);
		data.put("medium_PhaseIII_duration",66);
		//dummy market manu data
		data.put("medium_Market_duration",	300);
		*/
		
		data.put("BB_PC_batchLength",			4);
		data.put("BB_PhaseI_batchLength", 		4);
		data.put("BB_PhaseII_batchLength",		4);
		data.put("BB_PhaseIII_batchLength",		4);
		//dummy market manu data
		data.put("BB_Market_batchLength",		4);
		
		data.put("niche_PC_batchLength",		4);
		data.put("niche_PhaseI_batchLength",	4);
		data.put("niche_PhaseII_batchLength",	4);
		data.put("niche_PhaseIII_batchLength",	4);
		//dummy market manu data
		data.put("niche_Market_batchLength",	4);
		
		data.put("medium_PC_batchLength",		4);
		data.put("medium_PhaseI_batchLength",	4);
		data.put("medium_PhaseII_batchLength",	4);
		data.put("medium_PhaseIII_batchLength",	4);
		//dummy market manu data
		data.put("medium_Market_batchLength",	4);
		
		data.put("BB_PC_batchNumber",			3);
		data.put("BB_PhaseI_batchNumber",		1);
		data.put("BB_PhaseII_batchNumber",		3);
		data.put("BB_PhaseIII_batchNumber",		8);

		
		data.put("niche_PC_batchNumber",		1);
		data.put("niche_PhaseI_batchNumber",	1);
		data.put("niche_PhaseII_batchNumber",	1);
		data.put("niche_PhaseIII_batchNumber",	3);

		
		data.put("medium_PC_batchNumber",		2);
		data.put("medium_PhaseI_batchNumber",	1);
		data.put("medium_PhaseII_batchNumber",	1);
		data.put("medium_PhaseIII_batchNumber",	3);

		
		/*
		 * IMPORTANT!! 
		 * parallel number of batches required for market
		 */
		int numOfMarketYears=8;
		
		
		int[] numBB		={1,3,7,8,8,6,0,0};
		int[] numMedium	={1,1,1,1,1,1,0,0};
		int[] numNiche	={1,1,1,1,1,1,0,0};
		
		int[] bigParallel	=new int[numOfMarketYears*52];
		int[] mediumParallel=new int[numOfMarketYears*52];
		int[] nicheParallel	=new int[numOfMarketYears*52];
		
		int[] yearStart=new int[numOfMarketYears];
		
		for(int i=0;i<numOfMarketYears;i++){
			yearStart[i]=i*52;
			for(int j=yearStart[i];j<yearStart[i]+52;j++){
				bigParallel[j]		=numBB[i];
				mediumParallel[j]	=numMedium[i];
				nicheParallel[j]	=numNiche[i];
			}
		}
		
		data.put("BB_Market_batchParallel", 	bigParallel);
		data.put("medium_Market_batchParallel", mediumParallel);
		data.put("niche_Market_batchParallel", 	nicheParallel);
		
		
		data.put("BB_Market_batchNumber",		364);
		data.put("medium_Market_batchNumber",	45);
		data.put("niche_Market_batchNumber",	11);
		
		//this is the cummulative number of batches required for the first n years, important for splitting batch number
		int[] cumBatches_BB		={10,	39,		114,	207,	300,	364,	364,	364};
		int[] cumBatches_medium	={2,	6,		15,		26,		37,		45,		45,		45};
		int[] cumBatches_niche	={1,	3,		5,		7,		9,		11,		11,		11};
		data.put("BB_Market_cumBatch", cumBatches_BB);
		data.put("medium_Market_cumBatch", cumBatches_medium);
		data.put("niche_Market_cumBatch", cumBatches_niche);
		
		//for CMOvsInHouse problem, close these
		/*
		data.put("BB_PC_in-house_batchUnitCost",		125);
		data.put("BB_PhaseI_in-house_batchUnitCost",	125);
		data.put("BB_PhaseII_in-house_batchUnitCost",	125);
		data.put("BB_PhaseIII_in-house_batchUnitCost",	375);
		data.put("BB_Market_in-house_batchUnitCost",	375);
		
		data.put("niche_PC_in-house_batchUnitCost",			50);
		data.put("niche_PhaseI_in-house_batchUnitCost",		50);
		data.put("niche_PhaseII_in-house_batchUnitCost",	50);
		data.put("niche_PhaseIII_in-house_batchUnitCost",	125);
		data.put("niche_Market_in-house_batchUnitCost",		125);
		
		data.put("medium_PC_in-house_batchUnitCost",		75);
		data.put("medium_PhaseI_in-house_batchUnitCost",	75);
		data.put("medium_PhaseII_in-house_batchUnitCost",	75);
		data.put("medium_PhaseIII_in-house_batchUnitCost",	175);
		data.put("medium_Market_in-house_batchUnitCost",	175);
		
		//data.put("BB_PC_CMO_batchUnitCost",				250);
		//data.put("BB_PhaseI_CMO_batchUnitCost",			250);
		//data.put("BB_PhaseII_CMO_batchUnitCost",		250);
		data.put("BB_PhaseIII_CMO_batchUnitCost",		375);
		data.put("BB_Market_CMO_batchUnitCost",			375);
		
		//data.put("niche_PC_CMO_batchUnitCost",			125);
		//data.put("niche_PhaseI_CMO_batchUnitCost",		125);
		//data.put("niche_PhaseII_CMO_batchUnitCost",		125);
		data.put("niche_PhaseIII_CMO_batchUnitCost",	250);
		data.put("niche_Market_CMO_batchUnitCost",		250);
		
		//data.put("medium_PC_CMO_batchUnitCost",			175);
		//data.put("medium_PhaseI_CMO_batchUnitCost",		175);
		//data.put("medium_PhaseII_CMO_batchUnitCost",	175);
		data.put("medium_PhaseIII_CMO_batchUnitCost",	375);
		data.put("medium_Market_CMO_batchUnitCost",		375);
		*/
		//for CMOvsInhouse problem only
		
		data.put("BB_PC_batchSuccessRate",			1d);
		data.put("BB_PhaseI_batchSuccessRate",		1d);
		data.put("BB_PhaseII_batchSuccessRate",		1d);
		data.put("BB_PhaseIII_batchSuccessRate",	1d);
		//dummy market manu data
		data.put("BB_Market_batchSuccessRate",		1d);
		
		data.put("niche_PC_batchSuccessRate",		1d);
		data.put("niche_PhaseI_batchSuccessRate",	1d);
		data.put("niche_PhaseII_batchSuccessRate",	1d);
		data.put("niche_PhaseIII_batchSuccessRate",	1d);
		//dummy market manu data
		data.put("niche_Market_batchSuccessRate",	1d);
		
		data.put("medium_PC_batchSuccessRate",		1d);
		data.put("medium_PhaseI_batchSuccessRate",	1d);
		data.put("medium_PhaseII_batchSuccessRate",	1d);
		data.put("medium_PhaseIII_batchSuccessRate",1d);
		//dummy market manu data
		data.put("medium_Market_batchSuccessRate",	1d);
		
		data.put("BB_PC_minBatchRequired",			1);
		data.put("BB_PhaseI_minBatchRequired",		1);
		data.put("BB_PhaseII_minBatchRequired",		1);
		data.put("BB_PhaseIII_minBatchRequired",	1);
		//dummy market manu data
		data.put("BB_Market_minBatchRequired",		1);

		data.put("medium_PC_minBatchRequired",			1);
		data.put("medium_PhaseI_minBatchRequired",		1);
		data.put("medium_PhaseII_minBatchRequired",		1);
		data.put("medium_PhaseIII_minBatchRequired",	1);
		//dummy market manu data
		data.put("medium_Market_minBatchRequired",		1);
		
		data.put("niche_PC_minBatchRequired",			1);
		data.put("niche_PhaseI_minBatchRequired",		1);
		data.put("niche_PhaseII_minBatchRequired",		1);
		data.put("niche_PhaseIII_minBatchRequired",		1);
		//dummy market manu data
		data.put("niche_Market_minBatchRequired",		1);
		
		return data;
		
	}
}
