package org.bibsonomy.recommender.tags.simple;

import java.util.Collection;
import java.util.Iterator;
import java.util.Scanner;
import java.util.SortedSet;
import java.util.TreeSet;

import org.bibsonomy.model.Post;
import org.bibsonomy.model.RecommendedTag;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.comparators.RecommendedTagComparator;
import org.bibsonomy.recommender.tags.simple.termprocessing.TermProcessingIterator;
import org.bibsonomy.services.recommender.TagRecommender;

/**
 * Extracts tags from the title of the post. Cleans the words using a stopword list
 * and the cleanTag method according to the Discovery Challenge. Stops, when it has
 * found enough proper tags.
 * 
 * @see TermProcessingIterator
 * @author rja
 * @version $Id$
 */
public class SimpleContentBasedTagRecommender implements TagRecommender {

	private static final int DEFAULT_NUMBER_OF_TAGS_TO_RECOMMEND = 5;

	private int numberOfTagsToRecommend = DEFAULT_NUMBER_OF_TAGS_TO_RECOMMEND;


	/** Simply adds tags from the post's title to the given collection. The score of each tag
	 * is its inverse position in the title, such that tags coming earlier will have a higher
	 * score. 
	 * 
	 * @see org.bibsonomy.services.recommender.TagRecommender#addRecommendedTags(java.util.Collection, org.bibsonomy.model.Post)
	 */
	public void addRecommendedTags(Collection<RecommendedTag> recommendedTags, Post<? extends Resource> post) {
		final String title = post.getResource().getTitle();
		if (title != null) {
			/*
			 * extract tags from title using Jens' Termprocessor.
			 */

			final Iterator<String> extractor = buildTagExtractionIterator(title);
			/*
			 * add extracted tags (not more than numberOfTagsToRecommend
			 */
			int ctr = 0;
			while(extractor.hasNext() == true && ctr++ < numberOfTagsToRecommend) {
				recommendedTags.add(new RecommendedTag(extractor.next(), 1.0 / (ctr + 1.0), 0.0));
			}
		}
	}

	public String getInfo() {
		return "Simple content based recommender which extracts tags from title, description, URL.";
	}

	public SortedSet<RecommendedTag> getRecommendedTags(Post<? extends Resource> post) {
		final SortedSet<RecommendedTag> recommendedTags = new TreeSet<RecommendedTag>(new RecommendedTagComparator());
		addRecommendedTags(recommendedTags, post);
		return recommendedTags;
	}

	private Iterator<String> buildTagExtractionIterator(final String title) {
		final Scanner s = new Scanner(title);
		s.useDelimiter("([\\|/\\\\ \t;!,\\-:\\)\\(\\]\\[\\}\\{]+)|(\\.[\\t ]+)");
		return new TermProcessingIterator(s);
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

}
