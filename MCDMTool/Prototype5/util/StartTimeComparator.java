package util;

import java.util.Comparator;

import events.Event;

public class StartTimeComparator implements Comparator {

	public int compare(Object o1, Object o2){
		/*
		 * downcast to event so that this could be used to cts and manus
		 */
		
		int startTime1=(Integer)((Event)o1).getParameter("startTime");
		int startTime2=(Integer)((Event)o2).getParameter("startTime");
		
		if(startTime1>startTime2)
			return 1;
		else if(startTime1<startTime2)
			return -1;
		else
			return 0;
	}
	
}
