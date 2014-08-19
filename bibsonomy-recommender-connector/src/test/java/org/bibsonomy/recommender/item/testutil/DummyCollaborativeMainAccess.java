package org.bibsonomy.recommender.item.testutil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.recommender.item.content.AdaptedContentBasedItemRecommenderTest;
import org.bibsonomy.recommender.item.model.RecommendationUser;
import org.bibsonomy.recommender.item.service.ExtendedMainAccess;

/**
 * Extended dummy database implementation to return non random values.
 * @author lukas
 */
public abstract class DummyCollaborativeMainAccess<R extends Resource> extends DummyMainItemAccess<R> implements ExtendedMainAccess<R> {
	
	/*
	 * (non-Javadoc)
	 * @see org.bibsonomy.recommender.connector.testutil.DummyMainItemAccess#getAllItemsOfQueryingUser(int, java.lang.String)
	 */
	@Override
	public List<Post<? extends Resource>> getAllItemsOfQueryingUser(int count, String username) {
		final List<Post<? extends Resource>> items = new ArrayList<Post<? extends Resource>>();
		final Post<BibTex> bibtexPost = AdaptedContentBasedItemRecommenderTest.createBibTexPost("request bibtex", "recommender systems", "empty descr", AdaptedContentBasedItemRecommenderTest.REQUESTING_USER_NAME);
		final Post<Bookmark> bookmarkPost = AdaptedContentBasedItemRecommenderTest.createBookmarkPost("request bookmark", "empty descr", AdaptedContentBasedItemRecommenderTest.REQUESTING_USER_NAME);
		items.add(bibtexPost);
		items.add(bookmarkPost);
		return items;
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.recommender.item.testutil.DummyMainItemAccess#getItemsForUser(int, java.lang.String)
	 */
	@Override
	public List<Post<? extends Resource>> getItemsForUser(int count, String username) {
		return AdaptedContentBasedItemRecommenderTest.createItemsForCfUsers();
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.recommender.item.testutil.DummyMainItemAccess#getResourcesByIds(java.util.List)
	 */
	@Override
	public List<Post<R>> getResourcesByIds(List<Integer> ids) {
		return new LinkedList<Post<R>>();
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.recommender.item.service.ExtendedMainAccess#getSimilarUsers(int, org.bibsonomy.recommender.item.model.RecommendationUser)
	 */
	@Override
	public List<String> getSimilarUsers(int count, RecommendationUser entity) {
		return Collections.emptyList();
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.recommender.item.service.ExtendedMainAccess#getItemByTitle(java.lang.String)
	 */
	@Override
	public Post<? extends Resource> getItemByTitle(String title) {
		// TODO Auto-generated method stub
		return null;
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.recommender.item.service.ExtendedMainAccess#getItemByUserIdWithHash(java.lang.String, java.lang.String)
	 */
	@Override
	public Post<? extends Resource> getItemByUserIdWithHash(String hash,
			String userId) {
		// TODO Auto-generated method stub
		return null;
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.recommender.item.service.ExtendedMainAccess#getUserIdByName(java.lang.String)
	 */
	@Override
	public Long getUserIdByName(String username) {

		return Long.valueOf(42);
	}
}