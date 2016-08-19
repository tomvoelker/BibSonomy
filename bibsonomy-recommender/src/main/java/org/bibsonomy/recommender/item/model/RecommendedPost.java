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
package org.bibsonomy.recommender.item.model;

import java.io.Serializable;

import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;

import recommender.core.interfaces.model.RecommendationResult;

/**
 * TODO: add documentation to this class
 *
 * @author lha
 * @param <R> 
 */
public class RecommendedPost<R extends Resource> implements RecommendationResult, Serializable {
	private static final long serialVersionUID = -2529041486151001015L;
	
	private Post<R> post;
	
	private double score;
	private double confidence;
	
	/**
	 * @return the post
	 */
	public Post<R> getPost() {
		return this.post;
	}

	/**
	 * @param post the post to set
	 */
	public void setPost(Post<R> post) {
		this.post = post;
	}

	/* (non-Javadoc)
	 * @see recommender.core.interfaces.model.RecommendationResult#getScore()
	 */
	@Override
	public double getScore() {
		return score;
	}

	/* (non-Javadoc)
	 * @see recommender.core.interfaces.model.RecommendationResult#getConfidence()
	 */
	@Override
	public double getConfidence() {
		return confidence;
	}

	/* (non-Javadoc)
	 * @see recommender.core.interfaces.model.RecommendationResult#setScore(double)
	 */
	@Override
	public void setScore(double score) {
		this.score = score;
	}

	/* (non-Javadoc)
	 * @see recommender.core.interfaces.model.RecommendationResult#setConfidence(double)
	 */
	@Override
	public void setConfidence(double confidence) {
		this.confidence = confidence;
	}

	/* (non-Javadoc)
	 * @see recommender.core.interfaces.model.RecommendationResult#getRecommendationId()
	 */
	@Override
	public String getRecommendationId() {
		return String.valueOf(this.post.getContentId());
	}

	/* (non-Javadoc)
	 * @see recommender.core.interfaces.model.RecommendationResult#compareToOtherRecommendationResult(recommender.core.interfaces.model.RecommendationResult)
	 */
	@Override
	public int compareToOtherRecommendationResult(RecommendationResult o) {
		if (o instanceof RecommendedPost) {
			final RecommendedPost<? extends Resource> otherRecommendedPost = (RecommendedPost<? extends Resource>) o;
			
		}
		return -1;
	}

	/* (non-Javadoc)
	 * @see recommender.core.interfaces.model.RecommendationResult#getTitle()
	 */
	@Override
	public String getTitle() {
		return this.post.getResource().getTitle();
	}

}
