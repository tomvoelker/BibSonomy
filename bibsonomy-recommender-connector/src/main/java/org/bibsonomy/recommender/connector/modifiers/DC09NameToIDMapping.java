package org.bibsonomy.recommender.connector.modifiers;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import recommender.core.database.DBLogic;
import recommender.core.interfaces.database.RecommenderMainTagAccess;
import recommender.core.interfaces.factories.RecommenderUserFactory;
import recommender.core.interfaces.model.TagRecommendationEntity;
import recommender.impl.modifiers.EntityModifier;

/**
 * This class substitutes usernames in posts for anonymised user ids as
 * used in the ECMLPKDD09 discovery challenge's datasets.
 * 
 * @author fei
 * @version $Id$
 */
public class DC09NameToIDMapping implements EntityModifier<TagRecommendationEntity> {
	private static final Log log = LogFactory.getLog(DC09NameToIDMapping.class);
	private static final Integer UNKNOWNID = Integer.MIN_VALUE;
	
	private RecommenderUserFactory userFactory;
	
	/** used for mapping user names to ids and vice versa */
	private DBLogic dbLogic;
	
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
	public void alterEntity(TagRecommendationEntity post) {
		String userName = post.getUser().getName();
		Integer userID = nameMap.get(userName);
		if( userID == null )
			userID = this.getDbAccess().getUserIDByName(userName);
		
		if( userID == null )
			userID = UNKNOWNID;
		post.setUser(userFactory.getRecommendationuserInstance(userID.toString()));
		log.debug("Mapping user "+userName+" to id "+userID);
	}

	/**
	 * @return the dbLogic
	 */
	public DBLogic getDbLogic() {
		return this.dbLogic;
	}

	/**
	 * @param dbLogic the dbLogic to set
	 */
	public void setDbLogic(DBLogic dbLogic) {
		this.dbLogic = dbLogic;
	}
	
	public RecommenderMainTagAccess getDbAccess() {
		return dbAccess;
	}

	/**
	 * @param userFactory the userFactory to set
	 */
	public void setUserFactory(RecommenderUserFactory userFactory) {
		this.userFactory = userFactory;
	}
	
	public RecommenderUserFactory getUserFactory() {
		return userFactory;
	}
	
	public void setDbAccess(RecommenderMainTagAccess dbAccess) {
		this.dbAccess = dbAccess;
	}
	
}
