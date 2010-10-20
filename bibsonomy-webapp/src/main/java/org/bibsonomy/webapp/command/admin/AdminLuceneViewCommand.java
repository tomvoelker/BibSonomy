package org.bibsonomy.webapp.command.admin;

import java.util.ArrayList;
import java.util.List;

import org.bibsonomy.webapp.command.BaseCommand;


/**
 * Command bean for admin page 
 * 
 * @author Sven Stefani
 * @version $Id$
 */
public class AdminLuceneViewCommand extends BaseCommand {	
	/** the time interval (in hours) for retrieving spammers */
	//TODO: variable time intervals
//	private Integer[] interval = new Integer[] {12, 24, 168};
	
	/** specific action for admin page */
	private String action;
	private String adminResponse = "";

	
	private String envContextString;
	private String luceneBookmarksPath;
	private String lucenePublicationsPath;
	private String luceneDataSourceUrl;
	private String luceneDataSourceUsername;
	private List<LuceneIndexSettingsCommand> indices = new ArrayList<LuceneIndexSettingsCommand>();



	/**
	 * @return the luceneBookmarkPath
	 */
	public String getLuceneBookmarksPath() {
		return this.luceneBookmarksPath;
	}

	/**
	 * @return the action
	 */
	public String getAction() {
		return this.action;
	}

	/**
	 * @param action the action to set
	 */
	public void setAction(String action) {
		this.action = action;
	}

	/**
	 * @return the envContextString
	 */
	public String getEnvContextString() {
		return this.envContextString;
	}

	/**
	 * @param envContextString the envContextString to set
	 */
	public void setEnvContextString(String envContextString) {
		this.envContextString = envContextString;
	}

	/**
	 * @return the lucenePublicationsPath
	 */
	public String getLucenePublicationsPath() {
		return this.lucenePublicationsPath;
	}

	/**
	 * @param lucenePublicationsPath the lucenePublicationsPath to set
	 */
	public void setLucenePublicationsPath(String lucenePublicationsPath) {
		this.lucenePublicationsPath = lucenePublicationsPath;
	}

	/**
	 * @return the luceneDataSourceURL
	 */
	public String getLuceneDataSourceUrl() {
		return this.luceneDataSourceUrl;
	}

	/**
	 * @param luceneDataSourceUrl the luceneDataSourceURL to set
	 */
	public void setLuceneDataSourceUrl(String luceneDataSourceUrl) {
		this.luceneDataSourceUrl = luceneDataSourceUrl;
	}


	/**
	 * @return the luceneDataSourceURL
	 */
	public String getLuceneDataSourceUrlShort() {
		return this.luceneDataSourceUrl.substring(0, 135);
	}
	
	/**
	 * @return the luceneDataSourceUsername
	 */
	public String getLuceneDataSourceUsername() {
		return this.luceneDataSourceUsername;
	}

	/**
	 * @param luceneDataSourceUsername the luceneDataSourceUsername to set
	 */
	public void setLuceneDataSourceUsername(String luceneDataSourceUsername) {
		this.luceneDataSourceUsername = luceneDataSourceUsername;
	}

	/**
	 * @param luceneBookmarksPath the luceneBookmarksPath to set
	 */
	public void setLuceneBookmarksPath(String luceneBookmarksPath) {
		this.luceneBookmarksPath = luceneBookmarksPath;
	}

	/**
	 * @param indices the list of indices
	 */
	public void setIndices(List<LuceneIndexSettingsCommand> indices) {
		this.indices = indices;
	}

	/**
	 * @return the list of indices
	 */
	public List<LuceneIndexSettingsCommand> getIndices() {
		return indices;
	}

	/**
	 * @param adminResponse
	 */
	public void setAdminResponse(String adminResponse) {
		this.adminResponse = adminResponse;
	}

	/**
	 * @return the admin response
	 */
	public String getAdminResponse() {
		return adminResponse;
	}

}