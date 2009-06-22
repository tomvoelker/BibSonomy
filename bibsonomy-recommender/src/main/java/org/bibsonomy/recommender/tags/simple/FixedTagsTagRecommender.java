package org.bibsonomy.recommender.tags.simple;

import java.util.Collection;
import java.util.SortedSet;
import java.util.TreeSet;

import org.bibsonomy.model.Post;
import org.bibsonomy.model.RecommendedTag;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.comparators.RecommendedTagComparator;
import org.bibsonomy.services.recommender.TagRecommender;

/**
 * Always recommends the tags given in the constructor.
 * 
 * @author rja
 * @version $Id$
 */
public class FixedTagsTagRecommender implements TagRecommender {


	private SortedSet<RecommendedTag> tags;

	/**
	 * Adds the given tags to the fixed set of tags, ordered by their 
	 * occurrence in the arrays.
	 * 
	 * @param tags
	 */
	public FixedTagsTagRecommender(final String[] tags) {
		this.tags = new TreeSet<RecommendedTag>(new RecommendedTagComparator());
		for (int i = 0; i < tags.length; i++) {
			this.tags.add(new RecommendedTag(tags[i], 1.0 / (i + 1.0), 0.0));
		}
	}
	
	/**
	 * 
	 * @param tags - the tags this recommender will recommend.
	 */
	public FixedTagsTagRecommender(final SortedSet<RecommendedTag> tags) {
		super();
		this.tags = tags;
	}

	@Override
	public void addRecommendedTags(Collection<RecommendedTag> recommendedTags, Post<? extends Resource> post) {
		recommendedTags.addAll(tags);
	}

	@Override
	public String getInfo() {
		return "A simple recommender with a fixed set of tags.";
	}

	@Override
	public SortedSet<RecommendedTag> getRecommendedTags(Post<? extends Resource> post) {
		return tags;
	}

	@Override
	public void setFeedback(Post<? extends Resource> post) {
		/*
		 * ignored
		 */
	}
}
