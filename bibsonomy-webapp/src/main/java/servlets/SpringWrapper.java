package servlets;

import org.bibsonomy.services.recommender.TagRecommender;

/**
 * This class is configured in the spring bean definition files (web application
 * context) and can be used to access beans using old style servlets.
 * @author rja
 * @version $Id$
 */
public class SpringWrapper {

	private TagRecommender tagRecommender;

	private static SpringWrapper instance;
	
	private SpringWrapper() {
		// singleton
	}
	
	public static SpringWrapper getInstance() {
		if (instance == null) {
			instance = new SpringWrapper();
		}
		return instance;
	}
	
	
	public TagRecommender getTagRecommender() {
		return this.tagRecommender;
	}

	public void setTagRecommender(TagRecommender tagRecommender) {
		this.tagRecommender = tagRecommender;
	}
	
}
