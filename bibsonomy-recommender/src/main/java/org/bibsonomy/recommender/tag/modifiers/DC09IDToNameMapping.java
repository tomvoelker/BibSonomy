/**
 * BibSonomy Recommendation - Tag and resource recommender.
 *
 * Copyright (C) 2006 - 2016 Knowledge & Data Engineering Group,
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
package org.bibsonomy.recommender.tag.modifiers;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.recommender.tag.service.RecommenderMainTagAccess;

import recommender.impl.modifiers.EntityModifier;

/**
 * This class substitutes usernames in posts for anonymised user ids as
 * used in the ECMLPKDD09 discovery challenge's datasets.
 * 
 * @author fei
 */
public class DC09IDToNameMapping implements EntityModifier<Post<? extends Resource>> {
	private static final Log log = LogFactory.getLog(DC09IDToNameMapping.class);
	private static final String UNKOWNUSER = null;
	
	/** used for mapping user names to ids and vice versa */
	private RecommenderMainTagAccess dbAccess;
	
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
	public void alterEntity(Post<? extends Resource> post) {
		final Integer userID = new Integer(post.getUser().getName()); 
		String userName = this.idMap.get(userID);
		if (userName == null) {
			userName = this.dbAccess.getUserNameByID(userID.intValue());
		}
		
		if (userName == null) {
			userName = UNKOWNUSER;
		}
		
		post.getUser().setName(userName);
		log.debug("Mapping id "+ userID +" to name " + userName);
	}

	/**
	 * @param dbAccess the dbAccess to set
	 */
	public void setDbAccess(RecommenderMainTagAccess dbAccess) {
		this.dbAccess = dbAccess;
	}
}
