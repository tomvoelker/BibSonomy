package org.bibsonomy.common.enums;

import java.util.Collection;
import java.util.LinkedList;

/**
 * @author ema
 * @version $Id$
 */

//TODO use in model/Bibtex.java

public enum PublicationType {
	ARTICLE {
	    @Override
		public String toString() {
	        return "article";
	    }
	},
    BOOK {
	    @Override
		public String toString() {
	        return "book";
	    }
	},
	BOOKLET {
	    @Override
		public String toString() {
	        return "booklet";
	    }
	},
    INBOOK {
	    @Override
		public String toString() {
	        return "inbook";
	    }
	},
	INCOLLECTION {
	    @Override
		public String toString() {
	        return "incollection";
	    }
	},
    INPROCEEDINGS {
	    @Override
		public String toString() {
	        return "inproceedings";
	    }
	},
	MANUAL {
	    @Override
		public String toString() {
	        return "manual";
	    }
	},
    MASTERTHESIS {
	    @Override
		public String toString() {
	        return "mastersthesis";
	    }
	},
	MISC {
	    @Override
		public String toString() {
	        return "misc";
	    }
	},
    PHDTHESIS {
	    @Override
		public String toString() {
	        return "phdthesis";
	    }
	},
	PROCEEDINGS {
	    @Override
		public String toString() {
	        return "proceedings";
	    }
	},
    TECHREPORT {
	    @Override
		public String toString() {
	        return "techreport";
	    }
	},
	UNPUBLISHED {
	    @Override
		public String toString() {
	        return "unpublished";
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

