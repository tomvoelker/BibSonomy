package org.bibsonomy.recommender.tags.popular;

import java.sql.SQLException;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.log4j.Logger;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.RecommendedTag;
import org.bibsonomy.model.RecommendedTagComparator;
import org.bibsonomy.model.Resource;
import org.bibsonomy.recommender.DBAccess;
import org.bibsonomy.recommender.params.Pair;
import org.bibsonomy.recommender.tags.TagRecommender;

/**
 * Returns the most popular (i.e., most often attached) tags of the resource as 
 * recommendation for the post.  
 * 
 * @author fei
 * @version $Id$
 */
public class MostPopularByResourceTagRecommender implements TagRecommender {
	private static final Logger log = Logger.getLogger(DBAccess.class);
	
	private static final int MAX_NUMBER_OF_TAGS = 5;
	
	public void addRecommendedTags(final SortedSet<RecommendedTag> recommendedTags, final Post<? extends Resource> post) {
		recommendedTags.addAll(getRecommendedTags(post));
	}

	public String getInfo() {
		return "Most Popular Tags By User Recommender";
	}
	 
	/**
	 * Returns the resource's overall most popular tags
	 * 
	 * @see org.bibsonomy.recommender.tags.TagRecommender#getRecommendedTags(org.bibsonomy.model.Post)
	 */
	public SortedSet<RecommendedTag> getRecommendedTags(final Post<? extends Resource> post) {
		final Resource resource = post.getResource();
		/*
		 * FIXME: do we have to call recalculateHashes() first?
		 */
		final String intraHash = resource.getIntraHash();
		
		final SortedSet<RecommendedTag> result = new TreeSet<RecommendedTag>(new RecommendedTagComparator());
		
		if (intraHash != null) {
			try {
				/*
				 * we get the count to normalize the score
				 */
				final Integer count = DBAccess.getNumberOfTagsForResource(resource.getClass(), intraHash);
				
				final List<Pair<String,Integer>> tags = DBAccess.getMostPopularTagsForResource(resource.getClass(), intraHash, MAX_NUMBER_OF_TAGS);
				for (Pair<String,Integer> tag : tags) {
					// TODO: use some sensible confidence value
					final double tmp = (1.0*tag.getSecond())/count;
					final RecommendedTag recTag = new RecommendedTag(tag.getFirst(),tmp,0.5);
					result.add(recTag);
				}
			} catch (SQLException ex) {
				log.error("Error getting recommendations for resource " + resource, ex);
			}
		}
		// all done
		return result;
	}
}
