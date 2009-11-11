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
	
	private String firstYear;
	private String lastYear;
	private String year;
	
	public BibTexParam() {
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