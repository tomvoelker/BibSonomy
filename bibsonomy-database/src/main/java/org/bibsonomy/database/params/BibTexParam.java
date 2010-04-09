package org.bibsonomy.database.params;

import org.bibsonomy.common.enums.ConstantID;
import org.bibsonomy.model.BibTex;

/**
 * Parameters that are specific to BibTex.
 * 
 * @author Christian Schenk
 * @version $Id$
 */
public class BibTexParam extends ResourceParam<BibTex> {
	
	private String firstYear;
	private String lastYear;
	private String year;

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
		if (this.resource == null) {
			this.resource = new BibTex(); // TODO: why not returning null??! only for the bibtexExtraManager?!
		}
		
		return this.resource;
	}

	@Deprecated
	public String getFirstYear() {
		return this.firstYear;
	}

	@Deprecated
	public void setFirstYear(String firstYear) {
		this.firstYear = firstYear;
	}

	@Deprecated
	public String getLastYear() {
		return this.lastYear;
	}

	@Deprecated
	public void setLastYear(String lastYear) {
		this.lastYear = lastYear;
	}

	@Deprecated
	public String getYear() {
		return this.year;
	}

	@Deprecated
	public void setYear(String year) {
		this.year = year;
	}
}