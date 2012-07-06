package company;

import java.util.ArrayList;
import java.util.HashMap;

import mcdmProblem.SolutionTranslator;

import drugs.Drug;
import events.Event;
import events.EventFactory;

/*
 * this class is dedicated to form a company
 */

public class CompanyFactory {
	
	public static Company createCompany(int[] decisionVariables){//the decision variable is solution

		int[] startTimesSmall	=SolutionTranslator.TranslateBuildSmall(decisionVariables);
		int[] startTimesBig		=SolutionTranslator.TranslateBuildBig(decisionVariables);

		HashMap<String, Object> cmyPara=new HashMap<String, Object>();
		
		//initialise these arraylists so that assembled drugs and events can be put into them directly
		ArrayList<Drug> 	drugs			=new ArrayList<Drug>();
		ArrayList<Event> 	allEvents		=new ArrayList<Event>();
		ArrayList<Event>	allCTs			=new ArrayList<Event>();
		ArrayList<Event>	manuEquivalents	=new ArrayList<Event>();
		
		cmyPara.put("companyName", 		"wenhao's virtual company");//or could be any given name
		cmyPara.put("companyType", 		"in-house");//or could be CMO or partner
		cmyPara.put("drugs", 			drugs);
		cmyPara.put("allEvents", 		allEvents);
		cmyPara.put("allCTs", 			allCTs);
		cmyPara.put("manuEquivalents", 	manuEquivalents);
		
		Company cmy=new Company(cmyPara);
		/*
		 * initialise build events should be at company level, hence
		 */
		String[] smallFacilityNames	={"alpha", "beta", "gamma"};
		String[] bigFacilityNames	={"ALPHA", "BETA", "GAMMA", "DELTA", "EPSILON"};
		
		for(int i=0;i<startTimesSmall.length;i++){
			HashMap<String, Object> eventPara=new HashMap<String, Object>();
			eventPara.put("company", 	cmy);
			eventPara.put("eventName", 	smallFacilityNames[i]);
			eventPara.put("buildType", 	"Small");
			eventPara.put("noPrevious", true);
			eventPara.put("startTime", 	startTimesSmall[i]);
			
			
			allEvents.add(EventFactory.createEvent("Build", eventPara));
		}//small build events
		
		for(int i=0;i<startTimesBig.length;i++){
			HashMap<String, Object> eventPara=new HashMap<String, Object>();
			eventPara.put("company", 	cmy);
			eventPara.put("eventName", 	bigFacilityNames[i]);
			eventPara.put("buildType", 	"Big");
			eventPara.put("noPrevious", true);
			eventPara.put("startTime", 	startTimesBig[i]);
			
			allEvents.add(EventFactory.createEvent("Build", eventPara));
		}//big build events
		
		
		return cmy;
	}

}
