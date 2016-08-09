package org.bibsonomy.recommender.tag.modifiers;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.User;
import org.bibsonomy.recommender.tag.service.RecommenderMainTagAccess;

import recommender.impl.modifiers.EntityModifier;

/**
 * This class substitutes usernames in posts for anonymised user ids as
 * used in the ECMLPKDD09 discovery challenge's datasets.
 * 
 * @author fei
 */
public class DC09NameToIDMapping implements EntityModifier<Post<? extends Resource>> {
	private static final Log log = LogFactory.getLog(DC09NameToIDMapping.class);
	private static final Integer UNKNOWNID = Integer.valueOf(Integer.MIN_VALUE);
	
	/** used for mapping user names to ids and vice versa */
	private RecommenderMainTagAccess dbAccess;
	
	/** used for caching name->id mappings */
	private Map<String, Integer> nameMap;
	
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
	public void alterEntity(Post<? extends Resource> post) {
		String userName = post.getUser().getName();
		Integer userID = nameMap.get(userName);
		if (userID == null) {
			userID = this.dbAccess.getUserIDByName(userName);
		}
		
		if (userID == null) {
			userID = UNKNOWNID;
		}
		post.setUser(new User(userID.toString()));
		log.debug("Mapping user "+userName+" to id "+userID);
	}

	/**
	 * @param dbAccess the dbAccess to set
	 */
	public void setDbAccess(RecommenderMainTagAccess dbAccess) {
		this.dbAccess = dbAccess;
	}
}
