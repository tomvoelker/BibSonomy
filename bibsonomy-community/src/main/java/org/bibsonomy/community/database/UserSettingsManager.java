package org.bibsonomy.community.database;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.community.database.param.CommunityParam;
import org.bibsonomy.community.model.ResourceCluster;
import org.bibsonomy.community.model.User;
import org.bibsonomy.community.util.Pair;

public class UserSettingsManager extends AbstractDBManager {
	private final static Log log = LogFactory.getLog(UserSettingsManager.class);
	
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
		for( Entry<Integer, Double> entry : user.getCommunityAffiliation().entrySet() ) {
			final Integer communityUId = entry.getKey();
			final Double weight       = entry.getValue();
			
			CommunityParam param = new CommunityParam();
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
	public void removeUserAffiliation(User user, Collection<ResourceCluster> clusters) throws Exception {
		getSqlMap().startBatch();
		
		for( ResourceCluster cluster : clusters ) {
			CommunityParam param = new CommunityParam();
			param.setUserName(user.getName());
			param.setCommunityUID(cluster.getClusterID());
			
			getSqlMap().delete("removeUserAffiliation", param);
		};
		getSqlMap().executeBatch();
		
	}
	
	public void addUserAffiliation(User user, Collection<ResourceCluster> clusters) throws Exception {
		getSqlMap().startBatch();
		
		for( ResourceCluster cluster : clusters ) {
			CommunityParam param = new CommunityParam();
			param.setUserName(user.getName());
			param.setCommunityID(cluster.getClusterID());
			param.setWeight(cluster.getWeight());
		
			getSqlMap().insert("addUserAffiliation", param);
		};
		getSqlMap().executeBatch();
	}
	
	public void setAlgorithmForUser(String userName, Integer runId) throws Exception {
		CommunityParam param = new CommunityParam();
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
	public void fillUserAffiliation(User user) throws Exception {
		CommunityParam param = new CommunityParam();
		param.setUserName(user.getName());
		List<CommunityParam> entries = (List<CommunityParam>) getSqlMap().queryForList("getUserAffiliation", param);
		for( CommunityParam entry : entries ) {
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
	public Integer getCurrentAlgorithm(String userName) throws Exception {
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
	public List<Integer> getAlgorithmsForRunSet(Integer runSet) throws Exception {
		CommunityParam param = new CommunityParam();
		param.setBlockID(runSet);
		return (List<Integer>)getSqlMap().queryForList("getClusteringsForRunSet", param);
	}

}
