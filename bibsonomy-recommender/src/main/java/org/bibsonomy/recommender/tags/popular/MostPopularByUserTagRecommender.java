package org.bibsonomy.recommender.tags.popular;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Logger;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.RecommendedTag;
import org.bibsonomy.model.Resource;
import org.bibsonomy.recommender.tags.AbstractTagRecommender;
import org.bibsonomy.recommender.tags.database.DBLogic;
import org.bibsonomy.recommender.tags.database.params.Pair;
import org.bibsonomy.services.recommender.TagRecommender;

/**
 * Returns the most popular (i.e., most often used) tags of the user as 
 * recommendation for the post.  
 * 
 * @author fei
 * @version $Id$
 */
public class MostPopularByUserTagRecommender extends AbstractTagRecommender implements TagRecommender {
	private static final Log log = LogFactory.getLog(MostPopularByUserTagRecommender.class);
	
	private DBLogic dbLogic;

	
	protected void addRecommendedTagsInternal(final Collection<RecommendedTag> recommendedTags, final Post<? extends Resource> post) {
		final String username = post.getUser().getName();
		if (username != null) {
			try {
				/*
				 * we get the count to normalize the score
				 */
				final int count = dbLogic.getNumberOfTasForUser(username);
				
				final List<Pair<String,Integer>> tagsWithCount = dbLogic.getMostPopularTagsForUser(username, numberOfTagsToRecommend);
				for (final Pair<String,Integer> tagWithCount : tagsWithCount) {
					final String tag = getCleanedTag(tagWithCount.getFirst());
					if (tag != null) {
						recommendedTags.add(new RecommendedTag(tag, ((1.0 * tagWithCount.getSecond()) / count), 0.5));
					}
				}
			} catch (SQLException ex) {
				log.error("Error getting recommendations for user " + username, ex);
			}
		}
	}

	public String getInfo() {
		return "Most Popular Tags By User Recommender";
	}

	public DBLogic getDbLogic() {
		return this.dbLogic;
	}

	public void setDbLogic(DBLogic dbLogic) {
		this.dbLogic = dbLogic;
	}

	@Override
	protected void setFeedbackInternal(Post<? extends Resource> post) {
		/*
		 * this recommender ignores feedback
		 */
	}
}
