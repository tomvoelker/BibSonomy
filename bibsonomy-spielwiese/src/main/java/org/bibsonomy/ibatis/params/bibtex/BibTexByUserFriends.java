package org.bibsonomy.ibatis.params.bibtex;
import org.bibsonomy.ibatis.enums.ConstantID;
import org.bibsonomy.ibatis.params.generic.ByUserFriendsBibtex;



/**
 * @author mgr
 *
 */


/*
 * Can be used to get all bibtex entries of users friends.
 * 
 */

public class BibTexByUserFriends extends ByUserFriendsBibtex{
	
	
	
	public int getContentType() {
		return ConstantID.BIBTEX_CONTENT_TYPE.getId();
	}
	
	
	
	
}