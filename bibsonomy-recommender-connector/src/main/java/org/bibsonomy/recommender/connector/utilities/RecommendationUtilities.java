package org.bibsonomy.recommender.connector.utilities;

import java.util.SortedSet;
import java.util.TreeSet;

import org.bibsonomy.model.RecommendedTag;
import org.bibsonomy.model.comparators.RecommendedTagComparator;


public class RecommendationUtilities {

	public static SortedSet<RecommendedTag> getRecommendedTags(SortedSet<recommender.core.model.RecommendedTag> tags) {
		SortedSet<RecommendedTag> bibRecTags = new TreeSet<RecommendedTag>(new RecommendedTagComparator());
		for(recommender.core.model.RecommendedTag tag : tags) {
			RecommendedTag toAdd = new RecommendedTag(tag.getName(), tag.getScore(), tag.getConfidence());
			bibRecTags.add(toAdd);
		}
		return bibRecTags;
	}
	
	public static SortedSet<recommender.core.model.RecommendedTag> getRecommendedTagsFromBibRecTags(SortedSet<RecommendedTag> tags) {
		SortedSet<recommender.core.model.RecommendedTag> recTags = new TreeSet<recommender.core.model.RecommendedTag>(new recommender.impl.temp.copy.RecommendationResultComparator());
		for(RecommendedTag tag : tags) {
			recommender.core.model.RecommendedTag toAdd = new recommender.core.model.RecommendedTag(tag.getName(), tag.getScore(), tag.getConfidence());
			recTags.add(toAdd);
		}
		return recTags;
	}
}
