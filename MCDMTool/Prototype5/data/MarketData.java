package data;

import java.util.HashMap;

public class MarketData {

	public static HashMap<String, Object> createData(){
		
		HashMap<String, Object> data=new HashMap<String, Object>();
		
		int numOfMarketYears=8;
		
		/*
		 * create an array of revenue for the market duration, which in this case is 8 years
		 * 8 years=8*52=416 weeks
		 */
		
		int[] bigAnnualSales	={	4087,
									12019,
									31250,
									38462,
									38462,
									26442,
									0,
									0};
		int[] mediumAnnualSales	={	457,
									1322,
									3365,
									4567,
									4567,
									3005,
									0,
									0};
		int[] nicheAnnualSales	={	240,
									481,
									481,
									481,
									481,
									481,
									0,
									0};
		
		int[] bigSales		=new int[numOfMarketYears*52];
		int[] mediumSales	=new int[numOfMarketYears*52];
		int[] nicheSales	=new int[numOfMarketYears*52];
		
		int[] yearStart=new int[numOfMarketYears];
		for(int i=0;i<numOfMarketYears;i++){
			yearStart[i]=i*52;
			for(int j=yearStart[i];j<yearStart[i]+52;j++){
				bigSales[j]=bigAnnualSales[i];
				mediumSales[j]=mediumAnnualSales[i];
				nicheSales[j]=mediumAnnualSales[i];
			}
		}
		
		data.put("BB_revenue", 			bigSales);
		data.put("medium_revenue", 		mediumSales);
		data.put("niche_revenue", 		nicheSales);
		//sales
		
		/*
		 * material requirement: an 8 int array
		 * 
		 */
		int[] bigAnnualReq		={	189,
									555,
									1442,
									1775,
									1775,
									1220,
									0,
									0};
		
		int[] mediumAnnualReq	={	21,
									61,
									155,
									211,
									211,
									139,
									0,
									0};
		
		int[] nicheAnnualReq	={	11,
									22,
									22,
									22,
									22,
									22,
									0,
									0};
		
		int[] bigReq	=new int[numOfMarketYears*52];
		int[] mediumReq	=new int[numOfMarketYears*52];
		int[] nicheReq	=new int[numOfMarketYears*52];
		
		for(int i=0;i<numOfMarketYears;i++){
			yearStart[i]=i*52;
			for(int j=yearStart[i];j<yearStart[i]+52;j++){
				bigReq[j]=bigAnnualReq[i];
				mediumReq[j]=mediumAnnualReq[i];
				nicheReq[j]=nicheAnnualReq[i];
			}
		}
		
		data.put("BB_matReq", 		bigReq);
		data.put("medium_matReq", 	mediumReq);
		data.put("niche_matReq", 	nicheReq);
		
		
		return data;
		
	}	
}
