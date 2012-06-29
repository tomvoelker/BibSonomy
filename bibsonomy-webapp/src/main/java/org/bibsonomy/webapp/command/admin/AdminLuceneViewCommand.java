package org.bibsonomy.webapp.command.admin;

import java.util.LinkedList;
import java.util.List;

import org.bibsonomy.lucene.index.manager.LuceneResourceManager;
import org.bibsonomy.model.Resource;
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
	
	/** the specific index id for the indexd to be updated **/
	private int id;
	
	
	/** the resource class to handle */
	// TODO: should be Class<? extends Resource>
	private String resource;
	
	/** the string response for the admin */
	private String adminResponse = "";
	
	private final List<LuceneResourceManager<? extends Resource>> indices = new LinkedList<LuceneResourceManager<? extends Resource>>();

	/**
	 * @return the action
	 */
	public String getAction() {
		return this.action;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return this.id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @param action the action to set
	 */
	public void setAction(final String action) {
		this.action = action;
	}

	/**
	 * @return the list of indices
	 */
	public List<LuceneResourceManager<? extends Resource>> getIndices() {
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