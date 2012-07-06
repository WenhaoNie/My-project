package data;

import java.util.HashMap;

public class CTData {
	
	public static HashMap<String, Object> createData(){
		HashMap<String, Object> data=new HashMap<String, Object>();
		
		/*
		 * duration in weeks
		 * cost in thousands
		 */
		
		//costs
		data.put("BB_Dis_cost",			96);
		data.put("BB_LM_cost",			96);
		data.put("BB_PC_cost",			1026);
		data.put("BB_IND_cost",			77);
		data.put("BB_PhaseI_cost",		429);
		data.put("BB_PhaseII_cost",		577);
		data.put("BB_PhaseIII_cost",	895);
		data.put("BB_FDA_cost",			77);
		
		data.put("medium_Dis_cost",		72);
		data.put("medium_LM_cost",		72);
		data.put("medium_PC_cost",		1000);
		data.put("medium_IND_cost",		77);
		data.put("medium_PhaseI_cost",	433);
		data.put("medium_PhaseII_cost",	643);
		data.put("medium_PhaseIII_cost",981);
		data.put("medium_FDA_cost",		77);
		
		data.put("niche_Dis_cost",		48);
		data.put("niche_LM_cost",		48);
		data.put("niche_PC_cost",		909);
		data.put("niche_IND_cost",		77);
		data.put("niche_PhaseI_cost",	441);
		data.put("niche_PhaseII_cost",	682);
		data.put("niche_PhaseIII_cost",	817);
		data.put("niche_FDA_cost",		77);
		
		//durations
		data.put("BB_Dis_duration",			104);
		data.put("BB_LM_duration",			104);
		data.put("BB_PC_duration",			39);
		data.put("BB_IND_duration",			2);
		data.put("BB_PhaseI_duration",		35);
		data.put("BB_PhaseII_duration",		52);
		data.put("BB_PhaseIII_duration",	95);
		data.put("BB_FDA_duration",			26);
		
		data.put("BB_Market_duration", 		416);
		
		data.put("medium_Dis_duration",			104);
		data.put("medium_LM_duration",			104);
		data.put("medium_PC_duration",			30);
		data.put("medium_IND_duration",			26);
		data.put("medium_PhaseI_duration",		26);
		data.put("medium_PhaseII_duration",		35);
		data.put("medium_PhaseIII_duration",	65);
		data.put("medium_FDA_duration",			26);
		
		data.put("medium_Market_duration", 		416);
		
		data.put("niche_Dis_duration",			104);
		data.put("niche_LM_duration",			104);
		data.put("niche_PC_duration",			22);
		data.put("niche_IND_duration",			26);
		data.put("niche_PhaseI_duration",		17);
		data.put("niche_PhaseII_duration",		22);
		data.put("niche_PhaseIII_duration",		52);
		data.put("niche_FDA_duration",			26);
		
		data.put("niche_Market_duration", 		416);
		
		/*
		 * transition probabilities
		 * in this set up, the BB has the highest risk
		 */
		
		data.put("BB_Dis_TP",			1d);
		data.put("BB_LM_TP",			0.85d);
		data.put("BB_PC_TP",			0.6d);
		data.put("BB_IND_TP",			0.9d);
		data.put("BB_PhaseI_TP",		0.65d);
		data.put("BB_PhaseII_TP",		0.65d);
		data.put("BB_PhaseIII_TP",		0.7d);
		data.put("BB_FDA_TP",			0.9d);
		
		data.put("medium_Dis_TP",			1d);
		data.put("medium_LM_TP",			0.85d);
		data.put("medium_PC_TP",			0.7d);
		data.put("medium_IND_TP",			0.9d);
		data.put("medium_PhaseI_TP",		0.7d);
		data.put("medium_PhaseII_TP",		0.65d);
		data.put("medium_PhaseIII_TP",		0.75d);
		data.put("medium_FDA_TP",			0.9d);
		
		data.put("niche_Dis_TP",			1d);
		data.put("niche_LM_TP",				0.9d);
		data.put("niche_PC_TP",				0.8d);
		data.put("niche_IND_TP",			0.9d);
		data.put("niche_PhaseI_TP",			0.75d);
		data.put("niche_PhaseII_TP",		0.7d);
		data.put("niche_PhaseIII_TP",		0.8d);
		data.put("niche_FDA_TP",			0.9d);
		
		
		return data;
	}
	
	/*
	 * Dummydata created for test purpose
	 */
	public static HashMap<String, Object> createDummyData(){
		HashMap<String, Object> data=new HashMap<String, Object>();
		data.put("BB_Dis_duration",         106);
		data.put("BB_LM_duration",          111);
		data.put("BB_PC_duration",           53);
		data.put("BB_IND_duration",          13);
		data.put("BB_PhaseI_duration",       53);
		data.put("BB_PhaseII_duration",      80);
		data.put("BB_PhaseIII_duration",    106);
		data.put("BB_FDA_duration",          13);
		data.put("BB_Market_duration",		416);

		data.put("medium_Dis_duration",         106);
		data.put("medium_LM_duration",          111);
		data.put("medium_PC_duration",           53);
		data.put("medium_IND_duration",          13);
		data.put("medium_PhaseI_duration",       53);
		data.put("medium_PhaseII_duration",      80);
		data.put("medium_PhaseIII_duration",    106);
		data.put("medium_FDA_duration",          13);
		data.put("medium_Market_duration",		364);

		data.put("niche_Dis_duration",         106);
		data.put("niche_LM_duration",          111);
		data.put("niche_PC_duration",           53);
		data.put("niche_IND_duration",          13);
		data.put("niche_PhaseI_duration",       53);
		data.put("niche_PhaseII_duration",      80);
		data.put("niche_PhaseIII_duration",    106);
		data.put("niche_FDA_duration",          13);
		data.put("niche_Market_duration",		312);

		data.put("BB_Dis_TP",            1d);
		data.put("BB_LM_TP",          0.85d);
		data.put("BB_PC_TP",           0.6d);
		data.put("BB_IND_TP",          0.9d);
		data.put("BB_PhaseI_TP",      0.85d);
		data.put("BB_PhaseII_TP",      0.4d);
		data.put("BB_PhaseIII_TP",    0.65d);
		data.put("BB_FDA_TP",          0.9d);
		
		data.put("medium_Dis_TP",            1d);
		data.put("medium_LM_TP",          0.85d);
		data.put("medium_PC_TP",           0.7d);
		data.put("medium_IND_TP",          0.9d);
		data.put("medium_PhaseI_TP",      0.85d);
		data.put("medium_PhaseII_TP",      0.5d);
		data.put("medium_PhaseIII_TP",    0.75d);
		data.put("medium_FDA_TP",          0.9d);
		
		data.put("niche_Dis_TP",            1d);
		data.put("niche_LM_TP",           0.9d);
		data.put("niche_PC_TP",           0.8d);
		data.put("niche_IND_TP",          0.9d);
		data.put("niche_PhaseI_TP",       0.9d);
		data.put("niche_PhaseII_TP",      0.6d);
		data.put("niche_PhaseIII_TP",    0.85d);
		data.put("niche_FDA_TP",          0.9d);
		
		return data;
	}
	
	public static HashMap<String, Object> createCMOvsInHouseData(){
		HashMap<String, Object> data=new HashMap<String, Object>();
		
		/*
		 * duration in weeks
		 * cost in thousands
		 */
		
		//costs
		data.put("BB_Dis_cost",		48);
		data.put("BB_LM_cost",		48);
		data.put("BB_PC_cost",		909);
		data.put("BB_IND_cost",		77);
		data.put("BB_PhaseI_cost",	441);
		data.put("BB_PhaseII_cost",	682);
		data.put("BB_PhaseIII_cost",	817);
		data.put("BB_FDA_cost",		77);
		
		data.put("medium_Dis_cost",		48);
		data.put("medium_LM_cost",		48);
		data.put("medium_PC_cost",		909);
		data.put("medium_IND_cost",		77);
		data.put("medium_PhaseI_cost",	441);
		data.put("medium_PhaseII_cost",	682);
		data.put("medium_PhaseIII_cost",	817);
		data.put("medium_FDA_cost",		77);
		
		data.put("niche_Dis_cost",		48);
		data.put("niche_LM_cost",		48);
		data.put("niche_PC_cost",		909);
		data.put("niche_IND_cost",		77);
		data.put("niche_PhaseI_cost",	441);
		data.put("niche_PhaseII_cost",	682);
		data.put("niche_PhaseIII_cost",	817);
		data.put("niche_FDA_cost",		77);
		
		data.put("BB_Dis_duration",			104);
		data.put("BB_LM_duration",			104);
		data.put("BB_PC_duration",			22);
		data.put("BB_IND_duration",			26);
		data.put("BB_PhaseI_duration",		17);
		data.put("BB_PhaseII_duration",		22);
		data.put("BB_PhaseIII_duration",		52);
		data.put("BB_FDA_duration",			26);
		data.put("BB_Market_duration", 		416);
		
		data.put("medium_Dis_duration",			104);
		data.put("medium_LM_duration",			104);
		data.put("medium_PC_duration",			22);
		data.put("medium_IND_duration",			26);
		data.put("medium_PhaseI_duration",		17);
		data.put("medium_PhaseII_duration",		22);
		data.put("medium_PhaseIII_duration",		52);
		data.put("medium_FDA_duration",			26);
		data.put("medium_Market_duration", 		416);
		
		data.put("niche_Dis_duration",			104);
		data.put("niche_LM_duration",			104);
		data.put("niche_PC_duration",			22);
		data.put("niche_IND_duration",			26);
		data.put("niche_PhaseI_duration",		17);
		data.put("niche_PhaseII_duration",		22);
		data.put("niche_PhaseIII_duration",		52);
		data.put("niche_FDA_duration",			26);
		data.put("niche_Market_duration", 		416);
		
		return data;
	}
}
