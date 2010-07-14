package org.bibsonomy.recommender.tags.simple;

import java.util.Collection;
import java.util.Iterator;
import java.util.Scanner;

import org.bibsonomy.model.Post;
import org.bibsonomy.model.RecommendedTag;
import org.bibsonomy.model.Resource;
import org.bibsonomy.recommender.tags.AbstractTagRecommender;
import org.bibsonomy.recommender.tags.simple.termprocessing.TermProcessingIterator;

/**
 * Extracts tags from the title of the post. Cleans the words using a stopword list
 * and the cleanTag method according to the Discovery Challenge. Stops, when it has
 * found enough proper tags.
 * 
 * @see TermProcessingIterator
 * @author rja
 * @version $Id$
 */
public class SimpleContentBasedTagRecommender extends AbstractTagRecommender {
	
	/** Simply adds tags from the post's title to the given collection. The score of each tag
	 * is its inverse position in the title, such that tags coming earlier will have a higher
	 * score. 
	 * 
	 * @see org.bibsonomy.services.recommender.TagRecommender#addRecommendedTags(java.util.Collection, org.bibsonomy.model.Post)
	 */
	@Override
	protected void addRecommendedTagsInternal(Collection<RecommendedTag> recommendedTags, Post<? extends Resource> post) {
		final String title = post.getResource().getTitle();
		if (title != null) {
			/*
			 * extract tags from title using Jens' Termprocessor.
			 */

			final Iterator<String> extractor = buildTagExtractionIterator(title);
			/*
			 * add extracted tags (not more than numberOfTagsToRecommend)
			 */
			int ctr = 0;
			while(extractor.hasNext() == true && ctr < numberOfTagsToRecommend) {
				final String tag = getCleanedTag(extractor.next());
				if (tag != null) {
					ctr++;
					/*
					 * add one to not get 1.0 as score 
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
	protected void setFeedbackInternal(Post<? extends Resource> post) {
		/*
		 * ignored
		 */
	}

}
