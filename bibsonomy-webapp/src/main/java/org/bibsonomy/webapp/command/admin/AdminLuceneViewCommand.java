package org.bibsonomy.webapp.command.admin;

import java.util.List;

import org.bibsonomy.webapp.command.BaseCommand;


/**
 * Command bean for admin page 
 * 
 * @author Sven Stefani
 * @author bsc
 * @version $Id$
 */
public class AdminLuceneViewCommand extends BaseCommand {		
	/** specific action for admin page */
	private String action;
	
	/** the resource class to handle */
	private String resource;
	
	/** the string response for the admin */
	private String adminResponse = "";
	
	private List<LuceneIndexInfo> indices;

	/**
	 * @return the action
	 */
	public String getAction() {
		return this.action;
	}

	/**
	 * @param action the action to set
	 */
	public void setAction(final String action) {
		this.action = action;
	}

	/**
	 * @param indices the list of indices
	 */
	public void setIndices(final List<LuceneIndexInfo> indices) {
		this.indices = indices;
	}

	/**
	 * @return the list of indices
	 */
	public List<LuceneIndexInfo> getIndices() {
		return indices;
	}

	/**
	 * @param adminResponse
	 */
	public void setAdminResponse(final String adminResponse) {
		this.adminResponse = adminResponse;
	}

	/**
	 * @return the admin response
	 */
	public String getAdminResponse() {
		return adminResponse;
	}

	/**
	 * @return the resource
	 */
	public String getResource() {
		return this.resource;
	}

	/**
	 * @param resource the resource to set
	 */
	public void setResource(final String resource) {
		this.resource = resource;
	}
}