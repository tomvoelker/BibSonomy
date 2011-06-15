package org.bibsonomy.webapp.command.actions;

import java.net.URI;

import org.bibsonomy.webapp.command.BaseCommand;

/**
 * @author wla
 * @version $Id$
 */
public class SyncSettingsCommand extends BaseCommand {
	private URI service;
	private String serviceName;
	private String serverUserName;
	private String apiKey;
	private String action;
	/**
	 * @return the service
	 */
	public URI getService() {
		return this.service;
	}
	/**
	 * @param service the service to set
	 */
	public void setService(URI service) {
		this.service = service;
	}
	/**
	 * @return the serviceName
	 */
	public String getServiceName() {
		return this.serviceName;
	}
	/**
	 * @param serviceName the serviceName to set
	 */
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
	/**
	 * @return the serverUsername
	 */
	public String getServerUserName() {
		return this.serverUserName;
	}
	/**
	 * @param serverUsername the serverUsername to set
	 */
	public void setServerUserName(String serverUsername) {
		this.serverUserName = serverUsername;
	}
	/**
	 * @return the apiKey
	 */
	public String getApiKey() {
		return this.apiKey;
	}
	/**
	 * @param apiKey the apiKey to set
	 */
	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}
	/**
	 * @param action the action to set
	 */
	public void setAction(String action) {
		this.action = action;
	}
	/**
	 * @return the action
	 */
	public String getAction() {
		return action;
	}
}
