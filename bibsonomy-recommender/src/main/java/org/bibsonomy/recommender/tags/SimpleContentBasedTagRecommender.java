package org.bibsonomy.recommender.tags;

import java.util.List;

import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.Tag;
import org.bibsonomy.recommender.TagRecommender;

/**
 * @author rja
 * @version $Id$
 */
public class SimpleContentBasedTagRecommender implements TagRecommender {

	public void addRecommendedTags(List<Tag> recommendedTags, Post<? extends Resource> post) {
		// TODO Auto-generated method stub

	}

	public String getInfo() {
		return "Simple content based recommender which extracts tags from title, description, URL.";
	}

	public List<Tag> getRecommendedTags(Post<? extends Resource> post) {
		// TODO Auto-generated method stub
		return null;
	}

}
