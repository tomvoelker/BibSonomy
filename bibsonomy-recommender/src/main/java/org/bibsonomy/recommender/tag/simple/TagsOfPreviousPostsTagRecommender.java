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
package org.bibsonomy.recommender.tag.simple;

import java.util.Collection;
import java.util.List;

import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.recommender.tag.AbstractTagRecommender;
import org.bibsonomy.recommender.tag.model.RecommendedTag;
import org.bibsonomy.recommender.tag.service.RecommenderMainTagAccess;

import recommender.core.model.Pair;

/**
 * recommends the tags of the n previously posted items
 * 
 * @author dzo
 */
public class TagsOfPreviousPostsTagRecommender extends AbstractTagRecommender {
	private RecommenderMainTagAccess dbAccess;
	private int numberOfPreviousPosts = 1;

	/**
	 * init constructor
	 * @param dbAccess 
	 */
	public TagsOfPreviousPostsTagRecommender(final RecommenderMainTagAccess dbAccess) {
		super();
		this.dbAccess = dbAccess;
	}

	/**
	 * @param dbAccess 
	 * @param numberOfPreviousPosts
	 */
	public TagsOfPreviousPostsTagRecommender(final RecommenderMainTagAccess dbAccess, int numberOfPreviousPosts) {
		this(dbAccess);
		this.numberOfPreviousPosts = numberOfPreviousPosts;
	}
	
	/* (non-Javadoc)
	 * @see recommender.core.Recommender#getInfo()
	 */
	@Override
	public String getInfo() {
		return "Recommender that recommends the tags of the " + this.numberOfPreviousPosts + " previous posts.";
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.recommender.tag.AbstractTagRecommender#addRecommendedTagsInternal(java.util.Collection, org.bibsonomy.model.Post)
	 */
	@Override
	protected void addRecommendedTagsInternal(Collection<RecommendedTag> recommendedTags, Post<? extends Resource> entity) {
		final String username = entity.getUser().getName();
		final int count = this.dbAccess.getNumberOfTagsOfPreviousPostsForUser(username, this.numberOfPreviousPosts);
		
		final List<Pair<String, Integer>> tags = this.dbAccess.getTagsOfPreviousPostsForUser(username, this.numberOfPreviousPosts);
		for (final Pair<String, Integer> tagWithCount : tags) {
			final String tag = this.getCleanedTag(tagWithCount.getFirst());
			if (tag != null) {
				recommendedTags.add(new RecommendedTag(tag, ((1.0 * tagWithCount.getSecond().doubleValue()) / count), 0.5));
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.recommender.tag.AbstractTagRecommender#setFeedbackInternal(org.bibsonomy.model.Post, org.bibsonomy.recommender.tag.model.RecommendedTag)
	 */
	@Override
	protected void setFeedbackInternal(Post<? extends Resource> post, RecommendedTag tag) {
		// noop
	}

}
