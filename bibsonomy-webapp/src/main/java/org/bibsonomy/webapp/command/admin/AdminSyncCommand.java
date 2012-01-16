package org.bibsonomy.webapp.command.admin;

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
	private SyncService service;
	private boolean server;
	
	/**
	 * @param avlServer the avlServer to set
	 */
	public void setAvlServer(final List<SyncService> avlServer) {
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
	public void setAvlClients(final List<SyncService> avlClients) {
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
	public void setAction(final String action) {
		this.action = action;
	}
	
	/**
	 * @return the action
	 */
	public String getAction() {
		return action;
	}
	
	/**
	 * @return the service
	 */
	public SyncService getService() {
		return this.service;
	}

	/**
	 * @param service the service to set
	 */
	public void setService(final SyncService service) {
		this.service = service;
	}

	/**
	 * @param server the server to set
	 */
	public void setServer(final boolean server) {
		this.server = server;
	}
	
	/**
	 * @return the server
	 */
	public boolean isServer() {
		return server;
	}
}
