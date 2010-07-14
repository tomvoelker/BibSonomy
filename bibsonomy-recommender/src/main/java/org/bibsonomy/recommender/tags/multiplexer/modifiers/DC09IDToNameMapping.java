package org.bibsonomy.recommender.tags.multiplexer.modifiers;

import java.util.HashMap;
import java.util.Map;

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
public class DC09IDToNameMapping implements PostModifier {
	private static final Log log = LogFactory.getLog(DC09IDToNameMapping.class);
	private static final String UNKOWNUSER = null;
	
	/** used for mapping user names to ids and vice versa */
	private DBLogic dbLogic;
	
	/** used for caching id->name mappings */
	private Map<Integer, String> idMap;
	
	//------------------------------------------------------------------------
	// public interface 
	//------------------------------------------------------------------------
	/**
	 * constructor
	 */
	public DC09IDToNameMapping() {
		this.idMap = new HashMap<Integer, String>(2000);
	}
	
	/**
	 * replaces given post's user name with the corresponding anonymised user id
	 *  
	 * @param post the post for which tags will be queried
	 */
	@Override
	public void alterPost(Post<? extends Resource> post) {
		final Integer userID = Integer.parseInt(post.getUser().getName()); 
		String userName = this.idMap.get(userID);
		if (userName == null) {
			userName = this.getDbLogic().getUserNameByID(userID);
		}
			
		
		if (userName == null) {
		    userName = UNKOWNUSER;
		}
			
		post.getUser().setName(userName);
		log.debug("Mapping id "+ userID +" to name " + userName);
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
}
