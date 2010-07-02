package org.bibsonomy.webapp.controller.browsing;

import org.bibsonomy.webapp.command.ClusterSettingsCommand;
import org.bibsonomy.webapp.controller.SingleResourceListControllerWithTags;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;

/**
 * @author fei
 * @version $Id$
 */
public class ClusterPageController extends SingleResourceListControllerWithTags implements MinimalisticController<ClusterSettingsCommand> {
	
	private String baseUrl;
	private Integer maxClusterCount;

	public View workOn(ClusterSettingsCommand command) {
		command.setCommunityBaseUrl("/bibsonomy-community-servlet");
		command.setMaxClusterCount(3);
		return Views.CLUSTERPAGE;
	}
	
	public ClusterSettingsCommand instantiateCommand() {
		return new ClusterSettingsCommand();
	}

	/**
	 * @return the baseUrl
	 */
	public String getBaseUrl() {
		return this.baseUrl;
	}

	/**
	 * @param baseUrl the baseUrl to set
	 */
	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

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
}
