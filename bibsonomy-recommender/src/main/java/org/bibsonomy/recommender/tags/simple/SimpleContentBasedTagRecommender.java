package org.bibsonomy.recommender.tags.simple;

import java.util.Iterator;
import java.util.Scanner;
import java.util.SortedSet;
import java.util.TreeSet;

import org.bibsonomy.model.Post;
import org.bibsonomy.model.RecommendedTag;
import org.bibsonomy.model.RecommendedTagComparator;
import org.bibsonomy.model.Resource;
import org.bibsonomy.recommender.tags.TagRecommender;
import org.bibsonomy.recommender.tags.simple.termprocessing.TermProcessingIterator;

/**
 * @author rja
 * @version $Id$
 */
public class SimpleContentBasedTagRecommender implements TagRecommender {

	/** Simply adds recommendations at end of list. 
	 * 
	 * @see org.bibsonomy.recommender.tags.TagRecommender#addRecommendedTags(java.util.SortedSet, org.bibsonomy.model.Post)
	 */
	public void addRecommendedTags(SortedSet<RecommendedTag> recommendedTags, Post<? extends Resource> post) {
		recommendedTags.addAll(getRecommendedTags(post));
	}

	public String getInfo() {
		return "Simple content based recommender which extracts tags from title, description, URL.";
	}

	public SortedSet<RecommendedTag> getRecommendedTags(Post<? extends Resource> post) {
		final SortedSet<RecommendedTag> extracted = new TreeSet<RecommendedTag>(new RecommendedTagComparator());
		final String title = post.getResource().getTitle();
		if (title != null) {
			/*
			 * extract tags from title using Jens' Termprocessor.
			 */

			final Iterator<String> extractor = buildTagExtractionIterator(title);
			/*
			 * add all extracted tags
			 */
			while(extractor.hasNext() == true) {
				extracted.add(new RecommendedTag(extractor.next(), 0.5, 0.0));
			}
		}
		return extracted;
	}

	private Iterator<String> buildTagExtractionIterator(final String title) {
		final Scanner s = new Scanner(title);
		s.useDelimiter("([\\|/\\\\ \t;!,\\-:\\)\\(\\]\\[\\}\\{]+)|(\\.[\\t ]+)");
		return new TermProcessingIterator(s);
	}

}
