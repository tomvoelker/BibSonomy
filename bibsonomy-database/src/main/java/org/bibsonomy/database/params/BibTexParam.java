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

	public BibTexParam() {
		this.resource = new BibTex();
		this.simHash = HashID.SIM_HASH;
		this.requestedSimHash = HashID.SIM_HASH;
	}

	@Override
	public int getContentType() {
		return ConstantID.BIBTEX_CONTENT_TYPE.getId();
	}

	public BibTex getResource() {
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

	public void setRequestedSimHash(HashID requSim) {
		this.requestedSimHash = requSim;
	}
}