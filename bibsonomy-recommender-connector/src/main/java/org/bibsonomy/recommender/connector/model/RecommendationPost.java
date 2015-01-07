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

import java.util.ArrayList;
import java.util.List;

import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.Tag;

import recommender.core.interfaces.model.RecommendationItem;
import recommender.core.interfaces.model.RecommendationTag;

/**
 * This class wraps a BibSonomy post as the result of a recommendation to
 * allow greedy loading.
 * 
 * @author lukas
 *
 * @param <T>
 */
public class RecommendationPost implements RecommendationItem {
	
	private Post<? extends Resource> post;

	public RecommendationPost(Post<? extends Resource> post) {
		this.post = post;
	}
	
	@Override
	public String getId() {
		if(post != null) {
			return "" + post.getContentId();
		}
		return "";
	}

	@Override
	public void setId(String id) {
		if(post != null) {
			this.post.setContentId(Integer.parseInt(id));
		}
	}

	@Override
	public List<RecommendationTag> getTags() {
		if(this.post != null) {
			ArrayList<RecommendationTag> tags = new ArrayList<RecommendationTag>();
			for(Tag tag : post.getTags()) {
				tags.add(new TagWrapper(tag));
			}
			return tags;
		}
		return new ArrayList<RecommendationTag>();
	}

	@Override
	public void setTags(List<RecommendationTag> tags) {
		if(this.post != null) {
			ArrayList<Tag> bibTags = new ArrayList<Tag>();
			for(RecommendationTag tag : tags) {
				if(tag instanceof TagWrapper) {
					bibTags.add(((TagWrapper) tag).getTag());
				}
			}
		}
	}

	@Override
	public String getTitle() {
		if(this.post != null) {
			if(this.post.getResource() != null) {
				return this.post.getResource().getTitle();
			}
		}
		return "";
	}

	@Override
	public void setTitle(String title) {
		if(this.post != null) {
			if(this.post.getResource() != null) {
				this.post.getResource().setTitle(title);
			}
		}
	}
	
	public Post<? extends Resource> getPost() {
		return post;
	}
	
	public void setPost(Post<? extends Resource> post) {
		this.post = post;
	}

}
