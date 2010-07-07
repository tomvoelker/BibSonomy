package org.bibsonomy.community.algorithm;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.community.database.UserSettingsManager;

public class RandomSelector implements AlgorithmSelectionStrategy {
	private final static Log log = LogFactory.getLog(RandomSelector.class);
	private UserSettingsManager userLogic;

	public Integer getNewClustering(String userName) throws Exception {
		Integer runSet = getNewRunSet(userName);
		List<Integer> clusterings = this.userLogic.getAlgorithmsForRunSet(runSet);
		
		int selection = (int) Math.floor(Math.random()*clusterings.size());
		return clusterings.get(selection);
	}

	public Integer getNewRunSet(String userName) {
		try {
			return this.userLogic.getNewestRunSet();
		} catch( Exception e ) {
			log.error("Error getting newest run set for user " + userName);
			return 0;
		}
	}

	//------------------------------------------------------------------------
	// getter/setter
	//------------------------------------------------------------------------
	public void setUserLogic(UserSettingsManager userLogic) {
		this.userLogic = userLogic;
	}

	public UserSettingsManager getUserLogic() {
		return userLogic;
	}


}
