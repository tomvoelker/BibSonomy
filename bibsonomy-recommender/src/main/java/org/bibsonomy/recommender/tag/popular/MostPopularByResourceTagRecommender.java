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
package org.bibsonomy.recommender.tag.popular;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.Collection;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.recommender.tag.AbstractTagRecommender;
import org.bibsonomy.recommender.tag.model.RecommendedTag;
import org.bibsonomy.recommender.tag.service.RecommenderMainTagAccess;

import recommender.core.model.Pair;

/**
 * Returns the most popular (i.e., most often attached) tags of the resource as 
 * recommendation for the entity.
 * 
 * @author fei
 */
public class MostPopularByResourceTagRecommender extends AbstractTagRecommender {
	private static final Log log = LogFactory.getLog(MostPopularByResourceTagRecommender.class);

	private RecommenderMainTagAccess dbAccess;
	
	@Override
	protected void addRecommendedTagsInternal(final Collection<RecommendedTag> recommendedTags, final Post<? extends Resource> entity) {
		this.preparePost(entity);

		final String hash = entity.getResource().getInterHash(); // TODO: use intrahash?
		if (present(hash)) {
			/*
			 * we get the count to normalize the score
			 */
			final int count = this.dbAccess.getNumberOfTagAssignmentsForRecommendationEntity(entity, hash);
			log.debug("Resource has " + count + " TAS.");

			final List<Pair<String, Integer>> tagsWithCount = this.dbAccess.getMostPopularTagsForRecommendationEntity(entity, hash, this.numberOfTagsToRecommend);
			if (present(tagsWithCount)) {
				for (final Pair<String, Integer> tagWithCount : tagsWithCount) {
					final String tag = this.getCleanedTag(tagWithCount.getFirst());
					if (tag != null) {
						recommendedTags.add(new RecommendedTag(tag, ((1.0 * tagWithCount.getSecond().doubleValue()) / count), 0.5));
					}
				}
				log.debug("Returning the tags " + recommendedTags);
			} else {
				log.debug("Resource not found or no tags available.");
			}
		} else {
			log.debug("Could not get recommendations, because no intraHash was given.");
		}
	}

	protected void preparePost(Post<? extends Resource> entity) {
		/*
		 * recalculate the hashes
		 */
		entity.getResource().recalculateHashes();
	}

	@Override
	public String getInfo() {
		return "Most Popular Tags By Resource Recommender";
	}
	
	@Override
	protected void setFeedbackInternal(final Post<? extends Resource> entity, final RecommendedTag tag) {
		// this recommender ignores feedback
	}

	/**
	 * @param dbAccess the dbAccess to set
	 */
	public void setDbAccess(RecommenderMainTagAccess dbAccess) {
		this.dbAccess = dbAccess;
	}
}
