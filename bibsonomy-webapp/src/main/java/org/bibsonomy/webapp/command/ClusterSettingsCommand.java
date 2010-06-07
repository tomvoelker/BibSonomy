package org.bibsonomy.webapp.command;

/**
 * @author fei
 * @version $Id$
 */
public class ClusterSettingsCommand extends SimpleResourceViewCommand {
	/** max. number of clusters */
	private Integer maxClusterCount;
	
	/** base url for the cluster servlet */
	private String communityBaseUrl;

	//------------------------------------------------------------------------
	// getter/setter
	//------------------------------------------------------------------------
	public void setCommunityBaseUrl(String communityBaseUrl) {
		this.communityBaseUrl = communityBaseUrl;
	}

	public String getCommunityBaseUrl() {
		return communityBaseUrl;
	}

	public void setMaxClusterCount(Integer maxClusterCount) {
		this.maxClusterCount = maxClusterCount;
	}

	public Integer getMaxClusterCount() {
		return maxClusterCount;
	}
}
