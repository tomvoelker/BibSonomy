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
package org.bibsonomy.webapp.command.special;

import java.util.Date;

import org.bibsonomy.webapp.command.BaseCommand;

/**
 * 
 * Command to do redirects for
 * <ul>
 * <li>/my* pages like /myBibSonomy, /myRelations, etc.,</li>
 * <li>/redirect pages for search forms,</li>
 * <li>/uri/ content negotiation.</li>
 * </ul>
 * 
 * and for the {@link MementoController} (see there).
 * 
 * @author rja
 */
public class RedirectCommand extends BaseCommand {

	/**
	 * Name of the /my* page, e.g., "myRelations".
	 */
	private String myPage;
	
	/**
	 * The search terms for the general search form.
	 */
	private String search;
	/**
	 * The scope of the performed search (e.g., "user", "group", ...)
	 */
	private String scope;
	/**
	 * The user to restrict the author search to.  
	 */
	private String requUser;

	/**
	 * The timestamp for which an archived web page (memento) is requested
	 */
	private Date datetime;
	
	/**
	 * The URL to be used for content negotation and memento access
	 */
	private String url;
	
	/**
	 * @return datetime
	 */
	public Date getDatetime() {
		return datetime;
	}
	
	/**
	 * @param datetime
	 */
	public void setDatetime(final Date datetime) {
		this.datetime = datetime;
	}
	
	/** Requested URL for content negotiation and memento access
	 * 
	 * @return The URL for content negotiation.
	 */
	public String getUrl() {
		return this.url;
	}

	/** Sets the requested URL for content negotiation.
	 * @param url 
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/** Return the requested user - only relevant for /author pages, i.e., when scope=author.
	 * 
	 * @return The requested user name.
	 */
	public String getRequUser() {
		return this.requUser;
	}

	/** Set the requested user name - only relevant for /author pages, i.e., when scope=author. 
	 * @param requUser
	 */
	public void setRequUser(String requUser) {
		this.requUser = requUser;
	}

	/**
	 * @return The name of the /my* page, e.g., "myRelations".
	 */
	public String getMyPage() {
		return this.myPage;
	}

	/** Set the name of the /my* page, e.g., "myRelations".
	 * @param myPage
	 */
	public void setMyPage(String myPage) {
		this.myPage = myPage;
	}

	/** 
	 * 
	 * @return The search string.
	 */
	public String getSearch() {
		return this.search;
	}

	/** Set the search string.
	 * 
	 * @param search
	 */
	public void setSearch(String search) {
		this.search = search;
	}

	/**
	 * @return The scope of a search.
	 */
	public String getScope() {
		return this.scope;
	}

	/** Sets the scope of a search.
	 * @param scope
	 */
	public void setScope(String scope) {
		this.scope = scope;
	}
}
