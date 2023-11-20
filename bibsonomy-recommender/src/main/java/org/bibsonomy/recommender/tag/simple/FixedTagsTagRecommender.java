/**
 * BibSonomy Recommendation - Tag and resource recommender.
 *
 * Copyright (C) 2006 - 2021 Data Science Chair,
 *                               University of Würzburg, Germany
 *                               https://www.informatik.uni-wuerzburg.de/datascience/home/
 *                           Information Processing and Analytics Group,
 *                               Humboldt-Universität zu Berlin, Germany
 *                               https://www.ibi.hu-berlin.de/en/research/Information-processing/
 *                           Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               https://www.kde.cs.uni-kassel.de/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               https://www.l3s.de/
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

import java.util.TreeSet;

import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.recommender.simple.FixedRecommender;
import org.bibsonomy.recommender.tag.model.RecommendedTag;

import recommender.core.interfaces.model.RecommendationResult;
import recommender.core.util.RecommendationResultComparator;

/**
 * Always recommends the tags given in the constructor.
 * 
 * @author rja
 */
public class FixedTagsTagRecommender extends FixedRecommender<Post<? extends Resource>, RecommendedTag> {
	
	/**
	 * Adds the given tags to the fixed set of tags, ordered by their 
	 * occurrence in the arrays.
	 * 
	 * @param tags
	 */
	public FixedTagsTagRecommender(final String[] tags) {
		super(new TreeSet<RecommendedTag>(new RecommendationResultComparator<RecommendationResult>()));
		for (int i = 0; i < tags.length; i++) {
			this.results.add(new RecommendedTag(tags[i], 1.0 / (i + 1.0), 0.0));
		}
	}
}
