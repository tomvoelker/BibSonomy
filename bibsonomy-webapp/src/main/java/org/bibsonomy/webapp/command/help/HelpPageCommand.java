/**
 * BibSonomy-Webapp - The web application for BibSonomy.
 *
 * Copyright (C) 2006 - 2016 Knowledge & Data Engineering Group,
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
package org.bibsonomy.webapp.command.help;

import java.util.SortedSet;

import org.bibsonomy.services.help.HelpSearchResult;
import org.bibsonomy.webapp.command.actions.DownloadFileCommand;

/**
 * The command for the help pages and images.
 *
 * @author Johannes Blum
 */
public class HelpPageCommand extends DownloadFileCommand {

	private static final long serialVersionUID = -1480991183960187327L;

	/** The requested help page. */
	private String helpPage;

	/** the help page title */
	private String helpPageTitle;
	
	/** The main content of the help page. */
	private String content;
	
	/** The content of the sidebar. */
	private String sidebar;
	
	/** <code>true</code> if the requested help page could not be found. */
	private boolean pageNotFound = false;
	
	/** The project theme */
	private String theme;
	
	/** the language */
	private String language;
	
	private SortedSet<HelpSearchResult> searchResults;
	
	private String search;

	/**
	 * @return the helpPage
	 */
	public String getHelpPage() {
		return this.helpPage;
	}

	/**
	 * @param helpPage the helpPage to set
	 */
	public void setHelpPage(String helpPage) {
		this.helpPage = helpPage;
	}

	/**
	 * @return the helpPageTitle
	 */
	public String getHelpPageTitle() {
		return helpPageTitle;
	}

	/**
	 * @param helpPageTitle the helpPageTitle to set
	 */
	public void setHelpPageTitle(String helpPageTitle) {
		this.helpPageTitle = helpPageTitle;
	}

	/**
	 * @return the content of the requested helpPage
	 */
	public String getContent() {
		return content;
	}
	
	/**
	 * @param content The new content
	 */
	public void setContent(String content) {
		this.content = content;
	}

	/**
	 * @return the content of the sidebar
	 */
	public String getSidebar() {
		return this.sidebar;
	}
	
	/**
	 * @param sidebar The new content of the sidebar
	 */
	public void setSidebar(String sidebar) {
		this.sidebar = sidebar;
	}

	/**
	 * @return <code>true</code> if the requested file could not be found.
	 */
	public boolean isPageNotFound() {
		return this.pageNotFound;
	}

	/**
	 * @param pageNotFound the new value for pageNotFound
	 */
	public void setPageNotFound(boolean pageNotFound) {
		this.pageNotFound = pageNotFound;
	}

	/**
	 * @return the theme
	 */
	public String getTheme() {
		return this.theme;
	}

	/**
	 * @param theme the theme to set
	 */
	public void setTheme(String theme) {
		this.theme = theme;
	}

	/**
	 * @return the language
	 */
	public String getLanguage() {
		return this.language;
	}

	/**
	 * @param language the language to set
	 */
	public void setLanguage(String language) {
		this.language = language;
	}

	/**
	 * @return the searchResults
	 */
	public SortedSet<HelpSearchResult> getSearchResults() {
		return this.searchResults;
	}

	/**
	 * @param searchResults the searchResults to set
	 */
	public void setSearchResults(SortedSet<HelpSearchResult> searchResults) {
		this.searchResults = searchResults;
	}

	/**
	 * @return the search
	 */
	public String getSearch() {
		return this.search;
	}

	/**
	 * @param search the search to set
	 */
	public void setSearch(String search) {
		this.search = search;
	}

}
