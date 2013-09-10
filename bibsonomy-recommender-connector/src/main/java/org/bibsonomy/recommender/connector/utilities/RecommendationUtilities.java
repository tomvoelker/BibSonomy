package org.bibsonomy.recommender.connector.utilities;

import java.util.SortedSet;
import java.util.TreeSet;

import org.bibsonomy.model.RecommendedTag;
import org.bibsonomy.model.comparators.RecommendedTagComparator;

/**
 * This class provides procedures for the conversion of BibSonomy recommended
 * tags to the library internal representation and vice versa.
 * 
 * @author lukas
 *
 */
public class RecommendationUtilities {

	/**
	 * this method converts recommended tags from the library to BibSonomy's representation.
	 * 
	 * @param tags the library's recommended tag representations
	 * @return the BibSonomy's recommended tag representations
	 */
	public static SortedSet<RecommendedTag> getRecommendedTags(SortedSet<recommender.core.model.RecommendedTag> tags) {
		SortedSet<RecommendedTag> bibRecTags = new TreeSet<RecommendedTag>(new RecommendedTagComparator());
		for(recommender.core.model.RecommendedTag tag : tags) {
			RecommendedTag toAdd = new RecommendedTag(tag.getName(), tag.getScore(), tag.getConfidence());
			bibRecTags.add(toAdd);
		}
		return bibRecTags;
	}
	
	/**
	 * this method converts BibSonomy's recommended tags to the library's internal representation.
	 * 
	 * @param tags the BibSonomy recommended tag representations
	 * @return the library's internal representations
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static SortedSet<recommender.core.model.RecommendedTag> getRecommendedTagsFromBibRecTags(SortedSet<RecommendedTag> tags) {
		SortedSet<recommender.core.model.RecommendedTag> recTags = new TreeSet<recommender.core.model.RecommendedTag>(new recommender.core.util.RecommendationResultComparator());
		for(RecommendedTag tag : tags) {
			recommender.core.model.RecommendedTag toAdd = new recommender.core.model.RecommendedTag(tag.getName(), tag.getScore(), tag.getConfidence());
			recTags.add(toAdd);
		}
		return recTags;
	}
}
