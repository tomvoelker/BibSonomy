/**
 * BibSonomy Recommendation - Tag and resource recommender.
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

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.Tag;
import org.bibsonomy.recommender.item.model.RecommendationUser;
import org.bibsonomy.recommender.item.model.RecommendedPost;
import org.bibsonomy.recommender.item.service.ExtendedMainAccess;

/**
 * This class is an extension to the default CFFiltering algorithm in the recommender library.
 * It extends the similarity measure in a way to use bibsonomy's specific model for a more exact approach.
 * 
 * @author lukas
 * @param <R> 
 *
 */
public class AdaptedContentBasedItemRecommender<R extends Resource> extends ContentBasedItemRecommender<R> {
	
	/*
	 * (non-Javadoc)
	 * @see recommender.impl.item.collaborative.CollaborativeItemRecommender#addRecommendedItemsInternal(java.util.Collection, recommender.core.interfaces.model.ItemRecommendationEntity)
	 */
	@Override
	protected void addRecommendedItemsInternal(Collection<RecommendedPost<R>> recommendations, RecommendationUser entity) {
		final List<Post<? extends Resource>> requestingUserItems = new ArrayList<Post<? extends Resource>>();
		
		// take publication and bookmark resources of requesting user to generate a more significant description of the user preferences
		if (dbAccess instanceof ExtendedMainAccess) {
			requestingUserItems.addAll(((ExtendedMainAccess) this.dbAccess).getAllItemsOfQueryingUser(maxItemsToEvaluate, entity.getUserName()));
		} else {
			requestingUserItems.addAll(this.dbAccess.getItemsForUser(maxItemsToEvaluate, entity.getUserName()));
		}
		
		final Set<String> requestingUserTitles = calculateRequestingUserTitleSet(requestingUserItems);
		
		List<Post<R>> userItems = new ArrayList<Post<R>>();
		
		userItems.addAll(this.dbAccess.getItemsForContentBasedFiltering(maxItemsToEvaluate, entity));
	
		final List<RecommendedPost<R>> results = this.calculateSimilarItems(userItems, requestingUserItems, requestingUserTitles);
		
		// in case of ExtendedMainAccess was injected the complete post data has to be retrieved
		if (dbAccess instanceof ExtendedMainAccess) {
			final Map<Integer, RecommendedPost<R>> ids = new HashMap<Integer, RecommendedPost<R>>();
			for (RecommendedPost<R> item : results) {
				ids.put(item.getPost().getContentId(), item);
			}
			
			
			
			final List<Post<R>> completedItems = ((ExtendedMainAccess) this.dbAccess).getResourcesByIds(new ArrayList<Integer>(ids.keySet()));
			for (final Post<R> item : completedItems) {
				ids.get(item.getContentId()).setPost(item);
			}
		}
		
		recommendations.addAll(results);
	}
	
	/*
	 * (non-Javadoc)
	 * @see recommender.impl.item.collaborative.CollaborativeItemRecommender#calculateTokens(recommender.core.interfaces.model.RecommendationItem)
	 */
	@Override
	protected List<String> calculateTokens(Post<? extends Resource> item) {
		final List<String> tokens = new LinkedList<String>();
		
		// add tags to tokens
		for (Tag tag : item.getTags()) {
			tokens.add(tag.getName().toLowerCase());
		}
		final Resource resource = item.getResource();
		
		// add title terms to tokens
		final String title = resource.getTitle();
		if (present(title)) {
			for (String titleToken : title.split(TOKEN_DELIMITER)) {
				tokens.add(titleToken.toLowerCase());
			}
		}
		
		// add description and abstract terms to tokens
		final String description = item.getDescription();
		if (present(description)) {
			for (String token : description.split(TOKEN_DELIMITER)) {
				tokens.add(token.toLowerCase());
			}
		}
		
		if (resource instanceof BibTex) {
			final BibTex publication = (BibTex) resource;
			
			final String publAbstract = publication.getAbstract();
			if (publAbstract != null) {
				for (String token : publAbstract.split(TOKEN_DELIMITER)) {
					tokens.add(token.toLowerCase());
				}
			}
		}
		return tokens;
	}
}
