package org.bibsonomy.recommender.tag.simple;

import java.util.Collection;
import java.util.Iterator;
import java.util.Scanner;

import org.bibsonomy.recommender.util.termprocessing.TermProcessingIterator;

import recommender.core.interfaces.model.TagRecommendationEntity;
import recommender.impl.model.RecommendedTag;
import recommender.impl.tags.AbstractTagRecommender;

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
	protected void addRecommendedTagsInternal(Collection<RecommendedTag> recommendedTags, TagRecommendationEntity entity) {
		final String title = entity.getTitle();
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

	private Iterator<String> buildTagExtractionIterator(final String title) {
		final Scanner s = new Scanner(title);
		s.useDelimiter("([\\|/\\\\ \t;!,\\-:\\)\\(\\]\\[\\}\\{]+)|(\\.[\\t ]+)");
		return new TermProcessingIterator(s);
	}

	@Override
	protected void setFeedbackInternal(TagRecommendationEntity entity, RecommendedTag tag) {
		// ignored
	}

}
