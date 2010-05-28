package org.bibsonomy.lucene.database.params;

import org.bibsonomy.common.enums.ConstantID;
import org.bibsonomy.model.BibTex;

/**
 * Parameters that are specific to BibTex.
 * 
 * @author Christian Schenk
 * @version $Id$
 */
public class BibTexParam extends ResourcesParam<BibTex> {

	/** A single resource */
	private BibTex resource;
	
	/**
	 * these variables will be used with systemtags.
	 * firstYear defines the first year if someone requests bibtex posts
	 * form 2005 till 2007.
	 * therefore 2007 will be stored in lastYear.
	 * 
	 * if someone requests bibtex posts from only 2007, the year will be
	 * stored in year.
	 * 
	 * this ist necessary to differ between the 4 type of systags year:
	 * 1. 2007
	 * 2. 2005-2007
	 * 3. -2007
	 * 4. 2004-
	 */
	private String firstYear;
	private String lastYear;
	private String year;
	
	/**
	 * If <code>true</code>, methods should provide data (file name, hash, etc.)
	 * of documents (PDF, PS, ...) associated to posts. 
	 */
	private boolean documentsAttached;
	
	/**
	 * defines the entry type of the requested bibtex entries
	 */
	private String entryType;

	@Override
	public int getContentType() {
		return ConstantID.BIBTEX_CONTENT_TYPE.getId();
	}

	/**
	 * @return the publication
	 */
	public BibTex getResource() {
		if (this.resource == null) this.resource = new BibTex();
		return this.resource;
	}

	/**
	 * @param resource the bookmark to set
	 */
	public void setResource(BibTex resource) {
		this.resource = resource;
	}

	/**
	 * @return the firstYear
	 */
	public String getFirstYear() {
		return firstYear;
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
		return lastYear;
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
		return year;
	}

	/**
	 * @param year the year to set
	 */
	public void setYear(String year) {
		this.year = year;
	}

	/**
	 * @return the documentsAttached
	 */
	public boolean isDocumentsAttached() {
		return documentsAttached;
	}

	/**
	 * @param documentsAttached the documentsAttached to set
	 */
	public void setDocumentsAttached(boolean documentsAttached) {
		this.documentsAttached = documentsAttached;
	}

	/**
	 * @return the entryType
	 */
	public String getEntryType() {
		return entryType;
	}

	/**
	 * @param entryType the entryType to set
	 */
	public void setEntryType(String entryType) {
		this.entryType = entryType;
	}
}