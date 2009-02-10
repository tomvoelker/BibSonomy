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
 * @author fei
 * @version $Id$
 */
public class MostPopularByUserTagRecommender implements TagRecommender {
	private static final Logger log = Logger.getLogger(DBAccess.class);
	
	public void addRecommendedTags(SortedSet<RecommendedTag> recommendedTags,
			Post<? extends Resource> post) {
		recommendedTags.addAll(getRecommendedTags(post));
	}

	public String getInfo() {
		return "Most Popular Tags Recommender";
	}

	// returns user's five overall most popular tags
	public SortedSet<RecommendedTag> getRecommendedTags(
			Post<? extends Resource> post) {
		String username = post.getUser().getName();
		SortedSet<RecommendedTag> result = new TreeSet<RecommendedTag>(new RecommendedTagComparator());
		
		if( username!=null ) {
			try {
				Integer count = DBAccess.getNumberOfTagsForUser(username);
				List<Pair<String,Integer>> tags = DBAccess.getMostPopularTagsForUser(username);
				for( Pair<String,Integer> tag : tags ) {
					// TODO: use some sensible confidence value
					double tmp = (1.0*tag.getSecond())/count;
					RecommendedTag recTag = new RecommendedTag(tag.getFirst(),tmp,0.5);
					result.add(recTag);
				}
			} catch (SQLException ex) {
				log.error("Error getting recommendations for user "+username, ex);
			}
		}
		// all done
		return result;
	}
}
