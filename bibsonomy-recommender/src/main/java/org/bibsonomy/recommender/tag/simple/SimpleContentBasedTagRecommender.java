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
import java.util.Iterator;
import java.util.Scanner;

import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.recommender.tag.AbstractTagRecommender;
import org.bibsonomy.recommender.tag.model.RecommendedTag;
import org.bibsonomy.recommender.tag.util.termprocessing.TagTermProcessorIterator;
import org.bibsonomy.recommender.util.termprocessing.TermProcessingIterator;

/**
 * Extracts tags from the title of the entity. Cleans the words using a stopword list
 * and the cleanTag method according to the Discovery Challenge. Stops, when it has
 * found enough proper tags.
 * 
 * @see TermProcessingIterator
 * @author rja
 */
public class SimpleContentBasedTagRecommender extends AbstractTagRecommender {
	
	/** Simply adds tags from the entity's title to the given collection. The score of each tag
	 * is its inverse position in the title, such that tags coming earlier will have a higher
	 * score. 
	 * 
	 * @see recommender.core.Recommender#addRecommendation(Collection, recommender.core.interfaces.model.RecommendationEntity)
	 */
	@Override
	protected void addRecommendedTagsInternal(Collection<RecommendedTag> recommendedTags, Post<? extends Resource> entity) {
		final String title = entity.getResource().getTitle();
		if (title != null) {
			/*
			 * extract tags from title using Jens' Termprocessor.
			 */
			final Iterator<String> extractor = buildTagExtractionIterator(title);
			/*
			 * add extracted tags (not more than numberOfTagsToRecommend)
			 */
			int ctr = 0;
			while (extractor.hasNext() && ctr < numberOfTagsToRecommend) {
				final String tag = getCleanedTag(extractor.next());
				if (tag != null) {
					ctr++;
					/*
					 * add one to not get 1.0 as score
					 * TODO: check
					 */
					recommendedTags.add(new RecommendedTag(tag, 1.0 / (ctr + 1.0), 0.0));
				}
			}
		}
	}
	
	@Override
	public String getInfo() {
		return "Simple content based recommender which extracts tags from title, description, URL.";
	}

	private static Iterator<String> buildTagExtractionIterator(final String title) {
		final Scanner s = new Scanner(title);
		s.useDelimiter("([\\|/\\\\ \t;!,\\-:\\)\\(\\]\\[\\}\\{]+)|(\\.[\\t ]+)");
		return new TagTermProcessorIterator(s);
	}

	@Override
	protected void setFeedbackInternal(Post<? extends Resource> entity, RecommendedTag tag) {
		// ignored
	}

}
