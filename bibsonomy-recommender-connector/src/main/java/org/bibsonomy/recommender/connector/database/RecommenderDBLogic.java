package org.bibsonomy.recommender.connector.database;

import java.util.ArrayList;
import java.util.List;

import org.bibsonomy.common.exceptions.UnsupportedResourceTypeException;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.recommender.connector.database.params.ItemRecRequestParam;
import org.bibsonomy.recommender.connector.database.params.GetTagForResourceParam;
import org.bibsonomy.recommender.connector.model.PostWrapper;
import org.bibsonomy.recommender.connector.model.RecommendedPost;

import recommender.core.database.AbstractDatabaseManager;
import recommender.core.database.RecommenderDBSession;
import recommender.core.database.RecommenderDBSessionFactory;
import recommender.core.interfaces.database.RecommenderDBAccess;
import recommender.core.interfaces.model.ItemRecommendationEntity;
import recommender.core.interfaces.model.RecommendationItem;
import recommender.core.interfaces.model.TagRecommendationEntity;
import recommender.core.temp.copy.common.Pair;

public abstract class RecommenderDBLogic extends AbstractDatabaseManager implements RecommenderDBAccess{
private RecommenderDBSessionFactory mainFactory;
	
	protected RecommenderDBSession openMainSession() {
		return this.mainFactory.getDatabaseSession();
	}
	
	public void setMainFactory(RecommenderDBSessionFactory mainFactory) {
		this.mainFactory = mainFactory;
	}

	/*
	 * (non-Javadoc)
	 * @see recommender.core.interfaces.database.RecommenderDBAccess#getMostPopularTagsForUser(java.lang.String, int)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Pair<String, Integer>> getMostPopularTagsForUser(
			String username, int range) {
		final RecommenderDBSession mainSession = this.openMainSession();
		try {
			final GetTagForResourceParam param = new GetTagForResourceParam();
			param.setUserName(username);
			param.setRange(range);
			
			return (List<Pair<String, Integer>>) this.queryForList("getMostPopularTagsForUser", param, mainSession);
		} finally {
			mainSession.close();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see recommender.core.interfaces.database.RecommenderDBAccess#getMostPopularTagsForRecommendationEntity(java.lang.Class, java.lang.String, int)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Pair<String, Integer>> getMostPopularTagsForRecommendationEntity(
			TagRecommendationEntity entity, String entityId, int range) {
		final RecommenderDBSession mainSession = this.openMainSession();
		try {
			final GetTagForResourceParam param = new GetTagForResourceParam();
			param.setId(Integer.parseInt(entityId));
			param.setRange(range);
			
			if (entity instanceof PostWrapper<?>) {
				if (((PostWrapper<Resource>) entity).getPost() != null && ((PostWrapper<Resource>) entity).getPost().getResource() instanceof BibTex) {
					return (List<Pair<String, Integer>>) this.queryForList("getMostPopularTagsForBibTeX", param, mainSession);
				} else if (((PostWrapper<Resource>) entity).getPost() != null && ((PostWrapper<Resource>) entity).getPost().getResource() instanceof Bookmark) {
					return (List<Pair<String, Integer>>) this.queryForList("getMostPopularTagsForBookmark", param, mainSession);
				}

				throw new UnsupportedResourceTypeException("Unknown resource type " + (((PostWrapper<Resource>) entity).getPost().getResource()).getClass().getName());
			}
			throw new UnsupportedResourceTypeException("Expected PostWrapper but got: " + entity.getClass().getName());
		} finally {
			mainSession.close();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see recommender.core.interfaces.database.RecommenderDBAccess#getNumberOfTagsForUser(java.lang.String)
	 */
	@Override
	public Integer getNumberOfTagsForUser(String username) {
		final RecommenderDBSession mainSession = this.openMainSession();
		try {
			return this.queryForObject("getNumberOfTagsForUser", username, Integer.class, mainSession);
		} finally {
			mainSession.close();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see recommender.core.interfaces.database.RecommenderDBAccess#getNumberOfTaggingsForUser(java.lang.String)
	 */
	@Override
	public Integer getNumberOfTaggingsForUser(String username) {
		final RecommenderDBSession mainSession = this.openMainSession();
		try {
			return this.queryForObject("getNumberOfTasForUser", username, Integer.class, mainSession);
		} finally {
			mainSession.close();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see recommender.core.interfaces.database.RecommenderDBAccess#getNumberOfTagsForRecommendationEntity(java.lang.Class, java.lang.String)
	 */
	@Override
	public Integer getNumberOfTagsForRecommendationEntity(
			TagRecommendationEntity entity, String entityId) {
		final RecommenderDBSession mainSession = this.openMainSession();
		try {
			if (entity instanceof PostWrapper<?>) {
				if (((PostWrapper<Resource>) entity).getPost() != null && ((PostWrapper<Resource>) entity).getPost().getResource() instanceof BibTex) {
					return this.queryForObject("getNumberOfTagsForBibTeX", entityId, Integer.class, mainSession);
				} else if (((PostWrapper<Resource>) entity).getPost() != null && ((PostWrapper<Resource>) entity).getPost().getResource() instanceof Bookmark) {
					return this.queryForObject("getNumberOfTagsForBookmark", entityId, Integer.class, mainSession);
				}

				throw new UnsupportedResourceTypeException("Unknown resource type " + (((PostWrapper<Resource>) entity).getPost().getResource()).getClass().getName());
			}
			throw new UnsupportedResourceTypeException("Expected PostWrapper but got: " + entity.getClass().getName());
		} finally {
			mainSession.close();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see recommender.core.interfaces.database.RecommenderDBAccess#getNumberOfTasForRecommendationEntity(java.lang.Class, java.lang.String)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public Integer getNumberOfTasForRecommendationEntity(
			TagRecommendationEntity entity, String entityId) {
		final RecommenderDBSession mainSession = this.openMainSession();
		try {
			if (entity instanceof PostWrapper<?>) {
				if (((PostWrapper) entity).getPost() != null && ((PostWrapper) entity).getPost().getResource() instanceof BibTex) {
					return this.queryForObject("getNumberOfTasForBibTeX", entityId, Integer.class, mainSession);
				} else if (((PostWrapper) entity).getPost() != null && ((PostWrapper) entity).getPost().getResource() instanceof Bookmark) {
					return this.queryForObject("getNumberOfTasForBookmark", entityId, Integer.class, mainSession);
				}

				throw new UnsupportedResourceTypeException("Unknown resource type " + (((PostWrapper) entity).getPost().getResource()).getClass().getName());
			}
			throw new UnsupportedResourceTypeException("Expected PostWrapper but got: " + entity.getClass().getName());
		} finally {
			mainSession.close();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see recommender.core.interfaces.database.RecommenderDBAccess#getUserIDByName(java.lang.String)
	 */
	@Override
	public Integer getUserIDByName(String userName) {
		final RecommenderDBSession mainSession = this.openMainSession();
		try {
			return this.queryForObject("getUserIDByName", userName, Integer.class, mainSession);
		} finally {
			mainSession.close();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see recommender.core.interfaces.database.RecommenderDBAccess#getUserNameByID(int)
	 */
	@Override
	public String getUserNameByID(int userID) {
		final RecommenderDBSession mainSession = this.openMainSession();
		try {
			return this.queryForObject("getUserNameByID", userID, String.class, mainSession);
		} finally {
			mainSession.close();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see recommender.core.interfaces.database.RecommenderDBAccess#getTagNamesForRecommendationEntity(java.lang.Integer)
	 */
	@Override
	public List<String> getTagNamesForRecommendationEntity(Integer entitiyId) {
		final RecommenderDBSession mainSession = this.openMainSession();
		try {
			return this.queryForList("getTagNamesForCID", entitiyId, String.class, mainSession);
		} finally {
			mainSession.close();
		}
	}
	
	/*
	 * 
	 * 
	 * IMPLEMENTATION OF ITEM-RECOMMENDER METHODS
	 * 
	 * 
	 */

	
	/*
	 * (non-Javadoc)
	 * @see recommender.core.interfaces.database.RecommenderDBAccess#getMostActualItems(int)
	 */
	@Override
	public abstract List<RecommendationItem> getMostActualItems(final int count, final ItemRecommendationEntity entity);
	
	/*
	 * (non-Javadoc)
	 * @see recommender.core.interfaces.database.RecommenderDBAccess#getSimilarUsers(int, recommender.core.interfaces.model.ItemRecommendationEntity)
	 */
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
	 * @see recommender.core.interfaces.database.RecommenderDBAccess#getItemsForUser(int, java.lang.String)
	 */
	@Override
	public abstract List<RecommendationItem> getItemsForUser(int count, String username);
	
	/**
	 * This method retrieves resources from the bibsonomy database by contentid.
	 * This needed in the case the caching o the results fails and those have to be retrieved from the database.
	 * In this case the loading of fully wrapped resource should take place.
	 * 
	 * @param ids a list of content ids for which to retrieve content
	 * 
	 * @return a list of all 
	 */
	public abstract List<RecommendationItem> getResourcesByIds(final List<Integer> ids);
	
	/**
	 * This method should provide access to a maximum of count items belonging to the requesting user.
	 * This merges his or her bibtex and bookmark resources to get a better overview of his preferences.
	 * 
	 * @param count the maximum count of items to return
	 * @param username the username for whom to retrieve his items
	 * 
	 * @return a maximum of count items owned by the requesting user
	 */
	public List<RecommendationItem> getAllItemsOfQueryingUser(final int count, final String username) {
		
		final RecommenderDBSession mainSession = this.openMainSession();
		try {
			final ItemRecRequestParam param = new ItemRecRequestParam();
			param.setCount(count);
			param.setUserName(username);
			List<Post<Bookmark>> bookmarkResults = (List<Post<Bookmark>>) this.queryForList("getBookmarkForUser", param, mainSession);
			List<RecommendationItem> items = new ArrayList<RecommendationItem>();
			for(Post<Bookmark> bookmark : bookmarkResults) {
				RecommendationItem item =  new RecommendedPost<Bookmark>(bookmark);
				items.add(item);
			}
			List<Post<BibTex>> bibtexResults = (List<Post<BibTex>>) this.queryForList("getBibTexForUser", param, mainSession);
			for(Post<BibTex> bibtex : bibtexResults) {
				RecommendationItem item =  new RecommendedPost<BibTex>(bibtex);
				items.add(item);
			}
			
			return items;
		} finally {
			mainSession.close();
		}
		
	}
}
