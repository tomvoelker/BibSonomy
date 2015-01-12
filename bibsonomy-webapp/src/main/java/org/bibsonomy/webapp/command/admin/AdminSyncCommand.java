/**
 * BibSonomy-Webapp - The web application for BibSonomy.
 *
 * Copyright (C) 2006 - 2014 Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               http://www.kde.cs.uni-kassel.de/
 *                           Data Mining and Information Retrieval Group,
 *                               University of Würzburg, Germany
 *                               http://www.is.informatik.uni-wuerzburg.de/en/dmir/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               http://www.l3s.de/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.webapp.command.admin;

import java.util.List;

import org.bibsonomy.model.sync.SyncService;
import org.bibsonomy.webapp.command.BaseCommand;

/**
 * @author wla
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
