package org.bibsonomy.recommender.connector.database;

import java.util.ArrayList;
import java.util.List;

import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;
import org.bibsonomy.recommender.connector.database.params.ItemRecRequestParam;
import org.bibsonomy.recommender.connector.model.RecommendationPost;
import recommender.core.database.AbstractDatabaseManager;
import recommender.core.database.RecommenderDBSession;
import recommender.core.database.RecommenderDBSessionFactory;
import recommender.core.interfaces.model.ItemRecommendationEntity;
import recommender.core.interfaces.model.RecommendationItem;

public abstract class AbstractRecommenderMainItemAccessImpl extends AbstractDatabaseManager implements ExtendedMainAccess {
	private RecommenderDBSessionFactory mainFactory;
	
	protected RecommenderDBSession openMainSession() {
		return this.mainFactory.getDatabaseSession();
	}
	
	public void setMainFactory(RecommenderDBSessionFactory mainFactory) {
		this.mainFactory = mainFactory;
	}
	
	/*
	 * (non-Javadoc)
	 * @see recommender.core.interfaces.database.RecommenderMainItemAccess#getMostActualItems(int)
	 */
	@Override
	public abstract List<RecommendationItem> getMostActualItems(final int count, final ItemRecommendationEntity entity);
	
	/*
	 * (non-Javadoc)
	 * @see recommender.core.interfaces.database.RecommenderMainItemAccess#getSimilarUsers(int, recommender.core.interfaces.model.ItemRecommendationEntity)
	 */
	@Override
	public List<String> getSimilarUsers(final int count, final ItemRecommendationEntity entity) {
		
		final RecommenderDBSession mainSession = this.openMainSession();
		try {
			final ItemRecRequestParam param = new ItemRecRequestParam();
			param.setUserName(entity.getUserName());
			param.setCount(count);
			
			List<String> usernames = this.queryForList("getSimilarUsersByFolkrank", param, String.class, mainSession);
			
			if(usernames.size() == count) {
				return usernames;
			}
			
			final int tagsToEvaluate = 5;
			param.setCount(tagsToEvaluate);
			//in case of folkrank did not give enough information select similar users by a more simple strategy
			List<String> mostImportantUserTags = this.queryForList("getMostImportantTagsOfUser", param, String.class, mainSession);
			final List<String> usernamesSimple = new ArrayList<String>();
			
			//get all users which used at least one of the requesting user's important tags
			param.setCount(count);
			List<String> tempnames = new ArrayList<String>();
			for(String tagname : mostImportantUserTags) {
				param.setTag(tagname);
				tempnames = this.queryForList("getSimilarUsersByEqualTags", param, String.class, mainSession);
				for(String username : tempnames) {
					if(!usernamesSimple.contains(username)) {
						usernamesSimple.add(username);
					}
				}
			}
			
			if(usernamesSimple.size() > usernames.size()) {
				return usernamesSimple;
			}
			
			return usernames;
			
		} finally {
			mainSession.close();
		}
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see recommender.core.interfaces.database.RecommenderMainItemAccess#getItemsForUser(int, java.lang.String)
	 */
	@Override
	public abstract List<RecommendationItem> getItemsForUser(int count, String username);
	
	/*
	 * (non-Javadoc)
	 * @see org.bibsonomy.recommender.connector.database.ExtendedMainAccess#getResourcesByIds(java.util.List)
	 */
	@Override
	public abstract List<RecommendationItem> getResourcesByIds(final List<Integer> ids);
	
	/*
	 * (non-Javadoc)
	 * @see org.bibsonomy.recommender.connector.database.ExtendedMainAccess#getAllItemsOfQueryingUser(int, java.lang.String)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<RecommendationItem> getAllItemsOfQueryingUser(final int count, final String username) {
		
		final RecommenderDBSession mainSession = this.openMainSession();
		try {
			final ItemRecRequestParam param = new ItemRecRequestParam();
			param.setCount(count);
			param.setUserName(username);
			List<Post<Bookmark>> bookmarkResults = (List<Post<Bookmark>>) this.queryForList("getBookmarkForUser", param, mainSession);
			List<RecommendationItem> items = new ArrayList<RecommendationItem>();
			for(Post<Bookmark> bookmark : bookmarkResults) {
				RecommendationItem item =  new RecommendationPost<Bookmark>(bookmark);
				items.add(item);
			}
			List<Post<BibTex>> bibtexResults = (List<Post<BibTex>>) this.queryForList("getBibTexForUser", param, mainSession);
			for(Post<BibTex> bibtex : bibtexResults) {
				RecommendationItem item =  new RecommendationPost<BibTex>(bibtex);
				items.add(item);
			}
			
			return items;
		} finally {
			mainSession.close();
		}
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.bibsonomy.recommender.connector.database.ExtendedMainAccess#getUserIdByName(java.lang.String)
	 */
	@Override
	public Long getUserIdByName(final String username) {
		final RecommenderDBSession mainSession = this.openMainSession();
		try {
			return this.queryForObject("getUserIdByName", username, Long.class, mainSession);
		} finally {
			mainSession.close();
		}
	}
	
}
