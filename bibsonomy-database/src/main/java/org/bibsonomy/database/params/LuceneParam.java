package org.bibsonomy.database.params;

import org.bibsonomy.common.enums.GroupID;

/**
 * Parameters that are specific to Lucene.
 * 
 * @author Sven Stefani
 * @version $Id$
 */
public class LuceneParam {

	/** content id */
	private String cid;
	
	
	/**
	 * @return Cid content id
	 */
	public String getCid() {
		return this.cid;
	}

	/**
	 * @param Cid content id
	 */
	public void setCid(String cid) {
		this.cid = cid;
	}



}