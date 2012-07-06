package drugs;

import java.util.HashMap;

import events.Event;
import events.EventFactory;


public class Drug_niche extends Drug {

	public Drug_niche(HashMap<String, Object> parameters) {
		super(parameters);
		this.setParameter("drugType", "niche");
	}

	public Event createEvent(String eventType, String eventName) {
		HashMap<String, Object> parameters=new HashMap<String, Object>();
		parameters.put("eventName", eventName);
		parameters.put("drug", this);
		parameters.put("eventType", eventType);
		return EventFactory.createEvent(eventType, parameters);
	}

}
