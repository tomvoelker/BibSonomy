package org.bibsonomy.recommender.testutil;

import java.net.URL;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.bibsonomy.common.Pair;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.RecommendedTag;
import org.bibsonomy.model.Resource;
import org.bibsonomy.recommender.tags.database.DBLogic;
import org.bibsonomy.recommender.tags.database.params.RecAdminOverview;
import org.bibsonomy.recommender.tags.database.params.RecQueryParam;
import org.bibsonomy.recommender.tags.database.params.RecSettingParam;
import org.bibsonomy.recommender.tags.database.params.SelectorSettingParam;
import org.bibsonomy.recommender.tags.database.params.TasEntry;

/**
 * @author rja
 * @version $Id$
 */
public class DBLogicDummyImpl implements DBLogic {

	private final Map<Pair<Long, Long>, Collection<RecommendedTag>> recoMap = new HashMap<Pair<Long,Long>, Collection<RecommendedTag>>(); 
	
	private final Map<String, Long> recos = new HashMap<String, Long>();
	
	
	@Override
	public Long addQuery(final String userName, final Date date, final Post<? extends Resource> post, final int postID, final int queryTimeout) {
		return 0l;
	}

	@Override
	public int addRecommendation(final Long queryId, final Long settingsId, final SortedSet<RecommendedTag> tags, final long latency) {
		return this.recoMap.put(new Pair<Long, Long>(queryId, settingsId), tags).size();
	}

	@Override
	public Long addRecommender(final Long queryId, final String recId, final String recDescr, final byte[] recMeta) {
		if (!this.recos.containsKey(recId)) {
			this.recos.put(recId, Long.valueOf(this.recos.size()));
		} 
		return this.recos.get(recId);
	}

	@Override
	public Long addResultSelector(final Long qid, final String selectorInfo, final byte[] selectorMeta) {
		return 0l;
	}

	@Override
	public void addSelectedRecommender(final Long qid, final Long sid) {
		// TODO Auto-generated method stub

	}

	@Override
	public void connectWithPost(final Post<? extends Resource> post, final int postID) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<Long> getActiveRecommenderIDs(final Long qid) {
		return new LinkedList<Long>(this.recos.values());
	}

	@Override
	public List<Long> getAllNotSelectedRecommenderIDs(final Long qid) {
		return new LinkedList<Long>();
	}

	@Override
	public List<Long> getAllRecommenderIDs(final Long qid) {
		return new LinkedList<Long>(this.recos.values());
	}

	@Override
	public Integer getContentIDForQuery(final Long queryID) {
		return 0; // TODO
	}

	@Override
	public <T extends Resource> List<Pair<String, Integer>> getMostPopularTagsForResource(final Class<T> resourceType, final String intraHash, final int range) {
		final List<Pair<String, Integer>> mostPopularTagsForResource = new LinkedList<Pair<String,Integer>>();

		mostPopularTagsForResource.add(new Pair<String, Integer>("mpResource1", 10));
		mostPopularTagsForResource.add(new Pair<String, Integer>("mpResource2", 8));
		mostPopularTagsForResource.add(new Pair<String, Integer>("mpResource3", 6));
		mostPopularTagsForResource.add(new Pair<String, Integer>("mpResource4", 4));
		mostPopularTagsForResource.add(new Pair<String, Integer>("mpResource5", 2));


		return mostPopularTagsForResource;
	}

	@Override
	public List<Pair<String, Integer>> getMostPopularTagsForUser(final String username, final int range) {
		final List<Pair<String, Integer>> mostPopularTagsForUser = new LinkedList<Pair<String,Integer>>();

		mostPopularTagsForUser.add(new Pair<String, Integer>("mpUser1", 10));
		mostPopularTagsForUser.add(new Pair<String, Integer>("mpUser2", 8));
		mostPopularTagsForUser.add(new Pair<String, Integer>("mpUser3", 6));
		mostPopularTagsForUser.add(new Pair<String, Integer>("mpUser4", 4));
		mostPopularTagsForUser.add(new Pair<String, Integer>("mpUser5", 2));


		return mostPopularTagsForUser;
	}

	@Override
	public List<TasEntry> getNewestEntries(final Integer offset, final Integer range) {
		return new LinkedList<TasEntry>(); // TODO
	}

	@Override
	public <T extends Resource> Integer getNumberOfTagsForResource(final Class<T> resourceType, final String intraHash) {
		return 5;
	}

	@Override
	public Integer getNumberOfTagsForUser(final String username) {
		return 5;
	}

	@Override
	public <T extends Resource> Integer getNumberOfTasForResource(final Class<T> resourceType, final String intraHash) {
		return 5;
	}

	@Override
	public Integer getNumberOfTasForUser(final String username) {
		return 5;
	}

	@Override
	public List<RecQueryParam> getQueriesForRecommender(final Long sid) {
		return new LinkedList<RecQueryParam>();	
	}

	@Override
	public RecQueryParam getQuery(final Long qid) {
		return new RecQueryParam();
	}

	@Override
	public Long getQueryForPost(final String user_name, final Date date, final Integer postID) {
		return 0l;
	}

	@Override
	public SortedSet<RecommendedTag> getRecommendations(final Long qid, final Long sid) {
		return new TreeSet<RecommendedTag>(this.recoMap.get(new Pair<Long, Long>(qid, sid)));
	}

	@Override
	public void getRecommendations(final Long qid, final Long sid, final Collection<RecommendedTag> recommendedTags) {
		recommendedTags.addAll(this.recoMap.get(new Pair<Long, Long>(qid, sid)));
	}

	@Override
	public SortedSet<RecommendedTag> getRecommendations(final Long qid) {
		return new TreeSet<RecommendedTag>();
	}

	@Override
	public void getRecommendations(final Long qid, final Collection<RecommendedTag> recommendedTags) {
		// TODO Auto-generated method stub
	}

	@Override
	public RecSettingParam getRecommender(final Long sid) {
		return new RecSettingParam();
	}

	@Override
	public List<Pair<Long, Long>> getRecommenderSelectionCount(final Long qid) {
		return new LinkedList<Pair<Long,Long>>();
	}

	@Override
	public List<Long> getSelectedRecommenderIDs(final Long qid) {
		return new LinkedList<Long>();
	}

	@Override
	public List<RecommendedTag> getSelectedTags(final Long qid) {
		return new LinkedList<RecommendedTag>();
	}

	@Override
	public SelectorSettingParam getSelector(final Long sid) {
		return new SelectorSettingParam();
	}

	@Override
	public List<String> getTagNamesForPost(final Integer cid) {
		return new LinkedList<String>();
	}

	@Override
	public List<String> getTagNamesForRecQuery(final Long sid, final Long qid) {
		return new LinkedList<String>();
	}

	@Override
	public Integer guessPostFromQuery(final Long query_id) {
		return 0;
	}

	@Override
	public Long guessQueryFromPost(final Integer content_id) {
		return 0l;
	}

	@Override
	public boolean logRecommendation(final Long qid, final Long sid, final long latency, final SortedSet<RecommendedTag> tags, final SortedSet<RecommendedTag> preset) {
		return false;
	}

	@Override
	public int storeRecommendation(final Long qid, final Long rid, final Collection<RecommendedTag> result) {
		return this.recoMap.put(new Pair<Long, Long>(qid, rid), result).size();
	}

	@Override
	public Integer getUserIDByName(final String userName) {
		return 0;
	}

	@Override
	public String getUserNameByID(final int userID) {
		return "nouser";
	}

	@Override
	public void addRecommenderToQuery(final Long qid, final Long sid) {
		// TODO Auto-generated method stub
	}

	@Override
	public Long insertRecommenderSetting(final String recId, final String recDescr, final byte[] recMeta) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long insertSelectorSetting(final String selectorInfo, final byte[] selectorMeta) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setResultSelectorToQuery(final Long qid, final Long rid) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<Long> getActiveRecommenderSettingIds() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long getAverageLatencyForRecommender(final Long sid, final Long numberOfQueries) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Long> getDisabledRecommenderSettingIds() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RecAdminOverview getRecommenderAdminOverview(final String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<Long, String> getRecommenderIdsForSettingIds(final List<Long> sids) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void updateRecommenderUrl(final long sid, final URL url) {
		// TODO Auto-generated method stub
	}

	@Override
	public void updateRecommenderstatus(final List<Long> activeRecs, final List<Long> disabledRecs) {
		// TODO Auto-generated method stub
	}

	@Override
	public void removeRecommender(final String url) {
		// TODO Auto-generated method stub
	}

	@Override
	public List<Long> getLocalRecommenderSettingIds() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Long> getDistantRecommenderSettingIds() {
		// TODO Auto-generated method stub
		return null;
	}

}
