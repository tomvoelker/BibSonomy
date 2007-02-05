package org.bibsonomy.database.params;

import org.bibsonomy.common.enums.ConstantID;
import org.bibsonomy.database.params.GenericParam;
import org.bibsonomy.model.BibTex;

/**
 * Parameters that are specific to BibTex.
 * 
 * @author Christian Schenk
 */
public class BibTexParam extends GenericParam<BibTex> {

	/**
	 * This is used to restrict simHashes, i.e. which limit the overall
	 * resultset. By default simhash1 is used.
	 */
	private ConstantID simHash;
	/**
	 * A user can search for hashes and this defines which simHash should be
	 * used, e.g. either a restrictive or non-restrictive one. By default
	 * simhash1 is used.
	 */
	private ConstantID requestedSimHash;

	public BibTexParam() {
		this.simHash = ConstantID.SIM_HASH;
		this.requestedSimHash = ConstantID.SIM_HASH;
	}

	@Override
	public int getContentType() {
		return ConstantID.BIBTEX_CONTENT_TYPE.getId();
	}

	public int getSimHash() {
		return this.simHash.getId();
	}

	public void setSimHash(ConstantID simHash) {
		this.simHash = simHash;
	}

	public int getRequestedSimHash() {
		return this.requestedSimHash.getId();
	}

	public void setRequestedSimHash(ConstantID requSim) {
		this.requestedSimHash = requSim;
	}
}