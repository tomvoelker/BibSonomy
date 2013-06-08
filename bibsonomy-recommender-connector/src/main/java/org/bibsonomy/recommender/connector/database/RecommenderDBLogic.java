package org.bibsonomy.recommender.connector.database;

import java.util.List;

import org.bibsonomy.common.Pair;
import org.bibsonomy.common.exceptions.UnsupportedResourceTypeException;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.recommender.connector.database.params.PostParam;
import org.bibsonomy.recommender.connector.database.params.TasParam;

import recommender.core.database.AbstractDatabaseManager;
import recommender.core.database.RecommenderDBSession;
import recommender.core.database.RecommenderDBSessionFactory;
import recommender.core.database.params.UserTag;
import recommender.core.interfaces.database.RecommenderDBAccess;
import recommender.core.interfaces.model.RecommendationResource;
import recommender.core.model.TagRecommendationEntity;

/**
 * 
 * This class implements the database access on the bibsonomy database
 *  for the recommendation library
 * 
 * @author Lukas
 *
 */

public class RecommenderDBLogic extends AbstractDatabaseManager implements RecommenderDBAccess{

	private RecommenderDBSessionFactory mainFactory;
	
	private RecommenderDBSession openMainSession() {
		return this.mainFactory.getDatabaseSession();
	}
	
	public void setMainFactory(RecommenderDBSessionFactory mainFactory) {
		this.mainFactory = mainFactory;
	}
	
	/*
	 * (non-Javadoc)
	 * @see recommender.core.interfaces.database.RecommenderDBAccess#getRandomItems()
	 */
	@Override
	public List<RecommendationResource> getRandomItems() {
//		final DBSession mainSession = this.openMainSession();
//		
//		final List<BibTexResultParam> results =  (List<BibTexResultParam>) mainSession.queryForList("lookupNewestBibTex", null);
//		for(BibTexResultParam param : results) { 
//			param.setTags(getTagsForPost(Integer.parseInt(param.getContentId())));
//		}
//		
//		mainSession.close();
//		
//		return results;
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see recommender.core.interfaces.database.RecommenderDBAccess#getEntityIDForQuery(java.lang.Long)
	 */
	@Override
	public Integer getEntityIDForQuery(Long queryID) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see recommender.core.interfaces.database.RecommenderDBAccess#getNewestEntries(java.lang.Integer, java.lang.Integer)
	 */
	@Override
	public List<UserTag> getNewestEntries(Integer offset, Integer range) {
		final RecommenderDBSession mainSession = this.openMainSession();
		try {
			final TasParam param = new TasParam();
			param.setOffset(offset);
			param.setRange(range);
			return this.queryForList("getNewestEntries", param, UserTag.class, mainSession);
		} finally {
			mainSession.close();
		}
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
			final PostParam param = new PostParam();
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
	public <T extends TagRecommendationEntity> List<Pair<String, Integer>> getMostPopularTagsForRecommendationEntity(
			Class<T> resourceType, String entityId, int range) {
		final RecommenderDBSession mainSession = this.openMainSession();
		try {
			final PostParam param = new PostParam();
			param.setContentID(Integer.parseInt(entityId));
			param.setRange(range);
			
			if (BibTex.class.equals(resourceType)) {
				return (List<Pair<String, Integer>>) this.queryForList("getMostPopularTagsForBibTeX", param, mainSession);
			} else if (Bookmark.class.equals(resourceType)) {
				return (List<Pair<String, Integer>>) this.queryForList("getMostPopularTagsForBookmark", param, mainSession);
			}
			throw new UnsupportedResourceTypeException("Unknown resource type " + resourceType);
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
	public <T extends TagRecommendationEntity> Integer getNumberOfTagsForRecommendationEntity(
			Class<T> resourceType, String entityId) {
		final RecommenderDBSession mainSession = this.openMainSession();
		try {
			if (BibTex.class.equals(resourceType)) {
				return this.queryForObject("getNumberOfTagsForBibTeX", entityId, Integer.class, mainSession);
			} else if (Bookmark.class.equals(resourceType)) {
				return this.queryForObject("getNumberOfTagsForBookmark", entityId, Integer.class, mainSession);
			}
			throw new UnsupportedResourceTypeException("Unknown resource type " + resourceType);
		} finally {
			mainSession.close();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see recommender.core.interfaces.database.RecommenderDBAccess#getNumberOfTasForRecommendationEntity(java.lang.Class, java.lang.String)
	 */
	@Override
	public <T extends TagRecommendationEntity> Integer getNumberOfTasForRecommendationEntity(
			Class<T> resourceType, String entityId) {
		final RecommenderDBSession mainSession = this.openMainSession();
		try {
			if (BibTex.class.equals(resourceType)) {
				return this.queryForObject("getNumberOfTasForBibTeX", entityId, Integer.class, mainSession);
			} else if (Bookmark.class.equals(resourceType)) {
				return this.queryForObject("getNumberOfTasForBookmark", entityId, Integer.class, mainSession);
			}
			throw new UnsupportedResourceTypeException("Unknown resource type " + resourceType);
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

}
