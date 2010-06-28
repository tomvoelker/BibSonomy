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

	/**
	 * @return the maxClusterCount
	 */
	public Integer getMaxClusterCount() {
		return this.maxClusterCount;
	}

	/**
	 * @param maxClusterCount the maxClusterCount to set
	 */
	public void setMaxClusterCount(Integer maxClusterCount) {
		this.maxClusterCount = maxClusterCount;
	}

	/**
	 * @return the communityBaseUrl
	 */
	public String getCommunityBaseUrl() {
		return this.communityBaseUrl;
	}

	/**
	 * @param communityBaseUrl the communityBaseUrl to set
	 */
	public void setCommunityBaseUrl(String communityBaseUrl) {
		this.communityBaseUrl = communityBaseUrl;
	}
}
