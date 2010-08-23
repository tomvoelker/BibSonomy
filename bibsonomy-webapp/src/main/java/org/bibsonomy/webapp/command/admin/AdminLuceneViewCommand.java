package org.bibsonomy.webapp.command.admin;

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

	
	private String envContextString;
	private String luceneBookmarksPath;
	private String lucenePublicationsPath;
	private String luceneDataSourceUrl;
	private String luceneDataSourceUsername;

	private LuceneIndexSettingsCommand bookmarksIndex = new LuceneIndexSettingsCommand();
	private LuceneIndexSettingsCommand bookmarksIndex2 = new LuceneIndexSettingsCommand();
	private LuceneIndexSettingsCommand publicationsIndex = new LuceneIndexSettingsCommand();
	private LuceneIndexSettingsCommand publicationsIndex2 = new LuceneIndexSettingsCommand();
	private LuceneIndexSettingsCommand goldstandardIndex = new LuceneIndexSettingsCommand();
	private LuceneIndexSettingsCommand goldstandardIndex2 = new LuceneIndexSettingsCommand();



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
	
	/**
	 * @return the goldstandardIndex
	 */
	public LuceneIndexSettingsCommand getGoldstandardIndex() {
		return this.goldstandardIndex;
	}

	/**
	 * @param goldstandardIndex the goldstandardIndex to set
	 */
	public void setGoldstandardIndex(LuceneIndexSettingsCommand goldstandardIndex) {
		this.goldstandardIndex = goldstandardIndex;
	}
	/**
	 * @return the bookmarksIndex2
	 */
	public LuceneIndexSettingsCommand getBookmarksIndex2() {
		return this.bookmarksIndex2;
	}

	/**
	 * @param bookmarksIndex2 the bookmarksIndex2 to set
	 */
	public void setBookmarksIndex2(LuceneIndexSettingsCommand bookmarksIndex2) {
		this.bookmarksIndex2 = bookmarksIndex2;
	}

	/**
	 * @return the publicationsIndex2
	 */
	public LuceneIndexSettingsCommand getPublicationsIndex2() {
		return this.publicationsIndex2;
	}

	/**
	 * @param publicationsIndex2 the publicationsIndex2 to set
	 */
	public void setPublicationsIndex2(LuceneIndexSettingsCommand publicationsIndex2) {
		this.publicationsIndex2 = publicationsIndex2;
	}

	/**
	 * @return the goldstandardIndex2
	 */
	public LuceneIndexSettingsCommand getGoldstandardIndex2() {
		return this.goldstandardIndex2;
	}

	/**
	 * @param goldstandardIndex2 the goldstandardIndex2 to set
	 */
	public void setGoldstandardIndex2(LuceneIndexSettingsCommand goldstandardIndex2) {
		this.goldstandardIndex2 = goldstandardIndex2;
	}

}