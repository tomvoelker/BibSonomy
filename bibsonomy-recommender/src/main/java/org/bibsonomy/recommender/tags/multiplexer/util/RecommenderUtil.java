package org.bibsonomy.recommender.tags.multiplexer.util;

import org.bibsonomy.recommender.tags.TagRecommenderConnector;
import org.bibsonomy.services.recommender.TagRecommender;

/**
 * @author bsc
 * @version $Id$
 */
public class RecommenderUtil {
	/**
	 * Get the Recommenderid of a given TagRecommender.
	 * @param rec
	 * @return Recommenderid
	 */
	public static String getRecommenderId(TagRecommender rec) {
		if (rec instanceof TagRecommenderConnector) {
			return ((TagRecommenderConnector) rec).getId();
		} else {
			return rec.getClass().getCanonicalName();
		}
	}
}
