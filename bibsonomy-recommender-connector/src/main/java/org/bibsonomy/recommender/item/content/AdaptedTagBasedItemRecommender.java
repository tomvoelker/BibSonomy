package org.bibsonomy.recommender.item.content;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.Tag;
import org.bibsonomy.recommender.item.model.RecommendationUser;
import org.bibsonomy.recommender.item.model.RecommendedPost;
import org.bibsonomy.recommender.item.service.ExtendedMainAccess;

/**
 * This class extends the {@link TagBasedItemRecommender}, to allow it to use
 * bibsonomy specific model attributes.
 * 
 * @author lukas
 *
 */
public class AdaptedTagBasedItemRecommender<R extends Resource> extends TagBasedItemRecommender<R> {

	/* (non-Javadoc)
	 * @see org.bibsonomy.recommender.item.AbstractItemRecommender#addRecommendation(java.util.Collection, org.bibsonomy.recommender.item.model.RecommendationUser)
	 */
	@Override
	public void addRecommendation(Collection<RecommendedPost<R>> recommendations, RecommendationUser entity) {
		final List<Post<? extends Resource>> requestingUserItems = new ArrayList<Post<? extends Resource>>();
		
		//take bibtex and bookmark resources of requesting user to generate a more significant description of the user preferences
		final String userName = entity.getUserName();
		if (dbAccess instanceof ExtendedMainAccess) {
			requestingUserItems.addAll(((ExtendedMainAccess) this.dbAccess).getAllItemsOfQueryingUser(maxItemsToEvaluate, userName));
		} else {
			requestingUserItems.addAll(this.dbAccess.getItemsForUser(maxItemsToEvaluate, userName));
		}
		
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
	
	// FIXME: duplicate see AdptedContentBasedItemRecommender
	/*
	 * (non-Javadoc)
	 * @see recommender.impl.item.collaborative.CollaborativeItemRecommender#calculateTokens(recommender.core.interfaces.model.RecommendationItem)
	 */
	@Override
	protected List<String> calculateTokens(Post<? extends Resource> item) {
		final ArrayList<String> tokens = new ArrayList<String>();
		//add tags to tokens
		for (final Tag tag : item.getTags()) {
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
		
		//add description and abstract terms to tokens
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