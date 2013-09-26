package org.bibsonomy.recommender.connector.database;

import java.util.ArrayList;
import java.util.List;

import org.bibsonomy.common.enums.GroupID;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.enums.HashID;
import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.params.BibTexParam;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.recommender.connector.model.RecommendationPost;

import recommender.core.interfaces.model.ItemRecommendationEntity;
import recommender.core.interfaces.model.RecommendationItem;

/**
 * 
 * This class implements the database access on the bibsonomy database
 * for the recommendation library to recommend bibtex posts.
 * 
 * @author Lukas
 *
 */
public class RecommenderMainBibTexAccessImpl extends AbstractRecommenderMainItemAccessImpl{
	
	/*
	 * (non-Javadoc)
	 * @see recommender.core.interfaces.database.RecommenderDBAccess#getMostActualItems(int)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<RecommendationItem> getMostActualItems(final int count, final ItemRecommendationEntity entity) {
		
		final DBSession mainSession = this.openMainSession();
		try {
			BibTexParam bibtexParam = new BibTexParam();
			bibtexParam.setGrouping(GroupingEntity.ALL);
			bibtexParam.setGroupId(GroupID.PUBLIC.getId());
			bibtexParam.setOffset(0);
			bibtexParam.setLimit(count);
			bibtexParam.setSimHash(HashID.INTRA_HASH);
			
			List<Post<BibTex>> results = (List<Post<BibTex>>) this.queryForList("getMostActualBibTex", bibtexParam, mainSession);
			
			List<RecommendationItem> items = new ArrayList<RecommendationItem>(results.size());
			for(Post<BibTex> bibtex : results) {
				RecommendationItem item =  new RecommendationPost(bibtex);
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
		final DBSession mainSession = this.openMainSession();
		try {
			final BibTexParam bibtexParam = new BibTexParam();
			bibtexParam.setRequestedUserName(username);
			bibtexParam.setOffset(0);
			bibtexParam.setLimit(count);
			bibtexParam.setGroupId(GroupID.PUBLIC.getId());
			
			final List<RecommendationItem> items = new ArrayList<RecommendationItem>();
			// only get reduced data, because it's enough for calculation
			List<Post<BibTex>> results = (List<Post<BibTex>>) this.queryForList("getReducedUserBibTex", bibtexParam, mainSession);
			for(Post<BibTex> bibtex : results) {
				RecommendationItem item =  new RecommendationPost(bibtex);
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
		final DBSession mainSession = this.openMainSession();
		try {
			final BibTexParam bibtexParam = new BibTexParam();
			bibtexParam.setGroupId(GroupID.PUBLIC.getId());
			bibtexParam.setOffset(0);
			bibtexParam.setLimit(count);
			
			final List<RecommendationItem> items = new ArrayList<RecommendationItem>();
			
			for(String username : usernames) {
				bibtexParam.setRequestedUserName(username);
				// only get reduced data, because it's enough for calculation
				List<Post<BibTex>> results = (List<Post<BibTex>>) this.queryForList("getReducedUserBibTex", bibtexParam, mainSession);
				for(Post<BibTex> bibtex : results) {
					RecommendationItem item =  new RecommendationPost(bibtex);
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
		final DBSession mainSession = this.openMainSession();
		try {
			final List<RecommendationItem> items = new ArrayList<RecommendationItem>();
			final BibTexParam param = new BibTexParam();
			param.setSimHash(HashID.INTRA_HASH);
			for(Integer id : ids) {
				param.setRequestedContentId(id);
				Post<BibTex> bibtex = this.queryForObject("getBibTexById", param, Post.class, mainSession);
				items.add(new RecommendationPost(bibtex));
			}
			return items;
		} finally {
			mainSession.close();
		}
	}
}
