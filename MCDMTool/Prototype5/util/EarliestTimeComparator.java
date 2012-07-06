package util;

import java.util.Comparator;

import company.ManuFacility;

public class EarliestTimeComparator implements Comparator {

	public int compare(Object o1, Object o2) {
		int earliestAvailableTime1=(Integer)((ManuFacility)o1).getParameter("earliestAvailableTime");
		int earliestAvailableTime2=(Integer)((ManuFacility)o2).getParameter("earliestAvailableTime");

		if(earliestAvailableTime1>earliestAvailableTime2)
			return 1;
		else if(earliestAvailableTime1<earliestAvailableTime2)
			return -1;
		else
			return 0;
	}

}
