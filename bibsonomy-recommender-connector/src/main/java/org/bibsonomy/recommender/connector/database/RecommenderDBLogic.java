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
import recommender.core.interfaces.model.Item;
import recommender.core.interfaces.model.RecommendationResource;
import recommender.core.interfaces.model.RecommendationTag;

public class RecommenderDBLogic extends AbstractDatabaseManager implements RecommenderDBAccess{

	private RecommenderDBSessionFactory mainFactory;
	
	private RecommenderDBSession openMainSession() {
		return this.mainFactory.getDatabaseSession();
	}
	
	public void setMainFactory(RecommenderDBSessionFactory mainFactory) {
		this.mainFactory = mainFactory;
	}
	
	@Override
	public List<Item> getRandomItems() {
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

	@Override
	public List<RecommendationTag> getTagsForResource(int cid) {
		final RecommenderDBSession mainSession = this.openMainSession();
		try {
			return (List<RecommendationTag>) mainSession.queryForList("getCompleteTagsForCID", cid);
		} finally {
			mainSession.close();
		}
	}

	@Override
	public Integer getContentIDForQuery(Long queryID) {
		// TODO Auto-generated method stub
		return null;
	}

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

	@Override
	public <T extends RecommendationResource> List<Pair<String, Integer>> getMostPopularTagsForResource(
			Class<T> resourceType, String intraHash, int range) {
		final RecommenderDBSession mainSession = this.openMainSession();
		try {
			final PostParam param = new PostParam();
			param.setIntraHash(intraHash);
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

	@Override
	public Integer getNumberOfTagsForUser(String username) {
		final RecommenderDBSession mainSession = this.openMainSession();
		try {
			return this.queryForObject("getNumberOfTagsForUser", username, Integer.class, mainSession);
		} finally {
			mainSession.close();
		}
	}

	@Override
	public Integer getNumberOfTasForUser(String username) {
		final RecommenderDBSession mainSession = this.openMainSession();
		try {
			return this.queryForObject("getNumberOfTasForUser", username, Integer.class, mainSession);
		} finally {
			mainSession.close();
		}
	}

	@Override
	public <T extends RecommendationResource> Integer getNumberOfTagsForResource(
			Class<T> resourceType, String intraHash) {
		final RecommenderDBSession mainSession = this.openMainSession();
		try {
			if (BibTex.class.equals(resourceType)) {
				return this.queryForObject("getNumberOfTagsForBibTeX", intraHash, Integer.class, mainSession);
			} else if (Bookmark.class.equals(resourceType)) {
				return this.queryForObject("getNumberOfTagsForBookmark", intraHash, Integer.class, mainSession);
			}
			throw new UnsupportedResourceTypeException("Unknown resource type " + resourceType);
		} finally {
			mainSession.close();
		}
	}

	@Override
	public <T extends RecommendationResource> Integer getNumberOfTasForResource(
			Class<T> resourceType, String intraHash) {
		final RecommenderDBSession mainSession = this.openMainSession();
		try {
			if (BibTex.class.equals(resourceType)) {
				return this.queryForObject("getNumberOfTasForBibTeX", intraHash, Integer.class, mainSession);
			} else if (Bookmark.class.equals(resourceType)) {
				return this.queryForObject("getNumberOfTasForBookmark", intraHash, Integer.class, mainSession);
			}
			throw new UnsupportedResourceTypeException("Unknown resource type " + resourceType);
		} finally {
			mainSession.close();
		}
	}

	@Override
	public Integer getUserIDByName(String userName) {
		final RecommenderDBSession mainSession = this.openMainSession();
		try {
			return this.queryForObject("getUserIDByName", userName, Integer.class, mainSession);
		} finally {
			mainSession.close();
		}
	}

	@Override
	public String getUserNameByID(int userID) {
		final RecommenderDBSession mainSession = this.openMainSession();
		try {
			return this.queryForObject("getUserNameByID", userID, String.class, mainSession);
		} finally {
			mainSession.close();
		}
	}

	@Override
	public List<String> getTagNamesForPost(Integer cid) {
		final RecommenderDBSession mainSession = this.openMainSession();
		try {
			return this.queryForList("getTagNamesForCID", cid, String.class, mainSession);
		} finally {
			mainSession.close();
		}
	}

}
