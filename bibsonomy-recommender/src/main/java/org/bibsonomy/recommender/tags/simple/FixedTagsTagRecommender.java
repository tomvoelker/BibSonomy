package org.bibsonomy.recommender.tags.simple;

import java.util.Collection;
import java.util.SortedSet;

import org.bibsonomy.model.Post;
import org.bibsonomy.model.RecommendedTag;
import org.bibsonomy.model.Resource;
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
	 * 
	 * 
	 * @param tags - the tags this recommender will recommend.
	 */
	public FixedTagsTagRecommender(SortedSet<RecommendedTag> tags) {
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
}
