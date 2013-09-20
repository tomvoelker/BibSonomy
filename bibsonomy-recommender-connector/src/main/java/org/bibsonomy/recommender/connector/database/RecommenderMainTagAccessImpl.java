package org.bibsonomy.recommender.connector.database;

import java.util.List;

import org.bibsonomy.common.exceptions.UnsupportedResourceTypeException;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Resource;
import org.bibsonomy.recommender.connector.database.params.GetTagForResourceParam;
import org.bibsonomy.recommender.connector.model.PostWrapper;

import recommender.core.database.AbstractDatabaseManager;
import recommender.core.database.RecommenderDBSession;
import recommender.core.database.RecommenderDBSessionFactory;
import recommender.core.interfaces.database.RecommenderMainTagAccess;
import recommender.core.interfaces.model.TagRecommendationEntity;
import recommender.core.model.Pair;

/**
 * 
 * This class implements the database access on the bibsonomy database
 *  for the recommendation library to recommend tags.
 * 
 * @author Lukas
 *
 */
public class RecommenderMainTagAccessImpl extends AbstractDatabaseManager implements RecommenderMainTagAccess {
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
	@SuppressWarnings("unchecked")
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
	public Integer getNumberOfTagAssignmentsForRecommendationEntity(
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
	

}
