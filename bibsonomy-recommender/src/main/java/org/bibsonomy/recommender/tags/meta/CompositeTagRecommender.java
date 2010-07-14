package org.bibsonomy.recommender.tags.meta;

import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.bibsonomy.model.Post;
import org.bibsonomy.model.RecommendedTag;
import org.bibsonomy.model.Resource;
import org.bibsonomy.services.recommender.TagRecommender;

/**
 * @author rja
 * @version $Id$
 */
public class CompositeTagRecommender implements TagRecommender {

	private final List<TagRecommender> recommender = new LinkedList<TagRecommender>();
	private final Comparator<RecommendedTag> comparator;
	
	/** Create a new instance of this class. The comparator is necessary to fill the
	 * SortedSet in {@link #getRecommendedTags(Post)}. 
	 * 
	 * @param comparator
	 */
	public CompositeTagRecommender(final Comparator<RecommendedTag> comparator) {
		this.comparator = comparator;
	}
	
	@Override
	public SortedSet<RecommendedTag> getRecommendedTags(Post<? extends Resource> post) {
		final SortedSet<RecommendedTag> recommendedTags = new TreeSet<RecommendedTag>(comparator);
		addRecommendedTags(recommendedTags, post);
		return recommendedTags;
	}

	@Override
	public String getInfo() {
		return "Generic composite scraper.";
	}

	/** Adds a tag recommender to the list of recommenders.
	 *  
	 * @param tagRecommender
	 */
	public void addTagRecommender(final TagRecommender tagRecommender) {
		this.recommender.add(tagRecommender);
	}

	@Override
	public void addRecommendedTags(Collection<RecommendedTag> recommendedTags, Post<? extends Resource> post) {
		for (final TagRecommender t: recommender) {
			t.addRecommendedTags(recommendedTags, post);
		}
	}

	@Override
	public void setFeedback(Post<? extends Resource> post) {
		for (final TagRecommender t: recommender) {
			t.setFeedback(post);
		}
		
	}

}
