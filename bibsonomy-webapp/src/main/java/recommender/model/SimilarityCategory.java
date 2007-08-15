/*
 * Created on 17.02.2006
 */
package recommender.model;

import org.apache.log4j.Logger;

import recommender.db.operations.tagvector.AbstractGetVectorEntries;

public enum SimilarityCategory {
	CONTENT("MostSimTagsByContent"),
	USER("MostSimTagsByUser"),
	OVERALL("MostSimTagsOverall"),
	COMBIVECTOROVERALL("MostSimTagsCombiVectorOverall");
	
	private final String mostSimTableName;
	private static final Logger log = Logger.getLogger(SimilarityCategory.class);
	private static final SimilarityCategory[] overall = {OVERALL,COMBIVECTOROVERALL};
	
	private SimilarityCategory(String mostSimTableName) {
		this.mostSimTableName = mostSimTableName;
	}

	public String getMostSimTableName() {
		return mostSimTableName;
	}
	
	public static SimilarityCategory forEntryCategory(AbstractGetVectorEntries.Category c) {
		if (c == AbstractGetVectorEntries.Category.CONTENT) {
			return CONTENT;
		}
		if (c == AbstractGetVectorEntries.Category.USER) {
			return USER;
		}
		log.fatal("unknown category " + c);
		return null;
	}
	
	public boolean isOverallSimilarity() {
		for (SimilarityCategory u : overall) {
			if (u == this) {
				return true;
			}
		}
		return false;
	}
}
