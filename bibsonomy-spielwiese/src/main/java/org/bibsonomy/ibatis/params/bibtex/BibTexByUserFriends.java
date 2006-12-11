package org.bibsonomy.ibatis.params.bibtex;

import org.bibsonomy.ibatis.enums.ConstantID;
import org.bibsonomy.ibatis.params.generic.ByUserFriends;

/**
 * Can be used to get all bibtex entries of users friends.
 * 
 * @author mgr
 * 
 */
public class BibTexByUserFriends extends ByUserFriends {

	public int getContentType() {
		return ConstantID.BIBTEX_CONTENT_TYPE.getId();
	}
}