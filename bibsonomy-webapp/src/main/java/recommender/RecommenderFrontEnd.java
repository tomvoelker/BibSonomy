/*
 * Created on 08.04.2006
 */
package recommender;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import recommender.db.backend.Database;
import recommender.db.backend.DatabaseAction;
import recommender.db.operations.recommendation.GetMostUsedTagsOfUser;
import recommender.db.operations.recommendation.NeuroUserContentTagRecommendation;
import recommender.model.RecommendedTag;
import resources.Bibtex;
import resources.Bookmark;

public class RecommenderFrontEnd {
	private static final Logger log = Logger.getLogger(RecommenderFrontEnd.class);
	private static Comparator<RecommendedTag> idComp = new Comparator<RecommendedTag>() {

		public int compare(RecommendedTag o1, RecommendedTag o2) {
			return o1.getId() - o2.getId();
		}
		
	};
	
	
	public static Collection<RecommendedTag> getRecommendation(final String userName, String hash, Class resourceType, String title) {
		return getRecommendation(userName,hash,resourceType,title,8);
	}
	
	public static Collection<RecommendedTag> getRecommendation(final String userName, String hash, Class resourceType, String title, final int results) {
		final NeuroUserContentTagRecommendation recommender;
		final String extendedHash;
		if (Bookmark.class.equals(resourceType) == true) {
			extendedHash = Bookmark.CONTENT_TYPE + hash;
		} else if (Bibtex.class.equals(resourceType) == true) {
			extendedHash = Bibtex.CONTENT_TYPE + hash;
		} else {
			log.error("unknown ResourceType '" + resourceType.getName() + "'");
			return new ArrayList<RecommendedTag>(0);
		}
		final Iterator<String> extractor = NeuroUserContentTagRecommendation.buildTagExtractionIterator(title);
		final Collection<String> extracted = new ArrayList<String>();
		while(extractor.hasNext() == true) {
			extracted.add(extractor.next());
		}
		final String extractedString = NeuroUserContentTagRecommendation.buildTagCSVFromIterator(extracted.iterator());
		log.debug("recommending for content with hash=" + extendedHash);
		log.debug("keyword from title: " + extracted);
		recommender = new NeuroUserContentTagRecommendation(userName, extendedHash, extractedString, results);
		
		final Collection<RecommendedTag> rVal = new ArrayList<RecommendedTag>();
		final Set<String> containsIndex = new HashSet<String>();
		new Database(false, new DatabaseAction<List<RecommendedTag>>() {
			@Override
			protected List<RecommendedTag> action() {
				try {
					runDBOperation(recommender);
					log.debug("executing");
					for (RecommendedTag tag : recommender) {
						log.debug("found " + tag);
						rVal.add(tag);
						containsIndex.add(tag.getName());
					}
				} finally {
					recommender.close();
				}
				
				int missing = results - rVal.size();
				if (missing > 0) {
					GetMostUsedTagsOfUser mostUsed = new GetMostUsedTagsOfUser(userName);
					try {
						runDBOperation(mostUsed);
						for (RecommendedTag used : mostUsed) {
							if (containsIndex.contains(used.getName()) == false) {
								used.setScore(used.getScore() / 1000d);
								rVal.add(used);
								containsIndex.add(used.getName());
								if (--missing <= 0) {
									break;
								}
							}
						}
					} finally {
						mostUsed.close();
					}
				}
				return null;
			}
		});
		int missing = results - rVal.size();
		if (missing > 0) {
			for (RecommendedTag r : rVal) {
				containsIndex.add(r.getName().toLowerCase());
			}
			for (String x : extracted) {
				if (containsIndex.contains(x.toLowerCase()) == false) {
					rVal.add(new RecommendedTag(-1,x,0d));
					if (--missing <= 0) {
						break;
					}
				}
			}
		}
		return rVal;
	}
}
