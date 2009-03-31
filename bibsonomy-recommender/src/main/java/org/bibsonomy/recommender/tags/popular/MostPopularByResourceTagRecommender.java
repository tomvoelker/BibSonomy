package org.bibsonomy.recommender.tags.popular;

import java.sql.SQLException;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.log4j.Logger;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.RecommendedTag;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.comparators.RecommendedTagComparator;
import org.bibsonomy.recommender.tags.database.params.Pair;
import org.bibsonomy.services.recommender.TagRecommender;
import org.bibsonomy.recommender.tags.database.DBAccess;

/**
 * Returns the most popular (i.e., most often attached) tags of the resource as 
 * recommendation for the post.  
 * 
 * @author fei
 * @version $Id$
 */
public class MostPopularByResourceTagRecommender implements TagRecommender {
	private static final Logger log = Logger.getLogger(MostPopularByResourceTagRecommender.class);

	private static final int DEFAULT_NUMBER_OF_TAGS_TO_RECOMMEND = 5;
	
	private int numberOfTagsToRecommend = DEFAULT_NUMBER_OF_TAGS_TO_RECOMMEND;

	public void addRecommendedTags(final SortedSet<RecommendedTag> recommendedTags, final Post<? extends Resource> post) {
		recommendedTags.addAll(getRecommendedTags(post));
	}

	public String getInfo() {
		return "Most Popular Tags By Resource Recommender";
	}

	/**
	 * Returns the resource's overall most popular tags
	 * 
	 * @see org.bibsonomy.services.recommender.TagRecommender#getRecommendedTags(org.bibsonomy.model.Post)
	 */
	public SortedSet<RecommendedTag> getRecommendedTags(final Post<? extends Resource> post) {

		log.debug("Getting tag recommendations for " + post);

		final Resource resource = post.getResource();
		/*
		 * we have to call recalculateHashes() first, otherwise the intraHash is not available
		 */
		resource.recalculateHashes();
		
		final String intraHash = resource.getIntraHash();

		final SortedSet<RecommendedTag> result = new TreeSet<RecommendedTag>(new RecommendedTagComparator());

		if (intraHash != null) {
			try {
				/*
				 * we get the count to normalize the score
				 */
				final Integer count = DBAccess.getNumberOfTagsForResource(resource.getClass(), intraHash);
				log.debug("Resource has " + count + " different public tags.");

				final List<Pair<String,Integer>> tags = DBAccess.getMostPopularTagsForResource(resource.getClass(), intraHash, numberOfTagsToRecommend);
				if (tags != null && !tags.isEmpty()) {
					for (Pair<String,Integer> tag : tags) {
						// TODO: use some sensible confidence value
						final double tmp = (1.0 * tag.getSecond()) / count;
						final RecommendedTag recTag = new RecommendedTag(tag.getFirst(), tmp, 0.5);
						
						result.add(recTag);
					}
					log.debug("Returning the tags " + result);
				} else {
					log.debug("Resource not found or no tags available.");
				}
			} catch (SQLException ex) {
				log.error("Error getting recommendations for resource " + resource, ex);
			}
		} else {
			log.debug("Could not get recommendations, because no intraHash was given.");
		}
		// all done
		return result;
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
