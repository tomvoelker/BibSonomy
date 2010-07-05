package org.bibsonomy.community.database;

import java.util.Collection;

import org.bibsonomy.community.algorithm.Algorithm;
import org.bibsonomy.community.enums.Ordering;
import org.bibsonomy.community.model.Cluster;
import org.bibsonomy.community.model.Post;
import org.bibsonomy.community.model.ResourceCluster;
import org.bibsonomy.community.model.Tag;
import org.bibsonomy.community.model.User;
import org.bibsonomy.model.Resource;

/**
 * interface for managing different clusterings
 * @author fei
 *
 */
public interface DBManageInterface {

	//------------------------------------------------------------------------
	// add
	//------------------------------------------------------------------------
	/**
	 * add new algorithm
	 * 
	 * @param alg
	 * @return new algorithm's id - if the algorithm already existed, the old id is returned
	 */
	public int addAlgorithm(final Algorithm algorithm) throws Exception;
	
	/**
	 * start a new block of clusterings
	 * 
	 * @return new block's identifier 'block_id'
	 */
	public int addRunSet() throws Exception;
	
	/**
	 * add given algorithm to given run set - convenient method 
	 * @see{DBManageInterface#addAlgorithmToRunSet}
	 * 
	 * @param alg
	 * @param block_id
	 * @return given algorithm's run_id
	 */
	public int addAlgorithmToRunSet(final Algorithm algorithm, final int block_id, final int nClusters, final int nTopics) throws Exception;
	
	/**
	 * add given algorithm to given run set
	 * 
	 * @param algorithm_id
	 * @param block_id
	 * @param nClusters
	 * @param nTopics
	 * @return given algorithm's run_id
	 */
	public int addAlgorithmToRunSet(final int algorithm_id, final int block_id, final int nClusters, final int nTopics) throws Exception;
	
	/**
	 * add given user to given community for given run_id
	 * 
	 * @param run_id
	 * @param community_id
	 * @param user_name
	 * @param p
	 */
	public void addUserToCommunity(final int run_id, final int community_id, final String user_name, final double p) throws Exception;
	
	/**
	 * add given tag/topic pair to given community for given run_id
	 * @param run_id
	 * @param community_id
	 * @param topic_id
	 * @param tag_name
	 */
	public void addTagToCommunity(final int run_id, final int community_id, final int topic_id, final String tag_name, final int count, final double p) throws Exception;
	
	/**
	 * add given resource to given community for given run_id
	 * @param run_id
	 * @param community_id
	 * @param content_type
	 */
	public void addResourceToCommunity(final int run_id, final int community_id, final String hash, final Integer content_id, final int content_type, final double p) throws Exception;
	
	/** convenient method */
	public void addCommunities(final int run_id, final Collection<Cluster<User>> communities) throws Exception;
	
	/** convenient method */
	public void addTopics(final int run_id, final Collection<Cluster<Tag>> topics) throws Exception; 

	/** convenient method */
	public void addResources(final int run_id, final Collection<Cluster<Post<? extends Resource>>> posts) throws Exception; 
	//------------------------------------------------------------------------
	// lookup
	//------------------------------------------------------------------------

	/**
	 * get given algorithm's id - if existing, otherwise null
	 * 
	 * @return given algorithm's id if existing - otherwise null
	 */
	public Integer getAlgorithmID(final Algorithm algorithm) throws Exception;

	/**
	 * get user names for given community
	 * @param run_id
	 * @param community_id
	 * @param order
	 * @return
	 */
	public Collection<String> getUserNamesForCommunity(final int run_id, final int community_id, final Ordering order, final int limit, final int offset);

	/**
	 * get community members for given community
	 * @param community_uid
	 * @param order
	 * @return
	 */
	public Collection<User> getUsersForCommunity(final int community_uid, final Ordering order, final int limit, final int offset);

	/**
	 * get the number of different communities for the given run id
	 * @param runId
	 * @return
	 */
	public Integer getNumberOfCommunities(final int runId) throws Exception;
	
	/**
	 * retrieve a list of all community ids for the given run_id
	 * @param run_id
	 * @return
	 */
	public Collection<Integer> listCommunities(final int run_id, final int limit, final int offset);

	/**
	 * retrieve a list of all community ids for the given run_id
	 * @param run_id
	 * @return
	 */
	public Collection<Integer> listCommunities(final int run_id);

	/**
	 * retrieve (a sample of) all communities for a given run id
	 * 
	 * @param run_id
	 * @param tagCloudLimit max. number of most important tags to retrieve
	 * @param bibTexLimit max. number of most important publications to retrieve
	 * @param bookmarkLimit max. number of most important bookmarks to retrieve
	 * @param limit number of communities to retrieve
	 * @param offset number of communities to skip
	 * @return
	 */
	public Collection<ResourceCluster> getCommunities(final int runId, final int userCloudLimit, final int tagCloudLimit, final int bibTexLimit, final int bookmarkLimit, final int limit, final int offset);
}
