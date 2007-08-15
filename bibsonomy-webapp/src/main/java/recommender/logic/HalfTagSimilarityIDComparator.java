/*
 * Created on 16.12.2005
 */
package recommender.logic;

import java.util.Comparator;

import recommender.model.HalfTagSimilarity;

public class HalfTagSimilarityIDComparator implements Comparator<HalfTagSimilarity> {

	public int compare(HalfTagSimilarity o1, HalfTagSimilarity o2) {
		return o1.getLeftTagID() - o2.getLeftTagID();
	}

}
