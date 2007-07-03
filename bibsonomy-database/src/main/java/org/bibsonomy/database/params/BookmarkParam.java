package org.bibsonomy.database.params;

import org.bibsonomy.common.enums.ConstantID;
import org.bibsonomy.common.enums.HashID;
import org.bibsonomy.model.Bookmark;

/**
 * Parameters that are specific to bookmarks.
 * 
 * @author Christian Schenk
 * @version $Id$
 */
public class BookmarkParam extends ResourcesParam<Bookmark> {

	/** A single resource */
	private Bookmark resource;
	
	/**
	 * This is used to restrict simHashes, i.e. which limit the overall
	 * resultset. By default simhash1 is used.
	 */
	private HashID simHash;
	
	/**
	 * A user can search for hashes and this defines which simHash should be
	 * used, e.g. either a restrictive or non-restrictive one. By default
	 * simhash1 is used.
	 */
	
	private HashID requestedSimHash;

	public BookmarkParam() {
		this.simHash = HashID.SIM_HASH;
		this.requestedSimHash = HashID.SIM_HASH;
	}

	

	@Override
	public int getContentType() {
		return ConstantID.BOOKMARK_CONTENT_TYPE.getId();
	}

	public Bookmark getResource() {
		return this.resource;
	}

	public void setResource(Bookmark resource) {
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