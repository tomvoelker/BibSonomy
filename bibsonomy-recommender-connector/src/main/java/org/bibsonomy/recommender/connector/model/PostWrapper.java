/**
 * BibSonomy-Recommendation-Connector - Connector for the recommender framework for tag and resource recommendation
 *
 * Copyright (C) 2006 - 2014 Knowledge & Data Engineering Group,
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

import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import recommender.core.interfaces.model.RecommendationUser;
import recommender.core.interfaces.model.TagRecommendationEntity;

/**
 * This class wraps a BibSonomy post to pass it to the recommender framework.
 * 
 * @author Lukas
 *
 * @param <T>
 */

public class PostWrapper <T extends Resource> implements TagRecommendationEntity {

	/**
	 * for serialization
	 */
	private static final long serialVersionUID = -8716576273787930613L;
	
	private Post<T> post;

	public PostWrapper(Post<T> post) {
		this.post = post;
	}
	
	public Post<T> getPost() {
		return post;
	}
	
	public void setPost(Post<T> post) {
		this.post = post;
	}

	@Override
	public String getId() {
		if(this.post != null) {
			return ""+this.post.getContentId();
		}
		return null;
	}

	public void setId(String id) {
		if(this.post != null) {
			this.post.setContentId(Integer.parseInt(id));
		}
	}

	@Override
	public RecommendationUser getUser() {
		if(this.post != null) {
			return new UserWrapper(this.post.getUser());
		}
		return null;
	}

	public void setUser(RecommendationUser user) {
		if(this.post != null) {
			this.post.setUser(((UserWrapper) user).getUser());
		}
	}

	@Override
	public String getTitle() {
		if(this.post != null) {
			if (this.post.getResource() != null) {
				return this.post.getResource().getTitle();
			}
		}
		return "";
	}

	public void setTitle(String title) {
		if(this.post != null) {
			if (this.post.getResource() != null) {
				this.post.getResource().setTitle(title);
			}
		}
	}

	@Override
	public String getUrl() {
		if(this.post != null) {
			if (this.post.getResource() != null &&
					this.post.getResource() instanceof BibTex) {
				return ((BibTex) this.post.getResource()).getUrl();
			}
		}
		return "";
	}

	public void setUrl(String url) {
		if(this.post != null) {
			if (this.post.getResource() != null && 
					this.post.getResource() instanceof BibTex) {
				((BibTex) this.post.getResource()).setUrl(url);
			}
		}
	}

	@Override
	public String getUserName() {
		if(this.post != null && this.post.getUser() != null) {
			return this.post.getUser().getName();
		}
		return "";
	}
	
}