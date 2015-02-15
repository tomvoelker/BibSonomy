/**
 * BibSonomy-Recommendation-Connector - Connector for the recommender framework for tag and resource recommendation
 *
 * Copyright (C) 2006 - 2014 Knowledge & Data Engineering Group,
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
package org.bibsonomy.recommender.connector.content;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bibsonomy.model.BibTex;
import org.bibsonomy.recommender.connector.database.ExtendedMainAccess;
import org.bibsonomy.recommender.connector.model.RecommendationPost;

import recommender.core.interfaces.model.ItemRecommendationEntity;
import recommender.core.interfaces.model.RecommendationItem;
import recommender.core.interfaces.model.RecommendationTag;
import recommender.impl.item.content.TagBasedItemRecommender;
import recommender.impl.model.RecommendedItem;

/**
 * This class extends the {@link TagBasedItemRecommender}, to allow it to use
 * bibsonomy specific model attributes.
 * 
 * @author lukas
 *
 */
public class AdaptedTagBasedItemRecommender extends TagBasedItemRecommender {

	@Override
	protected void addRecommendedItemsInternal(
			Collection<RecommendedItem> recommendations,
			ItemRecommendationEntity entity) {
		final List<RecommendationItem> requestingUserItems = new ArrayList<RecommendationItem>();
		
		//take bibtex and bookmark resources of requesting user to generate a more significant description of the user preferences
		if(dbAccess instanceof ExtendedMainAccess) {
			requestingUserItems.addAll(((ExtendedMainAccess) this.dbAccess).getAllItemsOfQueryingUser(maxItemsToEvaluate, entity.getUserName()));
		} else {
			requestingUserItems.addAll(this.dbAccess.getItemsForUser(maxItemsToEvaluate, entity.getUserName())); 
		}
		
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
	
	/*
	 * (non-Javadoc)
	 * @see recommender.impl.item.collaborative.CollaborativeItemRecommender#calculateTokens(recommender.core.interfaces.model.RecommendationItem)
	 */
	@Override
	protected List<String> calculateTokens(RecommendationItem item) {

		final ArrayList<String> tokens = new ArrayList<String>();
		//add tags to tokens
		for(RecommendationTag tag : item.getTags()) {
			tokens.add(tag.getName().toLowerCase());
		}
		//add title terms to tokens
		for(String titleToken : item.getTitle().split(TOKEN_DELIMITER)) {
			tokens.add(titleToken.toLowerCase());
		}
		//add description and abstract terms to tokens
		if(item instanceof RecommendationPost && ((RecommendationPost) item).getPost() != null) {
			if(((RecommendationPost) item).getPost().getDescription() != null) {
				for(String token : ((RecommendationPost) item).getPost().getDescription().split(TOKEN_DELIMITER)) {
					tokens.add(token.toLowerCase());
				}
			}
			if(((RecommendationPost) item).getPost().getResource() instanceof BibTex) {
				BibTex b = (BibTex) ((RecommendationPost) item).getPost().getResource();
				if(b.getAbstract() != null) {
					for(String token : b.getAbstract().split(TOKEN_DELIMITER)) {
						tokens.add(token.toLowerCase());
					}
				}
			}
		}
		return tokens;
	}
	
}
