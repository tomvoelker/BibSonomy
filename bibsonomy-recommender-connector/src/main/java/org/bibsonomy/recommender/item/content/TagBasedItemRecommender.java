package org.bibsonomy.recommender.item.content;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import recommender.core.interfaces.model.ItemRecommendationEntity;
import recommender.core.interfaces.model.RecommendationItem;
import recommender.core.interfaces.model.RecommendationTag;
import recommender.impl.model.RecommendedItem;

/**
 * This recommender tries to find many items which have at least
 * one tag equal to the best user tags.
 * Found items are evaluated by cosine similarity.
 * 
 * @author lukas
 *
 */
public class TagBasedItemRecommender extends ContentBasedItemRecommender {

	protected static final String INFO = "";
	
	protected static final int DEFAULT_MAX_TAGS_TO_EVALUATE = 3;
	
	protected int maxTagsToEvaluate = DEFAULT_MAX_TAGS_TO_EVALUATE;

	@Override
	public String getInfo() {
		return INFO;
	}

	/*
	 * (non-Javadoc)
	 * @see recommender.impl.item.content.ContentBasedItemRecommender#addRecommendedItemsInternal(java.util.Collection, recommender.core.interfaces.model.ItemRecommendationEntity)
	 */
	@Override
	protected void addRecommendedItemsInternal(
			Collection<RecommendedItem> recommendations,
			ItemRecommendationEntity entity) {
		
		final List<RecommendationItem> requestingUserItems = this.dbAccess.getItemsForUser(maxItemsToEvaluate, null); // FIXME: (refactor) adapt was entity.getUserName()
		
		final Set<String> requestingUserTitles = this.calculateRequestingUserTitleSet(requestingUserItems);
		
		final List<RecommendationItem> userItems = new ArrayList<RecommendationItem>();
		
		List<CountedTag> sortedExtractedTags = this.extractTagsFromResources(requestingUserItems);
		
		final Set<String> tagsToUse = new HashSet<String>();
		if(sortedExtractedTags.size() <= maxTagsToEvaluate) {
			for(CountedTag tag : sortedExtractedTags) {
				tagsToUse.add(tag.getName());
			}
			userItems.addAll(this.dbAccess.getTaggedItems(maxItemsToEvaluate, tagsToUse));
		} else {
			final int halfSize = sortedExtractedTags.size()/2;
			for(int i = halfSize; (i-halfSize) < maxTagsToEvaluate && i < sortedExtractedTags.size(); i++) {
				tagsToUse.add(sortedExtractedTags.get(i).getName());
			}
			if(tagsToUse.size() > 0) {
				userItems.addAll(this.dbAccess.getTaggedItems(maxItemsToEvaluate, tagsToUse));
			} else {
				return;
			}
		}
		
		final List<RecommendedItem> results = this.calculateSimilarItems(userItems, requestingUserItems, requestingUserTitles);
		
		recommendations.addAll(results);
		
	}

	/**
	 * This method extracts the tags of all resources and counts their number
	 * of appearance.
	 * 
	 * @param requestingUserItems a list with all items of the requesting user
	 * @return a list with tags and their number of appearance
	 */
	protected List<CountedTag> extractTagsFromResources(List<RecommendationItem> requestingUserItems) {
		final Map<String, CountedTag> countTags = new HashMap<String, CountedTag>();
		for(RecommendationItem item: requestingUserItems) {
			for(RecommendationTag tag : item.getTags()) {
				if(countTags.keySet().contains(tag.getName())) {
					countTags.put(tag.getName(), countTags.get(tag.getName()).incrementCount());
				} else {
					countTags.put(tag.getName(), new CountedTag(tag.getName()));
				}
			}
		}
		final List<CountedTag> results = new ArrayList<CountedTag>();
		for(String tag : countTags.keySet()) {
			results.add(countTags.get(tag));
		}
		Collections.sort(results, new Comparator<CountedTag>() {
			@Override
			public int compare(CountedTag o1, CountedTag o2) {
				if(o1.getCount() > o2.getCount()) {
					return 1;
				} else if (o2.getCount() > o1.getCount()) {
					return -1;
				} else {
					return 0;
				}
			}
		});
		return results;
	}

	@Override
	protected void setFeedbackInternal(ItemRecommendationEntity entity,
			RecommendedItem item) {
		

	}
	
	/**
	 * This method creates a set with titles of the requesting user items.
	 * The set is needed to avoid recommendation of known items to the user.
	 * 
	 * @param requestingUserItems the items of the requesting user
	 */
	protected Set<String> calculateRequestingUserTitleSet(List<RecommendationItem> requestingUserItems) {
		final Set<String> requestingUserTitles = new HashSet<String>();
		for(RecommendationItem item : requestingUserItems) {
			requestingUserTitles.add(item.getTitle());
		}
		return requestingUserTitles;
	}
	

	protected class CountedTag {
		
		private String name;
		private int count;
		
		public CountedTag(final String name) {
			this.name = name;
			this.count = 1;
		}
		
		public int getCount() {
			return count;
		}
		public String getName() {
			return name;
		}
		public CountedTag incrementCount() {
			this.count++;
			return this;
		}
	}
}
