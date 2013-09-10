package org.bibsonomy.recommender.connector.database;

import java.util.ArrayList;
import java.util.List;

import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.recommender.connector.database.params.ItemRecRequestParam;
import org.bibsonomy.recommender.connector.model.RecommendedPost;

import recommender.core.database.RecommenderDBSession;
import recommender.core.interfaces.model.ItemRecommendationEntity;
import recommender.core.interfaces.model.RecommendationItem;

/**
 * 
 * This class implements the database access on the bibsonomy database
 *  for the recommendation library
 * 
 * @author Lukas
 *
 */

public class RecommenderBibTexDBLogic extends RecommenderMainItemAccessImpl{
			
	/*
	 * (non-Javadoc)
	 * @see recommender.core.interfaces.database.RecommenderDBAccess#getMostActualItems(int)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<RecommendationItem> getMostActualItems(final int count, final ItemRecommendationEntity entity) {
		
		final RecommenderDBSession mainSession = this.openMainSession();
		try {
			final ItemRecRequestParam param = new ItemRecRequestParam();
			param.setCount(count);
			param.setUserName(entity.getUserName());
			List<Post<BibTex>> results = (List<Post<BibTex>>) this.queryForList("getMostActualBibTex", param, mainSession);
			List<RecommendationItem> items = new ArrayList<RecommendationItem>(results.size());
			for(Post<BibTex> bibtex : results) {
				RecommendationItem item =  new RecommendedPost<BibTex>(bibtex);
				items.add(item);
			}
			
			return items;
		} finally {
			mainSession.close();
		}
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see recommender.core.interfaces.database.RecommenderDBAccess#getItemsForUser(int, java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<RecommendationItem> getItemsForUser(final int count, final String username) {
		final RecommenderDBSession mainSession = this.openMainSession();
		try {
			final ItemRecRequestParam param = new ItemRecRequestParam();
			param.setCount(count);
			param.setUserName(username);
			List<Post<BibTex>> results = (List<Post<BibTex>>) this.queryForList("getBibTexForUser", param, mainSession);
			List<RecommendationItem> items = new ArrayList<RecommendationItem>(results.size());
			for(Post<BibTex> bibtex : results) {
				RecommendationItem item =  new RecommendedPost<BibTex>(bibtex);
				items.add(item);
			}
			
			return items;
		} finally {
			mainSession.close();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see recommender.core.interfaces.database.RecommenderDBAccess#getItemsForUsers(int, java.util.List)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<RecommendationItem> getItemsForUsers(int count,
			List<String> usernames) {
		final RecommenderDBSession mainSession = this.openMainSession();
		try {
			final ItemRecRequestParam param = new ItemRecRequestParam();
			param.setCount(count);
			List<RecommendationItem> items = new ArrayList<RecommendationItem>();
			for(String username : usernames) {
				param.setUserName(username);
				List<Post<BibTex>> results = (List<Post<BibTex>>) this.queryForList("getBibTexForUser", param, mainSession);
				for(Post<BibTex> bibtex : results) {
					RecommendationItem item =  new RecommendedPost<BibTex>(bibtex);
					items.add(item);
				}
			}
			return items;
		} finally {
			mainSession.close();
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.bibsonomy.recommender.connector.database.RecommenderDBLogic#getResourcesByIds(java.util.List)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<RecommendationItem> getResourcesByIds(final List<Integer> ids) {
		final RecommenderDBSession mainSession = this.openMainSession();
		try {
			List<RecommendationItem> items = new ArrayList<RecommendationItem>();
			for(Integer id : ids) {
				Post<BibTex> bibtex = this.queryForObject("getBibTexById", id, Post.class, mainSession);
				items.add(new RecommendedPost<BibTex>(bibtex));
			}
			
			return items;
		} finally {
			mainSession.close();
		}
	}
}
