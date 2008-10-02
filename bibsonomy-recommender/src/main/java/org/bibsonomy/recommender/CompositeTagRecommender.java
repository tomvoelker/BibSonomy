package org.bibsonomy.recommender;

import java.util.LinkedList;
import java.util.List;

import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.Tag;

/**
 * @author rja
 * @version $Id$
 */
public class CompositeTagRecommender implements TagRecommender {

	private final List<TagRecommender> recommender = new LinkedList<TagRecommender>();

	public List<Tag> getRecommendedTags(Post<? extends Resource> post) {
		final List<Tag> recommendedTags = new LinkedList<Tag>();
		for (final TagRecommender t: recommender) {
			t.addRecommendedTags(recommendedTags, post);
		}
		return recommendedTags;
	}

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

	public void addRecommendedTags(List<Tag> recommendedTags, Post<? extends Resource> post) {
		for (final TagRecommender t: recommender) {
			t.addRecommendedTags(recommendedTags, post);
		}
	}

}
