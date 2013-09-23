package org.bibsonomy.recommender.connector.collaborative;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.bibsonomy.model.BibTex;
import org.bibsonomy.recommender.connector.database.ExtendedMainAccess;
import org.bibsonomy.recommender.connector.model.RecommendationPost;

import recommender.core.interfaces.model.ItemRecommendationEntity;
import recommender.core.interfaces.model.RecommendationItem;
import recommender.core.interfaces.model.RecommendationTag;
import recommender.impl.model.RecommendedItem;
import recommender.impl.item.collaborative.CollaborativeItemRecommender;

/**
 * This class is an extension to the default CFFiltering algorithm in the recommender library.
 * It extends the similarity measure in a way to use bibsonomy's specific model for a more exact approach.
 * 
 * @author lukas
 *
 */
public class AdaptedCollaborativeItemRecommender extends CollaborativeItemRecommender{


	/*
	 * (non-Javadoc)
	 * @see recommender.impl.item.collaborative.CollaborativeItemRecommender#addRecommendedItemsInternal(java.util.Collection, recommender.core.interfaces.model.ItemRecommendationEntity)
	 */
	@Override
	protected void addRecommendedItemsInternal(
			Collection<RecommendedItem> recommendations,
			ItemRecommendationEntity entity) {

		final List<String> similarUsers = this.dbAccess.getSimilarUsers(maxUsersToEvaluate, entity);
		final List<RecommendationItem> requestingUserItems = new ArrayList<RecommendationItem>();
		
		//take bibtex and bookmark resources of requesting user to generate a more significant description of the user preferences
		if(dbAccess instanceof ExtendedMainAccess) {
			requestingUserItems.addAll(((ExtendedMainAccess) this.dbAccess).getAllItemsOfQueryingUser(maxItemsToEvaluate, entity.getUserName()));
		} else {
			requestingUserItems.addAll(this.dbAccess.getItemsForUser(maxItemsToEvaluate, entity.getUserName())); 
		}
		
		final Set<String> requestingUserTitles = calculateRequestingUserTitleSet(requestingUserItems);
		
		List<RecommendationItem> userItems = new ArrayList<RecommendationItem>();
		
		userItems.addAll(this.dbAccess.getItemsForUsers(maxItemsToEvaluate, similarUsers));
		
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
