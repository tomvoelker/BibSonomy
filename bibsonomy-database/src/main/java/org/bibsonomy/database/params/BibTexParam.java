package org.bibsonomy.database.params;

import org.bibsonomy.common.enums.ConstantID;
import org.bibsonomy.model.BibTex;

/**
 * Parameters that are specific to BibTex.
 * 
 * @author Christian Schenk
 * @version $Id$
 */
public class BibTexParam extends ResourcesParam<BibTex> implements SingleResourceParam<BibTex> {

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
	
	public BibTexParam() {
		super();
		
		this.firstYear = null;
		this.lastYear = null;
		this.year = null;
		this.setBibtexKey(null);
	}

	@Override
	public int getContentType() {
		return ConstantID.BIBTEX_CONTENT_TYPE.getId();
	}

	/*
	 * (non-Javadoc)
	 * @see org.bibsonomy.database.params.SingleResourceParam#getResource()
	 */
	@Override
	public BibTex getResource() {
		if (this.resource == null) this.resource = new BibTex();
		return this.resource;
	}

	/*
	 * (non-Javadoc)
	 * @see org.bibsonomy.database.params.SingleResourceParam#setResource(org.bibsonomy.model.Resource)
	 */
	@Override
	public void setResource(BibTex resource) {
		this.resource = resource;
	}

	public String getFirstYear() {
		return this.firstYear;
	}

	public void setFirstYear(String firstYear) {
		this.firstYear = firstYear;
	}

	public String getLastYear() {
		return this.lastYear;
	}

	public void setLastYear(String lastYear) {
		this.lastYear = lastYear;
	}

	public String getYear() {
		return this.year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public boolean isDocumentsAttached() {
		return this.documentsAttached;
	}

	public void setDocumentsAttached(boolean documentsAttached) {
		this.documentsAttached = documentsAttached;
	}
	
	public String getEntryType() {
		return this.entryType;
	}

	public void setEntryType(String entryType) {
		this.entryType = entryType;
	}
}