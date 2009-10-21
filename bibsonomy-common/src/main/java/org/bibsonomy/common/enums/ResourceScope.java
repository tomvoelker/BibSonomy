package org.bibsonomy.common.enums;

import java.util.Collection;
import java.util.LinkedList;


/**
 * @author ema
 * @version $Id$
 */
public enum ResourceScope {
	PRIVATE {
	    @Override
		public String toString() {
	        return "private";
	    }
	},
    PUBLIC {
	    @Override
		public String toString() {
	        return "public";
	    }
	},
	FRIENDS {
	    @Override
		public String toString() {
	        return "friends";
	    }
	};
	
	
	/**
	 * Returns a Collection of all possible values, that are contained within this enum.
	 */
	public static Collection<String> getAllValues()
	{
		final Collection<String> colOfAll=new LinkedList<String>();
		for(int i=0; i<values().length; i++)
		{
			colOfAll.add((values())[i].toString());
		}
		return colOfAll;
	}
}
