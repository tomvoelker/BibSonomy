package org.bibsonomy.community.database;

import java.util.Collection;
import java.util.List;
import java.util.Map;

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
		for( Map.Entry<Pair<Integer,Integer>, Double> entry : user.getCommunityAffiliation().entrySet() ) {
			final Integer runId       = entry.getKey().getFirst();
			final Integer communityId = entry.getKey().getSecond();
			final Double weight       = entry.getValue();
			
			CommunityParam param = new CommunityParam();
			param.setUserName(user.getName());
			param.setRunID(runId);
			param.setCommunityID(communityId);
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
		
		int runId = 17;
		for( ResourceCluster cluster : clusters ) {
			CommunityParam param = new CommunityParam();
			param.setUserName(user.getName());
			param.setRunID(runId);
			param.setCommunityID(cluster.getClusterID());
			
			getSqlMap().delete("removeUserAffiliation", param);
		};
		getSqlMap().executeBatch();
		
	}
	
	public void addUserAffiliation(User user, Collection<ResourceCluster> clusters) throws Exception {
		getSqlMap().startBatch();
		
		int runId = 17;
		for( ResourceCluster cluster : clusters ) {
			CommunityParam param = new CommunityParam();
			param.setUserName(user.getName());
			param.setRunID(runId);
			param.setCommunityID(cluster.getClusterID());
			param.setWeight(cluster.getWeight());
		
			getSqlMap().insert("addUserAffiliation", param);
		};
		getSqlMap().executeBatch();
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
			user.setAffiliation(entry.getRunID(), entry.getCommunityID(), entry.getWeight());
		}
	}

}
