package org.bibsonomy.webapp.command;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Command bean for admin page 
 * 
 * @author Sven Stefani
 * @version $Id$
 */
public class AdminLuceneViewCommand extends BaseCommand {
	
	private static final Log log = LogFactory.getLog(AdminLuceneViewCommand.class);
	
	
	/** Command containing current admin settings */
//	private AdminSettingsCommand settingsCommand = new AdminSettingsCommand();
	
	/** the time interval (in hours) for retrieving spammers */
	//TODO: variable time intervals
//	private Integer[] interval = new Integer[] {12, 24, 168};
	
	/** specific action for admin page */
	private String action;

	
	private String envContextString;
	private String luceneBookmarksPath;
	private String lucenePublicationsPath;
	private String luceneDataSourceUrl;
	private String luceneDataSourceUsername;

	public LuceneIndexSettingsCommand bookmarksIndex = new LuceneIndexSettingsCommand();
	public LuceneIndexSettingsCommand publicationsIndex = new LuceneIndexSettingsCommand();

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
	 * @param luceneDataSourceURL the luceneDataSourceURL to set
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
	 * @return the bookmarksIndex
	 */
	public LuceneIndexSettingsCommand getBookmarksIndex() {
		return this.bookmarksIndex;
	}

	/**
	 * @param bookmarksIndex the bookmarksIndex to set
	 */
	public void setBookmarksIndex(LuceneIndexSettingsCommand bookmarksIndex) {
		this.bookmarksIndex = bookmarksIndex;
	}

	/**
	 * @return the publicationsIndex
	 */
	public LuceneIndexSettingsCommand getPublicationsIndex() {
		return this.publicationsIndex;
	}

	/**
	 * @param publicationsIndex the publicationsIndex to set
	 */
	public void setPublicationsIndex(LuceneIndexSettingsCommand publicationsIndex) {
		this.publicationsIndex = publicationsIndex;
	}


	
	

}