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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.recommender.connector.database.ExtendedMainAccess;
import org.bibsonomy.recommender.connector.model.RecommendationPost;

import recommender.core.interfaces.model.ItemRecommendationEntity;
import recommender.core.interfaces.model.RecommendationItem;
import recommender.core.interfaces.model.RecommendationTag;
import recommender.impl.model.RecommendedItem;
import recommender.impl.item.content.ContentBasedItemRecommender;

/**
 * This class is an extension to the default CFFiltering algorithm in the recommender library.
 * It extends the similarity measure in a way to use bibsonomy's specific model for a more exact approach.
 * 
 * @author lukas
 *
 */
public class AdaptedContentBasedItemRecommender extends ContentBasedItemRecommender {

	/*
	 * (non-Javadoc)
	 * @see recommender.impl.item.collaborative.CollaborativeItemRecommender#addRecommendedItemsInternal(java.util.Collection, recommender.core.interfaces.model.ItemRecommendationEntity)
	 */
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
		
		final Set<String> requestingUserTitles = calculateRequestingUserTitleSet(requestingUserItems);
		
		List<RecommendationItem> userItems = new ArrayList<RecommendationItem>();
		
		userItems.addAll(this.dbAccess.getItemsForContentBasedFiltering(maxItemsToEvaluate, entity));
	
		final List<RecommendedItem> results = this.calculateSimilarItems(userItems, requestingUserItems, requestingUserTitles);
		
		// in case of ExtendedMainAccess was injected the complete post data has to be retrieved
		if(dbAccess instanceof ExtendedMainAccess) {
			final Map<Integer, RecommendedItem> ids = new HashMap<Integer, RecommendedItem>();
			for(RecommendedItem item : results) {
				if( item.getItem() != null && item.getItem() instanceof RecommendationPost ) {
					final Post<? extends Resource> post = ((RecommendationPost) item.getItem()).getPost();
					if(post != null) {
						ids.put(post.getContentId(), item);
					}
				}
			}
			
			final List<RecommendationItem> completedItems = ((ExtendedMainAccess) this.dbAccess).getResourcesByIds(new ArrayList<Integer>(ids.keySet()));
			for(RecommendationItem item : completedItems) {
				if(item instanceof RecommendationPost) {
					final Post<? extends Resource> completedPost = ((RecommendationPost) item).getPost();
					if(completedPost != null) {
						ids.get(completedPost.getContentId()).setItem(item);
					}
				}
			}
		}
		
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
