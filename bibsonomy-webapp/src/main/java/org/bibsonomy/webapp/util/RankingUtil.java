package org.bibsonomy.webapp.util;

import java.util.HashMap;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.Tag;
import org.bibsonomy.webapp.controller.UserPageController;

/**
 * Util class to compute ranking
 * 
 * @author dbenz
 * @version $Id$
 */
public class RankingUtil {
	
	
	/**
	 * the (rough) maximal global count of a tag (used to compute tf/idf-weighting)
	 */
	private static int MAX_TAG_GLOBALCOUNT = 200000;
	
	private static final Log LOGGER = LogFactory.getLog(UserPageController.class);
	
	
	public enum RankingMethod {
		TAG_OVERLAP,
		TFIDF;
	}
	
	/**
	 * Compute the ranking of a list of posts
	 * 
	 * @param <T>
	 * @param sourceUserTags
	 * @param targetUserTags
	 * @param posts
	 */
	public static <T extends Resource> void computeRanking(List<Tag> sourceUserTags, List<Tag> targetUserTags, List<Post<T>> posts, RankingMethod rtype, Boolean normalize) {
		// first, build map of target user's tags
		HashMap<String, Integer> tagGlobalCounts = new HashMap<String,Integer>();
		HashMap<String, Integer> tagUserCounts = new HashMap<String,Integer>();
		int maxUserFreq = 0;
		for (Tag t : sourceUserTags) {
			if (t.getGlobalcount() > 0) {
				tagGlobalCounts.put(t.getName(), t.getGlobalcount());
			}
			tagUserCounts.put(t.getName(), t.getUsercount());
			if (t.getUsercount() > maxUserFreq) {
				maxUserFreq = t.getUsercount();
			}
		}		
		// compute a ranking value for each post
		if (RankingMethod.TFIDF.equals(rtype)) {
			for (Post<T> post : posts) {
				for (Tag tag : post.getTags()) {
					if (tagGlobalCounts.get(tag.getName()) != null  && targetUserTags.contains(tag) ) {					
						post.setRanking( post.getRanking() + ( (tagUserCounts.get(tag.getName()).doubleValue() / maxUserFreq ) * Math.log(MAX_TAG_GLOBALCOUNT / tagGlobalCounts.get(tag.getName()) ) ) );
					}
				}
				// normalize
				if (normalize) {
					post.setRanking(post.getRanking() / post.getTags().size());
				}
			}			
		}
		if (RankingMethod.TAG_OVERLAP.equals(rtype)) {
			for (Post<T> post : posts) {
				for (Tag tag : post.getTags()) {
					if (tagGlobalCounts.get(tag.getName()) != null  && targetUserTags.contains(tag) ) {
						post.setRanking( post.getRanking() + 1);
					}
				}
				// normalize
				if (normalize) {
					post.setRanking(post.getRanking() / post.getTags().size());
				}
			}			
		}						
	}
	
	
	public static <T extends Resource> void computeRanking(List<Tag> sourceUserTags, List<Tag> targetUserTags) {
		// first, build map of target user's tags
		HashMap<String, Integer> tagGlobalCounts = new HashMap<String,Integer>();
		HashMap<String, Integer> tagUserCounts = new HashMap<String,Integer>();
		int maxUserFreq = 0;
		for (Tag t : sourceUserTags) {
			if (t.getGlobalcount() > 0) {
				tagGlobalCounts.put(t.getName(), t.getGlobalcount());
			}
			if (t.getUsercount() > 0) {
				tagUserCounts.put(t.getName(), t.getUsercount());
			}
			if (t.getUsercount() > maxUserFreq) {
				maxUserFreq = t.getUsercount();
			}
		}
		// compute the intersection of tags
		targetUserTags.retainAll(sourceUserTags);
		
		// compute the ranking for the intersection
		for (Tag tag : targetUserTags) {
			// double weight = ( ( ( (double) tagUserCounts.get(tag.getName()) ) / maxUserFreq ) * Math.log(MAX_TAG_GLOBALCOUNT / tagGlobalCounts.get(tag.getName()) ) ) * 100 ;
			tag.setGlobalcount(tag.getUsercount());
			LOGGER.debug("working on tag " + tag.getName() + ", having user freq " + tagUserCounts.get(tag.getName()) + " and global count " + tagGlobalCounts.get(tag.getName()));
			if (tagUserCounts.get(tag.getName()) != null && tagGlobalCounts.get(tag.getName()) != null) {
				double weight = ( ( ( (double) tagUserCounts.get(tag.getName()) ) / maxUserFreq ) * Math.log(MAX_TAG_GLOBALCOUNT / tagGlobalCounts.get(tag.getName()) ) ) * 10 ;
				// tag.setGlobalcount((int) weight);				
				tag.setUsercount((int) weight);
			}
			else {
				tag.setUsercount(0);
			}
		}
				
	}
}

