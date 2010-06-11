package org.bibsonomy.database.systemstags.database;

import org.bibsonomy.database.systemstags.AbstractSystemTagImpl;
import org.bibsonomy.database.systemstags.SystemTag;

/**
 * @author dzo
 * @version $Id$
 */
public class YearSystemTag extends AbstractSystemTagImpl implements DatabaseSystemTag{
	
	/**
	 * these variables will be used with systemtags.
	 * firstYear defines the first year if someone requests publications
	 * form 2005 till 2007.
	 * therefore 2007 will be stored in lastYear.
	 * 
	 * if someone requests publications from only 2007, the year will be
	 * stored in year.
	 * 
	 * this is necessary to differ between the 4 type of systags year:
	 * 1. 2007
	 * 2. 2005-2007
	 * 3. -2007
	 * 4. 2004-
	 */
	private String firstYear;
	private String lastYear;
	private String year;
	
	/**
	 * @return the firstYear
	 */
	public String getFirstYear() {
		return this.firstYear;
	}

	/**
	 * @param firstYear the firstYear to set
	 */
	public void setFirstYear(String firstYear) {
		this.firstYear = firstYear;
	}

	/**
	 * @return the lastYear
	 */
	public String getLastYear() {
		return this.lastYear;
	}

	/**
	 * @param lastYear the lastYear to set
	 */
	public void setLastYear(String lastYear) {
		this.lastYear = lastYear;
	}

	/**
	 * @return the year
	 */
	public String getYear() {
		return this.year;
	}

	/**
	 * @param year the year to set
	 */
	public void setYear(String year) {
		this.year = year;
	}

	public SystemTag newInstance() {
		return new YearSystemTag();
	}

}
