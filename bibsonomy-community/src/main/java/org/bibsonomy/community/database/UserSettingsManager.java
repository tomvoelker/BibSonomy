package org.bibsonomy.community.database;

import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;

import org.bibsonomy.community.database.param.CommunityParam;
import org.bibsonomy.community.model.ResourceCluster;
import org.bibsonomy.community.model.User;

public class UserSettingsManager extends AbstractDBManager {
	
	private static final String SQL_MAP_FILE = "SqlMapConfig_community.xml";
	
	/** singleton pattern's instance reference */
	protected static UserSettingsManager instance = null;
	
	/** disabled constructor */
	private UserSettingsManager() {
		super(SQL_MAP_FILE);
	}

	/**
	 * @return An instance of this implementation of 
	 */
	public static UserSettingsManager getInstance() {
		if (instance == null) instance = new UserSettingsManager();
		return instance;
	}
	//------------------------------------------------------------------------
	// update
	//------------------------------------------------------------------------
	/**
	 * set user's affiliation to given cluster for a given run_id
	 * @param runId
	 * @param user
	 * @throws Exception
	 */
	public void setUserAffiliation(final User user) throws Exception {
		getSqlMap().startBatch();
		for( final Entry<Integer, Double> entry : user.getCommunityAffiliation().entrySet() ) {
			final Integer communityUId = entry.getKey();
			final Double weight       = entry.getValue();
			
			final CommunityParam param = new CommunityParam();
			param.setUserName(user.getName());
			param.setCommunityID(communityUId);
			param.setWeight(weight);
			getSqlMap().insert("setUserAffiliation", param);
		}
		getSqlMap().executeBatch();
	}
	
	//------------------------------------------------------------------------
	// set
	//------------------------------------------------------------------------
	public void removeUserAffiliation(final User user, final Collection<ResourceCluster> clusters) throws Exception {
		getSqlMap().startBatch();
		
		for( final ResourceCluster cluster : clusters ) {
			final CommunityParam param = new CommunityParam();
			param.setUserName(user.getName());
			param.setCommunityUID(cluster.getClusterID());
			
			getSqlMap().delete("removeUserAffiliation", param);
		};
		getSqlMap().executeBatch();
		
	}
	
	public void addUserAffiliation(final User user, final ResourceCluster cluster) throws Exception {
		final CommunityParam param = new CommunityParam();
		param.setUserName(user.getName());
		param.setCommunityID(cluster.getClusterID());
		param.setWeight(cluster.getWeight());
		getSqlMap().insert("addUserAffiliation", param);
	}

	public void addUserAffiliation(final User user, final Collection<ResourceCluster> clusters) throws Exception {
		getSqlMap().startBatch();
		
		for( final ResourceCluster cluster : clusters ) {
			addUserAffiliation(user, cluster);
		};
		getSqlMap().executeBatch();
	}

	public void updateUserAffiliation(final User user, final Collection<ResourceCluster> clusters) throws Exception {
		getSqlMap().startBatch();
		
		for( final ResourceCluster cluster : clusters ) {
			final CommunityParam param = new CommunityParam();
			param.setUserName(user.getName());
			param.setCommunityID(cluster.getClusterID());
			param.setWeight(cluster.getWeight());
		
			getSqlMap().insert("setUserAffiliation", param);
		};
		getSqlMap().executeBatch();
	}
	
	public void setAlgorithmForUser(final String userName, final Integer runId) throws Exception {
		final CommunityParam param = new CommunityParam();
		param.setUserName(userName);
		param.setRunID(runId);
		getSqlMap().insert("setAlgorithmForUser", param);
	}
	//------------------------------------------------------------------------
	// get
	//------------------------------------------------------------------------
	/**
	 * set user's affiliation to given cluster for a given run_id
	 * @param runId
	 * @param user
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void fillUserAffiliation(final User user) throws Exception {
		final CommunityParam param = new CommunityParam();
		param.setUserName(user.getName());
		final List<CommunityParam> entries = getSqlMap().queryForList("getUserAffiliation", param);
		for( final CommunityParam entry : entries ) {
			user.setAffiliation(entry.getCommunityID(), entry.getWeight());
		}
	}

	/**
	 * returns the id for the newest set of clusterings
	 * @throws Exception 
	 */
	public Integer getNewestRunSet() throws Exception {
		return (Integer) getSqlMap().queryForObject("getNewestRunSet");
	}

	/**
	 * returns the id for the newest set of clusterings
	 * @throws Exception 
	 */
	public Integer getCurrentAlgorithm(final String userName) throws Exception {
		return (Integer) getSqlMap().queryForObject("getCurrentAlgorithmForUser", userName);
	}

	/**
	 * returns ids for all available clusterings in the given run set
	 * 
	 * @param runSet
	 * @return
	 * @throws Exception 
	 */
	@SuppressWarnings("unchecked")
	public List<Integer> getAlgorithmsForRunSet(final Integer runSet) throws Exception {
		final CommunityParam param = new CommunityParam();
		param.setBlockID(runSet);
		return getSqlMap().queryForList("getClusteringsForRunSet", param);
	}
}
