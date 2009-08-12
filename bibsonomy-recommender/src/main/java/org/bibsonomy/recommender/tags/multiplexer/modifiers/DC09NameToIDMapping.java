package org.bibsonomy.recommender.tags.multiplexer.modifiers;

import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.recommender.tags.database.DBLogic;

/**
 * This class substitutes usernames in posts for anonymised user ids as
 * used in the ECMLPKDD09 discovery challenge's datasets.
 * 
 * @author fei
 * @version $Id$
 */
public class DC09NameToIDMapping implements PostModifier {
	private static final Log log = LogFactory.getLog(DC09NameToIDMapping.class);
	private static final String UNKOWNUSER = null;
	private static final Integer UNKNOWNID = Integer.MIN_VALUE;
	
	/** used for mapping user names to ids and vice versa */
	private DBLogic dbLogic;
	
	/** used for caching name->id mappings */
	private HashMap<String,Integer> nameMap;
	
	//------------------------------------------------------------------------
	// public interface 
	//------------------------------------------------------------------------
	/**
	 * constructor
	 */
	public DC09NameToIDMapping() {
		nameMap = new HashMap<String, Integer>(2000);
	}
	
	/**
	 * replaces given post's user name with the corresponding anonymised user id
	 *  
	 * @param post the post for which tags will be queried
	 */
	@Override
	public void alterPost(Post<? extends Resource> post) {
		String userName = post.getUser().getName();
		Integer userID = null;
		if( (userID = nameMap.get(userName))==null )
			userID = this.getDbLogic().getUserIDByName(userName);
		
		if( userID==null )
			userID = UNKNOWNID;
		post.getUser().setName(userID.toString());
		log.debug("Mapping user "+userName+" to id "+userID);
	}

	//------------------------------------------------------------------------
	// getter/setter
	//------------------------------------------------------------------------
	public void setDbLogic(DBLogic dbLogic) {
		this.dbLogic = dbLogic;
	}

	public DBLogic getDbLogic() {
		return dbLogic;
	}

}
