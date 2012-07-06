package util;

import events.Event;

public class OverlapJudge {

	public static boolean isOverlapped(Object o1, Object o2){
		/*
		 * downcast into event so that all the cts and manus could use it
		 */
		int startTime1=(Integer)((Event)o1).getParameter("startTime");
		int endTime1=(Integer)((Event)o1).getParameter("endTime");
		
		int startTime2=(Integer)((Event)o2).getParameter("startTime");
		int endTime2=(Integer)((Event)o2).getParameter("endTime");
		
		if(startTime1>=endTime2 || startTime2>=endTime1)
			return false;
		else
			return true;
	}
	
}
