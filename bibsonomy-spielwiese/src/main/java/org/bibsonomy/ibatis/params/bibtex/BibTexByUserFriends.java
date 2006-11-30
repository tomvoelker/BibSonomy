package org.bibsonomy.ibatis.params.bibtex;
import org.bibsonomy.ibatis.enums.ConstantID;
import org.bibsonomy.ibatis.params.generic.ByUserFriendsBibtex;


public class BibTexByUserFriends extends ByUserFriendsBibtex{
	
	
	
	public int getContentType() {
		return ConstantID.BIBTEX_CONTENT_TYPE.getId();
	}
	
	
	
	
}