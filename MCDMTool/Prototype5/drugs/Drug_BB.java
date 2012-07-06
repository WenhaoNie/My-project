package drugs;

import java.util.HashMap;

import events.Event;
import events.EventFactory;


public class Drug_BB extends Drug {
	
	public Drug_BB(HashMap<String, Object> parameters) {
		super(parameters);
		this.setParameter("drugType", "BB");
	}
	
	@Override
	public Event createEvent(String eventType, String eventName) {
		HashMap<String, Object> parameters=new HashMap<String, Object>();
		parameters.put("eventName", eventName);
		parameters.put("drug", this);
		parameters.put("eventType", eventType);
		return EventFactory.createEvent(eventType, parameters);
	}
}
