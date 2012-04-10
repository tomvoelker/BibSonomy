package org.bibsonomy.community.database;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.community.algorithm.Algorithm;
import org.bibsonomy.community.database.param.CommunityParam;
import org.bibsonomy.community.enums.Ordering;
import org.bibsonomy.community.model.Cluster;
import org.bibsonomy.community.model.Post;
import org.bibsonomy.community.model.ResourceCluster;
import org.bibsonomy.community.model.Tag;
import org.bibsonomy.community.model.User;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;

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
	private final BibTexPostManager bibtexLogic;
	
	/** bibtex post manager for accessing bibsonomy posts */
	private final BookmarkPostManager bookmarkLogic;

	/** tag manager for accessing tag clouds */
	private final TagManager tagLogic;
	
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
	@Override
	public int addAlgorithm(final Algorithm algorithm) throws Exception {
		final SqlMapClient sqlMap = getSqlMap();
		
		Integer algorithmID;
	   	try {
    		sqlMap.startTransaction();

    		final CommunityParam param = new CommunityParam();
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

	@Override
	public int addRunSet() throws Exception {
		final Date currentDate = new Date(System.currentTimeMillis());
		
		final CommunityParam param = new CommunityParam();
		param.setDate(currentDate);
		
		return (Integer) getSqlMap().insert("addRunSet", param);
	}	
	
	@Override
	public int addAlgorithmToRunSet(final int algorithm_id, final int block_id,
			final int clusters, final int topics) throws Exception {
		
		final CommunityParam param = new CommunityParam();
		param.setAlgorithmID(algorithm_id);
		param.setBlockID(block_id);
		param.setClusterCount(clusters);
		param.setTopicCount(topics);
		final Integer runSet = (Integer) getSqlMap().insert("addAlgorithmToRunSet", param);
		
		getSqlMap().startBatch();
		
		for( int i=0; i<clusters; i++ ) {
			param.setRunID(runSet);
			param.setCommunityID(i);
			getSqlMap().insert("addCommunityToAlgorithm", param);
		}
		
		getSqlMap().executeBatch();
		
		return runSet;
	}

	@Override
	public int addAlgorithmToRunSet(final Algorithm algorithm, final int block_id, final int nClusters, final int nTopics) throws Exception {
		final Integer algorithmID = getAlgorithmID(algorithm);
		
		return addAlgorithmToRunSet(algorithmID, block_id, nClusters, nTopics);
	}

	@Override
	public void addUserToCommunity(final int run_id, final int community_id,
			final String user_name, final double p) throws Exception {
		
		final CommunityParam param = new CommunityParam();
		param.setRunID(run_id);
		param.setCommunityID(community_id);
		param.setUserName(user_name);
		param.setWeight(p);
		
		getSqlMap().insert("addUserToCommunity", param);
	}

	@Override
	public void addResourceToCommunity(final int run_id, final int community_id, final String hash,
			final Integer content_id, final int content_type, final double p) throws Exception {

		final CommunityParam param = new CommunityParam();
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

	@Override
	public void addTagToCommunity(final int run_id, final int community_id, final int topic_id,final String tag_name, final int count, final double p) throws Exception {
		
		final CommunityParam param = new CommunityParam();
		param.setRunID(run_id);
		param.setCommunityID(community_id);
		param.setTopicID(topic_id);
		param.setTagName(tag_name);
		param.setGlobalcount(count);
		param.setWeight(p);
		
		getSqlMap().insert("addTagToCommunity", param);
	}

	@Override
	public void addCommunities(final int run_id, final Collection<Cluster<User>> communities) throws Exception {
		getSqlMap().startBatch();
		for( final Cluster<User> community : communities ) {
			for( final User user : community.getInstances() ) {
				this.addUserToCommunity(run_id, community.getClusterID(), user.getName(), user.getWeight());
			}
		}
		getSqlMap().executeBatch();
	}

	@Override
	public void addTopics(final int run_id, final Collection<Cluster<Tag>> topics) throws Exception {
		getSqlMap().startBatch();
		for( final Cluster<Tag> topic : topics ) {
			for( final Tag tag : topic.getInstances() ) {
				this.addTagToCommunity(run_id, topic.getClusterID(), tag.getTopicId(), tag.getName(), tag.getGlobalcount(), tag.getWeight());
			}
		}		
		getSqlMap().executeBatch();
	}	

	@Override
	public void addResources(final int run_id, final Collection<Cluster<Post<? extends org.bibsonomy.model.Resource>>> posts) throws Exception {
		getSqlMap().startBatch();
		for( final Cluster<Post<? extends org.bibsonomy.model.Resource>> cluster : posts) {	
			for( final Post<? extends org.bibsonomy.model.Resource> post : cluster.getInstances() ) {
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
	@Override
	public Integer getAlgorithmID(final Algorithm algorithm) throws Exception {
		
		final CommunityParam param = new CommunityParam();
		param.setAlgorithmName(algorithm.getName());
		param.setAlgorithmMeta(algorithm.getMeta());
		
		final Integer algorithmID = (Integer)getSqlMap().queryForObject("getAlgorithmID", param);
		
		return algorithmID;
	}

	@Override
	public Collection<String> getUserNamesForCommunity(final int run_id, final int community_id, final Ordering order, final int limit, final int offset) {
		final CommunityParam param = new CommunityParam();
		param.setRunID(run_id);
		param.setCommunityID(community_id);
		return queryForList("getUserNamesForCommunity", param);
	}

	@Override
	public Collection<User> getUsersForCommunity(final int community_uid, final Ordering order, final int limit, final int offset) {
		final CommunityParam param = new CommunityParam();
		param.setCommunityUID(community_uid);
		return queryForList("getUsersForCommunity", param);
	}

	
	@Override
	public Integer getNumberOfCommunities(final int runId) throws Exception {
		final CommunityParam param = new CommunityParam();
		param.setRunID(runId);
		return (Integer)getSqlMap().queryForObject("getNumberOfCommunities", param);
	}
	
	@Override
	public Collection<Integer> listCommunities(final int run_id, final int limit, final int offset) {
		final CommunityParam param = new CommunityParam();
		param.setRunID(run_id);
		param.setLimit(limit);
		param.setOffset(offset);
		return queryForList("listCommunities", param);
	}

	@Override
	public Collection<Integer> listCommunities(final int run_id) {
		final CommunityParam param = new CommunityParam();
		param.setRunID(run_id);
		return queryForList("listAllCommunities", param);
	}

	@Override
	public Collection<ResourceCluster> getCommunities(final int runId, final int userCloudLimit, final int tagCloudLimit, final int bibTexLimit, final int bookmarkLimit, final int limit, final int offset) {
		// get list of all community ids
		final CommunityParam communityParam = new CommunityParam();
		communityParam.setRunID(runId);
		final Collection<Integer> communityIdx = listCommunities(runId, limit, offset);
		
		// fetch each community
		final Collection<ResourceCluster> communities = new ArrayList<ResourceCluster>();
		for( final Integer communityUId : communityIdx ) {
			final ResourceCluster community = new ResourceCluster();
			community.setClusterID(communityUId);
			final Collection<Post<Bookmark>> bookmarks = new LinkedList<Post<Bookmark>>();
			//bookmarkLogic.getPostsForCommunity(runId, communityId, Ordering.POPULAR, bookmarkLimit, 0);
			final Collection<Post<BibTex>> bibTex = new LinkedList<Post<BibTex>>(); 
			//bibtexLogic.getPostsForCommunity(runId, communityId, Ordering.POPULAR, bibTexLimit, 0);
			final Collection<Tag> tagCloud = tagLogic.getTagCloudForCommunity(communityUId, Ordering.POPULAR, tagCloudLimit, 0);
			
			final Collection<User> communityMembers = this.getUsersForCommunity(communityUId, Ordering.POPULAR, userCloudLimit, 0);
			
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

	public Collection<Integer> getCommunitiesForUser(final String userName, final int limit, final int offset) {
		Collection<Integer> communities;
		final CommunityParam param = new CommunityParam();
		param.setUserName(userName);
		param.setLimit(limit);
		param.setOffset(offset);
		communities = queryForList("getCommunitiesForUser", param);
		if (communities == null) {
			return new ArrayList<Integer>();
		}
		
		return communities;
	}
}
