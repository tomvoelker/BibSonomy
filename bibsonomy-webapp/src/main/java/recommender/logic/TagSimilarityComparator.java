/*
 * Created on 16.12.2005
 */
package recommender.logic;

import java.util.Comparator;

import recommender.model.HalfTagSimilarity;

public class TagSimilarityComparator<T extends HalfTagSimilarity> implements Comparator<T> {
	private boolean allowEquality;
	
	public TagSimilarityComparator(boolean allowEquality) {
		this.allowEquality = allowEquality;
	}

	public int compare(T o1, T o2) {
		int rVal = (int) Math.signum(o1.getSimilarity() - o2.getSimilarity());
		if ((rVal == 0) && (allowEquality == false)) {
			return o1.hashCode() - o2.hashCode();
		}
		return rVal;
	}

}
