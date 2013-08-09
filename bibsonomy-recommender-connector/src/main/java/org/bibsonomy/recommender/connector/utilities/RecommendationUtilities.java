package org.bibsonomy.recommender.connector.utilities;

import java.util.SortedSet;
import java.util.TreeSet;

import org.bibsonomy.model.BibSonomyRecommendedTag;
import org.bibsonomy.model.comparators.RecommendedTagComparator;

import recommender.core.model.RecommendedTag;

public class RecommendationUtilities {

	public static SortedSet<BibSonomyRecommendedTag> getBibSonomyRecommendedTags(SortedSet<RecommendedTag> tags) {
		SortedSet<BibSonomyRecommendedTag> bibRecTags = new TreeSet<BibSonomyRecommendedTag>(new RecommendedTagComparator());
		for(RecommendedTag tag : tags) {
			BibSonomyRecommendedTag toAdd = new BibSonomyRecommendedTag(tag.getName(), tag.getScore(), tag.getConfidence());
			bibRecTags.add(toAdd);
		}
		return bibRecTags;
	}
	
	public static SortedSet<RecommendedTag> getRecommendedTagsFromBibRecTags(SortedSet<BibSonomyRecommendedTag> tags) {
		SortedSet<RecommendedTag> bibRecTags = new TreeSet<RecommendedTag>(new recommender.impl.temp.copy.RecommendationResultComparator());
		for(BibSonomyRecommendedTag tag : tags) {
			RecommendedTag toAdd = new RecommendedTag(tag.getName(), tag.getScore(), tag.getConfidence());
			bibRecTags.add(toAdd);
		}
		return bibRecTags;
	}
}
