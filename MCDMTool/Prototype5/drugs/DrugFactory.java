package drugs;

import java.util.ArrayList;
import java.util.HashMap;

import mcdmProblem.CMOvsInHouseProblem;
import mcdmProblem.SolutionTranslator;

import company.Company;
import company.ManuFacility;
import data.CTData;
import events.CT;
import events.Event;
import events.EventFactory;
import events.Manu;

public class DrugFactory {

	public static Drug createDrug(String drugType, HashMap<String, Object> parameters){
		if(drugType.equalsIgnoreCase("BB"))
			return new Drug_BB(parameters);
		else if(drugType.equalsIgnoreCase("medium"))
			return new Drug_medium(parameters);
		else if(drugType.equalsIgnoreCase("niche"))
			return new Drug_niche(parameters);
		else
			System.out.println("drugType error!!");
			return null;
	}
	
	public static void AssembleDrugs(Company cmy, int[] decisionVariables){
		//these are the lists for Company to include in its parameter
		/*
		ArrayList<Event> allEvents=new ArrayList<Event>();
		ArrayList<Event> manuEquivalents=new ArrayList<Event>();
		ArrayList<Event> allCTs=new ArrayList<Event>();
		ArrayList<Drug> drugs=new ArrayList<Drug>();
		 */
		
		ArrayList<Event> 	allEvents		=(ArrayList<Event>)cmy.getParameter("allEvents");
		ArrayList<Event> 	manuEquivalents	=(ArrayList<Event>)cmy.getParameter("manuEquivalents");
		ArrayList<Event> 	allCTs			=(ArrayList<Event>)cmy.getParameter("allCTs");
		ArrayList<Drug> 	drugs			=(ArrayList<Drug>)cmy.getParameter("drugs");
		
		
		/*
		 * initialise drugs according to decisinVariables
		 */
		//if(decisionVariable==){
			/*
			 * assuming there are 5 drugs, launch time: A_0; B_14; C_24; D_35; E_55 
			 */
			String[] drugNames={"A","B","C","D","E","F","G","H","I","J"};
			String[] drugTypes=CMOvsInHouseProblem.changePortfolioComposition();
			int[] launchTimes=SolutionTranslator.TranslateLaunchTime(decisionVariables);
			final int num_Of_Drugs=5;
			for(int i=0;i<num_Of_Drugs;i++){
				HashMap<String, Object> dPara=new HashMap<String, Object>();
				dPara.put("drugID", drugNames[decisionVariables[i]-1]);
				dPara.put("drugNumber", i+1);
				dPara.put("launchTime",launchTimes[i]);
				dPara.put("company", cmy);
				drugs.add(createDrug(drugTypes[decisionVariables[i]-1], dPara));
			}
			
		//}
		
		/*
		 * foreach drug in drugs, assembles events and add events to Company's event list 
		 */
		for(Drug drug:drugs){
			EventFactory.AssembleEvents(drug, decisionVariables);
			allEvents.addAll((ArrayList<Event>)drug.getParameter("events"));
			allCTs.addAll((ArrayList<Event>)drug.getParameter("CTs"));
			manuEquivalents.addAll((ArrayList<Event>)drug.getParameter("Manus"));
			for(Event e:(ArrayList<Event>)drug.getParameter("Ancillarys")){
				if((String)e.getParameter("eventName")=="TechLab")
					manuEquivalents.add(e);
				if((String)e.getParameter("eventName")=="ScaleUp")
					manuEquivalents.add(e);
			}
		}
		
		/*
		 * add all the lists to company's parameters
		 */
		cmy.setParameter("drugs", drugs);
		cmy.setParameter("allEvents", allEvents);
		cmy.setParameter("allCTs", allCTs);
		cmy.setParameter("manuEquivalents", manuEquivalents);
	}
	
}
