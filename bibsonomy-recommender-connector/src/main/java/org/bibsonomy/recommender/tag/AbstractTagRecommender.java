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
package org.bibsonomy.recommender.tag;

import java.util.Collection;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.recommender.tag.model.RecommendedTag;
import org.bibsonomy.util.TagStringUtils;

import recommender.core.Recommender;
import recommender.core.util.RecommendationResultComparator;

/**
 * The basic skeleton to implement a tag recommender.
 * 
 * @author rja
 */
public abstract class AbstractTagRecommender implements Recommender<Post<? extends Resource>, RecommendedTag> {
	private static final Log log = LogFactory.getLog(AbstractTagRecommender.class);
	
	/**
	 * The maximal number of tags the recommender shall return on a call to
	 * {@link #getRecommendation(RecommendationEntity)}.
	 */
	protected int numberOfTagsToRecommend = Recommender.DEFAULT_NUMBER_OF_RESULTS_TO_RECOMMEND;
	
	/**
	 * Should the recommender return only tags cleaned according to 
	 * {@link TagStringUtils#cleanTag(String)} and removed according to
	 * {@link TagStringUtils#isIgnoreTag(String)}?
	 */
	protected boolean cleanTags = false;

	/**
	 * Returns user's five overall most popular tags
	 * 
	 * @see recommender.core.Recommender#getRecommendation(recommender.core.interfaces.model.RecommendationEntity)
	 */
	@Override
	public SortedSet<RecommendedTag> getRecommendation(final Post<? extends Resource> entity) {
		final SortedSet<RecommendedTag> recommendedTags = new TreeSet<RecommendedTag>(new RecommendationResultComparator<RecommendedTag>());
		this.addRecommendation(recommendedTags, entity);
		
		return recommendedTags;
	}

	/**
	 * @return The (maximal) number of tags this recommender shall return.
	 */
	public int getNumberOfTagsToRecommend() {
		return this.numberOfTagsToRecommend;
	}

	/** Set the (maximal) number of tags this recommender shall return. The default is {@value #DEFAULT_NUMBER_OF_TAGS_TO_RECOMMEND}.
	 * 
	 * @param numberOfTagsToRecommend
	 */
	public void setNumberOfTagsToRecommend(int numberOfTagsToRecommend) {
		this.numberOfTagsToRecommend = numberOfTagsToRecommend;
	}

	@Override
	public void addRecommendation(final Collection<RecommendedTag> recommendedTags, final Post<? extends Resource> entity) {
		log.debug("Getting tag recommendations for " + entity);
		this.addRecommendedTagsInternal(recommendedTags, entity);
		if (log.isDebugEnabled()) log.debug("Recommending tags " + recommendedTags);
	}
	
	/**
	 * use this methods to add recommendations
	 * 
	 * @param recommendedTags
	 * @param entity
	 */
	protected abstract void addRecommendedTagsInternal(Collection<RecommendedTag> recommendedTags, Post<? extends Resource> entity);

	@Override
	public void setFeedback(Post<? extends Resource> entity, RecommendedTag tag) {
		log.debug("got TagRecomendationEntity with id " + entity + " as feedback.");
		this.setFeedbackInternal(entity, tag);
	}
	
	/**
	 * use this method to set feedback
	 * @param post
	 * @param tag
	 */
	protected abstract void setFeedbackInternal(Post<? extends Resource> post, RecommendedTag tag);

	
	/**
	 * @return The current value of cleanTags. Defaults to <code>false</code>.
	 */
	public boolean isCleanTags() {
		return this.cleanTags;
	}

	/**
	 * Should the recommender return only tags cleaned according to 
	 * {@link TagStringUtils#cleanTag(String)} and removed according to
	 * {@link TagStringUtils#isIgnoreTag(String)}?
	 * The default is <code>false</code>
	 * 
	 * @param cleanTags
	 */
	public void setCleanTags(boolean cleanTags) {
		this.cleanTags = cleanTags;
	}
	
	/**
	 * Cleans the tag depending on the setting of {@link #cleanTags}. 
	 * If it is <code>false</code> (default), the tag is returned as is.
	 * If it is <code>true</code>, the tag is cleaned according to {@link TagStringUtils#cleanTag(String)}
	 * and checked against {@link TagStringUtils#isIgnoreTag(String)}. 
	 * If it should be ignored, <code>null</code> is returned, else the
	 * cleaned tag.
	 * 
	 * This method should be used by all recommenders extending this class before
	 * adding tags to the result set.
	 * 
	 * @param tag 
	 * @return The tag - either cleaned or not, or <code>null</code> if it is
	 * an ignore tag.
	 */
	protected String getCleanedTag(final String tag) {
		if (cleanTags) {
			final String cleanedTag = TagStringUtils.cleanTag(tag);
			if (TagStringUtils.isIgnoreTag(cleanedTag)) {
				return null;
			}
			
			return cleanedTag;
		}
		
		return tag;
	}
	
	@Override
	public void setNumberOfResultsToRecommend(int numberOfResultsToRecommend) {
		this.numberOfTagsToRecommend = numberOfResultsToRecommend;
	}

}