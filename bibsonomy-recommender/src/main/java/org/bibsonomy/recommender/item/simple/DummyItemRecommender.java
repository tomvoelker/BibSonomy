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
package org.bibsonomy.recommender.item.simple;

import java.util.Collection;
import java.util.List;

import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.recommender.item.AbstractItemRecommender;
import org.bibsonomy.recommender.item.model.RecommendationUser;
import org.bibsonomy.recommender.item.model.RecommendedPost;

/**
 * Dummy recommender implementation which delivers the numberOfItemsToRecommend count of most actual items.
 * Can be used as fallback recommender.
 * 
 * @author lukas
 * @param <R> 
 */
public class DummyItemRecommender<R extends Resource> extends AbstractItemRecommender<R> {

	private static final String INFO = "This Itemrecommender returns the numberOfResultsToRecommend most actual itemsfrom the database";

	/*
	 * (non-Javadoc)
	 * @see recommender.impl.item.AbstractItemRecommender#addRecommendedItemsInternal(java.util.Collection, recommender.core.interfaces.model.ItemRecommendationEntity)
	 */
	@Override
	protected void addRecommendedItemsInternal(Collection<RecommendedPost<R>> recommendations, RecommendationUser entity) {
		final List<Post<R>> mostActualItems = this.dbAccess.getMostActualItems(this.numberOfItemsToRecommend, entity);
		int counter = 1;
		for (final Post<R> item : mostActualItems) {
			final RecommendedPost<R> recommendedPost = new RecommendedPost<R>();
			recommendedPost.setPost(item);
			recommendedPost.setScore(1.0 / (counter + 100));
			recommendations.add(recommendedPost);
			counter++;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see recommender.impl.item.AbstractItemRecommender#setFeedbackInternal(recommender.core.interfaces.model.ItemRecommendationEntity)
	 */
	@Override
	protected void setFeedbackInternal(RecommendationUser entity, RecommendedPost<R> item) {
		// ignore feedback
	}

	/*
	 * (non-Javadoc)
	 * @see recommender.core.Recommender#getInfo()
	 */
	@Override
	public String getInfo() {
		return INFO;
	}
}
