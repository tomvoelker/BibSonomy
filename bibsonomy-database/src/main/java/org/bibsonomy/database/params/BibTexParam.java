package org.bibsonomy.database.params;

import org.bibsonomy.common.enums.ConstantID;
import org.bibsonomy.common.enums.HashID;
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
	 * This is used to restrict simHashes, i.e. to limit the overall resultset.
	 * The default simHash is defined in {@link HashID}.
	 */
	private HashID simHash;
	/**
	 * A user can search for hashes and this defines which simHash should be
	 * used, e.g. either a restrictive or non-restrictive one. The default
	 * simHash is defined in {@link HashID}.
	 */
	private HashID requestedSimHash;
	
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
	
	
	private String bibtexKey;

	public BibTexParam() {
		this.simHash = HashID.SIM_HASH;
		this.requestedSimHash = HashID.SIM_HASH;
		
		this.firstYear = null;
		this.lastYear = null;
		this.year = null;
		this.bibtexKey = null;
	}

	@Override
	public int getContentType() {
		return ConstantID.BIBTEX_CONTENT_TYPE.getId();
	}

	public BibTex getResource() {
		if (this.resource == null) this.resource = new BibTex();
		return this.resource;
	}

	public void setResource(BibTex resource) {
		this.resource = resource;
	}

	public int getSimHash() {
		return this.simHash.getId();
	}

	public void setSimHash(HashID simHash) {
		this.simHash = simHash;
	}

	public int getRequestedSimHash() {
		return this.requestedSimHash.getId();
	}

	public void setRequestedSimHash(HashID requestedSimHash) {
		this.requestedSimHash = requestedSimHash;
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

	public String getBibtexKey() {
		return this.bibtexKey;
	}

	public void setBibtexKey(String requestedBibtexkey) {
		this.bibtexKey = requestedBibtexkey;
	}
}