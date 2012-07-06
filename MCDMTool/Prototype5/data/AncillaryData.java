package data;

import java.util.HashMap;

public class AncillaryData {

	
	public static HashMap<String, Object> createData(){
		HashMap<String, Object> data=new HashMap<String, Object>();
		
		data.put("TechLab_duration", 			62);
		data.put("ScaleUp_duration", 			53);
		data.put("CMO_Preparation_duration", 	40);
		data.put("TechTransfer_duration", 		27);
		data.put("Build_Small_duration", 		29);
		data.put("Build_Big_duration", 			133);
		
		//unit cost in thousands
		data.put("TechLab_cost", 			161);
		data.put("ScaleUp_cost", 			189);
		data.put("CMO_Preparation_cost", 	0);
		data.put("TechTransfer_cost", 		93);
		data.put("Build_Small_cost", 		241);
		data.put("Build_Big_cost", 			1232);
		
		return data;
	}
}
