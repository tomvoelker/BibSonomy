package org.bibsonomy.webapp.command.ajax;

import java.util.List;

import org.bibsonomy.model.sync.SyncService;

/**
 * @author wla
 * @version $Id$
 */
public class AjaxSynchronizationCommand extends AjaxCommand{

	private String userName;
	private int serviceId = 0;
	private String serviceName;
	private String syncUserName="";
	private String apiKey;
	private List<SyncService> syncServices;
	private int syncAction;
	

	/**
	 * @param userName the userName to set
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}
	/**
	 * @return the userName
	 */
	public String getUserName() {
		return userName;
	}
	/**
	 * @return the serviceId
	 */
	public int getServiceId() {
		return this.serviceId;
	}
	/**
	 * @param serviceId the serviceId to set
	 */
	public void setServiceId(int serviceId) {
		this.serviceId = serviceId;
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
	 * @param syncUserName the syncUserName to set
	 */
	public void setSyncUserName(String syncUserName) {
		this.syncUserName = syncUserName;
	}
	/**
	 * @return the syncUserName
	 */
	public String getSyncUserName() {
		return this.syncUserName;
	}
	/**
	 * @param apiKey the apiKey to set
	 */
	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}
	/**
	 * @return the apiKey
	 */
	public String getApiKey() {
		return apiKey;
	}
	/**
	 * @param syncServices the syncServices to set
	 */
	public void setSyncServices(List<SyncService> syncServices) {
		this.syncServices = syncServices;
	}
	/**
	 * @return the syncServices
	 */
	public List<SyncService> getSyncServices() {
		return syncServices;
	}
	/**
	 * @param syncAction the syncAction to set
	 */
	public void setSyncAction(int syncAction) {
		this.syncAction = syncAction;
	}
	/**
	 * @return the syncAction
	 */
	public int getSyncAction() {
		return syncAction;
	}
	
}
