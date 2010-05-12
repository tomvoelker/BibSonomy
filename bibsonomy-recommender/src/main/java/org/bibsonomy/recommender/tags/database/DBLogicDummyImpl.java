package org.bibsonomy.recommender.tags.database;

import java.net.URL;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.bibsonomy.model.Post;
import org.bibsonomy.model.RecommendedTag;
import org.bibsonomy.model.Resource;
import org.bibsonomy.recommender.tags.database.params.Pair;
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

	private Map<Pair<Long, Long>, Collection<RecommendedTag>> recoMap = new HashMap<Pair<Long,Long>, Collection<RecommendedTag>>(); 
	
	private Map<String, Long> recos = new HashMap<String, Long>();
	
	
	@Override
	public Long addQuery(String userName, Date date, Post<? extends Resource> post, int postID, int queryTimeout) throws SQLException {
		return new Long(0);
	}

	@Override
	public int addRecommendation(Long queryId, Long settingsId, SortedSet<RecommendedTag> tags, long latency) throws SQLException {
		return recoMap.put(new Pair<Long, Long>(queryId, settingsId), tags).size();
	}

	@Override
	public Long addRecommender(Long queryId, String recId, String recDescr, byte[] recMeta) throws SQLException {
		if (!recos.containsKey(recId)) recos.put(recId, new Long(recos.size())); 
		return recos.get(recId);
	}

	@Override
	public Long addResultSelector(Long qid, String selectorInfo, byte[] selectorMeta) throws SQLException {
		return new Long(0);
	}

	@Override
	public void addSelectedRecommender(Long qid, Long sid) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void connectWithPost(Post<? extends Resource> post, int postID) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public List<Long> getActiveRecommenderIDs(Long qid) throws SQLException {
		return new LinkedList<Long>(recos.values());
	}

	@Override
	public List<Long> getAllNotSelectedRecommenderIDs(Long qid) throws SQLException {
		return new LinkedList<Long>();
	}

	@Override
	public List<Long> getAllRecommenderIDs(Long qid) throws SQLException {
		return new LinkedList<Long>(recos.values());
	}

	@Override
	public Integer getContentIDForQuery(Long queryID) throws SQLException {
		return 0; // TODO
	}

	@Override
	public Integer getContentIDForQuery(String userName, Date date, Integer postID) {
		return 0; // TODO
	}

	@Override
	public <T extends Resource> List<Pair<String, Integer>> getMostPopularTagsForResource(Class<T> resourceType, String intraHash, int range) throws SQLException {
		final List<Pair<String, Integer>> mostPopularTagsForResource = new LinkedList<Pair<String,Integer>>();

		mostPopularTagsForResource.add(new Pair<String, Integer>("mpResource1", 10));
		mostPopularTagsForResource.add(new Pair<String, Integer>("mpResource2", 8));
		mostPopularTagsForResource.add(new Pair<String, Integer>("mpResource3", 6));
		mostPopularTagsForResource.add(new Pair<String, Integer>("mpResource4", 4));
		mostPopularTagsForResource.add(new Pair<String, Integer>("mpResource5", 2));


		return mostPopularTagsForResource;
	}

	@Override
	public List<Pair<String, Integer>> getMostPopularTagsForUser(String username, int range) throws SQLException {
		final List<Pair<String, Integer>> mostPopularTagsForUser = new LinkedList<Pair<String,Integer>>();

		mostPopularTagsForUser.add(new Pair<String, Integer>("mpUser1", 10));
		mostPopularTagsForUser.add(new Pair<String, Integer>("mpUser2", 8));
		mostPopularTagsForUser.add(new Pair<String, Integer>("mpUser3", 6));
		mostPopularTagsForUser.add(new Pair<String, Integer>("mpUser4", 4));
		mostPopularTagsForUser.add(new Pair<String, Integer>("mpUser5", 2));


		return mostPopularTagsForUser;
	}

	@Override
	public List<TasEntry> getNewestEntries(Integer offset, Integer range) throws SQLException {
		return new LinkedList<TasEntry>(); // TODO
	}

	@Override
	public <T extends Resource> Integer getNumberOfTagsForResource(Class<T> resourceType, String intraHash) throws SQLException {
		return 5;
	}

	@Override
	public Integer getNumberOfTagsForUser(String username) throws SQLException {
		return 5;
	}

	@Override
	public <T extends Resource> Integer getNumberOfTasForResource(Class<T> resourceType, String intraHash) throws SQLException {
		return 5;
	}

	@Override
	public Integer getNumberOfTasForUser(String username) throws SQLException {
		return 5;
	}

	@Override
	public List<RecQueryParam> getQueriesForRecommender(Long sid) throws SQLException {
		return new LinkedList<RecQueryParam>();	
	}

	@Override
	public RecQueryParam getQuery(Long qid) throws SQLException {
		return new RecQueryParam();
	}

	@Override
	public Long getQueryForPost(String user_name, Date date, Integer postID) throws SQLException {
		return new Long(0);
	}

	@Override
	public SortedSet<RecommendedTag> getRecommendations(Long qid, Long sid) throws SQLException {
		return new TreeSet<RecommendedTag>(recoMap.get(new Pair<Long, Long>(qid, sid)));
	}

	@Override
	public void getRecommendations(Long qid, Long sid, Collection<RecommendedTag> recommendedTags) throws SQLException {
		recommendedTags.addAll(recoMap.get(new Pair<Long, Long>(qid, sid)));
	}

	@Override
	public SortedSet<RecommendedTag> getRecommendations(Long qid) throws SQLException {
		return new TreeSet<RecommendedTag>();
	}

	@Override
	public void getRecommendations(Long qid, Collection<RecommendedTag> recommendedTags) throws SQLException {
		// TODO Auto-generated method stub
	}

	@Override
	public RecSettingParam getRecommender(Long sid) throws SQLException {
		return new RecSettingParam();
	}

	@Override
	public List<Pair<Long, Long>> getRecommenderSelectionCount(Long qid) throws SQLException {
		return new LinkedList<Pair<Long,Long>>();
	}

	@Override
	public List<Long> getSelectedRecommenderIDs(Long qid) throws SQLException {
		return new LinkedList<Long>();
	}

	@Override
	public List<RecommendedTag> getSelectedTags(Long qid) throws SQLException {
		return new LinkedList<RecommendedTag>();
	}

	@Override
	public SelectorSettingParam getSelector(Long sid) throws SQLException {
		return new SelectorSettingParam();
	}

	@Override
	public List<String> getTagNamesForPost(Integer cid) throws SQLException {
		return new LinkedList<String>();
	}

	@Override
	public List<String> getTagNamesForRecQuery(Long sid, Long qid) throws SQLException {
		return new LinkedList<String>();
	}

	@Override
	public Integer guessPostFromQuery(Long query_id) throws SQLException {
		return 0;
	}

	@Override
	public Long guessQueryFromPost(Integer content_id) throws SQLException {
		return new Long(0);
	}

	@Override
	public boolean logRecommendation(Long qid, Long sid, long latency, SortedSet<RecommendedTag> tags, SortedSet<RecommendedTag> preset) throws SQLException {
		return false;
	}

	@Override
	public int storeRecommendation(Long qid, Long rid, Collection<RecommendedTag> result) throws SQLException {
		return recoMap.put(new Pair<Long, Long>(qid, rid), result).size();
	}

	@Override
	public Integer getUserIDByName(String userName) {
		return 0;
	}

	@Override
	public String getUserNameByID(int userID) {
		return "nouser";
	}


	public void addRecommenderToQuery(Long qid, Long sid) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Long insertRecommenderSetting(String recId, String recDescr,
			byte[] recMeta) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long insertSelectorSetting(String selectorInfo, byte[] selectorMeta)
			throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setResultSelectorToQuery(Long qid, Long rid)
			throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<Long> getActiveRecommenderSettingIds() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long getAverageLatencyForRecommender(Long sid, Long numberOfQueries)
			throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Long> getDisabledRecommenderSettingIds() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RecAdminOverview getRecommenderAdminOverview(String id)
			throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<Long, String> getRecommenderIdsForSettingIds(List<Long> sids)
			throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void removeRecommender(long sid) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateRecommenderUrl(long sid, URL url) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateRecommenderstatus(List<Long> activeRecs,
			List<Long> disabledRecs) throws SQLException {
		// TODO Auto-generated method stub
		
	}

}
