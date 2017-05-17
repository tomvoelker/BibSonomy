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
package org.bibsonomy.recommender.tag.db;

import java.util.List;

import org.bibsonomy.common.exceptions.UnsupportedResourceTypeException;
import org.bibsonomy.database.common.AbstractDatabaseManager;
import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.common.DBSessionFactory;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.recommender.tag.db.params.GetTagForResourceParam;
import org.bibsonomy.recommender.tag.service.RecommenderMainTagAccess;

import recommender.core.model.Pair;

/**
 * 
 * This class implements the database access on the bibsonomy database
 * for the recommendation library to recommend tags.
 * 
 */
public class RecommenderMainTagAccessImpl extends AbstractDatabaseManager implements RecommenderMainTagAccess {

	private static int saveConvert(final Integer count) {
		return count == null ? 0 : count.intValue();
	}
	
	private DBSessionFactory mainFactory;
	
	protected DBSession openMainSession() {
		return this.mainFactory.getDatabaseSession();
	}
	
	public void setMainFactory(DBSessionFactory mainFactory) {
		this.mainFactory = mainFactory;
	}

	/*
	 * (non-Javadoc)
	 * @see recommender.core.interfaces.database.RecommenderDBAccess#getMostPopularTagsForUser(java.lang.String, int)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Pair<String, Integer>> getMostPopularTagsForUser(String username, int range) {
		final DBSession mainSession = this.openMainSession();
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
	public List<Pair<String, Integer>> getMostPopularTagsForRecommendationEntity(Post<? extends Resource> entity, String hash, int range) {
		final DBSession mainSession = this.openMainSession();
		try {
			final GetTagForResourceParam param = new GetTagForResourceParam();
			param.setHash(hash);
			param.setRange(range);
			final Resource resource = entity.getResource();
			
			if (resource instanceof BibTex) {
				return (List<Pair<String, Integer>>) this.queryForList("getMostPopularTagsForBibTeX", param, mainSession);
			} else if (resource instanceof Bookmark) {
				return (List<Pair<String, Integer>>) this.queryForList("getMostPopularTagsForBookmark", param, mainSession);
			}
			
			throw new UnsupportedResourceTypeException("Unknown resource type " + resource.getClass().getName());
		} finally {
			mainSession.close();
		}
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.recommender.tag.service.RecommenderMainTagAccess#getTagsOfPreviousPostsForUser(java.lang.String, int)
	 */
	@Override
	public List<Pair<String, Integer>> getTagsOfPreviousPostsForUser(final String username, int numberOfPreviousPosts, int numberOfTags) {
		final DBSession mainSession = this.openMainSession();
		try {
			final GetTagForResourceParam param = new GetTagForResourceParam();
			param.setUserName(username);
			param.setRange(numberOfPreviousPosts);
			param.setNumberOfTags(numberOfTags);
			this.modifyParamForPreviousPostsForUser(param);
			return (List<Pair<String, Integer>>) this.queryForList("getTagsOfPreviousPostsForUser", param, mainSession);
		} finally {
			mainSession.close();
		}
	}
	
	/**
	 * used for evaluation implementation
	 * @param param
	 */
	protected void modifyParamForPreviousPostsForUser(GetTagForResourceParam param) {
		// noop
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.recommender.tag.service.RecommenderMainTagAccess#getNumberOfTagsOfPreviousPostsForUser(java.lang.Class, java.lang.String, int)
	 */
	@Override
	public int getNumberOfTagsOfPreviousPostsForUser(String username, int numberOfPreviousPosts) {
		final DBSession mainSession = this.openMainSession();
		try {
			final GetTagForResourceParam param = new GetTagForResourceParam();
			param.setUserName(username);
			param.setRange(numberOfPreviousPosts);
			
			return saveConvert(this.queryForObject("getTagCountOfPreviousPostsForUser", param, Integer.class, mainSession));
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
		final DBSession mainSession = this.openMainSession();
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
	public int getNumberOfTaggingsForUser(String username) {
		final DBSession mainSession = this.openMainSession();
		try {
			return saveConvert(this.queryForObject("getNumberOfTasForUser", username, Integer.class, mainSession));
		} finally {
			mainSession.close();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see recommender.core.interfaces.database.RecommenderDBAccess#getNumberOfTasForRecommendationEntity(java.lang.Class, java.lang.String)
	 */
	@Override
	public int getNumberOfTagAssignmentsForRecommendationEntity(Post<? extends Resource> entity, final String hash) {
		final DBSession mainSession = this.openMainSession();
		final Resource resource = entity.getResource();
		try {
			if (resource instanceof BibTex) {
				return saveConvert(this.queryForObject("getNumberOfTasForBibTeX", hash, Integer.class, mainSession));
			} else if (resource instanceof Bookmark) {
				return saveConvert(this.queryForObject("getNumberOfTasForBookmark", hash, Integer.class, mainSession));
			}

			throw new UnsupportedResourceTypeException("Unknown resource type " + resource.getClass().getName());
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
		final DBSession mainSession = this.openMainSession();
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
		final DBSession mainSession = this.openMainSession();
		try {
			return this.queryForObject("getUserNameByID", userID, String.class, mainSession);
		} finally {
			mainSession.close();
		}
	}
}
