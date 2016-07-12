/**
 * BibSonomy-Recommendation-Connector - Connector for the recommender framework for tag and resource recommendation
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
package org.bibsonomy.recommender.connector.model;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.ArrayList;
import java.util.List;

import org.bibsonomy.model.Tag;
import org.bibsonomy.model.User;
import org.bibsonomy.recommender.connector.utilities.RecommendationUtilities;

import recommender.core.interfaces.model.ItemRecommendationEntity;
import recommender.core.interfaces.model.RecommendationItem;
import recommender.core.interfaces.model.RecommendationTag;
import recommender.core.interfaces.model.RecommendationUser;

public class UserWrapper implements RecommendationUser, ItemRecommendationEntity{

	/**
	 * for persistence
	 */
	private static final long serialVersionUID = -5249217271896497855L;
	
	private User user;
	
	public UserWrapper(User user) {
		this.user = user;
	}
	
	/**
	 * @return the id
	 */
	@Override
	public String getId() {
		if( user != null ) {
			return user.getName();
		}
		return "";
	}
	/**
	 * @param id the id to set
	 */
	@Override
	public void setId(String id) {
		if( user != null ) {
			this.user.setName(id);
		}
	}
		
	/**
	 * @return the name
	 */
	@Override
	public String getName() {
		if( user != null ) {
			return this.user.getName();
		}
		return null;
	}
	/**
	 * @param name the name to set
	 */
	@Override
	public void setName(String name) {
		if( user != null ) {
			this.user.setName(name);
		}
	}

	public User getUser() {
		return user;
	}
	
	public void setUser(User user) {
		this.user = user;
	}

	@Override
	public String getUserName() {
		if(this.user != null) {
			return this.user.getName();
		}
		return "";
	}

	@Override
	public List<RecommendationTag> getTags() {
		if(this.user != null && this.user.getTags() != null) {
			List<RecommendationTag> tags = new ArrayList<RecommendationTag>();
			for(Tag t : this.user.getTags()) {
				tags.add(new TagWrapper(t));
			}
			return tags;
		}
		return new ArrayList<RecommendationTag>();
	}

	@Override
	public List<RecommendationItem> getItems() {
		if(present(this.user)) {
			if(present(this.user.getPosts())) {
				return RecommendationUtilities.wrapPostList(this.user.getPosts());
			}
		}
		return new ArrayList<RecommendationItem>();
	}

}
