package org.bibsonomy.webapp.command.admin;

import java.net.URI;
import java.util.List;

import org.bibsonomy.model.sync.SyncService;
import org.bibsonomy.webapp.command.BaseCommand;

/**
 * @author wla
 * @version $Id$
 */
public class AdminSyncCommand extends BaseCommand {
	
	private List<SyncService> avlServer;
	private List<SyncService> avlClients;
	private String action;
	private URI service;
	private String sslDn;
	private URI secureAPI;
	private boolean server;
	
	/**
	 * @param avlServer the avlServer to set
	 */
	public void setAvlServer(List<SyncService> avlServer) {
		this.avlServer = avlServer;
	}
	/**
	 * @return the avlServer
	 */
	public List<SyncService> getAvlServer() {
		return avlServer;
	}
	/**
	 * @param avlClients the avlClients to set
	 */
	public void setAvlClients(List<SyncService> avlClients) {
		this.avlClients = avlClients;
	}
	/**
	 * @return the avlClients
	 */
	public List<SyncService> getAvlClients() {
		return avlClients;
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
	/**
	 * @param service the service to set
	 */
	public void setService(URI service) {
		this.service = service;
	}
	/**
	 * @return the service
	 */
	public URI getService() {
		return service;
	}
	/**
	 * @return the sslDn
	 */
	public String getSslDn() {
		return this.sslDn;
	}
	/**
	 * @param sslDn the sslDn to set
	 */
	public void setSslDn(String sslDn) {
		this.sslDn = sslDn;
	}
	/**
	 * @return the secureAPI
	 */
	public URI getSecureAPI() {
		return this.secureAPI;
	}
	/**
	 * @param secureAPI the secureAPI to set
	 */
	public void setSecureAPI(URI secureAPI) {
		this.secureAPI = secureAPI;
	}
	/**
	 * @param server the server to set
	 */
	public void setServer(boolean server) {
		this.server = server;
	}
	/**
	 * @return the server
	 */
	public boolean isServer() {
		return server;
	}
}
