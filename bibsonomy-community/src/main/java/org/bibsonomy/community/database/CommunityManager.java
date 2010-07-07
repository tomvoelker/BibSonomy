package org.bibsonomy.community.database;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.community.algorithm.Algorithm;
import org.bibsonomy.community.database.param.CommunityParam;
import org.bibsonomy.community.database.param.CommunityResourceParam;
import org.bibsonomy.community.enums.Ordering;
import org.bibsonomy.community.model.Cluster;
import org.bibsonomy.community.model.Post;
import org.bibsonomy.community.model.ResourceCluster;
import org.bibsonomy.community.model.Tag;
import org.bibsonomy.community.model.User;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Resource;

import com.ibatis.sqlmap.client.SqlMapClient;

/**
 * class for managing the database backend
 *  
 * @author fei
 *
 */
public class CommunityManager extends AbstractDBManager implements DBManageInterface {
	private final static Log log = LogFactory.getLog(CommunityManager.class);
	
	private static final String SQLMAP = "SqlMapConfig_community.xml";

	/** singleton pattern's instance reference */
	protected static CommunityManager instance = null;

	/** bookmark post manager for accessing bibsonomy posts */
	private BibTexPostManager bibtexLogic;
	
	/** bibtex post manager for accessing bibsonomy posts */
	private BookmarkPostManager bookmarkLogic;

	/** tag manager for accessing tag clouds */
	private TagManager tagLogic;
	
	private CommunityManager() {
		super(SQLMAP);
		
		bibtexLogic   = BibTexPostManager.getInstance();
		bookmarkLogic = BookmarkPostManager.getInstance(); 
		tagLogic      = TagManager.getInstance();
		
		log.debug("Community database manager initialzied!");
	}
	
	/**
	 * @return An instance of this implementation of 
	 */
	public static CommunityManager getInstance() {
		if (instance == null) instance = new CommunityManager();
		return instance;
	}
	
	//------------------------------------------------------------------------
	// add
	//------------------------------------------------------------------------
	public int addAlgorithm(Algorithm algorithm) throws Exception {
		SqlMapClient sqlMap = getSqlMap();
		
		Integer algorithmID;
	   	try {
    		sqlMap.startTransaction();

    		CommunityParam param = new CommunityParam();
    		param.setAlgorithmName(algorithm.getName());
    		param.setAlgorithmMeta(algorithm.getMeta());
    		
    		// check whether algorithm already exists
    		algorithmID = getAlgorithmID(algorithm);
    		if( algorithmID == null ) {
    			algorithmID = (Integer) sqlMap.insert("addAlgorithm", param);
    		}
    		
    		sqlMap.commitTransaction();
    	} finally {
    		sqlMap.endTransaction();
    	}		
		
		return algorithmID;
	}

	public int addRunSet() throws Exception {
		Date currentDate = new Date(System.currentTimeMillis());
		
		CommunityParam param = new CommunityParam();
		param.setDate(currentDate);
		
		return (Integer) getSqlMap().insert("addRunSet", param);
	}	
	
	public int addAlgorithmToRunSet(int algorithm_id, int block_id,
			int clusters, int topics) throws Exception {
		
		CommunityParam param = new CommunityParam();
		param.setAlgorithmID(algorithm_id);
		param.setBlockID(block_id);
		param.setClusterCount(clusters);
		param.setTopicCount(topics);
		Integer runSet = (Integer) getSqlMap().insert("addAlgorithmToRunSet", param);
		
		getSqlMap().startBatch();
		
		for( int i=0; i<clusters; i++ ) {
			param.setRunID(runSet);
			param.setCommunityID(i);
			getSqlMap().insert("addCommunityToAlgorithm", param);
		}
		
		getSqlMap().executeBatch();
		
		return runSet;
	}

	public int addAlgorithmToRunSet(Algorithm algorithm, int block_id, final int nClusters, final int nTopics) throws Exception {
		Integer algorithmID = getAlgorithmID(algorithm);
		
		return addAlgorithmToRunSet(algorithmID, block_id, nClusters, nTopics);
	}

	public void addUserToCommunity(int run_id, int community_id,
			String user_name, double p) throws Exception {
		
		CommunityParam param = new CommunityParam();
		param.setRunID(run_id);
		param.setCommunityID(community_id);
		param.setUserName(user_name);
		param.setWeight(p);
		
		getSqlMap().insert("addUserToCommunity", param);
	}

	public void addResourceToCommunity(int run_id, int community_id, final String hash,
			final Integer content_id, final int content_type, final double p) throws Exception {

		CommunityParam param = new CommunityParam();
		param.setRunID(run_id);
		param.setHash(hash);
		if( content_id != null ) {
			param.setContentID(content_id);
		}
		param.setCommunityID(community_id);
		param.setContentType(content_type);
		param.setWeight(p);
		
		getSqlMap().insert("addResourceToCommunity", param);
	}

	public void addTagToCommunity(int run_id, int community_id, int topic_id,String tag_name, final int count, final double p) throws Exception {
		
		CommunityParam param = new CommunityParam();
		param.setRunID(run_id);
		param.setCommunityID(community_id);
		param.setTopicID(topic_id);
		param.setTagName(tag_name);
		param.setGlobalcount(count);
		param.setWeight(p);
		
		getSqlMap().insert("addTagToCommunity", param);
	}

	public void addCommunities(int run_id, Collection<Cluster<User>> communities) throws Exception {
		getSqlMap().startBatch();
		for( Cluster<User> community : communities ) {
			for( User user : community.getInstances() ) {
				this.addUserToCommunity(run_id, community.getClusterID(), user.getName(), user.getWeight());
			}
		}
		getSqlMap().executeBatch();
	}

	public void addTopics(int run_id, Collection<Cluster<Tag>> topics) throws Exception {
		getSqlMap().startBatch();
		for( Cluster<Tag> topic : topics ) {
			for( Tag tag : topic.getInstances() ) {
				this.addTagToCommunity(run_id, topic.getClusterID(), tag.getTopicId(), tag.getName(), tag.getGlobalcount(), tag.getWeight());
			}
		}		
		getSqlMap().executeBatch();
	}	

	public void addResources(final int run_id, final Collection<Cluster<Post<? extends org.bibsonomy.model.Resource>>> posts) throws Exception {
		getSqlMap().startBatch();
		for( Cluster<Post<? extends org.bibsonomy.model.Resource>> cluster : posts) {	
			for( Post<? extends org.bibsonomy.model.Resource> post : cluster.getInstances() ) {
				String hash = null;
				if( post.getResource() != null ) {
					hash = post.getResource().getInterHash();
				}
				this.addResourceToCommunity(run_id, cluster.getClusterID(), hash, post.getContentId(), post.getContentType(), post.getWeight());
			}
		}	
		getSqlMap().executeBatch();
	}	
	//------------------------------------------------------------------------
	// lookup
	//------------------------------------------------------------------------
	public Integer getAlgorithmID(final Algorithm algorithm) throws Exception {
		
		CommunityParam param = new CommunityParam();
		param.setAlgorithmName(algorithm.getName());
		param.setAlgorithmMeta(algorithm.getMeta());
		
		final Integer algorithmID = (Integer)getSqlMap().queryForObject("getAlgorithmID", param);
		
		return algorithmID;
	}

	public Collection<String> getUserNamesForCommunity(int run_id, int community_id, Ordering order, int limit, int offset) {
		CommunityParam param = new CommunityParam();
		param.setRunID(run_id);
		param.setCommunityID(community_id);
		return queryForList("getUserNamesForCommunity", param);
	}

	public Collection<User> getUsersForCommunity(int community_uid, Ordering order, int limit, int offset) {
		CommunityParam param = new CommunityParam();
		param.setCommunityUID(community_uid);
		return queryForList("getUsersForCommunity", param);
	}

	
	public Integer getNumberOfCommunities(int runId) throws Exception {
		CommunityParam param = new CommunityParam();
		param.setRunID(runId);
		return (Integer)getSqlMap().queryForObject("getNumberOfCommunities", param);
	}
	
	public Collection<Integer> listCommunities(final int run_id, final int limit, final int offset) {
		CommunityParam param = new CommunityParam();
		param.setRunID(run_id);
		param.setLimit(limit);
		param.setOffset(offset);
		return queryForList("listCommunities", param);
	}

	public Collection<Integer> listCommunities(final int run_id) {
		CommunityParam param = new CommunityParam();
		param.setRunID(run_id);
		return queryForList("listAllCommunities", param);
	}

	public Collection<ResourceCluster> getCommunities(int runId, int userCloudLimit, int tagCloudLimit, int bibTexLimit, int bookmarkLimit, int limit, int offset) {
		// get list of all community ids
		final CommunityParam communityParam = new CommunityParam();
		communityParam.setRunID(runId);
		Collection<Integer> communityIdx = listCommunities(runId, limit, offset);
		
		// fetch each community
		Collection<ResourceCluster> communities = new ArrayList<ResourceCluster>();
		for( final Integer communityUId : communityIdx ) {
			ResourceCluster community = new ResourceCluster();
			community.setClusterID(communityUId);
			Collection<Post<Bookmark>> bookmarks = new LinkedList<Post<Bookmark>>();
			//bookmarkLogic.getPostsForCommunity(runId, communityId, Ordering.POPULAR, bookmarkLimit, 0);
			Collection<Post<BibTex>> bibTex = new LinkedList<Post<BibTex>>(); 
			//bibtexLogic.getPostsForCommunity(runId, communityId, Ordering.POPULAR, bibTexLimit, 0);
			Collection<Tag> tagCloud = tagLogic.getTagCloudForCommunity(communityUId, Ordering.POPULAR, tagCloudLimit, 0);
			
			Collection<User> communityMembers = this.getUsersForCommunity(communityUId, Ordering.POPULAR, userCloudLimit, 0);
			
			community.setBookmark(bookmarks);
			community.setBibtex(bibTex);
			community.setTags(tagCloud);
			community.setMembers(communityMembers);
			
			// store community
			communities.add(community);
		}
		
		// all done
		return communities;
	}

	public Collection<Integer> getCommunitiesForUser(final String userName, int limit, int offset) {
		Collection<Integer> communities;
		CommunityParam param = new CommunityParam();
		param.setUserName(userName);
		param.setLimit(limit);
		param.setOffset(offset);
		communities = queryForList("getCommunitiesForUser", param);
		if( communities==null ) {
			return new ArrayList<Integer>();
		} else {
			return communities;
		}
	}
}
