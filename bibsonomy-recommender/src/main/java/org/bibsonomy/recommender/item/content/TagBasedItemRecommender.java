/**
 * BibSonomy-Recommendation-Connector - Connector for the recommender framework for tag and resource recommendation
 *
 * Copyright (C) 2006 - 2016 Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               http://www.kde.cs.uni-kassel.de/
 *                           Data Mining and Information Retrieval Group,
 *                               University of Würzburg, Germany
 *                               http://www.is.informatik.uni-wuerzburg.de/en/dmir/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               http://www.l3s.de/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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

import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.Tag;
import org.bibsonomy.recommender.item.model.RecommendationUser;
import org.bibsonomy.recommender.item.model.RecommendedPost;

/**
 * This recommender tries to find many items which have at least
 * one tag equal to the best user tags.
 * Found items are evaluated by cosine similarity.
 * 
 * @author lukas
 * @param <R> 
 *
 */
public class TagBasedItemRecommender<R extends Resource> extends ContentBasedItemRecommender<R> {

	protected static final String INFO = "";
	
	protected static final int DEFAULT_MAX_TAGS_TO_EVALUATE = 3;
	
	protected int maxTagsToEvaluate = DEFAULT_MAX_TAGS_TO_EVALUATE;

	@Override
	public String getInfo() {
		return INFO;
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.recommender.item.content.ContentBasedItemRecommender#addRecommendedItemsInternal(java.util.Collection, org.bibsonomy.recommender.item.model.RecommendationUser)
	 */
	@Override
	protected void addRecommendedItemsInternal(Collection<RecommendedPost<R>> recommendations, RecommendationUser entity) {
		String userName = entity.getUserName();
		final List<Post<? extends Resource>> requestingUserItems = this.dbAccess.getItemsForUser(maxItemsToEvaluate, userName);
		
		final Set<String> requestingUserTitles = this.calculateRequestingUserTitleSet(requestingUserItems);
		
		final List<Post<R>> userItems = new ArrayList<Post<R>>();
		
		final List<CountedTag> sortedExtractedTags = this.extractTagsFromResources(requestingUserItems);
		
		final Set<String> tagsToUse = new HashSet<String>();
		if (sortedExtractedTags.size() <= maxTagsToEvaluate) {
			for (CountedTag tag : sortedExtractedTags) {
				tagsToUse.add(tag.getName());
			}
			userItems.addAll(this.dbAccess.getTaggedItems(maxItemsToEvaluate, tagsToUse));
		} else {
			final int halfSize = sortedExtractedTags.size()/2;
			for (int i = halfSize; (i-halfSize) < maxTagsToEvaluate && i < sortedExtractedTags.size(); i++) {
				tagsToUse.add(sortedExtractedTags.get(i).getName());
			}
			if (tagsToUse.size() > 0) {
				userItems.addAll(this.dbAccess.getTaggedItems(maxItemsToEvaluate, tagsToUse));
			} else {
				return;
			}
		}
		
		final List<RecommendedPost<R>> results = this.calculateSimilarItems(userItems, requestingUserItems, requestingUserTitles);
		recommendations.addAll(results);
	}

	/**
	 * This method extracts the tags of all resources and counts their number
	 * of appearance.
	 * 
	 * @param requestingUserItems a list with all items of the requesting user
	 * @return a list with tags and their number of appearance
	 */
	protected List<CountedTag> extractTagsFromResources(List<Post<? extends Resource>> requestingUserItems) {
		final Map<String, CountedTag> countTags = new HashMap<String, CountedTag>();
		for (Post<? extends Resource> item : requestingUserItems) {
			for (Tag tag : item.getTags()) {
				if (countTags.keySet().contains(tag.getName())) {
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
	
	/**
	 * This method creates a set with titles of the requesting user items.
	 * The set is needed to avoid recommendation of known items to the user.
	 * 
	 * @param requestingUserItems the items of the requesting user
	 */
	@Override
	protected Set<String> calculateRequestingUserTitleSet(List<Post<? extends Resource>> requestingUserItems) {
		final Set<String> requestingUserTitles = new HashSet<String>();
		for (Post<? extends Resource> item : requestingUserItems) {
			requestingUserTitles.add(item.getResource().getTitle());
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
