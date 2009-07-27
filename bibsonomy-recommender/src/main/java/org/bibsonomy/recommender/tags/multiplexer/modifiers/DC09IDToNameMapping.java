package org.bibsonomy.recommender.tags.multiplexer.modifiers;

import java.util.HashMap;

import org.apache.log4j.Logger;
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
public class DC09IDToNameMapping implements PostModifier {
	private static final Logger log = Logger.getLogger(DC09IDToNameMapping.class);
	private static final String UNKOWNUSER = null;
	private static final Integer UNKNOWNID = Integer.MIN_VALUE;
	
	/** used for mapping user names to ids and vice versa */
	private DBLogic dbLogic;
	
	/** used for caching id->name mappings */
	private HashMap<Integer,String> idMap;
	
	//------------------------------------------------------------------------
	// public interface 
	//------------------------------------------------------------------------
	/**
	 * constructor
	 */
	public DC09IDToNameMapping() {
		idMap   = new HashMap<Integer, String>(2000);
	}
	
	/**
	 * replaces given post's user name with the corresponding anonymised user id
	 *  
	 * @param post the post for which tags will be queried
	 */
	@Override
	public void alterPost(Post<? extends Resource> post) {
		Integer userID  = Integer.parseInt(post.getUser().getName()); 
		String userName = null;
		if( (userName = idMap.get(userID))==null )
			userName = this.getDbLogic().getUserNameByID(userID);
		
		if( userName==null )
			userName= UNKOWNUSER;
		post.getUser().setName(userName);
		log.debug("Mapping id "+userID+" to name "+userName);
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
