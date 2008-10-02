package org.bibsonomy.recommender;

import java.util.Comparator;

import org.bibsonomy.model.Tag;

/** Compares two recommended tags.
 * Tags are ordered by their score (confidence not considered).
 * <br/>Two tags are equal based on {@link Tag#equals(Object)} - 
 * independent of their scores!   
 * 
 * @author rja
 * @version $Id$
 */
public class RecommendedTagComparator implements Comparator<RecommendedTag> {

	public int compare(RecommendedTag o1, RecommendedTag o2) {
		if (o1 == null) return -1;
		if (o2 == null) return 1;
		/*
		 * tag names equal: regard them as equal
		 */
		if (o1.equals(o2)) return 0;
		/*
		 * the highest score should come first (in the set) - hence, 
		 * do o2 - o1 
		 */
		int signum = new Double(Math.signum(o2.getScore() - o1.getScore())).intValue();
		if (signum != 0) return signum;
		/*
		 * scores equal: consider confidence
		 */
		signum = new Double(Math.signum(o2.getConfidence() - o1.getConfidence())).intValue();
		if (signum != 0) return signum;
		/*
		 * scores and confidence equal (but tag names not): return using comparaTo from Tag.
		 */
		return o1.compareTo(o2);
	}
}
