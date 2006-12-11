package org.bibsonomy.ibatis.params.bibtex;

import org.bibsonomy.ibatis.enums.ConstantID;

/**
 * Can be used to get all bibtex entries, which are picked by the current user.
 * 
 * @author mgr
 * 
 */
public class BibTexByDownload {

	private String user;

	public int getContentType() {
		return ConstantID.BIBTEX_CONTENT_TYPE.getId();
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}
}
