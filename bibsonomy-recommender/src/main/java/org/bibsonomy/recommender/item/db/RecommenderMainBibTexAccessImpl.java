/**
 * BibSonomy-Recommendation-Connector - Connector for the recommender framework for tag and resource recommendation
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
package org.bibsonomy.recommender.item.db;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bibsonomy.common.enums.GroupID;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.enums.HashID;
import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.common.enums.ConstantID;
import org.bibsonomy.database.common.params.beans.TagIndex;
import org.bibsonomy.database.params.BibTexParam;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.recommender.item.model.RecommendationUser;

/**
 * 
 * This class implements the database access on the bibsonomy database
 * for the recommendation library to recommend bibtex posts.
 * 
 * @author Lukas
 *
 */
public class RecommenderMainBibTexAccessImpl extends AbstractRecommenderMainItemAccessImpl<BibTex> {
	
	/*
	 * (non-Javadoc)
	 * @see recommender.core.interfaces.database.RecommenderDBAccess#getMostActualItems(int)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Post<BibTex>> getMostActualItems(final int count, final RecommendationUser entity) {
		
		final DBSession mainSession = this.openMainSession();
		try {
			BibTexParam bibtexParam = new BibTexParam();
			bibtexParam.setGrouping(GroupingEntity.ALL);
			bibtexParam.setGroupId(GroupID.PUBLIC.getId());
			bibtexParam.setUserName(entity.getUserName());
			bibtexParam.setOffset(0);
			bibtexParam.setLimit(2*count);
			bibtexParam.setSimHash(HashID.INTRA_HASH);
			
			return (List<Post<BibTex>>) this.queryForList("getBibTexForHomepage", bibtexParam, mainSession);
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
	public List<Post<? extends Resource>> getItemsForUser(final int count, final String username) {
		final DBSession mainSession = this.openMainSession();
		try {
			final BibTexParam bibtexParam = new BibTexParam();
			bibtexParam.setRequestedUserName(username);
			bibtexParam.setOffset(0);
			bibtexParam.setLimit(count);
			bibtexParam.setGroupId(GroupID.PUBLIC.getId());
			
			return (List<Post<? extends Resource>>) this.queryForList("getReducedUserBibTex", bibtexParam, mainSession);
		} finally {
			mainSession.close();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see recommender.core.interfaces.database.RecommenderDBAccess#getItemsForUsers(int, java.util.List)
	 */
	@SuppressWarnings("unchecked")
	private List<Post<BibTex>> getItemsForUsers(final int count, final List<String> usernames) {
		final DBSession mainSession = this.openMainSession();
		try {
			final BibTexParam bibtexParam = new BibTexParam();
			bibtexParam.setGroupId(GroupID.PUBLIC.getId());
			bibtexParam.setOffset(0);
			bibtexParam.setLimit(count);
			
			final List<Post<BibTex>> items = new ArrayList<Post<BibTex>>();
			
			for (String username : usernames) {
				bibtexParam.setRequestedUserName(username);
				// only get reduced data, because it's enough for calculation
				List<Post<BibTex>> results = (List<Post<BibTex>>) this.queryForList("getReducedUserBibTex", bibtexParam, mainSession);
				items.addAll(results);
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
	public List<Post<BibTex>> getResourcesByIds(final List<Integer> ids) {
		final DBSession mainSession = this.openMainSession();
		try {
			final List<Post<BibTex>> items = new ArrayList<Post<BibTex>>();
			final BibTexParam param = new BibTexParam();
			param.setSimHash(HashID.INTRA_HASH);
			for(Integer id : ids) {
				param.setRequestedContentId(id);
				Post<BibTex> bibtex = this.queryForObject("getBibTexById", param, Post.class, mainSession);
				items.add(bibtex);
			}
			return items;
		} finally {
			mainSession.close();
		}
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.recommender.item.service.RecommenderMainItemAccess#getItemsForContentBasedFiltering(int, org.bibsonomy.recommender.item.model.RecommendationUser)
	 */
	@Override
	public List<Post<BibTex>> getItemsForContentBasedFiltering(int maxItemsToEvaluate, RecommendationUser entity) {
		final List<String> similarUsers = this.getSimilarUsers(USERS_TO_EVALUATE, entity);
		if (present(similarUsers)) {
			return this.getItemsForUsers(maxItemsToEvaluate/similarUsers.size(), similarUsers);
		}
		return new ArrayList<Post<BibTex>>();
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.bibsonomy.recommender.connector.database.AbstractRecommenderMainItemAccessImpl#getTaggedItems(int, java.util.Set)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Post<BibTex>> getTaggedItems(final int maxItemsToEvaluate, final Set<String> tags) {
		final DBSession mainSession = this.openMainSession();
		final List<Post<BibTex>> items = new ArrayList<Post<BibTex>>();
		try {
			final BibTexParam param = new BibTexParam();
			param.setLimit(maxItemsToEvaluate / tags.size());
			param.setOffset(0);
			param.setContentType(ConstantID.BIBTEX_CONTENT_TYPE);
			param.setGroupId(GroupID.PUBLIC.getId());
			param.setCaseSensitiveTagNames(false);
			final List<TagIndex> tagIndeces = new ArrayList<TagIndex>();
			TagIndex index;
			for (String tag : tags) {
				tagIndeces.clear();
				index = new TagIndex(tag, 1);
				tagIndeces.add(index);
				param.setTagIndex(tagIndeces);
				final List<Post<BibTex>> bibtexs = (List<Post<BibTex>>) this.queryForList("getBibTexByTagNames", param, mainSession);
				items.addAll(bibtexs);
			}
			return items;
		} finally {
			mainSession.close();
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.bibsonomy.recommender.connector.database.AbstractRecommenderMainItemAccessImpl#getItemByUserWithHash(java.lang.String, java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Post<BibTex> getItemByUserIdWithHash(final String hash, final String userId) {
		final DBSession mainSession = this.openMainSession();
		try {
			final BibTexParam param = new BibTexParam();
			param.setHash(hash);
			param.setRequestedUserName(userId);
			param.setSimHash(HashID.INTRA_HASH);
			param.setGrouping(GroupingEntity.USER);
			param.setOffset(0);
			param.setLimit(1);
			
			return this.queryForObject("getBibTexByHashForUserId", param, Post.class, mainSession);
		} finally {
			mainSession.close();
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.bibsonomy.recommender.connector.database.AbstractRecommenderMainItemAccessImpl#getItemByTitle(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Post<BibTex> getItemByTitle(final String title) {
		final DBSession mainSession = this.openMainSession();
		try {
			final BibTexParam param = new BibTexParam();
			param.setTitle(title);
			param.setSimHash(HashID.INTRA_HASH);
			param.setGroupId(GroupID.PUBLIC.getId());
			param.setOffset(0);
			param.setLimit(1);
			
			return this.queryForObject("getBibTexByTitle", param, Post.class, mainSession);
		} finally {
			mainSession.close();
		}
	}
}
