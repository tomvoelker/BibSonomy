package org.bibsonomy.recommender.tags.popular;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.log4j.Logger;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.RecommendedTag;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.comparators.RecommendedTagComparator;
import org.bibsonomy.recommender.tags.database.DBAccess;
import org.bibsonomy.recommender.tags.database.params.Pair;
import org.bibsonomy.services.recommender.TagRecommender;

/**
 * Returns the most popular (i.e., most often used) tags of the user as 
 * recommendation for the post.  
 * 
 * @author fei
 * @version $Id$
 */
public class MostPopularByUserTagRecommender implements TagRecommender {
	private static final Logger log = Logger.getLogger(MostPopularByUserTagRecommender.class);
	
	private static final int DEFAULT_NUMBER_OF_TAGS_TO_RECOMMEND = 5;
	
	private int numberOfTagsToRecommend = DEFAULT_NUMBER_OF_TAGS_TO_RECOMMEND;	
	
	public void addRecommendedTags(final Collection<RecommendedTag> recommendedTags, final Post<? extends Resource> post) {
		final String username = post.getUser().getName();
		if (username != null) {
			try {
				/*
				 * we get the count to normalize the score
				 */
				final Integer count = DBAccess.getNumberOfTagsForUser(username);
				
				final List<Pair<String,Integer>> tags = DBAccess.getMostPopularTagsForUser(username, numberOfTagsToRecommend);
				for (Pair<String,Integer> tag : tags) {
					// TODO: use some sensible confidence value
					final double tmp = (1.0*tag.getSecond())/count;
					final RecommendedTag recTag = new RecommendedTag(tag.getFirst(),tmp,0.5);
					recommendedTags.add(recTag);
				}
			} catch (SQLException ex) {
				log.error("Error getting recommendations for user " + username, ex);
			}
		}
	}

	public String getInfo() {
		return "Most Popular Tags By User Recommender";
	}
	 
	/**
	 * Returns user's five overall most popular tags
	 * 
	 * @see org.bibsonomy.services.recommender.TagRecommender#getRecommendedTags(org.bibsonomy.model.Post)
	 */
	public SortedSet<RecommendedTag> getRecommendedTags(final Post<? extends Resource> post) {
		final SortedSet<RecommendedTag> recommendedTags = new TreeSet<RecommendedTag>(new RecommendedTagComparator());
		addRecommendedTags(recommendedTags, post);
		// all done
		return recommendedTags;
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
