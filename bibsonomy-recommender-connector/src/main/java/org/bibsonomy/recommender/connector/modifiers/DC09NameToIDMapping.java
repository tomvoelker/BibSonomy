/**
 * BibSonomy-Recommendation-Connector - Connector for the recommender framework for tag and resource recommendation
 *
 * Copyright (C) 2006 - 2015 Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               http://www.kde.cs.uni-kassel.de/
 *                           Data Mining and Information Retrieval Group,
 *                               University of Würzburg, Germany
 *                               http://www.is.informatik.uni-wuerzburg.de/en/dmir/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               http://www.l3s.de/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.recommender.connector.modifiers;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.User;
import org.bibsonomy.recommender.connector.utilities.RecommendationUtilities;

import recommender.core.interfaces.database.RecommenderMainTagAccess;
import recommender.core.interfaces.model.TagRecommendationEntity;
import recommender.impl.modifiers.EntityModifier;

/**
 * This class substitutes usernames in posts for anonymised user ids as
 * used in the ECMLPKDD09 discovery challenge's datasets.
 * 
 * @author fei
 */
public class DC09NameToIDMapping implements EntityModifier<TagRecommendationEntity> {
	private static final Log log = LogFactory.getLog(DC09NameToIDMapping.class);
	private static final Integer UNKNOWNID = Integer.MIN_VALUE;
	
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
	public void alterEntity(TagRecommendationEntity entity) {
		final Post<? extends Resource> post = RecommendationUtilities.unwrapTagRecommendationEntity(entity);
		String userName = post.getUser().getName();
		Integer userID = nameMap.get(userName);
		if( userID == null )
			userID = this.getDbAccess().getUserIDByName(userName);
		
		if( userID == null )
			userID = UNKNOWNID;
		post.setUser(new User(userID.toString()));
		log.debug("Mapping user "+userName+" to id "+userID);
	}
	
	public RecommenderMainTagAccess getDbAccess() {
		return dbAccess;
	}
	
	public void setDbAccess(RecommenderMainTagAccess dbAccess) {
		this.dbAccess = dbAccess;
	}
	
}
