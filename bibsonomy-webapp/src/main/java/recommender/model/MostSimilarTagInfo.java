/*
 * Created on 16.12.2005
 */
package recommender.model;

import java.util.Comparator;

import org.apache.log4j.Logger;

import recommender.logic.BestList;
import recommender.logic.TagSimilarityComparator;

/**
 * Speichert die sortierten Listen von ähnlichsten Tags in den unterschiedlichen Kategorien. 
 * 
 * @author Jens Illig
 */ 
public class MostSimilarTagInfo {
	public static final int DEFAULT_MOSTSIMILARAMOUNT = 31; // 30 in Liste und eines danach
	public static final Comparator<HalfTagSimilarity> simComp = new TagSimilarityComparator<HalfTagSimilarity>(false);
	private static final Logger log = Logger.getLogger(MostSimilarTagInfo.class);
	private int tagID;
	private BestList<HalfTagSimilarity> userBased = new BestList<HalfTagSimilarity>(DEFAULT_MOSTSIMILARAMOUNT,simComp);
	private BestList<HalfTagSimilarity> contentBased = new BestList<HalfTagSimilarity>(DEFAULT_MOSTSIMILARAMOUNT,simComp);
	private BestList<HalfTagSimilarity> overall = new BestList<HalfTagSimilarity>(DEFAULT_MOSTSIMILARAMOUNT,simComp);
	private BestList<HalfTagSimilarity> combiVectorOverall = new BestList<HalfTagSimilarity>(DEFAULT_MOSTSIMILARAMOUNT,simComp);

	
	public MostSimilarTagInfo(int tagID) {
		this.tagID = tagID;
	}

	public BestList<HalfTagSimilarity> getContentBased() {
		return contentBased;
	}
	public void setContentBased(BestList<HalfTagSimilarity> contentBased) {
		this.contentBased = contentBased;
	}
	
	public BestList<HalfTagSimilarity> getOverall() {
		return overall;
	}
	public void setOverall(BestList<HalfTagSimilarity> overall) {
		this.overall = overall;
	}
	
	public BestList<HalfTagSimilarity> getUserBased() {
		return userBased;
	}
	public void setUserBased(BestList<HalfTagSimilarity> userBased) {
		this.userBased = userBased;
	}

	public int getTagID() {
		return tagID;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getTagID()).append(" by content :\n");
		if (contentBased == null) {
			sb.append("empty\n");
		} else {
			for (HalfTagSimilarity ts : contentBased.getBest()) {
				sb.append(ts.getLeftTagID()).append("\t\t").append(ts.getSimilarity()).append('\n');
			}	
		}
		sb.append(getTagID()).append(" by user :\n");
		if (userBased == null) {
			sb.append("empty\n");
		} else {
			for (HalfTagSimilarity ts : userBased.getBest()) {
				sb.append(ts.getLeftTagID()).append("\t\t").append(ts.getSimilarity()).append('\n');
			}	
		}
		sb.append(getTagID()).append(" overall :\n");
		if (overall == null) {
			sb.append("empty\n");
		} else {
			for (HalfTagSimilarity ts : overall.getBest()) {
				sb.append(ts.getLeftTagID()).append("\t\t").append(ts.getSimilarity()).append('\n');
			}	
		}
		return sb.toString();
	}

	public BestList<HalfTagSimilarity> getCombiVectorOverall() {
		return combiVectorOverall;
	}
	
	public BestList<HalfTagSimilarity> getBestList(SimilarityCategory c) {
		switch (c) {
		case CONTENT:
			return contentBased;
		case USER:
			return userBased;
		case COMBIVECTOROVERALL:
			return combiVectorOverall;
		case OVERALL:
			return overall;
		default:
			log.error("unknown category for " + this.getClass().getName());
			return null;
		}
	}
	
}
